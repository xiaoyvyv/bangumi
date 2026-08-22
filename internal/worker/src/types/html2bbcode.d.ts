declare module 'html2bbcode' {
	export interface HTML2BBCodeOptions {
		no_wrap?: boolean;
		imagescale?: boolean;
		noalign?: boolean;
		nolist?: boolean;
		noheadings?: boolean;
		debug?: boolean;
	}

	export class HTML2BBCode {
		constructor(options?: HTML2BBCodeOptions);
		feed(html: string): { toString(): string };
	}
}
