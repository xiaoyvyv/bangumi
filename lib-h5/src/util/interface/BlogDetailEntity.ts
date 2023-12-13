import {CommentTreeEntity} from "./CommentTreeEntity.ts";
import {SampleRelateEntity} from "./SampleRelateEntity.ts";

export interface BlogDetailEntity {
    id: string;
    title: string;
    time: string;
    userAvatar: string;
    userName: string;
    userId: string;
    content: string;
    related: SampleRelateEntity;
    tags: MediaTag[];
    comments: CommentTreeEntity[];
}

export interface MediaTag {
    tagName: string;
    title: string;
    url: string;
}
