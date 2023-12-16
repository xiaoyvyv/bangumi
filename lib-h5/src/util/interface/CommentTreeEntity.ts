import {LikeActionEntity} from "./LikeActionEntity.ts";
import {EmojiParam} from "./EmojiParam.ts";

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
    gh: string;
    emojiParam: EmojiParam;
    emojis: Array<LikeActionEntity>;
}
