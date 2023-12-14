import {CommentTreeEntity} from "./interface/CommentTreeEntity.ts";
import {
    CommentReplyEntity,
    CommentReplyMainContent,
    CommentReplySubContent,
    Post
} from "./interface/CommentReplyEntity.ts";

/**
 * 针对特定的节点进行交互优化
 *
 * @param content
 */
function optContentJs(content: HTMLElement | undefined | null) {
    if (!content) return;

    // 图片处理
    const images = content.querySelectorAll("img");
    const imageUrls: String[] = [];
    for (let i = 0; i < images.length; i++) {
        const imageEl = images[i];
        const imageSrc = imageEl.src;
        if (imageSrc != undefined && imageSrc.length > 0 && imageSrc.indexOf("/img/smiles") == -1) {
            imageUrls.push(imageSrc);

            imageEl.onclick = () => {
                if (window.android) {
                    window.android.onPreviewImage(imageSrc, imageUrls);
                }
            }
        }
    }

    // 遮罩交互处理
    content.querySelectorAll("span.text_mask").forEach(item => {
        const mask = item as HTMLElement;
        mask.onclick = (event) => {
            event.preventDefault();
            // 开关遮罩
            toggleMask(event.currentTarget as HTMLElement);
        }
    });
}

/**
 * 优化评论条目点击事件，是否拦截
 *
 * @param element
 */
const optReplyItemClick = (element: HTMLElement): boolean => {
    // 点击 Mask 文案
    if (toggleMask(element)) {
        return true;
    }

    // a 标签点击
    const tagName = element.tagName.toLowerCase();
    if (tagName == "a") return true;

    // 图片标签点击
    if (tagName == "img" && window.android) {
        const img = element as HTMLImageElement;
        // 表情不预览
        if (img.getAttribute('smileid') == null) {
            window.android.onPreviewImage(img.src, [img.src]);
        }
        return true
    }

    // 其它元素点击不拦截
    return false;
};

const toggleMask = (element: HTMLElement): boolean => {
    if (element.classList.contains("text_mask")) {
        if (element.classList.contains("show")) {
            element.classList.remove("show");
        } else {
            element.classList.add("show");
        }
        return true;
    }
    return false;
}

const scrollIntoView = (event: Event, container: Element | undefined | null, isMainComment: boolean) => {
    // 将 event 绑定的事件的元素本身移到视野内
    const element = event.currentTarget as HTMLElement;
    if (element && container) {
        const targetRect = element.getBoundingClientRect();
        container.scrollTo({
            top: container.scrollTop + targetRect.top - (isMainComment ? 40 : 10),
            behavior: 'smooth',
        });

        const animationend = () => {
            element.classList.remove("blinking");
            element.removeEventListener('animationend', animationend);
        }

        element.classList.add("blinking");
        element.addEventListener('animationend', animationend);
    }
};

/**
 * 优化回复内容，剔除重复的引用，并重新添加引用
 *
 * @param userName
 * @param replyContent
 */
const optReplyContent = (userName: string, replyContent: string): string => {
    let reply_to_content = replyContent
        .replace(/<div class="quote">([^^]*?)<\/div>/, '')
        .replace(/<span class="text_mask" style="([^^]*?)">([^^]*?)<\/span>/, '')
        .replace(/<\/?[^>]+>/g, '')
        .replace(/&lt;/g, '<')
        .replace(/&gt;/g, '>')
        .replace(/\B@([^\W_]\w*)\b/g, '＠$1');

    if (reply_to_content.length > 100) {
        reply_to_content = reply_to_content.slice(0, 100) + '...';
    }
    return '[quote][b]' + userName + '[/b] 说: ' + reply_to_content + '[/quote]';
}

/**
 * 优化文案
 *
 * @param text
 */
const optText = (text: string | null | undefined) => {
    const content = (text || '').trim()
    const space = "\u3000\u3000";
    return space + content
        .replace(/&nbsp;/g, " ").trim()
    // .replace(/(\r\n|\n|\r)\s+/g, "$1")
    // .replace(/(\r\n|\n|\r)/g, `$1${space}`);
}

async function delay(ms: number): Promise<void> {
    return new Promise(resolve => setTimeout(resolve, ms));
}

/**
 * 初始化评论发布填充逻辑
 *
 * @param param
 */
const initComment = (param: () => CommentTreeEntity[]) => {
    const refreshMainComment = (mainComment: CommentReplyMainContent) => {
        for (const postId in mainComment) {
            const post = mainComment[postId];
            let hasTargetMainComment = false;

            // 判断是否存在评论
            param().forEach((item) => {
                if (item.id == postId) hasTargetMainComment = true
            });

            // 不包含待添加的主评论，直接添加到第一条
            if (!hasTargetMainComment) {
                const mainCommentId = post.pst_id;
                const mainCommentUid = post.pst_uid;

                // 转换模型
                const newMainComment = replyCommentToTreeComment(post, mainCommentId, mainCommentUid, false);

                // 添加到头部
                param().unshift(newMainComment);
            }
        }
    }

    const refreshSubComment = (subComment: CommentReplySubContent) => {
        for (const postId in subComment) {
            const posts = subComment[postId];

            let targetMainComment: CommentTreeEntity | null = null;
            param().forEach((item) => {
                if (item.id == postId) {
                    targetMainComment = item;
                }
            });

            // 刷新目标主评论的子评论
            if (targetMainComment != null) {
                const comment = targetMainComment as CommentTreeEntity;
                const mainCommentId = comment.id;
                const mainCommentUid = comment.userId;
                const mainCommentSubReplyCommentIds = comment.topicSubReply.map(item => item.id);

                // 转换模型、并且过滤掉已经存在的子条目
                const newComments = posts
                    .map(post => replyCommentToTreeComment(post, mainCommentId, mainCommentUid, true))
                    .filter(item => !mainCommentSubReplyCommentIds.includes(item.id));

                // 添加
                comment.topicSubReply.push(...newComments);
            }
        }
    }

    // 添加评论
    window.addComment = (comment: CommentReplyEntity) => {
        // 主评论
        const main = comment.posts?.main;
        if (main != undefined) {
            refreshMainComment(main);
        }

        // 子评论
        const sub = comment.posts?.sub;
        if (sub != undefined) {
            refreshSubComment(sub);
        }
    }
}

/**
 * 映射
 *
 * var subReply = function (
 *    type,           // 评论的话题或日志等主题类型
 *    topic_id,       // 主评论的评论ID
 *    post_id,        // 评论的话题或日志等主题ID
 *    sub_reply_id,   // 发布评论者的那条评论ID
 *    sub_reply_uid,  // 发布评论者的用户ID
 *    post_uid,       // 主评论的用户ID
 *    sub_post_type   // 主评论 0、子评论 1
 *  )
 *
 * @param reply
 * @param mainCommentId
 * @param mainCommentUid
 * @param isSub
 */
const replyCommentToTreeComment = (
    reply: Post,
    mainCommentId: string,
    mainCommentUid: string,
    isSub: boolean = false
): CommentTreeEntity => {
    const target = {} as CommentTreeEntity;

    const subType = isSub ? 1 : 0;
    const subReplyId = isSub ? reply.pst_id : 0
    const subReplyUid = reply.pst_uid;

    const avatar = reply.avatar || '';

    target.id = reply.pst_id
    target.time = reply.dateline;
    target.userAvatar = avatar.startsWith("//") ? "https:" + avatar : avatar;
    target.userName = reply.nickname;
    target.userId = reply.username;
    target.replyContent = reply.pst_content;
    target.replyJs = `subReply('${reply.model}', ${reply.pst_mid}, ${mainCommentId}, ${subReplyId}, ${subReplyUid}, ${mainCommentUid}, ${subType})`;
    target.topicSubReply = [];
    // target.floor: string;
    // target.replyQuote: string;
    // target.topicSubReply: CommentTreeEntity[];
    return target;
};


export default {
    optContentJs: optContentJs,
    optText: optText,
    optReplyItemClick: optReplyItemClick,
    optReplyContent: optReplyContent,
    scrollIntoView: scrollIntoView,
    delay: delay,
    initComment: initComment
}