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
	const url = new URL(target);

	const headers = new Headers(req.headers);

	headers.delete('host');
	headers.delete('content-length');
	headers.delete('connection');

	if (anonymous) {
		headers.delete('cookie');
		headers.delete('authorization');
	}

	headers.set('host', url.host);

	const resp = await fetch(target, {
		headers: headers,
		method: req.method,
		body: req.method === 'GET' ? undefined : req.body
	});

	return opt.transform ? opt.transform(req, resp) : resp;
	};

	return opt.cache ? withCache(req, opt.cache, load, ctx) : load();
}
