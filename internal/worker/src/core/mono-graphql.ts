import { UPSTREAM } from '../config';
import { ComposeMonoDisplay, MonoType } from '../types';

export interface MonoReference {
	id: number;
	type: MonoType;
}

interface GraphqlMono {
	career?: string[];
	collects?: number;
	comment?: number;
	id: number;
	images?: ComposeMonoDisplay['info']['mono']['images'];
	infobox?: GraphqlInfobox[];
	lock?: number;
	name?: string;
	nsfw?: boolean;
	redirect?: number;
	role?: number;
	summary?: string;
	type?: number;
}

interface GraphqlInfobox {
	key: string;
	values: GraphqlInfoboxValue[];
}

interface GraphqlInfoboxValue {
	k?: string | null;
	v?: string | null;
}

interface GraphqlResponse {
	data?: Record<string, GraphqlMono | null>;
	errors?: { message: string }[];
}

export async function fetchMonoDisplays(references: Iterable<MonoReference>): Promise<Map<string, ComposeMonoDisplay>> {
	const grouped = new Map<MonoType, number[]>();
	for (const { id, type } of references) {
		if (!id || (type !== MonoType.CHARACTER && type !== MonoType.PERSON)) continue;
		const ids = grouped.get(type) ?? [];
		if (!ids.includes(id)) ids.push(id);
		grouped.set(type, ids);
	}

	const entries = await Promise.all([...grouped].map(async ([type, ids]) => fetchMonoGroup(type, ids)));
	return new Map(entries.flat());
}

async function fetchMonoGroup(type: MonoType, ids: number[]): Promise<[string, ComposeMonoDisplay][]> {
	const response = await fetch(UPSTREAM.GRAPHQL, {
		method: 'POST',
		headers: { 'content-type': 'application/json', accept: 'application/json' },
		body: JSON.stringify({ query: monoQuery(type, ids) }),
	});
	if (!response.ok) throw new Error(`Mono GraphQL request failed: ${response.status}`);

	const result = await response.json() as GraphqlResponse;
	if (result.errors?.length) throw new Error(`Mono GraphQL error: ${result.errors[0].message}`);

	return ids.flatMap((id) => {
		const mono = result.data?.[alias(id)];
		return mono ? [[displayKey(type, id), toDisplay(type, mono)] as [string, ComposeMonoDisplay]] : [];
	});
}

function monoQuery(type: MonoType, ids: number[]): string {
	const field = type === MonoType.CHARACTER ? 'character' : 'person';
	const specificFields = type === MonoType.CHARACTER ? 'role' : 'type career';
	return `query MonoList { ${ids.map((id) => `${alias(id)}: ${field}(id: ${id}) {
		id name comment collects images { grid large medium small } infobox { key values { k v } }
		lock nsfw redirect summary ${specificFields}
	}`).join('\n')} }`;
}

function toDisplay(displayType: MonoType, mono: GraphqlMono): ComposeMonoDisplay {
	const infobox = mono.infobox ?? [];
	return {
		type: displayType,
		info: { mono: {
			id: mono.id, name: mono.name ?? '', nameCN: chineseName(infobox), images: mono.images ?? {},
			comment: mono.comment ?? 0, collects: mono.collects ?? 0,
			infobox: infobox.map(({ key, values }) => ({ key, value: values })),
			lock: Boolean(mono.lock), nsfw: Boolean(mono.nsfw), redirect: mono.redirect ?? 0,
			role: mono.role ?? 0, summary: mono.summary ?? '', type: mono.type ?? 0, career: mono.career ?? [],
		} },
	};
}

function chineseName(infobox: GraphqlInfobox[]): string {
	return infobox.find(({ key }) => key === '简体中文名')?.values.find(({ v }) => Boolean(v))?.v ?? '';
}

function alias(id: number): string {
	return `item_${id}`;
}

export function displayKey(type: MonoType, id: number): string {
	return `${type}:${id}`;
}
