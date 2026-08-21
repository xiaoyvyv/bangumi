import { describe, it, expect } from 'vitest';
import worker from '../src/index';
import { env, createExecutionContext, waitOnExecutionContext } from 'cloudflare:test';

const IncomingRequest = Request<unknown, IncomingRequestCfProperties>;

describe('DNS query handler', () => {
	it('routes GET /dns-query with dns query parameter', async () => {
		const request = new IncomingRequest('http://example.com/dns-query?dns=AAABAAABAAAAAAAAB2V4YW1wbGUDY29tAAABAAEC');
		const ctx = createExecutionContext();
		const response = await worker.fetch(request, env, ctx);
		await waitOnExecutionContext(ctx);
		expect(response.status).toBe(200);
		expect(response.headers.get('content-type')).toContain('application/dns-message');
	});

	it('routes GET /dns-query with Accept application/dns-json', async () => {
		const request = new IncomingRequest('http://example.com/dns-query?name=bgm.tv&type=A', {
			headers: {
				'Accept': 'application/dns-json',
			},
		});
		const ctx = createExecutionContext();
		const response = await worker.fetch(request, env, ctx);
		await waitOnExecutionContext(ctx);
		expect(response.status).toBe(200);
		expect(response.headers.get('content-type')).toContain('application/dns-json');
	});

	it('returns 404 for invalid request on /dns-query', async () => {
		const request = new IncomingRequest('http://example.com/dns-query');
		const ctx = createExecutionContext();
		const response = await worker.fetch(request, env, ctx);
		await waitOnExecutionContext(ctx);
		expect(response.status).toBe(404);
	});
});
