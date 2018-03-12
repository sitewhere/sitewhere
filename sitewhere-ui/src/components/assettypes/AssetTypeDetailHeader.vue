<template>
  <navigation-header-panel v-if="assetType" :imageUrl="assetType.imageUrl"
    :qrCodeUrl="qrCodeUrl" height="190px">
    <span slot="content">
      <header-field label="Token">
        <clipboard-copy-field :field="assetType.token"
          message="Token copied to clipboard">
        </clipboard-copy-field>
      </header-field>
      <header-field label="Name">
        <span>{{ assetType.name }}</span>
      </header-field>
      <header-field label="Description">
        <span>{{ assetType.description }}</span>
      </header-field>
      <header-field label="Created">
        <span>{{ formatDate(assetType.createdDate) }}</span>
      </header-field>
      <header-field label="Updated">
        <span>{{ formatDate(assetType.updatedDate) }}</span>
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

  props: ['assetType'],

  components: {
    NavigationHeaderPanel,
    HeaderField,
    ClipboardCopyField
  },

  computed: {
    // Compute QR code URL.
    qrCodeUrl: function () {
      return 'assettypes/' + this.assetType.token + '/label/qrcode'
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
.assettype {
  min-height: 180px;
  min-width: 920px;
  overflow-y: hidden;
}

.assettype-logo {
  position: absolute;
  top: 10px;
  left: 7px;
  bottom: 7px;
  width: 180px;
}

.assettype-qrcode {
  position: absolute;
  top: 10px;
  right: 7px;
  bottom: 7px;
  width: 180px;
}

.assettype-headers {
  position: absolute;
  top: 20px;
  left: 200px;
  right: 200px;
}

.options-menu {
  position: absolute;
  top: 10px;
  right: 190px;
}
</style>
