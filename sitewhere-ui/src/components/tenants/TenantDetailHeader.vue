<template>
  <v-card v-if="tenant" color="white" class="grey--text text--darken-1">
    <v-layout class="pa-3" row>
      <v-flex xs3>
        <v-card-media :src="tenant.logoUrl" height="100%" contain>
        </v-card-media>
      </v-flex>
      <v-flex xs9>
        <v-card-title class="pa-0" primary-title>
          <div class="headline pt-1">{{ tenant.name }}</div>
          <v-spacer></v-spacer>
          <options-menu>
            <v-list slot="options">
              <v-list-tile>
                <v-btn block class="blue white--text" @click="onEditTenant">
                  Edit Tenant
                  <v-spacer></v-spacer>
                  <v-icon class="white--text pl-2">fa-edit</v-icon>
                </v-btn>
              </v-list-tile>
              <v-list-tile>
                <v-btn block class="red darken-2 white--text" @click="onDeleteTenant">
                  Delete Tenant
                  <v-spacer></v-spacer>
                  <v-icon class="white--text pl-2">fa-times</v-icon>
                </v-btn>
              </v-list-tile>
            </v-list>
          </options-menu>
        </v-card-title>
      </v-flex>
    </v-layout>
    <tenant-update-dialog ref="update" :tenantId="tenant.id"
      @tenantUpdated="onTenantEdited">
    </tenant-update-dialog>
    <tenant-delete-dialog ref="delete" :tenantId="tenant.id"
      @tenantDeleted="onTenantDeleted">
    </tenant-delete-dialog>
  </v-card>
</template>

<script>
import HeaderField from '../common/HeaderField'
import OptionsMenu from '../common/OptionsMenu'
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
    OptionsMenu,
    TenantUpdateDialog,
    TenantDeleteDialog
  },

  methods: {
    // Called to edit tenant.
    onEditTenant: function () {
      this.$refs['update'].onOpenDialog()
    },

    // Called after tenant is edited.
    onTenantEdited: function () {
      this.$emit('tenantUpdated')
    },

    // Called to delete tenant.
    onDeleteTenant: function () {
      this.$refs['delete'].showDeleteDialog()
    },

    // Called after tenant is deleted.
    onTenantDeleted: function () {
      this.$emit('tenantDeleted')
    }
  }
}
</script>

<style scoped>
</style>
