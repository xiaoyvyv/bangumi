import { UPSTREAM } from '../config';

const CONTENT_TYPE_DNS_MESSAGE = 'application/dns-message';
const CONTENT_TYPE_DNS_JSON = 'application/dns-json';

export async function handleDnsQuery(req: Request, env: any): Promise<Response> {
	const { method, headers, url } = req;
	const parsedUrl = new URL(url);
	const { searchParams, search } = parsedUrl;

	if (method === 'GET' && searchParams.has('dns')) {
		return fetch(`${UPSTREAM.DOH}?dns=${searchParams.get('dns')}`, {
			method: 'GET',
			headers: {
				'Accept': CONTENT_TYPE_DNS_MESSAGE,
			},
		});
	} else if (method === 'POST' && headers.get('content-type') === CONTENT_TYPE_DNS_MESSAGE) {
		return fetch(UPSTREAM.DOH, {
			method: 'POST',
			headers: {
				'Accept': CONTENT_TYPE_DNS_MESSAGE,
				'Content-Type': CONTENT_TYPE_DNS_MESSAGE,
			},
			body: req.body,
		});
	} else if (method === 'GET' && headers.get('Accept') === CONTENT_TYPE_DNS_JSON) {
		return fetch(`${UPSTREAM.DOH}${search}`, {
			method: 'GET',
			headers: {
				'Accept': CONTENT_TYPE_DNS_JSON,
			},
		});
	}

	return new Response(null, { status: 404 });
}
