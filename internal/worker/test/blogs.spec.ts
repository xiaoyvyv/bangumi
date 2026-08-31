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

	it('converts the JSON blog request into BGM multipart form data', async () => {
		fetchMock.get('https://bangumi.tv').intercept({
			path: '/blog/create',
			method: 'POST',
			headers: {
				cookie: 'chii_auth=token',
				origin: 'https://bangumi.tv',
				referer: 'https://bangumi.tv/blog/create',
				'content-type': (value) => value.startsWith('multipart/form-data; boundary='),
				'user-agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:154.0) Gecko/20100101 Firefox/154.0',
			},
			body: (value) => [
				'name="formhash"', 'turnstile-token', 'name="title"', '日志标题', 'name="content"', '日志正文',
				'name="tags"', '音乐 游戏', 'name="public"', '0', 'name="submit"', '加上去',
				'name="related_subject[]"', '1', '3',
			].every((part) => value.includes(part)),
		}).reply(302, '', { headers: { location: 'https://bgm.tv/blog/123' } });

		const request = new IncomingRequest('http://example.com/p1/blogs', {
			method: 'POST',
			headers: {
				Cookie: 'chii_auth=token',
				'Content-Type': 'application/json',
				BaseUrl: 'https://bangumi.tv/',
			},
			body: JSON.stringify({
				title: '日志标题', content: '日志正文', turnstileToken: 'turnstile-token',
				tags: ['音乐', '游戏'], public: true, subjectIDs: [1, 3],
			}),
		});
		const ctx = createExecutionContext();
		const response = await worker.fetch(request, env, ctx);
		await waitOnExecutionContext(ctx);

		expect(response.status).toBe(200);
		expect(await response.json()).toEqual({ id: 123 });
	});

	it('requires a JSON request and caller cookie to create a blog', async () => {
		const missingCookie = await worker.fetch(new IncomingRequest('http://example.com/p1/blogs', {
			method: 'POST',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify({ title: '标题', content: '正文', turnstileToken: 'token' }),
		}), env, createExecutionContext());
		expect(missingCookie.status).toBe(401);

		const wrongContentType = await worker.fetch(new IncomingRequest('http://example.com/p1/blogs', {
			method: 'POST',
			headers: { Cookie: 'chii_auth=token', 'Content-Type': 'multipart/form-data; boundary=test' },
			body: '--test--',
		}), env, createExecutionContext());
		expect(wrongContentType.status).toBe(415);
	});

	it('returns the original upstream response when BGM rejects creation', async () => {
		fetchMock.get('https://bgm.tv').intercept({
			path: '/blog/create',
			method: 'POST',
		}).reply(403, 'formhash error', { headers: { 'content-type': 'text/html; charset=UTF-8' } });

		const response = await worker.fetch(new IncomingRequest('http://example.com/p1/blogs', {
			method: 'POST',
			headers: { Cookie: 'chii_auth=token', 'Content-Type': 'application/json' },
			body: JSON.stringify({ title: '标题', content: '正文', turnstileToken: 'token' }),
		}), env, createExecutionContext());

		expect(response.status).toBe(403);
		expect(response.headers.get('content-type')).toContain('text/html');
		expect(await response.text()).toBe('formhash error');
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
