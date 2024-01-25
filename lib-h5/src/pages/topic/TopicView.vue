<script setup lang="ts">
import {nextTick, onMounted, reactive, ref} from "vue";
import common from "../../util/common.ts";
import InfiniteLoading from "v3-infinite-loading";
import CommentView from "../../components/CommentView.vue";
import "v3-infinite-loading/lib/style.css";
import BottomRobotView from "../../components/BottomRobotView.vue";
import RelativeItemView from "../../components/RelativeItemView.vue";
import {TopicDetailEntity} from "../../util/interface/TopicDetailEntity.ts";
import {CommentTreeEntity} from "../../util/interface/CommentTreeEntity.ts";
import BottomSpinnerView from "../../components/BottomSpinnerView.vue";
import EmojiView from "../../components/EmojiView.vue";
import {LikeActionEntity} from "../../util/interface/LikeActionEntity.ts";

const topic = ref<TopicDetailEntity>({} as TopicDetailEntity);
const topicContentRef = ref<HTMLDivElement>();

// 评论相关
const loadingIdentifier = ref(new Date().getDate());
let commentPageSize = 10;
const commentPage = ref(1);
const commentSort = ref("default");
const comments = reactive<CommentTreeEntity[]>([]);
const robotSay = ref("哼！Bangumi老娘我是有底线的人");

const topicHandler = {
  loadTopicDetail: async (obj: TopicDetailEntity) => {
    topic.value = obj;

    // 有标记评论情况下，一次性加载全部
    if (obj.anchorCommentId.length > 0) {
      commentPageSize = 10000;
      await loadComments(null);
    }

    // Html 交互处理
    await nextTick();
    common.optContentJs(topicContentRef.value);
    common.scrollToTargetComment(obj.anchorCommentId);
  }
}

/**
 * 加载评论
 *
 * @param $state
 */
const loadComments = async ($state: any | null) => {
  const pageCommentSort = window.android.onCommentSort(commentSort.value);
  const pageCommentJson = window.android.onLoadComments(commentPage.value, commentPageSize, pageCommentSort);
  const pageComments = JSON.parse(pageCommentJson);

  commentSort.value = pageCommentSort;

  if (pageComments.length == 0) {
    $state?.complete();
  } else {
    comments.push(...pageComments);
    commentPage.value++;
    await nextTick();
    if (pageComments.length < commentPageSize) {
      $state?.complete();
    } else {
      $state?.loaded();
    }
  }
}

/**
 * 话题内容底部的贴贴，构建一个假的评论传入
 *
 * @param topic
 */
const buildFakeComment = (topic: TopicDetailEntity): CommentTreeEntity => {
  return {
    emojiParam: topic.emojiParam,
    id: topic.emojiParam?.likeCommentId,
  } as CommentTreeEntity
}

/**
 * 主题内容下方贴贴
 *
 * @param event
 */
const onClickCommentAction = (event: MouseEvent) => {
  let fakeComment = buildFakeComment(topic.value);
  window.android && window.android.onClickCommentAction(JSON.stringify(fakeComment), event.clientX, event.clientY);
}

/**
 * 刷新主题内容底部的贴贴
 *
 * @param commentId
 * @param likeActionInfo
 */
const handleEmojis = (commentId: string, likeActionInfo: LikeActionEntity[]) => {
  if (commentId == topic.value.emojiParam.likeCommentId) {
    topic.value.emojis = likeActionInfo;
  }
}

/**
 * 点击用户
 */
const onClickUser = () => {
  const topicUserId = topic.value?.userId || '';
  if (window.android && topicUserId) {
    window.android && window.android.onClickUser(topicUserId, topic.value?.id);
  }
}

onMounted(() => {
  // 机器人说话
  window.robotSay = (message: string) => {
    robotSay.value = message;
  };

  // 评论填充初始化
  common.initComment(handleEmojis, () => comments, (sort: string) => {
    commentSort.value = sort;
    commentPage.value = 1;
    comments.length = 0;
    loadingIdentifier.value++;
  });

  window.topic = topicHandler;
  window.mounted = true;
});
</script>

<template>
  <div class="topic" id="topic" v-if="topic.id">
    <div class="topic-title">{{ topic.title }}</div>
    <div class="topic-info" @click.stop="onClickUser">
      <div class="topic-author" v-if="topic.userName">{{ topic.userName }}</div>
      <div class="topic-time">{{ topic.time }}</div>
    </div>

    <relative-item-view :related="topic.related"/>

    <div class="topic-content" ref="topicContentRef" v-html="common.optText(topic.content)"/>

    <div class="emoji" v-if="topic.emojiParam?.enable">
      <emoji-view class="topic-emoji" :emojis="topic.emojis" :comment="buildFakeComment(topic)"/>
      <div style="flex: 1"/>
      <img class="action"
           smileid src="../../assets/image/ic_like.svg"
           alt="action"
           @click.stop="onClickCommentAction($event)">
    </div>

    <div class="divider" v-if="topic.content"/>

    <comment-view target="#topic"
                  :comments="comments"
                  :sort="commentSort"
                  :master-id="topic.userId"
                  :anchor-id="topic.anchorCommentId"/>

    <infinite-loading class="loading"
                      target="#topic"
                      :identifier="loadingIdentifier"
                      :distance="300"
                      @infinite="loadComments">
      <!--suppress VueUnrecognizedSlot -->
      <template #spinner>
        <bottom-spinner-view/>
      </template>

      <!--suppress VueUnrecognizedSlot -->
      <template #complete>
        <bottom-robot-view :message="robotSay"/>
      </template>
    </infinite-loading>
  </div>
</template>

<style lang="scss">
.topic {
  height: 100%;
  width: 100%;
  overflow-x: hidden;
  overscroll-behavior-x: none;
  overflow-y: scroll;


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
      color: var(--primary-color);
      margin-right: 6px;
    }

    .topic-time {
      color: var(--on-surface-variant-color);
      opacity: 0.5;
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

  .emoji {
    margin-top: 16px;
    display: flex;
    flex-flow: row nowrap;
    width: 100%;
    align-items: center;
    padding-right: 16px;

    .topic-emoji {
      margin-left: 16px;
      margin-right: 16px;
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

  .divider {
    margin-top: 16px;
  }

  .tip {
    font-weight: bold;
  }

  .topic-tag {
    display: flex;
    flex-flow: row wrap;
    margin: 16px 16px;
    padding: 12px;
    border-radius: 6px;
    background-color: var(--surface-container-color);
    align-items: center;

    .topic-tag-item {
      margin: 4px 8px 4px 0;
      padding: 4px;
      color: var(--on-primary-color);
      font-size: 12px;
      border-radius: 6px;
      background: var(--primary-color);
    }
  }


  .loading {
    text-align: center;
    padding: 24px;
  }

  .topic-space {
    height: 600px;
    display: flex;
    align-items: end;
    justify-content: center;
    font-size: 10px;
    opacity: 0.5;
    padding-bottom: 24px;
  }
}
</style>