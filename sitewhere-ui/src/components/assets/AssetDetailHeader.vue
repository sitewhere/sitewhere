<template>
  <navigation-header-panel v-if="asset" :imageUrl="asset.imageUrl"
    :qrCodeUrl="qrCodeUrl" height="190px">
    <span slot="content">
      <header-field label="Token">
        <clipboard-copy-field :field="asset.token"
          message="Token copied to clipboard">
        </clipboard-copy-field>
      </header-field>
      <header-field label="Name">
        <span>{{ asset.name }}</span>
      </header-field>
      <linked-header-field label="Asset Type"
        :text="asset.assetType.name"
        :url="'/assettypes/' + asset.assetType.token">
      </linked-header-field>
      <header-field label="Image URL">
        <span>{{ asset.imageUrl }}</span>
      </header-field>
      <header-field label="Created">
        <span>{{ formatDate(asset.createdDate) }}</span>
      </header-field>
      <header-field label="Updated">
        <span>{{ formatDate(asset.updatedDate) }}</span>
      </header-field>
    </span>
  </navigation-header-panel>
</template>

<script>
import Utils from '../common/Utils'
import NavigationHeaderPanel from '../common/NavigationHeaderPanel'
import HeaderField from '../common/HeaderField'
import LinkedHeaderField from '../common/LinkedHeaderField'
import ClipboardCopyField from '../common/ClipboardCopyField'

export default {

  data: () => ({
  }),

  props: ['asset'],

  components: {
    NavigationHeaderPanel,
    HeaderField,
    LinkedHeaderField,
    ClipboardCopyField
  },

  computed: {
    // Compute QR code URL.
    qrCodeUrl: function () {
      return 'assets/' + this.asset.token + '/label/qrcode'
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
.asset {
  min-height: 200px;
  min-width: 920px;
  overflow-y: hidden;
}

.asset-logo {
  position: absolute;
  top: 10px;
  left: 7px;
  bottom: 7px;
  width: 180px;
}

.asset-qrcode {
  position: absolute;
  top: 10px;
  right: 7px;
  bottom: 7px;
  width: 180px;
}

.asset-headers {
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
