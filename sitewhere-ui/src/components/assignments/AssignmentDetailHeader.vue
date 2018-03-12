<template>
  <navigation-header-panel v-if="assignment"
    :imageUrl="assignment.device.deviceType.imageUrl"
    :qrCodeUrl="qrCodeUrl" height="220px">
    <span slot="content">
      <header-field label="Assignment token">
        <clipboard-copy-field :field="assignment.token"
          message="Assignment token copied to clipboard">
        </clipboard-copy-field>
      </header-field>
      <linked-header-field v-if="assignment.asset" label="Assigned asset"
        :text="assignment.assetName"
        :url="'/assets/' + assignment.asset.token">
      </linked-header-field>
      <header-field v-else label="Assigned asset">
        <span>No asset assigned</span>
      </header-field>
      <linked-header-field label="Assigned device"
        :text="assignment.device.deviceType.name"
        :url="'/devices/' + assignment.device.token">
      </linked-header-field>
      <header-field label="Created date">
        <span>{{ formatDate(assignment.createdDate) }}</span>
      </header-field>
      <header-field label="Last updated date">
        <span>{{ formatDate(assignment.updatedDate) }}</span>
      </header-field>
      <header-field label="Active date">
        <span>{{ formatDate(assignment.activeDate) }}</span>
      </header-field>
      <header-field label="Released date">
        <span>{{ formatDate(assignment.releasedDate) }}</span>
      </header-field>
    </span>
  </navigation-header-panel>
</template>

<script>
import Utils from '../common/Utils'
import Style from '../common/Style'
import NavigationHeaderPanel from '../common/NavigationHeaderPanel'
import HeaderField from '../common/HeaderField'
import LinkedHeaderField from '../common/LinkedHeaderField'
import ClipboardCopyField from '../common/ClipboardCopyField'

export default {

  data: () => ({
  }),

  props: ['assignment'],

  components: {
    NavigationHeaderPanel,
    HeaderField,
    LinkedHeaderField,
    ClipboardCopyField
  },

  computed: {
    // Compute card style based on assignment status.
    assignmentStyle: function () {
      let style = Style.styleForAssignmentStatus(this.assignment)
      style['position'] = 'absolute'
      style['left'] = 0
      style['right'] = 0
      style['top'] = 0
      style['bottom'] = 0
      return style
    },
    // Compute QR code URL.
    qrCodeUrl: function () {
      return 'assignments/' + this.assignment.token + '/label/qrcode'
    }
  },

  methods: {
    // Format date.
    formatDate: function (date) {
      return Utils.formatDate(date)
    },
    // Open the assignment emulator.
    onOpenEmulator: function () {
      this.$emit('emulatorOpened')
    }
  }
}
</script>

<style scoped>
</style>
