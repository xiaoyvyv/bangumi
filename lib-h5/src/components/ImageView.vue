<script setup lang="ts">
import {onMounted, toRefs} from 'vue';

const props = defineProps({
  src: {
    type: String
  },
  width: {
    type: String,
    default: "100%"
  },
  height: {
    type: String,
    default: "100%"
  }
});

const {src, width, height} = toRefs(props);

const handleImageError = async (event: Event) => {
  const img = event.currentTarget as HTMLImageElement;
  img.src = "/ic_holder.jpg"
};

onMounted(() => {
  // 在组件挂载后可以执行一些操作
  console.log('Component mounted');
});
</script>

<template>
  <div class="image-container">
    <img
        class="image"
        :src="src"
        alt="Image"
        @error="handleImageError($event)"
    />
  </div>
</template>

<style scoped lang="scss">
.image-container {
  width: v-bind(width);
  height: v-bind(height);
  overflow: hidden;
  display: block;

  img {
    object-fit: cover;
    margin: 0 !important;
    padding: 0 !important;
    border-radius: 0 !important;
    border: none !important;
    width: 100%;
    height: 100%;
    display: block;
    background: var(--surface-container-color);
    flex-shrink: 0;
  }
}

</style>
