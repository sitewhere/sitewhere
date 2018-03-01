<template>
  <div>
    <div v-if="device">
      <span class="assn-device-image" :style="imageStyle"></span>
      <div class="assn-device-title ellipsis">
        {{ device.deviceType.name }}
      </div>
      <div class="assn-device-value1 ellipsis">
        {{ device.token }}
      </div>
      <div class="assn-device-value2 ellipsis">
        {{ device.comments  }}
      </div>
      <div class="assn-device-tag">Device</div>
    </div>
    <div v-else>
      <div>
        <span class="assn-device-missing">
          <v-icon class="red--text">warning</v-icon> Referenced device does not exist!
        </span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data: () => ({
  }),

  props: ['device'],

  computed: {
    // Create background image style.
    imageStyle: function () {
      return {
        'background-color': '#fff',
        'background-image': 'url(' + this.device.deviceType.imageUrl + ')',
        'background-size': 'contain',
        'background-repeat': 'no-repeat',
        'background-position': '50% 50%',
        'border': '1px solid #eee'
      }
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
	bottom: 10px;
}
.assn-device-title {
  position: absolute;
  top: 6px;
  left: 90px;
  font-size: 16px;
  color: #333;
  font-weight: 700;
}
.assn-device-value1 {
  position: absolute;
  top: 35px;
  left: 90px;
  right: 10px;
  font-size: 12px;
  color: #333;
}
.assn-device-value2 {
  position: absolute;
  top: 58px;
  left: 90px;
  right: 10px;
  font-size: 12px;
  color: #333;
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
.assn-device-missing {
  position: absolute;
  top: 30px;
  left: 40px;
  font-size: 14px;
  font-weight: 500;
}
</style>
