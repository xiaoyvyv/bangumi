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
 * @param emoji
 */
const buildEmoji = (emoji: string) => {
  const map: { [key: string]: string; } = {
    "0": "https://bgm.tv/img/smiles/tv/44.gif",
    "79": "https://bgm.tv/img/smiles/tv/40.gif",
    "54": "https://bgm.tv/img/smiles/tv/15.gif",
    "140": "https://bgm.tv/img/smiles/tv/101.gif",
    "62": "https://bgm.tv/img/smiles/tv/23.gif",
    "122": "https://bgm.tv/img/smiles/tv/83.gif",
    "104": "https://bgm.tv/img/smiles/tv/65.gif",
    "80": "https://bgm.tv/img/smiles/tv/41.gif",
    "141": "https://bgm.tv/img/smiles/tv/102.gif",
    "88": "https://bgm.tv/img/smiles/tv/49.gif",
    "85": "https://bgm.tv/img/smiles/tv/46.gif",
    "90": "https://bgm.tv/img/smiles/tv/51.gif"
  }
  return map[emoji.toString()];
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
    margin: 6px 0;
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