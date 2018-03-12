<template>
  <navigation-header-panel v-if="areaType" :icon="areaType.icon"
    :qrCodeUrl="qrCodeUrl" height="200px">
    <span slot="content">
      <header-field label="Token">
        <clipboard-copy-field :field="areaType.token"
          message="Token copied to clipboard">
        </clipboard-copy-field>
      </header-field>
      <header-field label="Name">
        <span>{{ areaType.name }}</span>
      </header-field>
      <header-field label="Description">
        <span>{{ areaType.description }}</span>
      </header-field>
      <header-field label="Created">
        <span>{{ formatDate(areaType.createdDate) }}</span>
      </header-field>
      <header-field label="Updated">
        <span>{{ formatDate(areaType.updatedDate) }}</span>
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

  props: ['areaType', 'areaTypes'],

  components: {
    NavigationHeaderPanel,
    HeaderField,
    ClipboardCopyField
  },

  computed: {
    // Compute QR code URL.
    qrCodeUrl: function () {
      return 'areatypes/' + this.areaType.token + '/label/qrcode'
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
