import { UPSTREAM } from '../config';

const DEFAULT_COVER = 'https://bgm.tv/img/no_icon_subject.png';

export async function handleBlogs(req: Request, env: any): Promise<Response> {
	const url = new URL(req.url);

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
