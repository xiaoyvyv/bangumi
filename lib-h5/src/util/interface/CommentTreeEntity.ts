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
