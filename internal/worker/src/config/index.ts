export const UPSTREAM = {
	WEB_API: 'https://bgm.tv',
	PUBLIC_API: 'https://api.bgm.tv',
	GRAPHQL: 'https://api.bgm.tv/v0/graphql',
	PRIVATE_API: 'https://next.bgm.tv',
	DOH: 'https://security.cloudflare-dns.com/dns-query'
};

const WEB_API_HOSTS = new Set(['bgm.tv', 'bangumi.tv']);

/**
 * 获取本次请求指定的 Bangumi 网页主站。
 *
 * @param req Worker 入站请求
 */
export function webApiBase(req: Request): string {
	const value = req.headers.get('BaseUrl');
	if (!value) return UPSTREAM.WEB_API;

	try {
		const url = new URL(value);
		if (url.protocol === 'https:' && WEB_API_HOSTS.has(url.hostname) && url.pathname === '/' && !url.search && !url.hash) {
			return url.origin;
		}
	} catch {
		// Fall through to the stable default upstream.
	}
	return UPSTREAM.WEB_API;
}
