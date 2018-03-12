<template>
  <navigation-header-panel v-if="deviceType" :imageUrl="deviceType.imageUrl"
    :qrCodeUrl="qrCodeUrl" height="220px">
    <span slot="content">
      <header-field label="Token">
        <clipboard-copy-field :field="deviceType.token"
          message="Token copied to clipboard">
        </clipboard-copy-field>
      </header-field>
      <header-field label="Name">
        <span>{{ deviceType.name }}</span>
      </header-field>
      <header-field label="Description">
        <span>{{ deviceType.description }}</span>
      </header-field>
      <header-field label="Image URL">
        <span>{{ deviceType.imageUrl }}</span>
      </header-field>
      <header-field label="Container Policy">
        <span>{{ deviceType.containerPolicy }}</span>
      </header-field>
      <header-field label="Created">
        <span>{{ formatDate(deviceType.createdDate) }}</span>
      </header-field>
      <header-field label="Updated">
        <span>{{ formatDate(deviceType.updatedDate) }}</span>
      </header-field>
    </span>
  </navigation-header-panel>
</template>

<script>
import Utils from '../common/Utils'
import NavigationHeaderPanel from '../common/NavigationHeaderPanel'
import HeaderField from '../common/HeaderField'
import ClipboardCopyField from '../common/ClipboardCopyField'

export default {

  data: () => ({
  }),

  props: ['deviceType'],

  components: {
    NavigationHeaderPanel,
    HeaderField,
    ClipboardCopyField
  },

  computed: {
    // Compute QR code URL.
    qrCodeUrl: function () {
      return 'devicetypes/' + this.deviceType.token + '/label/qrcode'
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
</style>
