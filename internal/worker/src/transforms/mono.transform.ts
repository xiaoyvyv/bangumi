import { selectOne } from 'css-select';
import { parseDocument } from 'htmlparser2';
import {
	bgmImageVariants,
	elementHref,
	elementSrc,
	elementText,
	firstTextNode,
	hrefId,
	hrefLongId,
	nextElementSibling,
	selectElements,
	spanStyleAvatar,
} from '../config/util';
import { ComposeMonoDisplay, ComposeSection, MonoType } from '../types';

export async function transformMonoHomepage(_req: Request, res: Response): Promise<Response> {
	const html = await res.text();
	const items = parseMonoHomepage(html);

	return Response.json(items);
}

export function parseMonoHomepage(html: string): ComposeSection<ComposeMonoDisplay>[] {
	const doc = parseDocument(html);
	const sections: ComposeSection<ComposeMonoDisplay>[] = [];
	// The page begins with an empty #main.mainWrapper before the actual columns.
	// Select the uniquely identified columns from the document rather than the first wrapper.
	const columnSubjectBrowserA = selectOne('#columnSubjectBrowserA', doc);
	for (const section of selectElements('.section', columnSubjectBrowserA)) {
		const moreLink = selectOne(':scope > a', section);
		const sectionId = elementHref(moreLink);
		sections.push(header(sectionId, elementText(selectOne('h2', section)), elementText(moreLink).replace('»', '').trim()));

		for (const item of selectElements('ul > li', section)) {
			const link = selectOne('a[title]', item);
			if (!link) continue;
			const linkHref = elementHref(link);
			sections.push(monoSection(
				`${sectionId}-${hrefId(linkHref)}`,
				linkHref.includes('character') ? MonoType.CHARACTER : MonoType.PERSON,
				hrefLongId(linkHref),
				link.attribs.title ?? '',
				elementText(selectOne('p > small', item)),
				bgmImageVariants(elementSrc(selectOne('img', item))),
			));
		}
	}

	const columnSubjectBrowserB = selectOne('#columnSubjectBrowserB', doc);
	for (const title of selectElements('.sideInner > .subtitle', columnSubjectBrowserB)) {
		const side = nextElementSibling(title);
		if (!side) continue;
		const sectionId = elementHref(selectOne('a', title));
		sections.push(header(`sideInner-${sectionId}`, firstTextNode(title), elementText(selectOne('small', title)), sectionId));

		for (const item of selectElements('dl', side)) {
			const link = selectOne('a[title]', item);
			if (!link) continue;
			const linkHref = elementHref(link);
			const name = link.attribs.title ?? '';
			sections.push(monoSection(
				`${sectionId}-${hrefId(linkHref)}`,
				linkHref.includes('character') ? MonoType.CHARACTER : MonoType.PERSON,
				hrefLongId(linkHref),
				name,
				name,
				bgmImageVariants(spanStyleAvatar(selectOne('.avatar', item))),
				true,
				0,
			));
		}
	}

	return sections;
}

function header(key: string, title: string, more: string, id = key): ComposeSection<ComposeMonoDisplay> {
	return { key, header: { id, title, subtitle: '', more } };
}

function monoSection(
	key: string,
	type: MonoType,
	id: number,
	name: string,
	nameCN: string,
	images: ComposeMonoDisplay['info']['mono']['images'],
	includeMonoType = false,
	displayType: MonoType = type,
): ComposeSection<ComposeMonoDisplay> {
	const mono = { id, images, name, nameCN, ...(includeMonoType ? { type } : {}) };
	return { key, item: { type: displayType, info: { mono } } };
}
