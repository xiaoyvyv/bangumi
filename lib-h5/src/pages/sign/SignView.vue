<script setup lang="ts">
import {onMounted, ref} from "vue";
import {UserSignEntity} from "../../util/interface/entity.ts";

const sign = ref("");

const signHandler = {
  setSign: (signObj: UserSignEntity) => {
    sign.value = signObj.sign;
  }
}

onMounted(() => {
  window.sign = signHandler;
  window.mounted = true;
});

</script>

<template>
  <div class="sign">
    <div class="sign-html" v-html="sign"></div>
  </div>
</template>

<style scoped lang="scss">
.sign {
  height: 100%;
  width: 100%;
  overflow-x: hidden;
  overflow-y: scroll;
  color: #333333;

  .sign-html {
    padding: 16px 16px;
  }
}
</style>