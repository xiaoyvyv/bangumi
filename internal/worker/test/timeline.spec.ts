import {
	createExecutionContext,
	env,
	fetchMock,
	waitOnExecutionContext,
} from 'cloudflare:test';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import worker from '../src/index';

const IncomingRequest = Request<unknown, IncomingRequestCfProperties>;

describe('timeline handler', () => {
	beforeEach(() => {
		fetchMock.activate();
		fetchMock.disableNetConnect();
	});

	afterEach(() => {
		fetchMock.assertNoPendingInterceptors();
	});

	it('uses the user query parameter for a personal timeline', async () => {
		const timelineHtml = `
			<div id="timeline">
				<ul>
					<li id="tml_71478152" class="tml_item">
						<span class="info_full">
							<p class="status">测试吐槽</p>
							<div class="post_actions date">
								<a class="tml_comment">18 回复</a> ·
								<span class="titleTip" title="2026-08-20T06:14:00Z"></span> · web
							</div>
						</span>
					</li>
				</ul>
			</div>
		`;

		fetchMock
			.get('https://bgm.tv')
			.intercept({
				path: '/user/xiaoyvyv/timeline?type=say&page=1&ajax=1',
			})
			.reply(200, timelineHtml);

		const request = new IncomingRequest(
			'http://example.com/p1/timeline?mode=user&type=say&user=xiaoyvyv&page=1',
		);
		const ctx = createExecutionContext();
		const response = await worker.fetch(request, env, ctx);
		await waitOnExecutionContext(ctx);

		expect(response.status).toBe(200);
		expect(await response.json()).toMatchObject([
			{
				id: 71478152,
				cat: 5,
				type: 1,
				replies: 18,
				memo: { status: { tsukkomi: '测试吐槽' } },
			},
		]);
	});
});
