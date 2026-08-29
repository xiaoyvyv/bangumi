import { UPSTREAM } from '../config';
import { proxy } from '../core/proxy';
import { transformMonoBrowser, transformMonoHomepage } from '../transforms/mono.transform';
import { MonoType } from '../types';

type MonoRouteHandler = (req: Request, ctx?: ExecutionContext) => Promise<Response>;

const routes: Record<string, MonoRouteHandler> = {
	home: handleMonoHome,
	character: (req) => handleMonoBrowser(req, 'character'),
	person: (req) => handleMonoBrowser(req, 'person'),
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

async function handleMonoBrowser(req: Request, type: 'character' | 'person'): Promise<Response> {
	const source = new URL(req.url);
	const query = new URLSearchParams();
	for (const key of ['page', 'type', 'gender', 'bloodtype', 'month', 'day', 'orderby']) {
		const value = source.searchParams.get(key);
		if (value) query.set(key, value);
	}
	const suffix = query.size ? `?${query}` : '';

	const monoType = type === 'character' ? MonoType.CHARACTER : MonoType.PERSON;
	return proxy(req, `${UPSTREAM.WEB_API}/${type}${suffix}`, { transform: transformMonoBrowser(monoType) }, true);
}
