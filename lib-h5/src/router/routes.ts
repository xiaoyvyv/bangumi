const blog = () => import('../pages/blog/BlogView.vue');
const blogPost = () => import('../pages/post/BlogPostView.vue');
const index = () => import('../pages/IndexView.vue');

export const RoutePaths = {
    blog: 'blog',
    blogPost: 'blog-post',
    index: 'index'
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
        name: RoutePaths.blogPost,
        path: '/blog-post',
        component: blogPost
    }
];
