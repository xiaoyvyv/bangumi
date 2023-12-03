<script setup lang="ts">
import {nextTick, onMounted, ref} from "vue";
import {CommentTreeEntity, TopicDetailEntity} from "../../util/interface/entity.ts";
import common from "../../util/common.ts";

const topic = ref<TopicDetailEntity>({} as TopicDetailEntity);
const topicContentRef = ref<HTMLDivElement>();

const topicHandler = {
  loadTopicDetail: async (obj: TopicDetailEntity) => {
    topic.value = obj;

    await nextTick();
    const content = topicContentRef.value;

    // 图片处理
    common.injectImageClick(content);
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
  const item = event.target as HTMLElement;
  if (common.injectHandleItemClick(item)) {
    return;
  }

  common.scrollIntoView(event, document.getElementById("topic"), subComment == null);

  const subReplyJs = subComment?.replyJs || "";
  const replyMainJs = mainComment.replyJs || "";

  // 是否回复的二级评论
  const isReplaySub = subReplyJs.length > 0;
  const replyJs = isReplaySub ? subReplyJs : replyMainJs;
  const replyComment = subComment ? subComment : mainComment;

  if (window.android) {
    if (replyJs.length > 0) {
      if (isReplaySub) {
        replyComment.replyQuote = common.handleQuote(replyComment.userName, replyComment.replyContent);
      }

      window.android.onReplyUser(subReplyJs.length > 0 ? subReplyJs : replyJs, JSON.stringify(subComment ? subComment : mainComment));
      return
    }

    window.android.onNeedLogin();
  }
};

const onClickNewComment = (event: Event) => {
  common.scrollIntoView(event, document.getElementById("topic"), true);
  if (window.android) {
    window.android.onReplyNew();
  }
}

const onClickRelated = (topic: TopicDetailEntity) => {
  if (window.android) {
    window.android.onClickRelated(JSON.stringify(topic))
  }
}

const onClickUser = (comment: CommentTreeEntity) => {
  if (window.android) {
    window.android.onClickUser(comment.userId || "");
  }
}

onMounted(() => {
  window.topic = topicHandler;
  window.mounted = true;
});
</script>

<template>
  <div class="topic" id="topic" v-if="topic.content || topic.comments">
    <div class="topic-title">
      {{ topic.title }}
    </div>
    <div class="topic-info">
      <div class="topic-author">{{ topic.userName }}</div>
      <div class="topic-time">{{ topic.time }}</div>
    </div>
    <div class="topic-relative">
      <div class="topic-relative-subject">
        <div class="tip">关联的讨论</div>
        <div class="relative" @click.stop="onClickRelated(topic)">
          <img :src="topic.headerAvatar" alt="img">
          <div class="title"># {{ topic.headerName }}</div>
        </div>
      </div>
    </div>
    <div class="topic-content" ref="topicContentRef" v-html="optText(topic.content)"/>
    <!--    <div class="topic-tag" v-if="(topic.likeMap || []).length > 0">-->
    <!--      <div class="tip">标签：</div>-->
    <!--      <div class="topic-tag-item" v-for="item in (topic.tags || [])">{{ item.title }}</div>-->
    <!--    </div>-->
    <hr class="divider" v-if="topic.content">
    <div class="topic-comment" v-if="topic.content">
      <div class="topic-comment-title">
        <div class="title">精选留言</div>
        <div style="flex: 1"/>
        <div class="write" @click.stop="onClickNewComment($event)">写留言</div>
      </div>
      <div class="topic-comment-item" v-for="comment in (topic.comments || [])">
        <img class="avatar" :src="comment.userAvatar" alt="img" @click.stop="onClickUser(comment)">
        <div class="comment-content">
          <div class="info" @click.stop="onClickComment($event, comment, null)">
            <div class="user-name" @click.stop="onClickUser(comment)">{{ comment.userName }}</div>
            <div class="time">{{ comment.time }}</div>
          </div>
          <div class="topic-html" v-html="comment.replyContent" @click.stop="onClickComment($event, comment, null)"/>

          <div style="height: 12px"/>

          <!-- 嵌套条目 -->
          <div class="topic-comment-item" v-for="subComment in (comment.topicSubReply || [])">
            <img class="avatar sub" :src="subComment.userAvatar" alt="img" @click.stop="onClickUser(subComment)">
            <div class="comment-content" @click.stop="onClickComment($event, comment, subComment)">
              <div class="info">
                <div class="user-name" @click.stop="onClickUser(subComment)">{{ subComment.userName }}</div>
                <div class="time">{{ subComment.time }}</div>
              </div>
              <div class="topic-html" v-html="subComment.replyContent"/>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="topic-space" v-if="topic.content">
      我是有底线的
    </div>
  </div>
</template>

<style lang="scss">
.topic {
  height: 100%;
  width: 100%;
  overflow-x: hidden;
  overscroll-behavior-x: none;
  overflow-y: scroll;

  img {
    display: block;
    max-width: 100%;
    height: auto;
    border-radius: 6px;
    margin: 12px 0;
    border: 1px #cccccc7f solid !important;
  }
}

.topic-title {
  font-weight: 800 !important;
  font-size: 20px;
  margin: 12px 16px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  overflow: hidden;
  -webkit-box-orient: vertical;
  text-overflow: ellipsis;
}

.topic-info {
  display: flex;
  flex-direction: row;
  align-items: center;
  font-size: 15px;
  margin-left: 16px;

  .topic-author {
    color: deepskyblue;
    margin-right: 6px;
  }

  .topic-time {
    color: #888888;
  }
}

.topic-content {
  overflow-x: hidden !important;
  padding: 12px 16px;
  font-size: 16px;
  line-height: 1.75 !important;
  word-break: break-all;

  img {
    min-width: 120px;
  }
}

.tip {
  font-weight: bold;
}

.topic-relative {
  width: 100%;

  .topic-relative-subject {
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

.topic-tag {
  display: flex;
  flex-flow: row wrap;
  margin: 16px 16px;
  padding: 12px;
  border-radius: 6px;
  background: #cccccc3f;
  align-items: center;

  .topic-tag-item {
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

.topic-comment {
  padding: 12px 16px;

  .topic-comment-title {
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

  .topic-comment-item {
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
      width: 0;
      flex: 1;

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

      .topic-html {
        width: 100%;
        max-width: 100%;
        word-break: break-all;
        overflow-x: hidden !important;

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

.topic-space {
  height: 400px;
  display: flex;
  align-items: end;
  justify-content: center;
  font-size: 10px;
  opacity: 0.5;
  padding-bottom: 24px;
}
</style>