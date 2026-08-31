import { CacheOptions, withCache } from './cache';

export async function proxy(
	req: Request,
	target: string,
	opt: {
		transform?: (req: Request, res: Response) => Promise<Response> | Response
		cache?: CacheOptions
	},
	anonymous: boolean = false,
	ctx?: ExecutionContext,
) {
	const load = async () => {
	const headers = proxyHeaders(req, target, anonymous);

	const resp = await fetch(target, {
		headers: headers,
		method: req.method,
		body: req.method === 'GET' ? undefined : req.body
	});

	return opt.transform ? opt.transform(req, resp) : resp;
	};

	return opt.cache ? withCache(req, opt.cache, load, ctx) : load();
}

export function proxyHeaders(req: Request, target: string, anonymous: boolean = false): Headers {
	const url = new URL(target);
	const headers = new Headers(req.headers);

	headers.delete('host');
	headers.delete('content-length');
	headers.delete('connection');
	headers.delete('baseurl');

	if (anonymous) {
		headers.delete('cookie');
		headers.delete('authorization');
	}

	headers.set('host', url.host);
	return headers;
}
