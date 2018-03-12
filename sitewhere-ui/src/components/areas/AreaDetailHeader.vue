<template>
  <navigation-header-panel v-if="area" :imageUrl="area.imageUrl"
    :qrCodeUrl="qrCodeUrl" height="200px">
    <span slot="content">
      <header-field label="Token">
        <clipboard-copy-field :field="area.token"
          message="Token copied to clipboard">
        </clipboard-copy-field>
      </header-field>
      <linked-header-field label="Area Type" :text="area.areaType.name"
        :url="'/areatypes/' + area.areaType.token">
      </linked-header-field>
      <header-field label="Name">
        <span>{{ area.name }}</span>
      </header-field>
      <header-field label="Description">
        <span>{{ area.description }}</span>
      </header-field>
      <header-field label="Created">
        <span>{{ formatDate(area.createdDate) }}</span>
      </header-field>
      <header-field label="Updated">
        <span>{{ formatDate(area.updatedDate) }}</span>
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

  props: ['area'],

  components: {
    NavigationHeaderPanel,
    HeaderField,
    LinkedHeaderField,
    ClipboardCopyField
  },

  computed: {
    // Compute QR code URL.
    qrCodeUrl: function () {
      return 'areas/' + this.area.token + '/label/qrcode'
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
