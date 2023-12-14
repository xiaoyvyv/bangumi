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

const topic = ref<TopicDetailEntity>({} as TopicDetailEntity);
const topicContentRef = ref<HTMLDivElement>();

// 评论相关
const loadingIdentifier = ref(new Date().getDate());
const commentPageSize = 10;
const commentPage = ref(1);
const commentSort = ref("desc");
const comments = reactive<CommentTreeEntity[]>([]);
const robotSay = ref("哼！Bangumi老娘我是有底线的人");

const topicHandler = {
  loadTopicDetail: async (obj: TopicDetailEntity) => {
    topic.value = obj;

    // Html 交互处理
    await nextTick();
    common.optContentJs(topicContentRef.value);
  }
}

/**
 * 加载评论
 *
 * @param $state
 */
const loadComments = async ($state: any) => {
  const pageCommentJson = window.android.onLoadComments(commentPage.value, commentPageSize, commentSort.value);
  const pageComments = JSON.parse(pageCommentJson);

  if (pageComments.length == 0) {
    $state.complete();
  } else {
    await common.delay(200);
    comments.push(...pageComments);
    commentPage.value++;
    await nextTick();
    if (pageComments.length < commentPageSize) {
      $state.complete();
    } else {
      $state.loaded();
    }
  }
}

onMounted(() => {
  // 机器人说话
  window.robotSay = (message: string) => {
    robotSay.value = message;
  };

  // 更改排序
  window.changeCommentSort = (sort: string) => {
    commentSort.value = sort;
    commentPage.value = 1;
    comments.length = 0;
    loadingIdentifier.value++;
  };

  // 评论填充初始化
  common.initComment(() => comments);

  window.topic = topicHandler;
  window.mounted = true;
});
</script>

<template>
  <div class="topic" id="topic" v-if="topic.id">
    <div class="topic-title">{{ topic.title }}</div>
    <div class="topic-info">
      <div class="topic-author">{{ topic.userName }}</div>
      <div class="topic-time">{{ topic.time }}</div>
    </div>

    <relative-item-view :related="topic.related"/>

    <div class="topic-content" ref="topicContentRef" v-html="common.optText(topic.content)"/>
    <div class="divider" v-if="topic.content"/>

    <comment-view target="#topic" :comments="comments" :sort="commentSort"/>

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
</style>