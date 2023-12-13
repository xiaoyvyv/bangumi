<script setup lang="ts">

import {onMounted, ref, toRefs} from "vue";

const props = defineProps({
  message: {
    type: String,
    required: true
  }
});

const {message} = toRefs(props);
const robot = ref<HTMLElement>();
const robotClasses = ['robot-1', 'robot-2', 'robot-3', 'robot-4', 'robot-5', 'robot-6'];

onMounted(() => {
  const randomIndex = Math.floor(Math.random() * robotClasses.length);
  return robot.value?.classList.add(robotClasses[randomIndex]);
})
</script>

<template>
  <div class="robot">
    <div style="flex: 1"/>
    <div class="robot-pic" ref="robot"/>
    <div class="message">{{ message }}</div>
  </div>
</template>

<style scoped lang="scss">
.robot {
  width: 100%;
  height: 400px;
  display: flex;
  flex-flow: column nowrap;
  align-items: center;

  .message {
    padding: 16px 60px;
    font-size: 12px;
    color: var(--on-surface-variant-color);
    text-align: center;
  }

  .robot-pic {
    border-radius: 0 !important;
    border: none !important;
    margin: 0 auto;
    background: #333333;
  }

  .robot-pic {
    left: -40px;
    background: url('../assets/image/bg_bangumi.png') 0 -10px no-repeat;
    background-size: 280px 75px;
    width: 40px;
    height: 50px
  }

  .robot-1 {
    background-position: -40px 0
  }

  .robot-2 {
    background-position: -80px 0
  }

  .robot-3 {
    background-position: -120px 0
  }

  .robot-4 {
    background-position: -160px 0
  }

  .robot-5 {
    background-position: -200px 0
  }

  .robot-6 {
    background-position: -240px 0
  }
}
</style>