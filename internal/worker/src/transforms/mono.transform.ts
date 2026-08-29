import { selectOne } from 'css-select';
import { parseDocument } from 'htmlparser2';
import {
	elementHref,
	elementText,
	firstTextNode,
	hrefLongId,
	nextElementSibling,
	selectElements,
} from '../config/util';
import { displayKey, fetchMonoDisplays, MonoReference } from '../core/mono-graphql';
import { ComposeMonoDisplay, ComposeSection, MonoType } from '../types';

const emptyMonoDisplay: ComposeMonoDisplay = {
	type: MonoType.UNKNOWN,
	info: { mono: { id: 0, images: {}, name: '', nameCN: '' } },
};

interface MonoHomepageEntry {
	key: string;
	header?: ComposeSection<ComposeMonoDisplay>['header'];
	reference?: MonoReference;
}

export async function transformMonoHomepage(_req: Request, res: Response): Promise<Response> {
	try {
		const entries = parseMonoHomepage(await res.text());
		const displays = await fetchMonoDisplays(entries.flatMap((entry) => entry.reference ?? []));
		return Response.json(entries.map((entry) => toSection(entry, displays)));
	} catch (error) {
		return graphQlFailure(error);
	}
}

export function transformMonoBrowser(type: MonoType) {
	return async (_req: Request, res: Response): Promise<Response> => {
		try {
			const references = parseMonoBrowser(await res.text(), type);
			const displays = await fetchMonoDisplays(references);
			return Response.json(references.flatMap((reference) => {
				const display = displays.get(displayKey(reference.type, reference.id));
				return display ? [display] : [];
			}));
		} catch (error) {
			return graphQlFailure(error);
		}
	};
}

/** HTML is only used to retain homepage structure and locate the Mono IDs. */
export function parseMonoHomepage(html: string): MonoHomepageEntry[] {
	const doc = parseDocument(html);
	const entries: MonoHomepageEntry[] = [];
	const columnSubjectBrowserA = selectOne('#columnSubjectBrowserA', doc);
	for (const section of selectElements('.section', columnSubjectBrowserA)) {
		const moreLink = selectOne(':scope > a', section);
		const sectionId = elementHref(moreLink);
		entries.push({
			key: sectionId,
			header: { id: sectionId, title: elementText(selectOne('h2', section)), subtitle: '', more: elementText(moreLink).replace('»', '').trim() },
		});
		for (const item of selectElements('ul > li', section)) {
			const reference = monoReference(elementHref(selectOne('a[title]', item)));
			if (reference) entries.push({ key: `${sectionId}-${reference.id}`, reference });
		}
	}

	const columnSubjectBrowserB = selectOne('#columnSubjectBrowserB', doc);
	for (const title of selectElements('.sideInner > .subtitle', columnSubjectBrowserB)) {
		const side = nextElementSibling(title);
		if (!side) continue;
		const sectionId = elementHref(selectOne('a', title));
		entries.push({
			key: `sideInner-${sectionId}`,
			header: { id: sectionId, title: firstTextNode(title), subtitle: '', more: elementText(selectOne('small', title)) },
		});
		for (const item of selectElements('dl', side)) {
			const reference = monoReference(elementHref(selectOne('a[title]', item)));
			if (reference) entries.push({ key: `${sectionId}-${reference.id}`, reference });
		}
	}
	return entries;
}

function parseMonoBrowser(html: string, type: MonoType): MonoReference[] {
	const doc = parseDocument(html);
	const column = selectOne('#columnCrtBrowserB', doc);
	return selectElements('.browserCrtList > div a.avatar', column)
		.map((avatar) => hrefLongId(elementHref(avatar)))
		.filter(Boolean)
		.map((id) => ({ id, type }));
}

function monoReference(href: string): MonoReference | null {
	const id = hrefLongId(href);
	if (!id) return null;
	return { id, type: href.includes('/character/') ? MonoType.CHARACTER : MonoType.PERSON };
}

function toSection(entry: MonoHomepageEntry, displays: Map<string, ComposeMonoDisplay>): ComposeSection<ComposeMonoDisplay> {
	return {
		key: entry.key,
		...(entry.header ? { header: entry.header, item: emptyMonoDisplay } : { item: displays.get(displayKey(entry.reference!.type, entry.reference!.id)) ?? emptyMonoDisplay }),
	};
}

function graphQlFailure(error: unknown): Response {
	const message = error instanceof Error ? error.message : 'Mono GraphQL request failed';
	return Response.json({ message }, { status: 502 });
}
