import {CommentTreeEntity} from "./CommentTreeEntity.ts";
import {CommentFormEntity} from "./CommentFormEntity.ts";
import {SampleRelateEntity} from "./SampleRelateEntity.ts";
import {LikeActionEntity} from "./LikeActionEntity.ts";
import {EmojiParam} from "./EmojiParam.ts";

export interface TopicDetailEntity {
    id: string;
    title: string;
    time: string;
    userAvatar: string;
    userName: string;
    userId: string;
    userSign: string;
    content: string;
    anchorCommentId: string;
    related: SampleRelateEntity;
    comments: CommentTreeEntity[];
    replyForm: CommentFormEntity;
    gh: string;
    emojiParam: EmojiParam;
    emojis: Array<LikeActionEntity>;
}