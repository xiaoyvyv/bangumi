<script setup lang="ts">
import {onMounted, ref} from "vue";
import {PostInfoEntity} from "../../util/interface/PostInfoEntity.ts";
import RelativeItemView from "../../components/RelativeItemView.vue";

const blog = ref<PostInfoEntity>({} as PostInfoEntity);

const blogPostHandler = {
  setPostInfo: (newPostInfo: PostInfoEntity) => {
    newPostInfo.filled = true;

    if (newPostInfo.userName.length === 0) {
      newPostInfo.userName = "未登录"
    }
    blog.value = newPostInfo;
  },
  /*
    addRelated: (related: SampleRelateEntity) => {
      // if (related.id == null || related.id.length === 0) return;
      // const tmp = postInfo.value;
      // const added = tmp.related || [];
      //
      // for (let i = 0; i < added.length; i++) {
      //   if (related.id == added[i].id) return;
      // }
      // added.push(related);
      // tmp.related = added;
      // postInfo.value = tmp
    },*/
  getPostInfo: (): string => {
    return JSON.stringify(blog.value);
  },
}

const clickPublic = (isPublic: boolean) => {
  const tmp = blog.value;
  tmp.isPublic = isPublic;
  blog.value = tmp
}

onMounted(() => {
  window.blogPost = blogPostHandler;
  window.mounted = true;
});
</script>

<template>
  <div class="blog-post" v-if="blog.filled === true">
    <div class="blog-post-title">{{ blog.pageName || "发表内容" }}</div>
    <div class="blog-post-info">
      <div class="blog-post-author">{{ blog.userName }}</div>
      <div class="blog-post-time">{{ blog.time }}</div>
    </div>
    <div class="blog-post-form">
      <div class="title">标题</div>
      <input name="title" type="text" class="input" placeholder="请输入标题" v-model="blog.title">
      <div class="title">正文内容<span class="sub-tip">支持BBCode</span></div>
      <textarea name="content" class="textarea" rows="8" placeholder="请输入内容" v-model="blog.content"></textarea>
      <div class="title">标签</div>
      <input name="tags" type="text" class="input" placeholder="请输入标签，多个空格分割" v-model="blog.tags">
      <div class="title">隐私设置</div>
      <div class="blog-post-ratio">
        <div class="ratio" :class="{'selected': blog.isPublic }" @click.stop="clickPublic(true)">公开</div>
        <div class="ratio" :class="{'selected': !blog.isPublic }" @click.stop="clickPublic(false)">仅好友可见</div>
      </div>
      <div class="title">关联</div>
    </div>

    <RelativeItemView :related="blog.related"/>
  </div>
</template>

<style scoped lang="scss">
.blog-post {
  height: 100%;
  width: 100%;
  overflow-x: hidden;
  overflow-y: scroll;
  user-select: none !important;
  padding-bottom: 300px;

  * {
    user-select: none !important;
  }

  .sub-tip {
    padding: 12px;
    opacity: 0.5;
    font-size: 75%;
    color: var(--primary-color);
  }
}

.blog-post-title {
  font-weight: 800 !important;
  font-size: 20px;
  margin: 12px 16px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  overflow: hidden;
  -webkit-box-orient: vertical;
  text-overflow: ellipsis;
}

.blog-post-info {
  display: flex;
  flex-direction: row;
  align-items: center;
  font-size: 15px;
  margin-left: 16px;

  .blog-post-author {
    color: var(--primary-color);
    margin-right: 6px;
  }

  .blog-post-time {
    color: #888888;
  }
}

.blog-post-form {
  width: 100%;
  padding: 0 16px;
  margin-top: 16px;

  .title {
    font-size: 18px;
    font-weight: bold;
    margin-bottom: 16px;
    margin-top: 6px;
  }

  .input {
    width: 100%;
    padding: 8px;
    font-size: 16px;
    border: none !important;
    outline: none !important;
    background-color: #cccccc3f;
    border-radius: 6px;
    margin-bottom: 12px;
  }

  .textarea {
    width: 100%;
    padding: 8px;
    font-size: 16px;
    border: none !important;
    outline: none !important;
    background-color: #cccccc3f;
    border-radius: 6px;
    margin-bottom: 12px;
    resize: none;
  }
}

.blog-post-ratio {
  display: flex;
  flex-flow: row nowrap;
  align-items: center;
  margin-bottom: 16px;

  .ratio {
    font-size: 14px;
    border-radius: 6px;
    background: #cccccc7f;
    padding: 6px;
    margin-right: 12px;
  }

  .ratio.selected {
    color: white;
    background: var(--primary-color);
  }
}
</style>