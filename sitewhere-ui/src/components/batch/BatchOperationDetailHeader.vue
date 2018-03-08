<template>
  <navigation-header-panel v-if="operation" icon="fa-cogs"
    :qrCodeUrl="qrCodeUrl" height="200px">
    <span slot="content">
      <header-field label="Token">
        <clipboard-copy-field :field="operation.token"
          message="Operation token copied to clipboard">
        </clipboard-copy-field>
      </header-field>
      <header-field label="Operation type">
        <span>{{ operation.operationType }}</span>
      </header-field>
      <header-field label="Status">
        <span>{{ operation.processingStatus }}</span>
      </header-field>
      <header-field label="Created">
        <span>{{ formatDate(operation.createdDate) }}</span>
      </header-field>
      <header-field label="Processing Started">
        <span>{{ formatDate(operation.processingStartedDate) }}</span>
      </header-field>
      <header-field label="Processing Finished">
        <span>{{ formatDate(operation.processingEndedDate) }}</span>
      </header-field>
    </span>
  </navigation-header-panel>
</template>

<script>
import Utils from '../common/Utils'
import NavigationHeaderPanel from '../common/NavigationHeaderPanel'
import HeaderField from '../common/HeaderField'
import ClipboardCopyField from '../common/ClipboardCopyField'
import {createCoreApiUrl} from '../../http/sitewhere-api-wrapper'

export default {

  data: function () {
    return {
    }
  },

  components: {
    NavigationHeaderPanel,
    HeaderField,
    ClipboardCopyField
  },

  props: ['operation'],

  computed: {
    // Compute QR code URL.
    qrCodeUrl: function () {
      var tenant = this.$store.getters.selectedTenant
      return createCoreApiUrl(this.$store) +
        'batch/' + this.operation.token +
        '/symbol?tenantAuthToken=' + tenant.authenticationToken
    }
  },

  methods: {
    // Fire event to have parent refresh content.
    refresh: function () {
      this.$emit('refresh')
    },

    // Format date.
    formatDate: function (date) {
      return Utils.formatDate(date)
    }
  }
}
</script>

<style scoped>
</style>
