<script setup lang="ts">
import {nextTick, onMounted, reactive, ref} from "vue";
import {BlogDetailEntity} from "../../util/interface/BlogDetailEntity.ts";
import RelativeItemView from "../../components/RelativeItemView.vue";
import BottomRobotView from "../../components/BottomRobotView.vue";
import CommentView from "../../components/CommentView.vue";
import InfiniteLoading from "v3-infinite-loading";
import common from "../../util/common.ts";
import {CommentTreeEntity} from "../../util/interface/CommentTreeEntity.ts";
import BottomSpinnerView from "../../components/BottomSpinnerView.vue";

const blog = ref<BlogDetailEntity>({} as BlogDetailEntity);
const blogContentRef = ref<HTMLDivElement>();

// 评论相关
const loadingIdentifier = ref(new Date().getDate());
const commentPageSize = 10;
const commentPage = ref(1);
const commentSort = ref("desc");
const comments = reactive<CommentTreeEntity[]>([]);
const robotSay = ref("哼！Bangumi老娘我是有底线的人");

const blogHandler = {
  loadBlogDetail: async (obj: BlogDetailEntity) => {
    blog.value = obj;

    // Html 交互处理
    await nextTick();
    common.optContentJs(blogContentRef.value);
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

  // 评论填充初始化
  common.initComment(null, () => comments, (sort: string) => {
    commentSort.value = sort;
    commentPage.value = 1;
    comments.length = 0;
    loadingIdentifier.value++;
  });

  window.blog = blogHandler;
  window.mounted = true;
});
</script>

<template>
  <div class="blog" id="blog" v-if="blog.id">
    <div class="blog-title">{{ blog.title }}</div>
    <div class="blog-info">
      <div class="blog-author">{{ blog.userName }}</div>
      <div class="blog-time">{{ blog.time }}</div>
    </div>

    <relative-item-view :related="blog.related"/>

    <div class="blog-content" ref="blogContentRef" v-html="common.optText(blog.content)"/>
    <div class="blog-tag" v-if="(blog.tags || []).length > 0">
      <div class="tip">标签：</div>
      <div class="blog-tag-item" v-for="item in (blog.tags || [])">{{ item.title }}</div>
    </div>
    <div class="divider" v-if="blog.content"/>

    <comment-view target="#blog" :comments="comments" :sort="commentSort"/>

    <infinite-loading class="loading"
                      target="#blog"
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
.blog {
  height: 100%;
  width: 100%;
  overflow-x: hidden;
  overflow-y: scroll;
  overscroll-behavior-x: none;
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
    color: var(--primary-color);
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
  word-break: break-all;

  img {
    min-width: 120px;
  }
}

.tip {
  font-weight: bold;
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
    background: var(--primary-color);
  }
}

</style>