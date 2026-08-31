import { webApiBase } from '../config';

export interface CacheOptions {
	ttl: number;
}

/**
 * Cache successful GET responses in Cloudflare's Cache API and expose the same TTL to clients.
 */
export async function withCache(
	request: Request,
	options: CacheOptions,
	load: () => Promise<Response>,
	ctx?: ExecutionContext,
): Promise<Response> {
	if (request.method !== 'GET') return load();

	const cache = caches.default;
	const cacheKey = cacheRequest(request);
	const cached = await cache.match(cacheKey);
	if (cached) return cached;

	const response = await load();
	if (!response.ok) return response;

	const headers = new Headers(response.headers);
	headers.set('Cache-Control', `public, max-age=${options.ttl}, s-maxage=${options.ttl}`);
	const cacheable = new Response(response.body, { status: response.status, statusText: response.statusText, headers });
	const write = cache.put(cacheKey, cacheable.clone()).catch((error) => console.warn('Cache write failed', error));

	if (ctx) ctx.waitUntil(write);
	else await write;

	return cacheable;
}

function cacheRequest(request: Request): Request {
	const url = new URL(request.url);
	url.searchParams.set('__upstream_base', webApiBase(request));
	return new Request(url, { method: 'GET' });
}
