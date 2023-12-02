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