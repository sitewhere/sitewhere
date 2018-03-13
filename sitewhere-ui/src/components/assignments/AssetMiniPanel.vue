<template>
  <div>
    <div v-if="assignment.asset" class="assn-asset-image"
      :style="backgroundImageStyle(assignment.asset.imageUrl)"></div>
    <div v-else-if="assignment.area" class="assn-asset-image"
      :style="backgroundImageStyle(assignment.area.imageUrl)"></div>
    <div v-else class="assn-asset-image"
      :style="backgroundImageStyle(assignment.device.imageUrl)"></div>
    <div v-if="assignment.asset" class="assn-asset-title">
      {{ assignment.asset.name }}
    </div>
    <div v-else-if="assignment.area" class="assn-asset-title">
      {{ assignment.area.name }}
    </div>
    <div v-if="assignment.asset" class="assn-asset-value1">
      {{ assignment.asset.token }}
    </div>
    <div v-else-if="assignment.area" class="assn-asset-value1">
      {{ assignment.area.token }}
    </div>

    <div v-if="(assignment.assignmentType == 'Unassociated') && (assignment.device)" class="assn-asset-image"
      :style="backgroundImageStyle(assignment.device.assetImageUrl)"></div>
      <div v-if="assignment.assignmentType == 'Unassociated'" class="assn-asset-title">
        Unassociated Device
      </div>
    <div v-if="assignment.asset" class="assn-asset-tag">Asset</div>
    <div v-else-if="assignment.area" class="assn-asset-tag">Area</div>
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
    }
  }
}
</script>

<style scoped>
.assn-asset-image {
  position: absolute;
  top: 8px;
  left: 10px;
  width: 70px;
	height: 70px;
  border: 1px solid #eee;
}
.assn-asset-title {
  position: absolute;
  top: 6px;
  left: 90px;
  font-size: 16px;
  color: #333;
  font-weight: 700;
  white-space: nowrap;
}
.assn-asset-value1 {
  position: absolute;
  top: 30px;
  left: 90px;
  font-size: 12px;
  color: #333;
  white-space: nowrap;
}
.assn-asset-label2 {
  position: absolute;
  top: 58px;
  left: 90px;
  font-size: 12px;
  color: #333;
  font-weight: 700;
  white-space: nowrap;
}
.assn-asset-value2 {
  position: absolute;
  top: 58px;
  left: 135px;
  font-size: 12px;
  color: #333;
  white-space: nowrap;
}
.assn-asset-tag {
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
