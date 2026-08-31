import {
	createExecutionContext,
	env,
	fetchMock,
	waitOnExecutionContext,
} from 'cloudflare:test';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import worker from '../src/index';

const IncomingRequest = Request<unknown, IncomingRequestCfProperties>;
const sampleCookie =
	'chii_sec_id=avGU%2FfAVPLMjUaftyOdXoROrppEMlKO4Z3b%2FFw; chii_sid=rLGjw1; chii_auth=ZvDG%2BvAZO';

describe('terminal handler strictly under /p1/terminal', () => {
	beforeEach(() => {
		fetchMock.activate();
		fetchMock.disableNetConnect();
	});

	afterEach(() => {
		fetchMock.assertNoPendingInterceptors();
	});

	it('returns 404 when calling terminal without /p1 prefix', async () => {
		const request = new IncomingRequest('http://example.com/terminal/personality');
		const ctx = createExecutionContext();
		const response = await worker.fetch(request, env, ctx);
		await waitOnExecutionContext(ctx);

		expect(response.status).toBe(404);
		expect(await response.text()).toBe('Not Found');
	});

	it('returns 404 json when calling unknown action under /p1/terminal', async () => {
		const request = new IncomingRequest('http://example.com/p1/terminal/unknown');
		const ctx = createExecutionContext();
		const response = await worker.fetch(request, env, ctx);
		await waitOnExecutionContext(ctx);

		expect(response.status).toBe(404);
		expect(await response.json()).toEqual({ message: 'Not Found' });
	});

	describe('GET /p1/terminal/personality', () => {
		it('fetches and transforms terminal personality data forwarding Cookie header', async () => {
			const rawHtml = `1 Bangumi娘 | Speech count:1050 | by Sai` + `\u{1F596}` + ` @ 1256570485 <br />	2 橘花 | Speech count:60 | by Sai` + `\u{1F596}` + ` @ 1256570491`;

			fetchMock
				.get('https://bangumi.tv')
				.intercept({
					path: '/terminal',
					method: 'POST',
					headers: {
						cookie: (val) => val.includes('chii_sec_id=avGU'),
					},
					body: 'input=personality&cmd=personality&arg=personality',
				})
				.reply(200, rawHtml, {
					headers: { 'Content-Type': 'text/html; charset=UTF-8' },
				});

			const request = new IncomingRequest('http://example.com/p1/terminal/personality', {
				headers: { Cookie: sampleCookie, BaseUrl: 'https://bangumi.tv/' },
			});
			const ctx = createExecutionContext();
			const response = await worker.fetch(request, env, ctx);
			await waitOnExecutionContext(ctx);

			expect(response.status).toBe(200);
			const data = await response.json();
			expect(data).toHaveLength(2);
			expect(data[0]).toEqual({
				id: 1,
				name: 'Bangumi娘',
				speechCount: 1050,
				creator: 'Sai🖖',
				createdAt: 1256570485,
			});
		});

		it('filters personalities by creator query parameter', async () => {
			const rawHtml = `1 Bangumi娘 | Speech count:1050 | by Sai` + `\u{1F596}` + ` @ 1256570485 <br />	2 橘花 | Speech count:60 | by 小玉 @ 1256570491`;

			fetchMock
				.get('https://bgm.tv')
				.intercept({
					path: '/terminal',
					method: 'POST',
					body: 'input=personality&cmd=personality&arg=personality',
				})
				.reply(200, rawHtml, {
					headers: { 'Content-Type': 'text/html; charset=UTF-8' },
				});

			const request = new IncomingRequest('http://example.com/p1/terminal/personality?creator=小玉');
			const ctx = createExecutionContext();
			const response = await worker.fetch(request, env, ctx);
			await waitOnExecutionContext(ctx);

			expect(response.status).toBe(200);
			const data = await response.json();
			expect(data).toHaveLength(1);
			expect(data[0]).toEqual({
				id: 2,
				name: '橘花',
				speechCount: 60,
				creator: '小玉',
				createdAt: 1256570491,
			});
		});

	});

	describe('GET /p1/terminal/list', () => {
		it('returns 400 json if cur_psn is missing', async () => {
			const request = new IncomingRequest('http://example.com/p1/terminal/list');
			const ctx = createExecutionContext();
			const response = await worker.fetch(request, env, ctx);
			await waitOnExecutionContext(ctx);

			expect(response.status).toBe(400);
			expect(await response.json()).toEqual({
				message: 'Missing required parameter: cur_psn',
			});
		});

		it('requests -all speeches when all=true forwarding Cookie header', async () => {
			const rawHtml = `1 铁血真汉子 <br /> 2 纯爷们 <br />`;

			fetchMock
				.get('https://bgm.tv')
				.intercept({
					path: '/terminal',
					method: 'POST',
					headers: {
						cookie: (val) => val.includes('chii_sec_id=avGU'),
					},
					body: 'input=list+-all&cmd=list&arg=-all&cur_psn=1',
				})
				.reply(200, rawHtml, {
					headers: { 'Content-Type': 'text/html; charset=UTF-8' },
				});

			const request = new IncomingRequest('http://example.com/p1/terminal/list?cur_psn=1&all=true', {
				headers: { Cookie: sampleCookie },
			});
			const ctx = createExecutionContext();
			const response = await worker.fetch(request, env, ctx);
			await waitOnExecutionContext(ctx);

			expect(response.status).toBe(200);
			const data = await response.json();
			expect(data).toEqual([
				{ id: 1, speech: '铁血真汉子' },
				{ id: 2, speech: '纯爷们' },
			]);
		});

		it('requests -m speeches when all=false or omitted forwarding Cookie header', async () => {
			const rawHtml = `=====================================
                                	 	 My Tyokyo
                               =====================================<br />	5016 hello by 小玉 @ 1787781591 <br />	5017 1 by 小玉 @ 1787784240 <br />	5018 qqq by 小玉 @ 1787784750 <br />`;

			fetchMock
				.get('https://bgm.tv')
				.intercept({
					path: '/terminal',
					method: 'POST',
					headers: {
						cookie: (val) => val.includes('chii_sec_id=avGU'),
					},
					body: 'input=list+-m&cmd=list&arg=-m&cur_psn=2',
				})
				.reply(200, rawHtml, {
					headers: { 'Content-Type': 'text/html; charset=UTF-8' },
				});

			const request = new IncomingRequest('http://example.com/p1/terminal/list?cur_psn=2', {
				headers: { Cookie: sampleCookie },
			});
			const ctx = createExecutionContext();
			const response = await worker.fetch(request, env, ctx);
			await waitOnExecutionContext(ctx);

			expect(response.status).toBe(200);
			const data = await response.json();
			expect(data).toEqual([
				{
					id: 5016,
					speech: 'hello',
					creator: '小玉',
					createdAt: 1787781591,
				},
				{
					id: 5017,
					speech: '1',
					creator: '小玉',
					createdAt: 1787784240,
				},
				{
					id: 5018,
					speech: 'qqq',
					creator: '小玉',
					createdAt: 1787784750,
				},
			]);
		});
	});

	describe('POST /p1/terminal/create', () => {
		it('returns 400 json if name is missing', async () => {
			const request = new IncomingRequest('http://example.com/p1/terminal/create', {
				method: 'POST',
			});
			const ctx = createExecutionContext();
			const response = await worker.fetch(request, env, ctx);
			await waitOnExecutionContext(ctx);

			expect(response.status).toBe(400);
			expect(await response.json()).toEqual({
				message: 'Missing required parameter: name',
			});
		});

		it('creates a new persona with name forwarding Cookie header', async () => {
			fetchMock
				.get('https://bgm.tv')
				.intercept({
					path: '/terminal',
					method: 'POST',
					headers: {
						cookie: (val) => val.includes('chii_sec_id=avGU'),
					},
					body: 'input=create+橘花&cmd=create&arg=橘花',
				})
				.reply(200, '人格创建成功');

			const request = new IncomingRequest('http://example.com/p1/terminal/create', {
				method: 'POST',
				headers: {
					'Content-Type': 'application/x-www-form-urlencoded',
					Cookie: sampleCookie,
				},
				body: 'name=橘花',
			});
			const ctx = createExecutionContext();
			const response = await worker.fetch(request, env, ctx);
			await waitOnExecutionContext(ctx);

			expect(response.status).toBe(200);
			expect(await response.json()).toEqual({
				message: '人格创建成功',
			});
		});
	});

	describe('POST /p1/terminal/speech', () => {
		it('returns 400 json if speech or cur_psn is missing', async () => {
			const request = new IncomingRequest('http://example.com/p1/terminal/speech', {
				method: 'POST',
				headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
				body: 'speech=hello',
			});
			const ctx = createExecutionContext();
			const response = await worker.fetch(request, env, ctx);
			await waitOnExecutionContext(ctx);

			expect(response.status).toBe(400);
			expect(await response.json()).toEqual({
				message: 'Missing required parameters: speech, cur_psn',
			});
		});

		it('adds speech for a persona forwarding Cookie header', async () => {
			fetchMock
				.get('https://bgm.tv')
				.intercept({
					path: '/terminal',
					method: 'POST',
					headers: {
						cookie: (val) => val.includes('chii_sec_id=avGU'),
					},
					body: 'input=speech+你好&cmd=speech&arg=你好&cur_psn=1',
				})
				.reply(200, '添加成功');

			const request = new IncomingRequest('http://example.com/p1/terminal/speech', {
				method: 'POST',
				headers: {
					'Content-Type': 'application/x-www-form-urlencoded',
					Cookie: sampleCookie,
				},
				body: 'speech=你好&cur_psn=1',
			});
			const ctx = createExecutionContext();
			const response = await worker.fetch(request, env, ctx);
			await waitOnExecutionContext(ctx);

			expect(response.status).toBe(200);
			expect(await response.json()).toEqual({
				message: '添加成功',
			});
		});
	});
});
