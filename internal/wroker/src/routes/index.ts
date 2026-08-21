import { handleTimeline } from './timeline';
import { handleDnsQuery } from './dns';

export async function router(req: Request, env: any) {
	console.log(req.url);
	const url = new URL(req.url);

	if (url.pathname.startsWith('/p1/timeline')) {
		return handleTimeline(req, env);
	}

	if (url.pathname.startsWith('/dns-query') || url.pathname.startsWith('/p1/dns-query')) {
		return handleDnsQuery(req, env);
	}

	return new Response('Not Found', { status: 404 });
}

