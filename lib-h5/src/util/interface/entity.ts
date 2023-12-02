export interface BlogDetailEntity {
    id: string;
    title: string;
    time: string;
    userAvatar: string;
    userName: string;
    userId: string;
    content: string;
    related: MediaRelative[];
    tags: MediaTag[];
    comments: CommentTreeEntity[];
}


export interface TopicDetailEntity {
    id: string;
    headerAvatar: string;
    headerName: string;
    headUrl: string;
    subHeaderName: string;
    subHeadUrl: string;

    title: string;
    time: string;
    userAvatar: string;
    userName: string;
    userId: string;
    userSign: string;
    deleteHash: string;

    content: string;
    comments: CommentTreeEntity[];
    replyForm: CommentFormEntity;

    likeMap: Map<string, Map<string, LikeAction>>;
    likeEmojis: LikeEmoji[];
}

interface LikeAction {
    emoji?: string | null;
    mainId: number;
    total: number;
    type: number;
    users?: {
        nickname?: string | null;
        username?: string | null;
    };
    value?: string | null;
}

export interface LikeEmoji {
    likeValue: string;
    emojiUrl: string;
}

export interface CommentFormEntity {
    action: string;
    inputs: {
        formhash: string;
        lastview: string;
        submit: string;
    }
}


export interface MediaRelative {
    cover: string;
    id: string;
    titleCn: string;
    titleNative: string;
    type: string;
    empty: boolean;
}

export interface MediaTag {
    tagName: string;
    title: string;
    url: string;
}

export interface CommentTreeEntity {
    id: string;
    floor: string;
    time: string;
    userAvatar: string;
    userName: string;
    userId: string;
    replyContent: string;
    replyJs: string;
    replyQuote: string;
    topicSubReply: CommentTreeEntity[];
}

export interface PostInfoEntity {
    filled: boolean;
    pageName: string;
    userName: string;
    time: string;
    related: MediaRelative[];
    title: string;
    content: string;
    tags: string;
    isPublic: boolean;
}