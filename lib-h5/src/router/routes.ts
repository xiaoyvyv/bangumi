const blog = () => import('../pages/blog/BlogView.vue');
const blogPost = () => import('../pages/post/BlogPostView.vue');
const index = () => import('../pages/IndexView.vue');
const topic = () => import('../pages/topic/TopicView.vue');
const sign = () => import('../pages/sign/SignView.vue');

export const RoutePaths = {
    blog: 'blog',
    blogPost: 'blog-post',
    index: 'index',
    topic: 'topic',
    sign: 'sign'
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
        name: RoutePaths.blogPost,
        path: '/blog-post',
        component: blogPost
    },
    {
        name: RoutePaths.sign,
        path: '/sign',
        component: sign
    }
];
