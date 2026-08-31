import { proxy } from '../core/proxy';
import { transformTimeline } from '../transforms/timeline.transform';
import { webApiBase } from '../config';


export async function handleTimeline(req: Request, env: any) {
	const url = new URL(req.url);

	const mode = url.searchParams.get('mode')?.toLowerCase() || 'all';
	const type = url.searchParams.get('type') || '';
	const username = (
		url.searchParams.get('user') ?? url.searchParams.get('username')
	)?.trim().toLowerCase() || '';
	const page = url.searchParams.get('page') || '';

	let anonymous = false;
	let path: string;

	switch (mode) {
		case 'user':
			path = `/user/${username}/timeline`;
			break;

		case 'friends':
			path = '/timeline';
			break;

		default:
			path = '/timeline';
			anonymous = true;
			break;
	}

	const params = new URLSearchParams();

	if (type) {
		params.set('type', type);
	}

	if (page) {
		params.set('page', page);
	}

	params.set('ajax', '1');

	const query = params.toString();
	const targetUrl = webApiBase(req) + path + `?${query}`;

	return proxy(
		req,
		targetUrl,
		{ transform: transformTimeline },
		anonymous
	);
}
