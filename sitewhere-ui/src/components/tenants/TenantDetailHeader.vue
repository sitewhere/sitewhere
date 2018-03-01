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
        </v-card-title>
      </v-flex>
    </v-layout>
  </v-card>
</template>

<script>
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
  }
}
</script>

<style scoped>
</style>
