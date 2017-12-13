<template>
  <div>
    <v-card v-if="tenant" color="white" class="grey--text">
      <v-container fluid grid-list-lg>
        <v-layout row>
          <v-flex xs3>
            <v-card-media :src="tenant.logoUrl" height="50px" contain>
            </v-card-media>
          </v-flex>
          <v-flex xs9>
            <div>
              <div class="headline">{{ tenant.name }}</div>
              <div>{{ tenant.id }}</div>
            </div>
          </v-flex>
        </v-layout>
      </v-container>
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

  props: ['tenant', 'tenantCommandRunning', 'tenantCommandPercent'],

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
