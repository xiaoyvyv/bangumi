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
            mask.style.color = "white";
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
    if (element.classList.contains("text_mask")) {
        if (element.classList.contains("show")) {
            element.classList.remove("show");
        } else {
            element.classList.add("show");
        }
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

export default {
    optContentJs: optContentJs,
    optText: optText,
    optReplyItemClick: optReplyItemClick,
    optReplyContent: optReplyContent,
    scrollIntoView: scrollIntoView,
    delay: delay
}