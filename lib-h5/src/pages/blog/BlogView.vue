<script setup lang="ts">
import {nextTick, onMounted, ref} from "vue";

export interface BlogDetailEntity {
  id: string;
  title: string;
  time: string;
  userAvatar: string;
  userName: string;
  userId: string;
  content: string;
}

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
        if (imageSrc != undefined && imageSrc.length > 0) {
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

onMounted(() => {
  window.blog = blogHandler;
  window.mounted = true;
});
</script>

<template>
  <div class="blog">
    <div class="blog-title">
      {{ blogRef?.title }}
    </div>
    <div class="blog-info">
      <div class="blog-author">{{ blogRef?.userName }}</div>
      <div class="blog-time">{{ blogRef?.time }}</div>
    </div>
    <div class="blog-content" ref="blogContentRef" v-html="optText(blogRef?.content)"/>
  </div>
</template>

<style lang="scss">
.blog {
  height: 100%;
  width: 100%;
  overflow-x: hidden;
  overflow-y: scroll;
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
    max-width: 100%;
    margin: 12px 0;
    border-radius: 6px;
  }

  img[smileid] {
    vertical-align: center;
    margin: 0 4px;
    border: 1px solid #ff80ab;
    padding: 1px;
  }
}
</style>