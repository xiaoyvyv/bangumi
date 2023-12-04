<script setup lang="ts">
import {onMounted, ref} from "vue";
import {MediaRelative, PostInfoEntity} from "../../util/interface/entity.ts";

const postInfo = ref<PostInfoEntity>({} as PostInfoEntity);

const blogPostHandler = {
  setPostInfo: (newPostInfo: PostInfoEntity) => {
    newPostInfo.related = optRelatedList(newPostInfo.related);
    newPostInfo.filled = true;
    if (newPostInfo.userName.length === 0) {
      newPostInfo.userName = "未登录"
    }
    postInfo.value = newPostInfo;
  },
  addRelated: (related: MediaRelative) => {
    if (related.id == null || related.id.length === 0) return;
    const tmp = postInfo.value;
    const added = tmp.related || [];

    for (let i = 0; i < added.length; i++) {
      if (related.id == added[i].id) return;
    }
    added.push(related);
    tmp.related = added;
    postInfo.value = tmp
  },
  getPostInfo: (): string => {
    return JSON.stringify(postInfo.value);
  },
}

const clickPublic = (isPublic: boolean) => {
  const tmp = postInfo.value;
  tmp.isPublic = isPublic;
  postInfo.value = tmp
}
const onImageError = (event: Event) => {
  const img = event.target as HTMLImageElement;
  img.src = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/wcAAwAB/9+p2gAAAABJRU5ErkJggg==";
}

const onClickRelated = (related: MediaRelative) => {
  if (window.android) {
    window.android.onClickRelated(JSON.stringify(related), related.empty)
  }
}

const relatedRealSize = (media: MediaRelative[]) => {
  let count = 0;
  media.forEach(item => {
    if ((item.id || "").length > 0) {
      count++;
    }
  })
  return count;
}

const optRelatedList = (media: MediaRelative[]): MediaRelative[] => {
  const size = relatedRealSize(media);
  if (size == 0) {
    media.push({titleNative: "添加关联", cover: "", empty: true} as MediaRelative);
  }
  return media;
}

onMounted(() => {
  window.blogPost = blogPostHandler;
  window.mounted = true;
});
</script>

<template>
  <div class="blog-post" v-if="postInfo.filled === true">
    <div class="blog-post-title">{{ postInfo.pageName || "发表内容" }}</div>
    <div class="blog-post-info">
      <div class="blog-post-author">{{ postInfo.userName }}</div>
      <div class="blog-post-time">{{ postInfo.time }}</div>
    </div>
    <div class="blog-post-form">
      <div class="title">标题</div>
      <input name="title" type="text" class="input" placeholder="请输入标题" v-model="postInfo.title">
      <div class="title">正文内容<span class="sub-tip">支持BBCode</span></div>
      <textarea name="content" class="textarea" rows="8" placeholder="请输入内容" v-model="postInfo.content"></textarea>
      <div class="title">标签</div>
      <input name="tags" type="text" class="input" placeholder="请输入标签，多个空格分割" v-model="postInfo.tags">
      <div class="title">隐私设置</div>
      <div class="blog-post-ratio">
        <div class="ratio" :class="{'selected': postInfo.isPublic }" @click.stop="clickPublic(true)">公开</div>
        <div class="ratio" :class="{'selected': !postInfo.isPublic }" @click.stop="clickPublic(false)">仅好友可见</div>
      </div>
      <div class="title">关联</div>
    </div>
    <div class="blog-post-relative" v-if="(postInfo.related || []).length > 0">
      <div class="blog-post-relative-subject">
        <div class="tip">关联的条目 {{ relatedRealSize(postInfo.related) }} 个</div>
        <div class="relative" v-for="item in postInfo.related" @click.stop="onClickRelated(item)">
          <img :src="item.cover" alt="img" @error="onImageError($event)">
          <div class="title"># {{ item['titleNative'] }}</div>
        </div>
      </div>
    </div>
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
    color: deepskyblue;
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
    color: deepskyblue;
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

.blog-post-relative {
  width: 100%;

  .blog-post-relative-subject {
    margin: 0 16px 16px 16px;
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
    background: deepskyblue;
  }
}
</style>