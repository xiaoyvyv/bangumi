<script setup lang="ts">
import {toRefs} from "vue";
import {LikeActionEntity} from "../util/interface/LikeActionEntity.ts";
import {CommentTreeEntity} from "../util/interface/CommentTreeEntity.ts";

const props = defineProps({
  emojis: {
    type: Array<LikeActionEntity>,
    required: true,
  },
  comment: {
    type: Object,
    required: true
  }
});

const {emojis, comment} = toRefs(props);

/**
 * 构建表情
 *
 * @param likeValue 和 bgm tv 表情编号差值 39，0 除外
 */
const buildEmoji = (likeValue: string) => {
  try {
    const value = parseInt(likeValue);
    if (value === 0) return "https://bgm.tv/img/smiles/tv/44.gif";
    return `https://bgm.tv/img/smiles/tv/${(value - 39)}.gif`;
  } catch (e) {
    return ""
  }
}

/**
 * 开关 smail
 *
 * @param item
 */
const toggleSmile = (item: LikeActionEntity) => {
  const cmt = comment?.value as CommentTreeEntity;
  window.android.onToggleSmile(cmt.id, cmt.gh, JSON.stringify(item));
};
</script>

<template>
  <div class="emoji-container">
    <div class="emoji"
         :class="{'selected': item.selected}"
         v-for="item in emojis"
         :key="item.value"
         @click.stop="toggleSmile(item)">
      <img class="smile" :src="buildEmoji(item.value)" alt="smile">
      <span class="user-count">{{ (item as LikeActionEntity).users.length }}</span>
    </div>
  </div>
</template>
<style scoped lang="scss">
.emoji-container {
  width: fit-content;
  display: flex;
  flex-flow: row wrap;

  .emoji {
    width: fit-content;
    display: flex;
    flex-flow: row nowrap;
    align-items: center;
    padding: 4px 12px;
    margin: 6px 12px 6px 0;
    border-radius: 100px;
    background: var(--surface-container-color);

    &.selected {
      background: #ffc4d7;

      span {
        color: var(--primary-color);
      }
    }

    .smile {
      height: 20px;
      width: 20px;
      line-height: 20px;
      margin: 0 !important;
      padding: 1px;
      border: none !important;
      background: transparent !important;
      border-radius: 0 !important;
    }

    .user-count {
      color: var(--on-surface-variant-color);
      padding: 0 0 0 6px;
      line-height: 20px;
      font-size: 14px;
    }
  }
}
</style>