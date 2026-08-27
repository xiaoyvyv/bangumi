import {
	createExecutionContext,
	env,
	fetchMock,
	waitOnExecutionContext,
} from 'cloudflare:test';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import worker from '../src/index';

const IncomingRequest = Request<unknown, IncomingRequestCfProperties>;

describe('blogs handler under /p1/blogs', () => {
	beforeEach(() => {
		fetchMock.activate();
		fetchMock.disableNetConnect();
	});

	afterEach(() => {
		fetchMock.assertNoPendingInterceptors();
	});

	it('returns 404 for invalid blog path', async () => {
		const request = new IncomingRequest('http://example.com/p1/blogs/abc/cover');
		const ctx = createExecutionContext();
		const response = await worker.fetch(request, env, ctx);
		await waitOnExecutionContext(ctx);

		expect(response.status).toBe(404);
	});

	it('extracts photo icon from photos API and returns 302 redirect', async () => {
		fetchMock
			.get('https://next.bgm.tv')
			.intercept({ path: '/p1/blogs/379101/photos?limit=1&offset=0' })
			.reply(
				200,
				{
					data: [
						{
							id: 170190,
							icon: 'https://lain.bgm.tv/r/200x200/pic/photo/l/40/af/276758_T0c2a.jpg',
						},
					],
					total: 1,
				},
				{ headers: { 'content-type': 'application/json' } }
			);

		const request = new IncomingRequest('http://example.com/p1/blogs/379101/cover');
		const ctx = createExecutionContext();
		const response = await worker.fetch(request, env, ctx);
		await waitOnExecutionContext(ctx);

		expect(response.status).toBe(302);
		expect(response.headers.get('location')).toBe(
			'https://lain.bgm.tv/r/200x200/pic/photo/l/40/af/276758_T0c2a.jpg'
		);
	});

	it('extracts subject medium image when photos API is empty', async () => {
		fetchMock
			.get('https://next.bgm.tv')
			.intercept({ path: '/p1/blogs/10000/photos?limit=1&offset=0' })
			.reply(200, { data: [], total: 0 }, { headers: { 'content-type': 'application/json' } });

		fetchMock
			.get('https://next.bgm.tv')
			.intercept({ path: '/p1/blogs/10000/subjects' })
			.reply(
				200,
				[
					{
						id: 256668,
						images: {
							medium: 'https://lain.bgm.tv/r/200/pic/cover/l/68/85/256668_aVp27.jpg',
						},
					},
				],
				{ headers: { 'content-type': 'application/json' } }
			);

		const request = new IncomingRequest('http://example.com/p1/blogs/10000/cover');
		const ctx = createExecutionContext();
		const response = await worker.fetch(request, env, ctx);
		await waitOnExecutionContext(ctx);

		expect(response.status).toBe(302);
		expect(response.headers.get('location')).toBe(
			'https://lain.bgm.tv/r/200/pic/cover/l/68/85/256668_aVp27.jpg'
		);
	});

	it('returns 302 default cover when all APIs fail', async () => {
		fetchMock
			.get('https://next.bgm.tv')
			.intercept({ path: '/p1/blogs/99999/photos?limit=1&offset=0' })
			.reply(404, 'Not Found');

		fetchMock
			.get('https://next.bgm.tv')
			.intercept({ path: '/p1/blogs/99999/subjects' })
			.reply(404, 'Not Found');

		const request = new IncomingRequest('http://example.com/p1/blogs/99999/cover');
		const ctx = createExecutionContext();
		const response = await worker.fetch(request, env, ctx);
		await waitOnExecutionContext(ctx);

		expect(response.status).toBe(302);
		expect(response.headers.get('location')).toBe('https://bgm.tv/img/no_icon_subject.png');
	});
});
