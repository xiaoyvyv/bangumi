<script setup lang="ts">
import {toRefs} from "vue";

const props = defineProps({
  show: {
    type: Boolean,
    default: true,
  }
});

const {show} = toRefs(props);

</script>

<template>
  <div class="spinner-container" v-if="show">
    <div class="spinner"></div>
  </div>
</template>

<style scoped lang="scss">
.spinner-container {
  display: flex;
  flex-flow: column;
  align-items: center;
  height: 60px;
  width: 100%;
}

.spinner {
  border: 2px solid rgba(0, 0, 0, 0.1);
  border-top: 2px solid var(--primary-color);
  border-radius: 50%;
  width: 24px;
  height: 24px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}
</style>