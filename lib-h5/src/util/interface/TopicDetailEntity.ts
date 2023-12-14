import {CommentTreeEntity} from "./CommentTreeEntity.ts";
import {CommentFormEntity} from "./CommentFormEntity.ts";
import {SampleRelateEntity} from "./SampleRelateEntity.ts";
import {LikeActionEntity} from "./LikeActionEntity.ts";

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
    topicEmojis: LikeActionEntity[];
}