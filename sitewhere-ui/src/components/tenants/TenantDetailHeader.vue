<template>
  <div>
    <v-card v-if="tenant" color="white" class="grey--text">
      <v-layout class="pa-2" row>
        <v-flex xs3>
          <v-card-media :src="tenant.logoUrl" height="60px" contain>
          </v-card-media>
        </v-flex>
        <v-flex xs9>
          <v-card-title class="pa-3" primary-title>
            <div class="headline">{{ tenant.name }}</div>
          </v-card-title>
        </v-flex>
      </v-layout>
    </v-card>
    <tenant-update-dialog ref="update" :tenantId="tenant.id"
      @tenantUpdated="onTenantEdited">
    </tenant-update-dialog>
    <tenant-delete-dialog ref="delete" :tenantId="tenant.id"
      @tenantDeleted="onTenantDeleted">
    </tenant-delete-dialog>
  </div>
</template>

<script>
import Utils from '../common/Utils'
import HeaderField from '../common/HeaderField'
import TenantUpdateDialog from './TenantUpdateDialog'
import TenantDeleteDialog from './TenantDeleteDialog'

export default {

  data: () => ({
    copyData: null,
    showIdCopied: false
  }),

  props: ['tenant'],

  components: {
    HeaderField,
    TenantUpdateDialog,
    TenantDeleteDialog
  },

  methods: {
    // Format date.
    formatDate: function (date) {
      return Utils.formatDate(date)
    },
    // Called to edit tenant.
    onEditTenant: function () {
      this.$refs['update'].onOpenDialog()
    },
    // Called after tenant is edited.
    onTenantEdited: function () {
      this.$emit('refresh')
    },
    // Called to delete tenant.
    onDeleteTenant: function () {
      this.$refs['delete'].showDeleteDialog()
    },
    // Called after tenant is deleted.
    onTenantDeleted: function () {
      this.$router.push('/system/tenants')
    }
  }
}
</script>

<style scoped>
.progress {
  position: absolute;
  bottom: 0px;
  left: 0px;
  right: 0px;
}
</style>
