const blog = () => import('../pages/blog/BlogView.vue');
export const RoutePaths = {
    blog: 'blog',
};

export default [
    {
        path: '/',
        component: blog
    },
    {
        name: RoutePaths.blog,
        path: '/blog',
        component: blog
    }
];
