<template>
  <div>
    <div v-if="assignment.associatedPerson" class="assn-asset-image"
      :style="backgroundImageStyle(assignment.associatedPerson.imageUrl)"></div>
    <div v-if="assignment.associatedPerson" class="assn-asset-title">
      {{ ellipsis(assignment.associatedPerson.name, charWidth) }}
    </div>
    <div v-if="assignment.associatedPerson" class="assn-asset-label1">Email:</div>
    <div v-if="assignment.associatedPerson" class="assn-asset-value1">
      {{ ellipsis(assignment.associatedPerson.emailAddress, charWidth) }}
    </div>
    <div v-if="assignment.associatedPerson" class="assn-asset-label2">Roles:</div>
    <div v-if="assignment.associatedPerson" class="assn-asset-value2">
      {{ ellipsis(assignment.associatedPerson.roles.join(', '), charWidth)  }}
    </div>

    <div v-if="assignment.associatedHardware" class="assn-asset-image"
      :style="backgroundImageStyle(assignment.associatedHardware.imageUrl)"></div>
    <div v-if="assignment.associatedHardware" class="assn-asset-title">
      {{ ellipsis(assignment.associatedHardware.name, charWidth) }}
    </div>
    <div v-if="assignment.associatedHardware" class="assn-asset-label1">SKU:</div>
    <div v-if="assignment.associatedHardware" class="assn-asset-value1">
      {{ ellipsis(assignment.associatedHardware.sku, charWidth) }}
    </div>
    <div v-if="assignment.associatedHardware" class="assn-asset-label2">Info:</div>
    <div v-if="assignment.associatedHardware" class="assn-asset-value2">
      {{ ellipsis(assignment.associatedHardware.description, charWidth) }}
    </div>

    <div v-if="assignment.associatedLocation" class="assn-asset-image"
      :style="backgroundImageStyle(assignment.associatedLocation.imageUrl)"></div>
    <div v-if="assignment.associatedLocation" class="assn-asset-title">
      {{ ellipsis(assignment.associatedLocation.name, charWidth) }}
    </div>
    <div v-if="assignment.associatedLocation" class="assn-asset-label1">Lat:</div>
    <div v-if="assignment.associatedLocation" class="assn-asset-value1">
      {{ ellipsis(assignment.associatedLocation.latitude, charWidth) }}
    </div>
    <div v-if="assignment.associatedLocation" class="assn-asset-label2">Lon:</div>
    <div v-if="assignment.associatedLocation" class="assn-asset-value2">
      {{ ellipsis(assignment.associatedLocation.longitude, charWidth) }}
    </div>

    <div v-if="(assignment.assignmentType == 'Unassociated') && (assignment.device)" class="assn-asset-image"
      :style="backgroundImageStyle(assignment.device.assetImageUrl)"></div>
      <div v-if="assignment.assignmentType == 'Unassociated'" class="assn-asset-title">
        Unassociated Device
      </div>
    <div v-if="assignment.assignmentType == 'Unassociated'" class="assn-asset-tag">Unassociated</div>
    <div v-else class="assn-asset-tag">Asset</div>
  </div>
</template>

<script>

export default {
  data: () => ({
    charWidth: 27
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
.assn-asset-label1 {
  position: absolute;
  top: 35px;
  left: 90px;
  font-size: 12px;
  color: #333;
  font-weight: 700;
  white-space: nowrap;
}
.assn-asset-value1 {
  position: absolute;
  top: 35px;
  left: 135px;
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
