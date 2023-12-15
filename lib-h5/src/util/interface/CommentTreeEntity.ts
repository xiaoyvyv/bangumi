import {LikeActionEntity} from "./LikeActionEntity.ts";

export interface CommentTreeEntity {
    id: string;
    mineId: string;
    floor: string;
    time: string;
    userAvatar: string;
    userName: string;
    userId: string;
    replyContent: string;
    replyJs: string;
    replyQuote: string;
    topicSubReply: CommentTreeEntity[];
    emojis: Array<LikeActionEntity>;
    gh: string;
    emojiParam: any;
}
