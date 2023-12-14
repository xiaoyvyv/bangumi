export interface CommentReplyEntity {
    posts: Posts;
    timestamp: number;
    status: string;
}

export interface Post {
    pst_id: string;
    pst_mid: string;
    pst_uid: string;
    pst_content: string;
    username: string;
    nickname: string;
    sign: string;
    avatar: string;
    dateline: string;
    model: string;
    is_self: boolean;
}

export interface CommentReplyMainContent {
    [postId: string]: Post;
}

export interface CommentReplySubContent {
    [postId: string]: Post[];
}

interface Posts {
    main: CommentReplyMainContent;
    sub: CommentReplySubContent;
}