<script setup lang="ts">
import {nextTick, onMounted, ref} from "vue";
import {BlogDetailEntity, CommentTreeEntity, MediaRelative} from "../../util/interface/entity.ts";

const blogRef = ref<BlogDetailEntity>();
const blogContentRef = ref<HTMLDivElement>();

const blogHandler = {
  loadBlogDetail: async (obj: BlogDetailEntity) => {
    blogRef.value = obj;
    await nextTick();
    const blogContent = blogContentRef.value;
    if (blogContent) {
      const images = blogContent.querySelectorAll("img");
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
    }
  }
}

const optText = (text: string | null | undefined) => {
  const content = (text || '').trim()
  const space = "\u3000\u3000";
  const str = space + content
      .replace(/&nbsp;/g, " ").trim()
      .replace(/(\r\n|\n|\r)\s+/g, "$1")
      .replace(/(\r\n|\n|\r)/g, `$1${space}`);

  console.log(str);
  return str;
}

/**
 * 点击评论
 */
const onClickComment = (event: Event, mainComment: CommentTreeEntity, subComment: CommentTreeEntity | null) => {
  scrollIntoView(event, subComment == null);
  const subReplyJs = subComment?.replyJs || "";
  const replyMainJs = mainComment.replyJs || "";
  const replyJs = subReplyJs.length > 0 ? subReplyJs : replyMainJs
  if (window.android) {
    if (replyJs.length > 0) {
      window.android.onReplyUser(subReplyJs.length > 0 ? subReplyJs : replyJs, JSON.stringify(subComment ? subComment : mainComment));
      return
    }

    window.android.onNeedLogin();
  }
}

const onClickNewComment = (event: Event) => {
  scrollIntoView(event, true);
  if (window.android) {
    window.android.onReplyNew();
  }
}

const onClickRelated = (related: MediaRelative) => {
  if (window.android) {
    window.android.onClickRelated(JSON.stringify(related))
  }
}

const onClickUser = (comment: CommentTreeEntity) => {
  if (window.android) {
    window.android.onClickUser(comment.userId);
  }
}

const scrollIntoView = (event: Event, isMainComment: boolean) => {
  const container = document.getElementById("blog");
  const element = event.target as HTMLElement
  if (element && container) {
    const targetRect = element.getBoundingClientRect();
    container.scrollTo({
      top: container.scrollTop + targetRect.top - (isMainComment ? 40 : 60),
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

onMounted(() => {
  window.blog = blogHandler;
  window.mounted = true;
});
</script>

<template>
  <div class="blog" id="blog">
    <div class="blog-title">
      {{ blogRef?.title }}
    </div>
    <div class="blog-info">
      <div class="blog-author">{{ blogRef?.userName }}</div>
      <div class="blog-time">{{ blogRef?.time }}</div>
    </div>
    <div class="blog-relative" v-if="(blogRef?.related || []).length > 0">
      <div class="blog-relative-subject">
        <div class="tip">关联的条目 {{ blogRef?.related?.length }} 个</div>
        <div class="relative" v-for="item in (blogRef?.related || [])" @click.stop="onClickRelated(item)">
          <img :src="item.cover" alt="img">
          <div class="title"># {{ item.titleNative }}</div>
        </div>
      </div>
    </div>
    <div class="blog-content" ref="blogContentRef" v-html="optText(blogRef?.content)"/>
    <div class="blog-tag" v-if="(blogRef?.tags || []).length > 0">
      <div class="tip">标签：</div>
      <div class="blog-tag-item" v-for="item in (blogRef?.tags || [])">{{ item.title }}</div>
    </div>
    <hr class="divider" v-if="blogRef?.content">
    <div class="blog-comment" v-if="blogRef?.content">
      <div class="blog-comment-title">
        <div class="title">精选留言</div>
        <div style="flex: 1"/>
        <div class="write" @click.stop="onClickNewComment($event)">写留言</div>
      </div>
      <div class="blog-comment-item" v-for="comment in (blogRef?.comments || [])">
        <img class="avatar" :src="comment.userAvatar" alt="img" @click.stop="onClickUser(comment)">
        <div class="comment-content">
          <div class="info">
            <div class="user-name" @click.stop="onClickUser(comment)">{{ comment.userName }}</div>
            <div class="time">{{ comment.time }}</div>
          </div>
          <div class="blog-html" v-html="comment.replyContent" @click.stop="onClickComment($event, comment, null)"/>

          <div style="height: 12px"/>

          <!-- 嵌套条目 -->
          <div class="blog-comment-item" v-for="subComment in (comment.topicSubReply || [])">
            <img class="avatar sub" :src="subComment.userAvatar" alt="img" @click.stop="onClickUser(subComment)">
            <div class="comment-content">
              <div class="info">
                <div class="user-name" @click.stop="onClickUser(subComment)">{{ subComment.userName }}</div>
                <div class="time">{{ subComment.time }}</div>
              </div>
              <div class="blog-html" v-html="subComment.replyContent"
                   @click.stop="onClickComment($event, comment, subComment)"/>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="blog-space" v-if="blogRef?.content">
      我是有底线的
    </div>
  </div>
</template>

<style lang="scss">
.blog {
  height: 100%;
  width: 100%;
  overflow-x: hidden;
  overflow-y: scroll;

  img {
    display: block;
    max-width: 100%;
    height: auto;
    border-radius: 6px;
    margin: 12px 0;
    border: 1px #cccccc7f solid !important;
  }

  img[smileid] {
    margin: 4px 6px 4px 6px !important;
    vertical-align: bottom !important;
    border: 1px solid #ff80ab !important;
    padding: 1px !important;
    height: 20px !important;
    width: 20px !important;
    min-width: unset !important;
    border-radius: 4px !important;
    display: inline-block;
  }
}

.blog-title {
  font-weight: 800 !important;
  font-size: 20px;
  margin: 12px 16px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  overflow: hidden;
  -webkit-box-orient: vertical;
  text-overflow: ellipsis;
}

.blog-info {
  display: flex;
  flex-direction: row;
  align-items: center;
  font-size: 15px;
  margin-left: 16px;

  .blog-author {
    color: deepskyblue;
    margin-right: 6px;
  }

  .blog-time {
    color: #888888;
  }
}

.blog-content {
  overflow-x: hidden !important;
  padding: 12px 16px;
  font-size: 16px;
  line-height: 1.75 !important;

  img {
    min-width: 120px;
  }
}

.tip {
  font-weight: bold;
}

.blog-relative {
  width: 100%;

  .blog-relative-subject {
    margin: 16px 16px;
    padding: 12px;
    border-radius: 6px;
    background: #cccccc3f;


    .relative {
      width: 100%;
      display: flex;
      flex-flow: row nowrap;
      align-items: center;
      margin-top: 6px;
    }

    img {
      margin: 6px 0;
      height: 44px;
      width: 44px;
      border-radius: 6px;
      background: #cccccc7f;
    }

    .title {
      width: 0;
      flex: 1;
      padding: 2px 6px 4px 12px;
      color: deepskyblue;
    }
  }
}

.blog-tag {
  display: flex;
  flex-flow: row wrap;
  margin: 16px 16px;
  padding: 12px;
  border-radius: 6px;
  background: #cccccc3f;
  align-items: center;

  .blog-tag-item {
    margin: 4px 8px 4px 0;
    padding: 4px;
    color: white;
    font-size: 12px;
    border-radius: 6px;
    background: deepskyblue;
  }
}

.divider {
  height: 12px;
  background-color: #cccccc;
  width: 100%;
  margin-top: 24px;
  opacity: 0.2;
}

.blog-comment {
  padding: 12px 16px;

  .blog-comment-title {
    display: flex;
    flex-flow: row nowrap;
    padding-bottom: 16px;
    align-items: center;

    .title {
      color: #cccccc;
      font-weight: bold;
    }

    .write {
      color: deepskyblue;
      font-weight: bold;
    }
  }

  .blog-comment-item {
    display: flex;
    flex-flow: row nowrap;

    .avatar {
      margin: 6px 0;
      height: 36px;
      width: 36px;
      border-radius: 6px;
      background: #cccccc7f;
    }

    .avatar.sub {
      height: 24px;
      width: 24px;
    }

    .comment-content {
      padding-bottom: 12px;
      margin-left: 12px;
      width: 100%;

      .info {
        width: 100%;
        display: flex;
        flex-flow: row nowrap;
        align-items: center;

        .user-name {
          font-size: 14px;
          color: #3333339f;
          padding: 4px 0;
        }

        .time {
          font-size: 12px;
          color: #3333337f;
          margin-left: 12px;
        }
      }

      .blog-html {
        width: 100%;

        img {
          min-width: 120px;
        }
      }

      .sub-reply {
        width: 100%;
        display: flex;
        flex-flow: column nowrap;
      }
    }
  }
}

.blinking {
  color: #3b4f64;
  border-radius: 6px;
  animation: blinkAnimation 1s forwards;
}

@keyframes blinkAnimation {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: .5;
  }
}

.blog-space {
  height: 400px;
  display: flex;
  align-items: end;
  justify-content: center;
  font-size: 10px;
  opacity: 0.5;
  padding-bottom: 24px;
}
</style>