function injectImageClick(content: HTMLElement | undefined | null) {
    if (content) {
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

        content.querySelectorAll("span.text_mask").forEach(item => {
            const mask = item as HTMLElement;
            mask.onclick = (event) => {
                event.preventDefault();
                mask.style.color = "white";
            }
        });
    }
}

const injectHandleItemClick = (element: HTMLElement): boolean => {
    // Mask 文案
    if (element.classList.contains("text_mask")) {
        element.style.color = "white";
        return true
    }
    if (element instanceof HTMLImageElement) {
        if (window.android) {
            window.android.onPreviewImage(element.src, [element.src]);
        }
        return true
    }
    return false;
};

const scrollIntoView = (event: Event, container: Element | undefined | null, isMainComment: boolean) => {
    const element = event.target as HTMLElement
    if (element && container) {
        const targetRect = element.getBoundingClientRect();
        container.scrollTo({
            top: container.scrollTop + targetRect.top - (isMainComment ? 40 : 40),
            behavior: 'smooth',
        });

        const animationend = () => {
            element.classList.remove("blinking");
            element.removeEventListener('animationend', animationend);
        }

        element.classList.add("blinking");
        element.addEventListener('animationend', animationend);
    }
}

const handleQuote = (userName: string, replyContent: string): string => {
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

export default {
    injectImageClick: injectImageClick,
    injectHandleItemClick: injectHandleItemClick,
    scrollIntoView: scrollIntoView,
    handleQuote: handleQuote
}