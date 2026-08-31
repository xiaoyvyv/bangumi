import { proxy } from '../core/proxy';
import {
	transformTerminalPersonality,
	transformTerminalSpeech,
	transformTerminalText,
} from '../transforms/terminal.transform';
import { webApiBase } from '../config';

async function extractParams(req: Request): Promise<Record<string, string>> {
	const url = new URL(req.url);
	const params: Record<string, string> = {};

	for (const [key, value] of url.searchParams.entries()) {
		params[key] = value;
	}

	if (req.method === 'POST') {
		try {
			const clone = req.clone();
			const contentType = req.headers.get('content-type') || '';
			if (contentType.includes('application/x-www-form-urlencoded')) {
				const bodyText = await clone.text();
				const formData = new URLSearchParams(bodyText);
				for (const [key, value] of formData.entries()) {
					if (!params[key]) params[key] = value;
				}
			} else if (contentType.includes('application/json')) {
				const json = (await clone.json()) as any;
				if (json && typeof json === 'object') {
					for (const [key, value] of Object.entries(json)) {
						if (!params[key] && value !== undefined && value !== null) {
							params[key] = String(value);
						}
					}
				}
			}
		} catch (e) {
			// ignore parse error
		}
	}

	return params;
}

export async function handleTerminal(req: Request, env: any): Promise<Response> {
	const url = new URL(req.url);
	const params = await extractParams(req);

	const pathSegments = url.pathname.split('/').filter(Boolean);
	const action =
		pathSegments.length >= 3 && pathSegments[0] === 'p1' && pathSegments[1] === 'terminal'
			? pathSegments[2]
			: '';

	const targetUrl = `${webApiBase(req)}/terminal`;
	let body = '';
	let transform: ((req: Request, res: Response) => Promise<Response> | Response) | undefined;

	switch (action) {
		case 'personality': {
			body = 'input=personality&cmd=personality&arg=personality';
			transform = transformTerminalPersonality;
			break;
		}

		case 'list': {
			const curPsn = params.cur_psn?.trim();
			if (!curPsn) {
				return Response.json({ message: 'Missing required parameter: cur_psn' }, { status: 400 });
			}
			const isAll = params.all === 'true';
			const flag = isAll ? '-all' : '-m';
			body = `input=list+${flag}&cmd=list&arg=${flag}&cur_psn=${curPsn}`;
			transform = transformTerminalSpeech;
			break;
		}

		case 'create': {
			const name = params.name?.trim();
			if (!name) {
				return Response.json({ message: 'Missing required parameter: name' }, { status: 400 });
			}
			body = `input=create+${name}&cmd=create&arg=${name}`;
			transform = transformTerminalText;
			break;
		}

		case 'speech': {
			const speech = params.speech?.trim();
			const curPsn = params.cur_psn?.trim();
			if (!speech || !curPsn) {
				return Response.json({ message: 'Missing required parameters: speech, cur_psn' }, { status: 400 });
			}
			body = `input=speech+${speech}&cmd=speech&arg=${speech}&cur_psn=${curPsn}`;
			transform = transformTerminalText;
			break;
		}

		default:
			return Response.json({ message: 'Not Found' }, { status: 404 });
	}

	const upstreamReq = new Request(req.url, {
		method: 'POST',
		headers: req.headers,
		body: body,
	});

	return proxy(upstreamReq, targetUrl, { transform: transform });

}
