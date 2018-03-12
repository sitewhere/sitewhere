<template>
  <navigation-header-panel v-if="device" :imageUrl="device.deviceType.imageUrl"
    :qrCodeUrl="qrCodeUrl" height="190px">
    <span slot="content">
      <header-field label="Token">
        <clipboard-copy-field :field="device.token"
          message="Token copied to clipboard">
        </clipboard-copy-field>
      </header-field>
      <linked-header-field label="Device Type"
        :text="device.deviceType.name"
        :url="'/devicetypes/' + device.deviceType.token">
      </linked-header-field>
      <linked-header-field v-if="device.assignment" label="Assignment"
        :text="device.assignment.assetName"
        :url="'/assignments/' + device.assignment.token">
      </linked-header-field>
      <header-field v-else label="Assignment">
        <span>Device is not assigned</span>
      </header-field>
      <header-field label="Comments">
        <span>{{ device.comments }}</span>
      </header-field>
      <header-field label="Created">
        <span>{{ formatDate(device.createdDate) }}</span>
      </header-field>
      <header-field label="Updated">
        <span>{{ formatDate(device.updatedDate) }}</span>
      </header-field>
    </span>
  </navigation-header-panel>
</template>

<script>
import Utils from '../common/Utils'
import NavigationHeaderPanel from '../common/NavigationHeaderPanel'
import ClipboardCopyField from '../common/ClipboardCopyField'
import HeaderField from '../common/HeaderField'
import LinkedHeaderField from '../common/LinkedHeaderField'

export default {

  data: () => ({
  }),

  props: ['device'],

  components: {
    NavigationHeaderPanel,
    ClipboardCopyField,
    HeaderField,
    LinkedHeaderField
  },

  computed: {
    // Compute QR code URL.
    qrCodeUrl: function () {
      return 'devices/' + this.device.token + '/label/qrcode'
    }
  },

  methods: {
    // Format date.
    formatDate: function (date) {
      return Utils.formatDate(date)
    }
  }
}
</script>

<style scoped>
