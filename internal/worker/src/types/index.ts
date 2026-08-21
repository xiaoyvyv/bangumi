export interface Timeline {
	batch: boolean;
	cat: number;
	createdAt: number;
	id: number;
	memo: Memo;
	reactions: Reaction[];
	replies: number;
	source: Source;
	type: number;
	uid: number;
	user: User;
}

export interface TimelineCatAndType {
	cat: number;
	type: number;
}


export interface Memo {
	blog?: Blog;
	daily?: Daily;
	index?: Index;
	mono?: Mono;
	progress?: Progress;
	status?: Status;
	subject?: SubjectItem[];
	wiki?: Wiki;
}

export interface Blog {
	createdAt: number;
	icon: string;
	id: number;
	public: boolean;
	replies: number;
	summary: string;
	title: string;
	type: number;
	uid: number;
	updatedAt: number;
	user?: User;
}

export interface Daily {
	groups?: Group[];
	users?: User[];
}

export interface Group {
	accessible: boolean;
	createdAt: number;
	creatorID: number;
	icon?: Images;
	id: number;
	members: number;
	name: string;
	nsfw: boolean;
	title: string;
}

export interface Index {
	createdAt: number;
	id: number;
	private: boolean;
	stats?: Stats;
	title: string;
	total: number;
	type: number;
	uid: number;
	updatedAt: number;
	user?: User;
}

export interface Stats {
	blog: number;
	character: number;
	episode: number;
	groupTopic: number;
	person: number;
	subject: SubjectStats;
	subjectTopic: number;
}

export interface SubjectStats {
	anime: number;
	book: number;
	game: number;
	music: number;
	real: number;
}

export interface Mono {
	characters?: Character[];
	persons?: Person[];
}

export interface Character {
	comment?: number;
	id: number;
	images?: Images;
	info?: string;
	lock?: boolean;
	name: string;
	nameCN: string;
	nsfw?: boolean;
	role?: number;
}

export interface Person {
	career?: string;
	comment?: number;
	id: number;
	images?: Images;
	info: string;
	lock?: boolean;
	name: string;
	nameCN: string;
	nsfw?: boolean;
	type: number;
}

export interface Progress {
	batch?: BatchProgress;
	single?: SingleProgress;
}

export interface BatchProgress {
	epsTotal: string;
	epsUpdate: number;
	volsTotal: string;
	volsUpdate: number;
	subject: Subject;
}

export interface SingleProgress {
	episode: Episode;
	subject: Subject;
}

export interface Episode {
	airdate: string;
	collection: Collection;
	comment: number;
	desc: string;
	disc: number;
	duration: string;
	id: number;
	name: string;
	nameCN: string;
	sort: number;
	subject: Subject;
	subjectID: number;
	type: number;
}

export interface Collection {
	status: number;
	updatedAt: number;
}

export interface Status {
	nickname?: {
		after: string
		before: string
	};
	sign?: string;
	tsukkomi?: string;
}

export interface SubjectItem {
	collectID: number;
	comment: string;
	rate: number;
	subject: Subject;
}

export interface Wiki {
	subject?: Subject;
}

export interface Subject {
	id: number;
	info: string;
	images: Images;
	locked: boolean;
	name: string;
	nameCN: string;
	nsfw: boolean;
	type: number;
	rating?: SubjectRating;
}

export interface SubjectRating {
	count?: number[];
	rank?: number;
	score?: number;
	total?: number;
}

export interface Reaction {
	emoji?: string;
	main_id?: number;
	selected?: boolean;
	total?: number;
	type?: number;
	users: User[];
	value: string;
}

export interface Source {
	name: string;
	url: string;
}

export interface User {
	avatar?: Images;
	group?: number;
	id: number;
	joinedAt?: number;
	nickname: string;
	sign?: string;
	username: string;
}

export interface Images {
	grid?: string;
	large?: string;
	medium?: string;
	small?: string;
}

export enum SubjectType {
	BOOK = 1,
	ANIME = 2,
	MUSIC = 3,
	GAME = 4,
	REAL = 6,
}
