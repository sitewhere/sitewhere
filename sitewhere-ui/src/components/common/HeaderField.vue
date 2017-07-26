<template>
  <div class="header-row">
    <span class="header-label">
      {{ label }}:
    </span>
    <span class="header-content">
      <slot></slot>
    </span>
  </div>
</template>

<script>

export default {

  data: () => ({
  }),

  props: ['label'],

  methods: {
  }
}
</script>

<style scoped>
.header-row {
  height: 27px;
}
.header-label {
  min-width: 160px;
  font-weight: 700;
  font-size: 14px;
  display: inline-block;
  vertical-align: top;
}
.header-content {
  font-size: 14px;
  vertical-align: top;
  position: relative;
}
</style>
