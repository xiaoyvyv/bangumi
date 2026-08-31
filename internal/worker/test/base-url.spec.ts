import { describe, expect, it } from 'vitest';
import { UPSTREAM, webApiBase } from '../src/config';

describe('BaseUrl upstream selection', () => {
	it('accepts the supported Bangumi web domains', () => {
		expect(webApiBase(new Request('https://worker.example', { headers: { BaseUrl: 'https://bangumi.tv/' } })))
			.toBe('https://bangumi.tv');
		expect(webApiBase(new Request('https://worker.example', { headers: { BaseUrl: 'https://bgm.tv/' } })))
			.toBe('https://bgm.tv');
	});

	it('falls back when BaseUrl is not a supported web origin', () => {
		expect(webApiBase(new Request('https://worker.example', { headers: { BaseUrl: 'https://example.com/' } })))
			.toBe(UPSTREAM.WEB_API);
		expect(webApiBase(new Request('https://worker.example', { headers: { BaseUrl: 'https://bangumi.tv/blog/create' } })))
			.toBe(UPSTREAM.WEB_API);
	});
});
