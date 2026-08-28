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
	const cached = await cache.match(request);
	if (cached) return cached;

	const response = await load();
	if (!response.ok) return response;

	const headers = new Headers(response.headers);
	headers.set('Cache-Control', `public, max-age=${options.ttl}, s-maxage=${options.ttl}`);
	const cacheable = new Response(response.body, { status: response.status, statusText: response.statusText, headers });
	const write = cache.put(request, cacheable.clone()).catch((error) => console.warn('Cache write failed', error));

	if (ctx) ctx.waitUntil(write);
	else await write;

	return cacheable;
}
