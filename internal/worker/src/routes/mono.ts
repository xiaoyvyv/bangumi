import { UPSTREAM } from '../config';
import { proxy } from '../core/proxy';
import { transformMonoHomepage } from '../transforms/mono.transform';

export async function handleMono(req: Request, _env: any): Promise<Response> {
	const url = new URL(req.url);
	if (!/^\/p1\/mono\/home\/?$/.test(url.pathname)) {
		return Response.json({ message: 'Not Found' }, { status: 404 });
	}

	return proxy(req, `${UPSTREAM.WEB_API}/mono`, { transform: transformMonoHomepage }, true);
}
