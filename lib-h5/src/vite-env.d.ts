/// <reference types="vite/client" />
declare module '@bbob/preset-html5';
declare module '@bbob/html';

declare interface Window {
    blog: any;
    blogPost: any;
    topic: any;
    sign: any;
    mounted: boolean;
    android: Android;
}

declare interface Android {
    onPreviewImage: any;
    onReplyUser: any;
    onReplyNew: any;
    onClickUser: any;
    onNeedLogin: any;
    onClickRelated: any;
}