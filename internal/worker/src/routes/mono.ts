import { UPSTREAM } from '../config';
import { proxy } from '../core/proxy';
import { transformMonoHomepage } from '../transforms/mono.transform';

type MonoRouteHandler = (req: Request, ctx?: ExecutionContext) => Promise<Response>;

const routes: Record<string, MonoRouteHandler> = {
	home: handleMonoHome,
};

export async function handleMono(req: Request, _env: any, ctx?: ExecutionContext): Promise<Response> {
	const segments = new URL(req.url).pathname.split('/').filter(Boolean);
	const action = segments[0] === 'p1' && segments[1] === 'mono' && segments.length === 3
		? segments[2]
		: '';
	const handler = routes[action];

	return handler ? handler(req, ctx) : Response.json({ message: 'Not Found' }, { status: 404 });
}

async function handleMonoHome(req: Request, ctx?: ExecutionContext): Promise<Response> {
	return proxy(
		req,
		`${UPSTREAM.WEB_API}/mono`,
		{ transform: transformMonoHomepage, cache: { ttl: 60 * 60 } },
		true,
		ctx,
	);
}
