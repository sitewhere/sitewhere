<template>
  <div>
    <div v-if="assignment.device">
      <div class="assn-device-image"
        :style="backgroundImageStyle(assignment.device.assetImageUrl)"></div>
      <div class="assn-device-title">
        {{ ellipsis(assignment.device.assetName, 25) }}
      </div>
      <div class="assn-device-label1">Id:</div>
      <div class="assn-device-value1">
        {{ ellipsis(assignment.device.hardwareId, 25) }}
      </div>
      <div class="assn-device-label2">Info:</div>
      <div class="assn-device-value2">
        {{ ellipsis(assignment.device.comments, 25)  }}
      </div>
      <div class="assn-device-tag">Device</div>
    </div>
    <div v-else>
    </div>
  </div>
</template>

<script>

export default {
  data: () => ({
  }),

  props: ['assignment'],

  methods: {
    // Create background image style.
    backgroundImageStyle: function (image) {
      return {
        'background-image': 'url(' + image + ')',
        'background-size': 'contain',
        'background-repeat': 'no-repeat',
        'background-position': '50% 50%'
      }
    },
    // Shortened string with ellipsis.
    ellipsis: function (val, max) {
      return (val.length > max) ? (val.substring(0, max) + '...') : val
    }
  }
}
</script>

<style scoped>
.assn-device-image {
  position: absolute;
  top: 8px;
  left: 10px;
  width: 70px;
	height: 70px;
  border: 1px solid #eee;
}
.assn-device-title {
  position: absolute;
  top: 6px;
  left: 90px;
  font-size: 16px;
  color: #333;
  font-weight: 700;
  white-space: nowrap;
  overflow-x: hidden;
}
.assn-device-label1 {
  position: absolute;
  top: 35px;
  left: 90px;
  font-size: 12px;
  color: #333;
  font-weight: 700;
  white-space: nowrap;
  overflow-x: hidden;
}
.assn-device-value1 {
  position: absolute;
  top: 35px;
  left: 135px;
  font-size: 12px;
  color: #333;
  white-space: nowrap;
  overflow-x: hidden;
}
.assn-device-label2 {
  position: absolute;
  top: 58px;
  left: 90px;
  font-size: 12px;
  color: #333;
  font-weight: 700;
  white-space: nowrap;
  overflow-x: hidden;
}
.assn-device-value2 {
  position: absolute;
  top: 58px;
  left: 135px;
  font-size: 12px;
  color: #333;
  white-space: nowrap;
  overflow-x: hidden;
}
.assn-device-tag {
  position: absolute;
	top: 3px;
	left: 3px;
  background-color: #3a87ad;
  color: #f0f0f0;
  font-size: 10px;
  padding: 2px 4px;
  border-radius: 3px;
}
</style>
