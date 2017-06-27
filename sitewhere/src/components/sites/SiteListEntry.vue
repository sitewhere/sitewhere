<template>
  <v-card hover @click="onOpenSite(site.token)" class="site white pa-2">
    <v-card-row>
      <div class="site-logo"
        v-bind:style="{ 'background': 'url(' + site.imageUrl + ')', 'background-size': 'cover', 'background-repeat': 'no-repeat'}">
      </div>
      <div class="site-name">{{site.name}}</div>
      <div class="site-desc">{{site.description}}</div>
    </v-card-row>
    <v-card-row class="site-actions" actions>
      <site-delete-dialog :token="site.token" @siteDeleted="onSiteDeleted"/>
    </v-card-row>
  </v-card>
</template>

<script>
import SiteDeleteDialog from './SiteDeleteDialog'

export default {

  data: () => ({
  }),

  components: {
    SiteDeleteDialog
  },

  props: ['site'],

  methods: {
    // Called when a site is clicked.
    onOpenSite: function (token) {
      var tenant = this.$store.getters.selectedTenant
      if (tenant) {
        this.$router.push('/admin/' + tenant.id + '/sites/' + token)
      }
    },

    // Called when a site is deleted.
    onSiteDeleted: function () {
      this.$emit('siteDeleted')
    }
  }
}
</script>

<style scoped>
.site {
  min-height: 120px;
}

.site-logo {
  position: absolute;
  top: 10px;
  left: 10px;
  width: 100px;
  height: 100px;
}

.site-name {
  position: absolute;
  top: 6px;
  left: 125px;
  font-size: 20px;
  font-weight: 400;
}

.site-desc {
  position: absolute;
  top: 37px;
  left: 125px;
  font-size: 14px;
  width: 60%;
  max-height: 4.5em;
  overflow-y: hidden;
}

.site-actions {
  position: absolute;
  bottom: 0px;
  left: 0px;
  right: 0px;
}
</style>
