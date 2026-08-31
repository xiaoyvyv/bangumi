import { UPSTREAM, webApiBase } from '../config';

const DEFAULT_COVER = 'https://bgm.tv/img/no_icon_subject.png';

export async function handleBlogs(req: Request, env: any): Promise<Response> {
	const url = new URL(req.url);
	if (/^\/p1\/blogs\/?$/.test(url.pathname)) {
		return handleBlogCreate(req);
	}

	// 匹配 /p1/blogs/:entryId/cover
	const match = url.pathname.match(/^\/p1\/blogs\/(\d+)\/cover\/?$/);
	if (!match) {
		return Response.json({ message: 'Not Found' }, { status: 404 });
	}

	const entryId = match[1];

	// 1. 尝试从 photos API 获取日志插图
	const photosUrl = `${UPSTREAM.PRIVATE_API}/p1/blogs/${entryId}/photos?limit=1&offset=0`;
	try {
		const photosRes = await fetch(photosUrl, {
			headers: {
				Accept: 'application/json',
				'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)',
			},
		});
		if (photosRes.ok) {
			const json = (await photosRes.json()) as any;
			if (Array.isArray(json?.data) && json.data.length > 0 && json.data[0]?.icon) {
				let photoUrl = json.data[0].icon.trim();
				if (photoUrl.startsWith('//')) {
					photoUrl = 'https:' + photoUrl;
				}
				return Response.redirect(photoUrl, 302);
			}
		}
	} catch (e) {
		// ignore
	}

	// 2. 尝试从 subjects API 获取关联条目封面 (medium 尺寸)
	const subjectsUrl = `${UPSTREAM.PRIVATE_API}/p1/blogs/${entryId}/subjects`;
	try {
		const subjectsRes = await fetch(subjectsUrl, {
			headers: {
				Accept: 'application/json',
				'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)',
			},
		});
		if (subjectsRes.ok) {
			const json = (await subjectsRes.json()) as any;
			if (Array.isArray(json) && json.length > 0 && json[0]?.images) {
				const images = json[0].images;
				let imgUrl = (images.medium || images.common || images.large || '').trim();
				if (imgUrl) {
					if (imgUrl.startsWith('//')) {
						imgUrl = 'https:' + imgUrl;
					}
					return Response.redirect(imgUrl, 302);
				}
			}
		}
	} catch (e) {
		// ignore
	}

	// 默认兜底图
	return Response.redirect(DEFAULT_COVER, 302);
}

/**
 * 创建日志。
 *
 * 接收应用的 JSON 请求，并转换为 BGM 网页端需要的 multipart 表单。
 *
 * @param req 客户端提交的 JSON 请求
 */
async function handleBlogCreate(req: Request): Promise<Response> {
	if (req.method !== 'POST') {
		return Response.json({ message: 'Method Not Allowed' }, {
			status: 405,
			headers: { Allow: 'POST' },
		});
	}

	if (!req.headers.get('content-type')?.toLowerCase().startsWith('application/json')) {
		return Response.json({ message: 'Content-Type must be application/json' }, { status: 415 });
	}

	const cookie = req.headers.get('cookie');
	if (!cookie) {
		return Response.json({ message: 'Missing required header: Cookie' }, { status: 401 });
	}

	const request = await parseCreateBlogEntryRequest(req);
	if (request instanceof Response) return request;

	const body = new FormData();
	body.append('formhash', request.turnstileToken);
	body.append('title', request.title);
	body.append('content', request.content);
	body.append('tags', request.tags.join(' '));
	body.append('public', request.public ? '0' : '1');
	body.append('submit', '加上去');
	for (const subjectID of request.subjectIDs) body.append('related_subject[]', String(subjectID));

	const baseUrl = webApiBase(req);
	const headers = new Headers({
		Accept: 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8',
		'Accept-Language': 'zh-CN,zh;q=0.9',
		Cookie: cookie,
		Origin: baseUrl,
		Referer: `${baseUrl}/blog/create`,
		'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:154.0) Gecko/20100101 Firefox/154.0',
	});

	const response = await fetch(`${baseUrl}/blog/create`, {
		method: 'POST',
		headers,
		body,
		redirect: 'manual',
	});
	const id = blogId(response.headers.get('location'));
	if (id) return Response.json({ id });
	return response;
}

interface CreateBlogEntryRequest {
	content: string;
	public: boolean;
	subjectIDs: number[];
	tags: string[];
	title: string;
	turnstileToken: string;
}

async function parseCreateBlogEntryRequest(req: Request): Promise<CreateBlogEntryRequest | Response> {
	let value: Partial<CreateBlogEntryRequest>;
	try {
		value = await req.json() as Partial<CreateBlogEntryRequest>;
	} catch {
		return Response.json({ message: 'Invalid JSON body' }, { status: 400 });
	}

	if (!nonBlank(value.title) || !nonBlank(value.content) || !nonBlank(value.turnstileToken)) {
		return Response.json({ message: 'title, content and turnstileToken are required' }, { status: 400 });
	}
	if (value.tags && (!Array.isArray(value.tags) || value.tags.some((tag) => !nonBlank(tag)))) {
		return Response.json({ message: 'tags must be an array of non-blank strings' }, { status: 400 });
	}
	if (value.subjectIDs && (!Array.isArray(value.subjectIDs) || value.subjectIDs.some((id) => !Number.isSafeInteger(id) || id <= 0))) {
		return Response.json({ message: 'subjectIDs must be an array of positive integers' }, { status: 400 });
	}
	if (value.public != null && typeof value.public !== 'boolean') {
		return Response.json({ message: 'public must be a boolean' }, { status: 400 });
	}

	return {
		title: value.title,
		content: value.content,
		turnstileToken: value.turnstileToken,
		tags: value.tags ?? [],
		public: value.public ?? true,
		subjectIDs: value.subjectIDs ?? [],
	};
}

function nonBlank(value: unknown): value is string {
	return typeof value === 'string' && value.trim().length > 0;
}

function blogId(location: string | null): number {
	const id = location?.match(/\/blog\/(\d+)(?:\/|$)/)?.[1];
	return Number(id) || 0;
}
