<template>
  <v-card hover class="area white pa-2">
    <v-card-text @click="onOpenArea">
      <div class="area-logo"
        v-bind:style="{ 'background': 'url(' + area.imageUrl + ')', 'background-size': 'cover', 'background-repeat': 'no-repeat', 'background-position': '50% 50%'}">
      </div>
      <div class="area-name">{{area.name}}</div>
      <div class="area-desc">{{area.description}}</div>
    </v-card-text>
  </v-card>
</template>

<script>
export default {

  data: () => ({
  }),

  props: ['area'],

  methods: {
    // Called when an area is clicked.
    onOpenArea: function () {
      this.$emit('areaOpened', this.area.token)
    }
  }
}
</script>

<style scoped>
.area {
  min-height: 180px;
  overflow-y: hidden;
}

.area-logo {
  position: absolute;
  top: 0px;
  left: 0px;
  bottom: 0px;
  width: 140px;
}

.area-name {
  position: absolute;
  top: 5px;
  left: 158px;
  right: 10px;
  font-size: 22px;
  font-weight: 400;
  white-space: nowrap;
  overflow-x: hidden;
}

.area-desc {
  position: absolute;
  top: 42px;
  left: 160px;
  right: 10px;
  bottom: 10px;
  font-size: 14px;
  overflow-y: hidden;
}
</style>
