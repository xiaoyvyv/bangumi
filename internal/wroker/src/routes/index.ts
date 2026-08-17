import { handleTimeline } from './timeline';

export async function router(req: Request, env: any) {
	const url = new URL(req.url);

	if (url.pathname.startsWith('/p1/timeline')) {
		return handleTimeline(req, env);
	}

	return new Response('Not Found', { status: 404 });
}
