import {CommentTreeEntity} from "./CommentTreeEntity.ts";
import {LikeActionEntity, LikeEmoji} from "./LikeActionEntity.ts";
import {CommentFormEntity} from "./CommentFormEntity.ts";
import {SampleRelateEntity} from "./SampleRelateEntity.ts";

export interface TopicDetailEntity {
    id: string;
    title: string;
    time: string;
    userAvatar: string;
    userName: string;
    userId: string;
    userSign: string;
    deleteHash: string;
    content: string;
    related: SampleRelateEntity;
    comments: CommentTreeEntity[];
    replyForm: CommentFormEntity;

    likeMap: Map<string, Map<string, LikeActionEntity>>;
    likeEmojis: LikeEmoji[];
}