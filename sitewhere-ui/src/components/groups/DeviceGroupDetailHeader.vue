<template>
  <navigation-header-panel v-if="group" icon="fa-microchip"
    :qrCodeUrl="qrCodeUrl" height="170px">
    <span slot="content">
      <header-field label="Token">
        <clipboard-copy-field :field="group.token"
          message="Token copied to clipboard">
        </clipboard-copy-field>
      </header-field>
      <header-field label="Name">
        <span>{{ group.name }}</span>
      </header-field>
      <header-field label="Description">
        <span>{{ group.description }}</span>
      </header-field>
      <header-field label="Roles">
        <span>{{ rolesView }}</span>
      </header-field>
    </span>
  </navigation-header-panel>
</template>

<script>
import Utils from '../common/Utils'
import NavigationHeaderPanel from '../common/NavigationHeaderPanel'
import ClipboardCopyField from '../common/ClipboardCopyField'
import HeaderField from '../common/HeaderField'
import {createCoreApiUrl} from '../../http/sitewhere-api-wrapper'

export default {

  data: function () {
    return {
    }
  },

  components: {
    NavigationHeaderPanel,
    ClipboardCopyField,
    HeaderField
  },

  props: ['group'],

  computed: {
    rolesView: function () {
      return this.group.roles.join(', ')
    },
    // Compute QR code URL.
    qrCodeUrl: function () {
      var tenant = this.$store.getters.selectedTenant
      return createCoreApiUrl(this.$store) +
        'groups/' + this.group.token +
        '/symbol?tenantAuthToken=' + tenant.authenticationToken
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
