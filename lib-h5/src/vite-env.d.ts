/// <reference types="vite/client" />

declare interface Window {
    blog: any;
    mounted: boolean;
    android: Android;
}

declare interface Android {
    onPreviewImage: any;
}