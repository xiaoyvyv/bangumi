<script setup lang="ts">
import {toRefs} from "vue";
import common from "../util/common.ts";
import {CommentTreeEntity} from "../util/interface/CommentTreeEntity.ts";
import ImageView from "./ImageView.vue";
import EmojiView from "./EmojiView.vue";

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
  masterId: {
    type: String,
    required: false,
    default: ""
  },
  anchorId: {
    type: String,
    required: false,
    default: ""
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
    case"asc": {
      return "时间"
    }
    default: {
      return "默认"
    }
  }
}

/**
 * 点击用户
 * @param comment
 */
const onClickUser = (comment: CommentTreeEntity) => {
  if (window.android && comment.userId) {
    window.android && window.android.onClickUser(comment.userId, comment.id);
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
 * 新建贴贴
 */
const onClickCommentAction = (event: MouseEvent, comment: CommentTreeEntity) => {
  window.android && window.android.onClickCommentAction(JSON.stringify(comment), event.clientX, event.clientY);
};

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

    <div class="comment-item" v-for="comment in comments" :key="comment.id"
         :id="'post_' + comment.id">
      <image-view class="avatar"
                  width="36px" height="36px"
                  :src="comment.userAvatar"
                  @click.stop="onClickUser(comment)"/>
      <div class="comment-content">
        <div class="info" @click.stop="onClickReplyComment($event, comment, null)">
          <div class="user-name" @click.stop="onClickUser(comment)">
            <span>{{ (comment as CommentTreeEntity).userName }}</span>
            <span v-if="masterId === comment.userId" class="master">楼主</span>
          </div>
          <div class="time">{{ comment.time }}<span class="floor">{{ comment.floor }}</span></div>
          <div style="flex: 1"/>
          <img class="action" v-if="comment.emojiParam?.enable"
               smileid src="../assets/image/ic_like.svg"
               alt="action"
               @click.stop="onClickCommentAction($event,comment)">
        </div>

        <div class="topic-html-wrap">
          <div class="topic-html" v-html="comment.replyContent"
               @click.stop="onClickReplyComment($event, comment, null)"/>
          <div class="anchor" v-if="comment.id === anchorId"/>
        </div>

        <emoji-view :emojis="comment.emojis" :comment="comment" style="margin-top: 16px"/>

        <!-- 嵌套条目 -->
        <div class="comment-item sub" v-for="subComment in (comment.topicSubReply || [])" :key="subComment.id"
             :class="{'anchor': subComment.id === anchorId}"
             :id="'post_' + subComment.id">
          <image-view class="avatar" height="24px" width="24px"
                      :src="subComment.userAvatar"
                      @click.stop="onClickUser(subComment)"/>
          <div class="comment-content" @click.stop="onClickReplyComment($event, comment, subComment)">
            <div class="info">
              <div class="user-name sub"
                   @click.stop="onClickUser(subComment)">
                <span>{{ (subComment as CommentTreeEntity).userName }}</span>
                <span v-if="masterId === subComment.userId" class="master">楼主</span>
              </div>
              <div class="time">{{ subComment.time }}<span class="floor">{{ subComment.floor }}</span></div>
              <div style="flex: 1"/>
              <img class="action" v-if="subComment.emojiParam?.enable"
                   smileid src="../assets/image/ic_like.svg"
                   alt="action"
                   @click.stop="onClickCommentAction($event,subComment)">
            </div>
            <div class="topic-html-wrap">
              <div class="topic-html" v-html="subComment.replyContent"/>
              <div class="anchor" v-if="subComment.id === anchorId"/>
            </div>
            <emoji-view :emojis="subComment.emojis" :comment="subComment" style="margin-top: 16px"/>
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
  min-height: 300px;

  .comment-title {
    display: flex;
    flex-flow: row nowrap;
    padding: 16px 16px;
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
    padding: 8px 16px;

    &.sub {
      padding: 0;
    }

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
          opacity: 0.75;
          padding: 4px 0;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
          max-width: 50%;
          display: flex;
          flex-flow: row nowrap;
          justify-content: center;
          align-items: center;

          &.sub {
            max-width: 35%;
          }

          .master {
            color: var(--on-primary-color);
            border-radius: 6px;
            background: var(--primary-color);
            font-weight: bold;
            font-size: 8px;
            padding: 2px 4px;
            margin-left: 4px;
          }
        }

        .time {
          font-size: 12px;
          color: var(--on-surface-variant-color);
          opacity: 0.5;
          margin-left: 12px;
        }

        .floor {
          padding: 0 6px;
        }

        .action {
          height: 24px;
          width: 24px;
          flex-shrink: 0;
          line-height: 24px;
          margin: 0 !important;
          box-sizing: border-box;
          padding: 1px 0 !important;
          border: none;
          filter: invert(0.5);
        }
      }

      .topic-html-wrap {
        width: 100%;
        position: relative;

        .topic-html {
          width: 100%;
          max-width: 100%;
          line-height: 1.5;
          word-break: break-all;
          overflow: hidden !important;

          img {
            min-width: 120px;
          }
        }

        .anchor {
          position: absolute;
          top: -3px;
          left: -6px;
          right: -6px;
          bottom: -3px;
          background: var(--primary-color);
          opacity: 0.1;
          border-radius: 6px;
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