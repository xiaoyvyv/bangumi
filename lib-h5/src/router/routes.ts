const blog = () => import('../pages/blog/BlogView.vue');
const blogPost = () => import('../pages/post/BlogPostView.vue');
const index = () => import('../pages/IndexView.vue');
const topic = () => import('../pages/topic/TopicView.vue');
export const RoutePaths = {
    blog: 'blog',
    topic: 'topic',
    blogPost: 'blog-post',
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
        name: RoutePaths.blogPost,
        path: '/blog-post',
        component: blogPost
    }
];
