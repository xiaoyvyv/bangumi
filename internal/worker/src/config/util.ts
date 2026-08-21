import { type AnyNode, Element, isTag } from 'domhandler';
import { selectAll } from 'css-select';
import { textContent } from 'domutils';
import { Images } from '../types';
import { HTML2BBCode } from 'html2bbcode';

const bbcodeConverter = new HTML2BBCode({
	no_wrap: true
});

/**
 * HTML 转 BBCode，基于 html2bbcode 标准转换并保持现有特定规则
 */
export function html2bbcode(html: string): string {
	if (!html) return '';

	const processed = html
		.replace(/src=(['"])\/\//g, 'src=$1https://')
		.replace(/href=(['"])\/\//g, 'href=$1https://')
		.replace(/\[([a-zA-Z0-9_-]+)(=[^\]]*)?\]\s*<a[^>]*>([\s\S]*?)<\/a>\s*\[\/\1\]/gi, (match, tag, val, text) => {
			return `[${tag}${val || ''}]${text}[/${tag}]`;
		})
		.replace(/<img\s+[^>]*smileid=['"]?[^'"\s>]+['"]?[^>]*>/gi, (imgTag) => {
			const altMatch = imgTag.match(/alt=['"]?([^'"]+)['"]?/i);
			return altMatch ? altMatch[1] : imgTag;
		});

	return bbcodeConverter.feed(processed).toString().trim();
}

export function selectElements(q: string, root: any): Element[] {
	return selectAll(q, root).filter(isTag);
}

export function selectElement(q: string, root: any | null): Element | null {
	if (root == null) return null;
	const elements = selectAll(q, root).filter(isTag);
	return elements.length > 0 ? elements[0] : null;
}

/**
 * 获取字符串中最后出现的一段数字
 */
export function numberContent(node: AnyNode | AnyNode[]) {
	const match = (textContent(node) || '').match(/\d+(?!.*\d)/);
	return match ? Number(match[0]) : 0;
}

/**
 * 提取头像地址
 */
export function spanStyleAvatar(element: Element | null): string {
	if (element == null) return '';
	const span = element.children.find(
		(node): node is Element =>
			node.type === 'tag' && node.name === 'span'
	);

	if (!span) return '';
	const style = span.attribs?.style ?? '';
	const match = style.match(/url\((['"]?)(.*?)\1\)/i);
	if (!match) return '';
	let url = match[2].trim();
	if (url.startsWith('//')) {
		url = 'https:' + url;
	}
	return url;
}

/**
 * //lain.bgm.tv/pic/user/l/000/02/18/21804_2Oro7.jpg?r=1757342413&amp;hd=1
 * //lain.bgm.tv/pic/user/l/000/02/18/21804.jpg?r=1757342413&amp;hd=1
 */
export function imageUrlId(url: string | null | undefined): number {
	if (!url) return 0;
	const match = url.match(/\/(\d+)(?:_[^\/.?]+)?\.(jpg|jpeg|png|webp|gif)/i);
	return match ? Number(match[1]) : 0;
}

export function subAfter(text: string, delimiter: string) {
	const index = text.indexOf(delimiter);
	return index === -1 ? text : text.substring(index + delimiter.length);
}

/**
 * 截取最后一次出现 delimiter 之后的内容
 * 类似 Kotlin substringAfterLast()
 *
 * @param text 原文本
 * @param delimiter 分隔符
 * @param missingDelimiterValue 未找到时返回值（默认原文本）
 */
export function subAfterLast(
	text: string,
	delimiter: string,
	missingDelimiterValue: string = text
): string {
	if (!delimiter) return missingDelimiterValue;

	const index = text.lastIndexOf(delimiter);

	if (index < 0) return missingDelimiterValue;

	return text.slice(index + delimiter.length);
}

/**
 * 判断文本是否包含关键词中的任意一个
 *
 * @param text 原文本
 * @param keywords 单个关键词 或 关键词数组
 * @param ignoreCase 是否忽略大小写（默认 true）
 */
export function containsAny(
	text: string,
	keywords: string[] | string,
	ignoreCase: boolean = true
): boolean {
	if (!text || !keywords) return false;

	const list = Array.isArray(keywords) ? keywords : [keywords];
	if (list.length === 0) return false;

	const source = ignoreCase ? text.toLowerCase() : text;

	return list.some(keyword => {
		if (!keyword) return false;

		const target = ignoreCase ? keyword.toLowerCase() : keyword;
		return source.includes(target);
	});
}

/**
 * 判断文本是否匹配任意一个正则
 *
 * @param text 原文本
 * @param patterns 正则数组
 */
export function containsAnyRegex(
	text: string,
	patterns: RegExp[]
): boolean {
	if (!text || patterns.length === 0) return false;
	return patterns.some(regex => regex.test(text));
}

/**
 * 判断文本是否包含全部关键词
 *
 * @param text 原文本
 * @param keywords 单个关键词 或 关键词数组
 * @param ignoreCase 是否忽略大小写（默认 true）
 */
export function containsAll(
	text: string,
	keywords: string[] | string,
	ignoreCase: boolean = true
): boolean {
	if (!text || !keywords) return false;

	const list = Array.isArray(keywords) ? keywords : [keywords];
	if (list.length === 0) return false;

	const source = ignoreCase ? text.toLowerCase() : text;

	return list.every(keyword => {
		if (!keyword) return false;

		const target = ignoreCase ? keyword.toLowerCase() : keyword;
		return source.includes(target);
	});
}

/**
 * https://lain.bgm.tv/pic/cover/m/f3/05/641187_8Jjz9.jpg
 * https://lain.bgm.tv/r/200/pic/cover/l/f3/05/641187_8Jjz9.jpg
 * @param url
 */
export function optImageUrl(url: string | null): Images {
	if (url == null) return {};
	if (url.startsWith('//')) url = 'https:' + url;

	return {
		grid: avatarResize(url, 'g'),
		large: avatarResize(url, 'l'),
		medium: avatarResize(url, 'm'),
		small: avatarResize(url, 's')
	};
}

/**
 * 头像路径尺寸转换
 *
 * /r/200/ -> /
 * /s/     -> /size/
 * /m/     -> /size/
 * /l/     -> /size/
 * /g/     -> /size/
 */
export function avatarResize(url: string, size: string): string {
	if (!url) return '';
	return url
		.replace(/\/r\/\d+\//, '/')
		.replace(/\/([smlg])\//, `/${size}/`);
}
