import {SampleRelateEntity} from "./SampleRelateEntity.ts";

export interface PostInfoEntity {
    filled: boolean;
    pageName: string;
    userName: string;
    time: string;
    title: string;
    content: string;
    tags: string;
    isPublic: boolean;
    related: SampleRelateEntity;
}