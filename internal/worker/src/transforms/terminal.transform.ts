import { TerminalPersonality, TerminalSpeech } from '../types';

export function parseTerminalPersonality(html: string): TerminalPersonality[] {
	const lines = html.split(/<br\s*\/?>|\n/gi);
	const results: TerminalPersonality[] = [];

	for (const rawLine of lines) {
		const line = rawLine.replace(/&nbsp;/gi, ' ').trim();
		if (!line) continue;

		const parts = line.split('|').map((p) => p.trim());
		if (parts.length < 3) continue;

		const idNameMatch = parts[0].match(/^(\d+)\s+(.+)$/);
		const speechMatch = parts[1].match(/Speech count:\s*(\d+)/i);
		const creatorMatch = parts[2].match(/by\s+(.+?)\s*@\s*(\d+)/i);

		if (idNameMatch && speechMatch && creatorMatch) {
			results.push({
				id: parseInt(idNameMatch[1], 10),
				name: idNameMatch[2].trim(),
				speechCount: parseInt(speechMatch[1], 10),
				creator: creatorMatch[1].trim(),
				createdAt: parseInt(creatorMatch[2], 10),
			});
		}
	}

	return results;
}

export async function transformTerminalPersonality(req: Request, res: Response): Promise<Response> {
	const html = await res.text();
	let items = parseTerminalPersonality(html);

	const url = new URL(req.url);
	const creator = (url.searchParams.get('creator') || url.searchParams.get('user'))?.trim();
	if (creator) {
		const target = creator.toLowerCase();
		items = items.filter((item) => item.creator.toLowerCase() === target);
	}

	return new Response(JSON.stringify(items), {
		headers: { 'content-type': 'application/json' },
	});
}

export function parseTerminalSpeech(html: string): TerminalSpeech[] {
	const lines = html.split(/<br\s*\/?>|\n/gi);
	const results: TerminalSpeech[] = [];

	for (const rawLine of lines) {
		const line = rawLine.replace(/&nbsp;/gi, ' ').trim();
		if (!line || line.includes('====')) continue;

		const idMatch = line.match(/^(\d+)\s+(.+)$/);
		if (!idMatch) continue;

		const id = parseInt(idMatch[1], 10);
		const rest = idMatch[2].trim();

		const restMatch = rest.match(/^(.+?)(?:\s+by\s+(.+?)\s*@\s*(\d+))?$/);
		if (restMatch) {
			const item: TerminalSpeech = {
				id: id,
				speech: restMatch[1].trim(),
			};
			if (restMatch[2]) {
				item.creator = restMatch[2].trim();
			}
			if (restMatch[3]) {
				item.createdAt = parseInt(restMatch[3], 10);
			}
			results.push(item);
		}
	}

	return results;
}

export async function transformTerminalSpeech(req: Request, res: Response): Promise<Response> {
	const html = await res.text();
	const items = parseTerminalSpeech(html);
	return new Response(JSON.stringify(items), {
		headers: { 'content-type': 'application/json' },
	});
}

export async function transformTerminalText(req: Request, res: Response): Promise<Response> {
	const html = await res.text();
	const text = html.replace(/<br\s*\/?>/gi, '\n').replace(/&nbsp;/gi, ' ').trim();
	return Response.json({ message: text });
}
