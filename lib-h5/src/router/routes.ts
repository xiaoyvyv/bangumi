const blog = () => import('../pages/blog/BlogView.vue');
const topic = () => import('../pages/topic/TopicView.vue');
const bbCode = () => import('../pages/bbcode/BBCodeView.vue');
const index = () => import('../pages/IndexView.vue');
export const RoutePaths = {
    blog: 'blog',
    topic: 'topic',
    bbcode: 'bb-code',
    index: 'index',
};

export default [
    {
        path: '/',
        component: index
    },
    {
        name: RoutePaths.blog,
        path: '/blog',
        component: blog
    },
    {
        name: RoutePaths.topic,
        path: '/topic',
        component: topic
    },
    {
        name: RoutePaths.bbcode,
        path: '/bb-code',
        component: bbCode
    }
];
