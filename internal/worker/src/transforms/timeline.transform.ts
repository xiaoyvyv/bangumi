// noinspection DuplicatedCode

import { parseDocument } from 'htmlparser2';
import { selectAll, selectOne } from 'css-select';
import { UPSTREAM } from '../config';
import { removeElement, textContent } from 'domutils';
import render from 'dom-serializer';
import { Element, isTag } from 'domhandler';
import '../config/array.extensions';
import {
	containsAll,
	containsAny,
	containsAnyRegex,
	imageUrlId,
	numberContent,
	optImageUrl,
	selectElement,
	selectElements,
	spanStyleAvatar,
	subAfter,
	subAfterLast
} from '../config/util';
import {
	Blog,
	Character,
	Daily,
	Group,
	Images, Index,
	Memo,
	Mono,
	Person, Progress,
	Reaction, Status, SubjectItem,
	SubjectType,
	Timeline,
	TimelineCatAndType,
	User,
	Wiki
} from '../types';

enum TimelineCat {
	UNKNOWN = 0,
	DAILY = 1,        // 日常行为
	WIKI = 2,         // 维基操作
	SUBJECT = 3,      // 收藏条目
	PROGRESS = 4,     // 收视进度
	STATUS = 5,       // 状态
	BLOG = 6,         // 日志
	INDEX = 7,        // 目录
	MONO = 8,         // 人物
	WINDOW = 9,       // 天窗
}

export enum WebTimelineCat {
	RELATION = 'relation',
	WIKI = 'wiki',
	SUBJECT = 'subject',
	PROGRESS = 'progress',
	SAY = 'say',
	BLOG = 'blog',
	INDEX = 'index',
	MONO = 'mono',
	DOUJIN = 'doujin',
	UNKNOWN = ''
}

export async function transformTimeline(req: Request, res: Response) {
	const html = await res.text();
	const doc = parseDocument(html);
	const timelines = selectElements('#timeline > ul > li', doc);

	let avatar: Element | null = null;

	const items: Timeline[] = timelines.map((node) => {
		const id = parseInt(node.attribs.id?.replace('tml_', '') || '0');
		const actions = selectOne('.post_actions', node)!;

		const imgs = selectOne('.imgs', node);

		const date = selectOne('.titleTip', node)?.attribs?.title || '';
		const comment = selectOne('a.tml_comment', node);
		const commentCnt = comment ? numberContent(comment) : 0;
		const sourceName = subAfterLast(textContent(actions), ' · ');
		const sourceUrl = selectOne('small.grey > a', actions)?.attribs?.href;

		const tmp = selectOne('a.avatar', node);
		avatar = (tmp == null ? avatar : tmp);
		const avatarUrl = spanStyleAvatar(avatar);
		const username = subAfterLast(avatar?.attribs?.href || '', '/');
		const userId = isNaN(parseInt(username)) ? imageUrlId(avatarUrl) : parseInt(username);

		const user = selectAll('a.l', node)
			.filter(item => selectOne('img', item) == null && containsAny(item.attribs.href, '/user/'))
			.firstOrNull();

		const nickname = user ? textContent(user) : '';

		const reactions: Reaction[] = [];

		const catAndType = parseTimelineCatAndType(selectElement('span.info', node));
		const memo = parseTimelineMemo(node, catAndType);

		return {
			cat: catAndType.cat,
			type: catAndType.type,
			batch: imgs != null,
			id: id,
			createdAt: new Date(date).getTime(),
			replies: commentCnt,
			source: {
				name: sourceName,
				url: sourceUrl ? UPSTREAM.WEB_API + sourceUrl : ''
			},
			memo: memo,
			reactions: reactions,
			uid: userId,
			user: {
				nickname: nickname,
				username: username,
				id: userId,
				avatar: optImageUrl(avatarUrl)
			}
		} as Timeline;
	});

	return new Response(JSON.stringify(items), {
		headers: { 'content-type': 'application/json' }
	});
}


function parseTimelineCatAndType(element: Element | null): TimelineCatAndType {
	if (element == null) return { cat: TimelineCat.UNKNOWN, type: 0 };

	const item = element.cloneNode(true);
	const collectInfo = selectOne('.collectInfo', item);
	if (collectInfo != null) {
		removeElement(collectInfo);
	}

	const info = textContent(item);

	let cat: TimelineCat;
	let type = 0;

	const animeRealKeys = ['想看', '看过', '在看', '搁置', '抛弃'];
	const bookKeys = ['想读', '读过', '在读', '搁置', '抛弃'];
	const musicKeys = ['想听', '听过', '在听', '搁置', '抛弃'];
	const gameKeys = ['想玩', '玩过', '在玩', '搁置', '抛弃'];
	const progressKeys = ['完成'];

	const status = selectOne('p.status', element);
	if (status != null) {
		// 0 = 更新签名
		// 1 = 吐槽
		// 2 = 修改昵称
		cat = TimelineCat.STATUS;
		type = 1;
		if (containsAny(info, '更新了签名')) type = 0;
		if (containsAny(info, '改名为')) type = 2;
	} else if (containsAll(info, ['收藏了', '角色']) || containsAll(info, ['收藏了', '人物'])) {
		cat = TimelineCat.MONO;
		if (containsAll(info, ['收藏了', '角色'])) type = 1;
		if (containsAll(info, ['收藏了', '人物'])) type = 2;
	} else if (containsAny(info, ['发表了新日志'])) {
		cat = TimelineCat.BLOG;
	} else if (containsAny(info, ['收藏了目录', '创建了新目录'])) {
		cat = TimelineCat.INDEX;
	} else if (containsAny(info, ['添加了新'])) {
		// 1 = 添加了新书
		// 2 = 添加了新动画
		// 3 = 添加了新唱片
		// 4 = 添加了新游戏
		// 5 = 添加了新图书系列
		// 6 = 添加了新影视
		cat = TimelineCat.WIKI;
		if (containsAny(info, '添加了新书')) type = 1;
		if (containsAny(info, '添加了新动画')) type = 2;
		if (containsAny(info, '添加了新唱片')) type = 3;
		if (containsAny(info, '添加了新游戏')) type = 4;
		if (containsAny(info, '添加了新图书系列')) type = 5;
		if (containsAny(info, '添加了新影视')) type = 6;
	} else if (containsAny(info, ['加入了', '创建了', '加为了好友', '注册'])) {
		// 0 = 神秘的行动
		// 1 = 注册
		// 2 = 添加好友
		// 3 = 加入小组
		// 4 = 创建小组
		// 5 = 加入乐园
		cat = TimelineCat.DAILY;
		if (containsAny(info, '注册')) type = 1;
		if (containsAny(info, '加为了好友')) type = 2;
		if (containsAny(info, '加入了')) type = 3;
		if (containsAny(info, '创建了')) type = 4;
	} else if (containsAny(info, progressKeys)
		|| containsAny(info, animeRealKeys)
		|| containsAny(info, bookKeys)
		|| containsAny(info, musicKeys)
		|| containsAny(info, gameKeys)
	) {
		if (containsAny(info, progressKeys) || containsAnyRegex(info, [
			/ep\\.?\s*\d+/i,
			/第\s*\d+\s*话/i,
			/第\s*\d+\s*卷/i,
			/\d+\s*of\s*(.*?)\s*话/i,
			/\d+\s*of\s*(.*?)\s*卷/i
		])) {
			// 0 = batch(完成)
			// 1 = 想看
			// 2 = 看过
			// 3 = 抛弃
			cat = TimelineCat.PROGRESS;
			if (containsAnyRegex(info, [/\d+\s*of\s*(.*?)\s*话/i, /\d+\s*of\s*(.*?)\s*卷/i])) type = 0;
			if (containsAny(info, ['想看', '想读', '想玩', '想听'])) type = 1;
			if (containsAny(info, ['看过', '读过', '玩过', '听过'])) type = 2;
			if (containsAny(info, ['抛弃'])) type = 3;
		} else {
			// 1  = 想读
			// 2  = 想看
			// 3  = 想听
			// 4  = 想玩
			// 5  = 读过
			// 6  = 看过
			// 7  = 听过
			// 8  = 玩过
			// 9  = 在读
			// 10 = 在看
			// 11 = 在听
			// 12 = 在玩
			// 13 = 搁置了
			// 14 = 抛弃了
			cat = TimelineCat.SUBJECT;
			if (containsAny(info, '想读')) type = 1;
			if (containsAny(info, '想看')) type = 2;
			if (containsAny(info, '想听')) type = 3;
			if (containsAny(info, '想玩')) type = 4;
			if (containsAny(info, '读过')) type = 5;
			if (containsAny(info, '看过')) type = 6;
			if (containsAny(info, '听过')) type = 7;
			if (containsAny(info, '玩过')) type = 8;
			if (containsAny(info, '在读')) type = 9;
			if (containsAny(info, '在看')) type = 10;
			if (containsAny(info, '在听')) type = 11;
			if (containsAny(info, '在玩')) type = 12;
			if (containsAny(info, '搁置')) type = 13;
			if (containsAny(info, '抛弃')) type = 14;
		}
	} else {
		cat = TimelineCat.WINDOW;
	}

	return { cat: cat, type: type };
}


function parseTimelineMemo(element: Element | null, catAndType: TimelineCatAndType): Memo {
	if (element == null || catAndType.cat == TimelineCat.UNKNOWN) return {};
	const type = catAndType.type;
	const memo: Memo = {};

	switch (catAndType.cat) {
		// 1:注册, 2:加为了好友, 3:加入了小组, 4:创建了小组,
		case TimelineCat.DAILY:
			memo.daily = parseTimelineMemoDaily(element, type);
			break;
		case TimelineCat.WIKI:
			memo.wiki = parseTimelineMemoWiki(element, type);
			break;
		case TimelineCat.SUBJECT:
			memo.subject = parseTimelineMemoSubject(element, type);
			break;
		case TimelineCat.PROGRESS:
			memo.progress = parseTimelineMemoProgress(element, type);
			break;
		case TimelineCat.STATUS:
			memo.status = parseTimelineMemoStaus(element, type);
			break;
		case TimelineCat.BLOG:
			memo.blog = parseTimelineMemoBlog(element, type);
			break;
		case TimelineCat.INDEX:
			memo.index = parseTimelineMemoIndex(element, type);
			break;
		case TimelineCat.MONO: {
			memo.mono = parseTimelineMemoMono(element, type);
			break;
		}
	}
	return memo;
}


function parseTimelineMemoDaily(element: Element, type: number): Daily {
	const daily: Daily = {};

	// 图片映射解析
	const imgs = selectElements('div.imgs > a', element);
	const img = selectElements('a.l', element).filter((item) => selectOne('.rr', item) != null);
	if (img.length > 0) imgs.push(img[0]);
	const pics = new Map<string, Images>();
	imgs.forEach(item => {
		const id = subAfterLast(item.attribs.href, '/');
		const imageUrl = optImageUrl(selectElement('img', item)?.attribs?.src || '');
		pics.set(id, imageUrl);
	});

	const items =
		selectElements('a.l', element)
			.filter(item => selectElement('img', item) == null)
			.filter(item => containsAny(item.attribs.href, ['/group/', '/user/']));

	switch (type) {
		case 3:
		case 4: {
			daily.groups = items.map(item => {
				const name = subAfterLast(item.attribs.href, '/');
				const title = textContent(item);
				const group: Group = {
					id: imageUrlId(pics.get(name)?.large),
					name: name,
					title: title,
					icon: pics.get(name) || undefined,
					nsfw: false,
					accessible: true,
					createdAt: 0,
					creatorID: 0,
					members: 0
				};
				return group;
			});
			break;
		}
		case 2: {
			// 第一个为时间线的用户，剔除
			items.shift();

			daily.users = items.map(item => {
				const name = subAfterLast(item.attribs.href, '/');
				const title = textContent(item);
				let id = parseInt(name);
				if (isNaN(id)) {
					id = imageUrlId(pics.get(name)?.large);
				}

				const user: User = {
					username: name,
					nickname: title,
					id: id,
					avatar: pics.get(name)
				};
				return user;
			});
			break;
		}
	}
	return daily;
}

function parseTimelineMemoSubject(element: Element, type: number): SubjectItem[] {
	return [];
}

function parseTimelineMemoProgress(element: Element, type: number): Progress {
	return {};
}

function parseTimelineMemoStaus(element: Element, type: number): Status {
	const statusElement = selectElement('p.status', element);
	if (!statusElement) return {};

	const html = render(statusElement.children);

	if (type === 0) {
		// 更新签名
		const sign = subAfter(subAfter(html, '更新了签名: '), '更新了签名：');
		return { sign };
	} else if (type === 2) {
		// 改名
		const strongs = selectElements('strong', statusElement);
		return {
			nickname: {
				before: strongs[0] ? textContent(strongs[0]).trim() : '',
				after: strongs[1] ? textContent(strongs[1]).trim() : '',
			}
		};
	} else {
		// 吐槽
		return {
			tsukkomi: html
		};
	}
}

function parseTimelineMemoBlog(element: Element, type: number): Blog {
	return {
		createdAt: 0,
		icon: '',
		id: 0,
		public: false,
		replies: 0,
		summary: '',
		title: '',
		type: 0,
		uid: 0,
		updatedAt: 0,
	};
}

function parseTimelineMemoIndex(element: Element, type: number): Index {
	return {
		createdAt: 0,
		id: 0,
		private: false,
		title: '',
		total: 0,
		type: 0,
		uid: 0,
		updatedAt: 0,
	};
}

function parseTimelineMemoMono(element: Element, type: number): Mono {
	const mono: Mono = {};

	const imgs = selectElements('div.imgs > a', element);
	const img = selectElement('.rr', element)?.parentNode || null;
	if (img != null && isTag(img)) imgs.push(img);

	const pics = new Map<string, Images>();
	imgs.forEach(item => {
		const id = subAfterLast(item.attribs.href, '/');
		const imageUrl = optImageUrl(selectElement('img', item)?.attribs?.src || '');
		pics.set(id, imageUrl);
	});

	const items = selectElements('a.l', element)
		.filter(item => containsAny(item.attribs.href, ['/character/', '/person/']));

	// 角色
	if (type == 1) {
		mono.characters = items.map(item => {
			const id = subAfterLast(item.attribs.href, '/');
			const title = textContent(item);

			const character: Character = {
				id: parseInt(id),
				images: pics.get(id),
				name: title,
				nameCN: title,
				info: '收藏了角色'
			};
			return character;
		});
	}
	// 人物
	else {
		mono.persons = items.map(item => {
			const id = subAfterLast(item.attribs.href, '/');
			const title = textContent(item);
			const person: Person = {
				id: parseInt(id),
				images: pics.get(id),
				name: title,
				nameCN: title,
				nsfw: false,
				info: '收藏了人物',
				type: 0
			};
			return person;
		});
	}
	return mono;
}

function parseTimelineMemoWiki(element: Element, type: number): Wiki {
	const wiki: Wiki = {};
	const container = selectElement('.container', element);
	const links = selectElements('a.l', element)
		.filter((item) => containsAny(item.attribs.href, '/subject/'));
	const subjectId = links.length > 0 ? parseInt(subAfterLast(links[0].attribs.href, '/')) : 0;
	const subjectName = links.length > 0 ? textContent(links[0]) : '';
	const imageUrl = selectElement('img', container)?.attribs?.src || '';
	const info = selectElement('p.info', container);
	const description = info ? textContent(info).trim() : '';

	let subjectType = 0;
	switch (type) {
		case 1:
			subjectType = SubjectType.BOOK;
			break;
		case 2:
			subjectType = SubjectType.ANIME;
			break;
		case 3:
			subjectType = SubjectType.MUSIC;
			break;
		case 4:
			subjectType = SubjectType.GAME;
			break;
		case 5:
			subjectType = SubjectType.BOOK;
			break;
		case 6:
			subjectType = SubjectType.REAL;
			break;
	}

	wiki.subject = {
		id: subjectId,
		info: description,
		name: subjectName,
		nameCN: subjectName,
		images: optImageUrl(imageUrl),
		locked: false,
		nsfw: container == null,
		type: subjectType
	};

	return wiki;
}
