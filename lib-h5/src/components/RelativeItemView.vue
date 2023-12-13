<script setup lang="ts">
import {toRefs} from "vue";
import {RelateItem, SampleRelateEntity} from "../util/interface/SampleRelateEntity.ts";
import ImageView from "./ImageView.vue";

const props = defineProps({
  related: {
    type: Object as () => SampleRelateEntity,
    required: false
  }
});

const {related} = toRefs(props);

/**
 * 点击相关的条目
 *
 * @param item
 */
const onClickRelated = (item: RelateItem) => {
  window.android && window.android.onClickRelated(JSON.stringify(item));
};
</script>

<template>
  <div class="relative" v-if="related && related.items.length">
    <div class="relative-subject">
      <div class="tip">{{ related.title }}</div>
      <div class="relative" v-for="item in related.items" @click.stop="onClickRelated(item)">
        <image-view class="cover" height="44px" width="44px" :src="item.image"/>
        <div class="title"># {{ item.title }}</div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.relative {
  width: 100%;
  display: flex;
  flex-flow: column nowrap;

  .relative-subject {
    margin: 16px 16px;
    padding: 12px;
    border-radius: 6px;
    background-color: var(--surface-container-color);

    .relative {
      width: 100%;
      display: flex;
      flex-flow: row nowrap;
      align-items: center;
      margin-top: 6px;
    }

    .cover {
      margin: 6px 0;
      border-radius: 6px;
      background-color: var(--surface-container-color);
    }

    .title {
      width: 0;
      flex: 1;
      padding: 2px 6px 4px 12px;
      color: var(--primary-color);
    }
  }
}
</style>