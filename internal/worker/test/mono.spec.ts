import {
	createExecutionContext,
	env,
	fetchMock,
	waitOnExecutionContext,
} from 'cloudflare:test';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import worker from '../src/index';
import { parseMonoHomepage } from '../src/transforms/mono.transform';

const IncomingRequest = Request<unknown, IncomingRequestCfProperties>;

describe('mono homepage handler', () => {
	beforeEach(() => {
		fetchMock.activate();
		fetchMock.disableNetConnect();
	});

	afterEach(() => {
		fetchMock.assertNoPendingInterceptors();
	});

	it('parses GET /p1/mono/home into ComposeSection<ComposeMonoDisplay> items', async () => {
		fetchMock.get('https://bgm.tv').intercept({ path: '/mono' }).reply(200, `
			<div id="main" class="mainWrapper"></div>
			<div class="mainWrapper">
				<div id="columnSubjectBrowserA">
					<div class="section"><h2>热门角色</h2><a href="/character">更多 »</a>
						<ul><li><a href="/character/123" title="角色名"><img src="//lain.bgm.tv/pic/crt/l/a/b/123_crt.jpg"></a><p><small>角色中文名</small></p></li></ul>
					</div>
				</div>
				<div id="columnSubjectBrowserB"><div class="sideInner">
					<div class="subtitle">最近人物 <small><a href="/person">更多</a></small></div>
					<div><dl><dt><a class="avatar" href="/person/456" title="人物名"><span style="background-image:url('//lain.bgm.tv/pic/crt/g/a/b/456_prsn.jpg')"></span></a></dt></dl></div>
				</div></div>
			</div>
		`);

		const request = new IncomingRequest('http://example.com/p1/mono/home');
		const ctx = createExecutionContext();
		const response = await worker.fetch(request, env, ctx);
		await waitOnExecutionContext(ctx);

		expect(response.status).toBe(200);
		const body = await response.json() as any[];
		expect(body).toEqual([
			expect.objectContaining({
				key: '/character',
				header: { id: '/character', title: '热门角色', subtitle: '', more: '更多' },
			}),
			expect.objectContaining({
				key: '/character-123',
				item: expect.objectContaining({
					type: 2,
					info: { mono: expect.objectContaining({ id: 123, name: '角色名', nameCN: '角色中文名' }) },
				}),
			}),
			expect.objectContaining({
				key: 'sideInner-/person',
				header: { id: '/person', title: '最近人物', subtitle: '', more: '更多' },
			}),
			expect.objectContaining({
				key: '/person-456',
				item: expect.objectContaining({
					type: 1,
					info: { mono: expect.objectContaining({ id: 456, name: '人物名', nameCN: '人物名', type: 1 }) },
				}),
			}),
		]);
		expect(body[0].item).toEqual({ type: 0, info: { mono: { id: 0, images: {}, name: '', nameCN: '' } } });
		expect(body[2].item).toEqual({ type: 0, info: { mono: { id: 0, images: {}, name: '', nameCN: '' } } });
		expect(body[1]).not.toHaveProperty('header');
		expect(body[3]).not.toHaveProperty('header');
		expect(response.headers.get('cache-control')).toBe('public, max-age=3600, s-maxage=3600');
	});

	it('returns 404 for unknown mono endpoints', async () => {
		const request = new IncomingRequest('http://example.com/p1/mono/unknown');
		const ctx = createExecutionContext();
		const response = await worker.fetch(request, env, ctx);
		await waitOnExecutionContext(ctx);

		expect(response.status).toBe(404);
	});

	it('parses character and person browser pages while forwarding supported filters', async () => {
		fetchMock.get('https://bgm.tv').intercept({ path: '/character?page=2&type=anime&gender=female' }).reply(200, `
			<div id="columnCrtBrowserB"><div class="browserCrtList">
				<div id="item_character123"><a class="avatar" href="/character/123"><img src="//lain.bgm.tv/pic/crt/m/a/b/123_crt.jpg"></a><h3>角色名</h3><div class="prsn_info"><span class="tip">性别 女 / 血型 A</span></div></div>
			</div></div>
		`);
		fetchMock.get('https://bgm.tv').intercept({ path: '/person?page=3&orderby=collects' }).reply(200, `
			<div id="columnCrtBrowserB"><div class="browserCrtList">
				<div id="item_person456"><a class="avatar" href="/person/456"><img src="//lain.bgm.tv/pic/crt/m/a/b/456_prsn.jpg"></a><h3>人物名</h3><div class="prsn_info"><span class="tip">职业 声优</span></div></div>
			</div></div>
		`);

		const characterCtx = createExecutionContext();
		const characterResponse = await worker.fetch(
			new IncomingRequest('http://example.com/p1/mono/character?page=2&type=anime&gender=female&ignored=value'),
			env,
			characterCtx,
		);
		await waitOnExecutionContext(characterCtx);

		const personCtx = createExecutionContext();
		const personResponse = await worker.fetch(
			new IncomingRequest('http://example.com/p1/mono/person?page=3&orderby=collects'),
			env,
			personCtx,
		);
		await waitOnExecutionContext(personCtx);

		expect(await characterResponse.json()).toEqual([
			expect.objectContaining({
				type: 2,
				info: { mono: expect.objectContaining({ id: 123, name: '角色名', nameCN: '' }) },
			}),
		]);
		expect(await personResponse.json()).toEqual([
			expect.objectContaining({
				type: 1,
				info: { mono: expect.objectContaining({ id: 456, name: '人物名', nameCN: '' }) },
			}),
		]);
	});

	it('caches a successful homepage response for one hour', async () => {
		fetchMock.get('https://bgm.tv').intercept({ path: '/mono' }).reply(
			200,
			'<div class="mainWrapper"><div id="columnSubjectBrowserA"></div></div>',
		);

		const request = new IncomingRequest('http://example.com/p1/mono/home?cache-test=1');
		const firstCtx = createExecutionContext();
		const first = await worker.fetch(request, env, firstCtx);
		await waitOnExecutionContext(firstCtx);

		const secondCtx = createExecutionContext();
		const second = await worker.fetch(request, env, secondCtx);
		await waitOnExecutionContext(secondCtx);

		expect(await first.json()).toEqual([]);
		expect(await second.json()).toEqual([]);
		expect(second.headers.get('cache-control')).toBe('public, max-age=3600, s-maxage=3600');
	});

	it('treats absent homepage columns as empty sections', () => {
		expect(parseMonoHomepage('<div class="mainWrapper"></div>')).toEqual([]);
	});
});
