<script setup lang="ts">
import {onMounted, ref} from "vue";
import bbobHTML from '@bbob/html'
import presetHTML5 from '@bbob/preset-html5'

const bbcode = ref("");

/**
 * 设置 BBCode
 *
 * @param code
 */
const setCode = (code: any) => {
  bbcode.value = bbobHTML(code["code"] || '', presetHTML5())
}

const bbCodeHandler = {
  setCode: setCode
}

onMounted(() => {
  window.bbcode = bbCodeHandler;
  window.mounted = true;
});
</script>

<template>
  <div class="bbcode">
    <div v-html="bbcode"></div>
  </div>
</template>

<style scoped lang="scss">
.bbcode {
  width: 100%;
  padding: 16px;
  font-size: 16px;
  display: flex;
  flex-direction: column;
  word-break: break-word;
}
</style>