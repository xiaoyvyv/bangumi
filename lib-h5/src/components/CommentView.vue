<script setup lang="ts">
import {toRefs} from "vue";
import common from "../util/common.ts";
import {CommentTreeEntity} from "../util/interface/CommentTreeEntity.ts";
import ImageView from "./ImageView.vue";

/**
 * 参数
 */
const props = defineProps({
  target: {
    type: String,
    required: true
  },
  comments: {
    type: Array<CommentTreeEntity>,
    default: [],
    required: true
  },
  sort: {
    type: String,
    required: true
  },
});

/**
 * 转为响应式
 */
const {target, comments, sort} = toRefs(props);

/**
 * 排序名称
 *
 * @param sort
 */
const sortName = (sort: string) => {
  switch (sort) {
    case "desc": {
      return "最新";
    }
    case "hot": {
      return "最热";
    }
    default: {
      return "时间"
    }
  }
}

/**
 * 点击用户
 * @param comment
 */
const onClickUser = (comment: CommentTreeEntity) => {
  if (window.android && comment.userId) {
    window.android && window.android.onClickUser(comment.userId);
  }
}

/**
 * 点击评论
 */
const onClickReplyComment = (event: Event, mainComment: CommentTreeEntity, subComment: CommentTreeEntity | null) => {
  // 判断点击的元素是否需要拦截处理
  const clickElement = event.target as HTMLElement;
  if (common.optReplyItemClick(clickElement)) return;

  // 将点击的条目会移到视野
  common.scrollIntoView(event, document.querySelector(target?.value), subComment == null);

  const subReplyJs = subComment?.replyJs || "";
  const replyMainJs = mainComment.replyJs || "";

  // 是否回复的二级评论
  const isReplaySub = subReplyJs.length > 0;
  const replyJs = isReplaySub ? subReplyJs : replyMainJs;
  const replyComment = subComment ? subComment : mainComment;

  // 没注册原生接口
  if (!window.android) return;

  if (replyJs.length > 0) {
    if (isReplaySub) {
      replyComment.replyQuote = common.optReplyContent(replyComment.userName, replyComment.replyContent);
    }

    window.android.onReplyUser(subReplyJs.length > 0 ? subReplyJs : replyJs, JSON.stringify(subComment ? subComment : mainComment));
    return;
  }

  // 需要登录
  window.android.onNeedLogin();
};

/**
 * 更改排序
 */
const onClickCommentSort = () => {
  window.android && window.android.onClickCommentSort();
}

/**
 * 新建评论
 * @param event
 */
const onClickNewComment = (event: Event) => {
  common.scrollIntoView(event, document.querySelector(target?.value), true);

  // 回调
  window.android && window.android.onReplyNew();
}
</script>

<template>
  <div class="comment">
    <div class="comment-title">
      <div class="title">用户讨论</div>
      <div class="sort" @click.stop="onClickCommentSort">排序：{{ sortName(sort) }}</div>
      <div style="flex: 1"/>
      <div class="write" @click.stop="onClickNewComment($event)">写留言</div>
    </div>

    <div class="comment-item" v-for="comment in comments" :key="comment.id">
      <image-view class="avatar"
                  width="36px" height="36px"
                  :src="comment.userAvatar"
                  @click.stop="onClickUser(comment)"/>
      <div class="comment-content">
        <div class="info" @click.stop="onClickReplyComment($event, comment, null)">
          <div class="user-name" @click.stop="onClickUser(comment)">
            {{ (comment as CommentTreeEntity).userName }}
          </div>
          <div class="time">{{ comment.time }}</div>
        </div>
        <div class="topic-html" v-html="comment.replyContent" @click.stop="onClickReplyComment($event, comment, null)"/>

        <div style="height: 12px"/>

        <!-- 嵌套条目 -->
        <div class="comment-item" v-for="subComment in (comment.topicSubReply || [])" :key="subComment.id">
          <image-view class="avatar" height="24px" width="24px"
                      :src="subComment.userAvatar"
                      @click.stop="onClickUser(subComment)"/>
          <div class="comment-content" @click.stop="onClickReplyComment($event, comment, subComment)">
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
</template>

<style scoped lang="scss">
.comment {
  width: 100%;
  display: flex;
  flex-flow: column nowrap;
  padding: 12px 16px;
  min-height: 300px;

  .comment-title {
    display: flex;
    flex-flow: row nowrap;
    padding-bottom: 16px;
    align-items: center;
    font-size: 16px;

    .title {
      color: var(--on-surface-color);
      font-weight: bold;
    }

    .sort {
      margin-left: 16px;
      font-size: 16px;
      color: var(--primary-color);
    }

    .write {
      color: var(--primary-color);
      font-weight: bold;
    }
  }

  .comment-item {
    display: flex;
    flex-flow: row nowrap;

    .avatar {
      margin: 6px 0;
      border-radius: 6px;
      background: var(--surface-container-color);
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
          color: var(--on-surface-color);
          opacity: 0.5;
          padding: 4px 0;
        }

        .time {
          font-size: 12px;
          color: var(--on-surface-variant-color);
          opacity: 0.5;
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
</style>