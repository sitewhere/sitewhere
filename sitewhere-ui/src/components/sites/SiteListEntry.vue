<template>
  <v-card hover @click="onOpenSite(site.token)" class="site white pa-2">
    <v-card-text>
      <div class="site-logo"
        v-bind:style="{ 'background': 'url(' + site.imageUrl + ')', 'background-size': 'cover', 'background-repeat': 'no-repeat', 'background-position': '50% 50%'}">
      </div>
      <div class="site-name">{{site.name}}</div>
      <div class="site-desc">{{site.description}}</div>
    </v-card-text>
  </v-card>
</template>

<script>
export default {

  data: () => ({
  }),

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
  min-height: 180px;
  overflow-y: hidden;
}

.site-logo {
  position: absolute;
  top: 10px;
  left: 10px;
  width: 140px;
  height: 160px;
}

.site-name {
  position: absolute;
  top: 5px;
  left: 158px;
  right: 10px;
  font-size: 24px;
  font-weight: 400;
  white-space: nowrap;
  overflow-x: hidden;
}

.site-desc {
  position: absolute;
  top: 42px;
  left: 160px;
  right: 10px;
  bottom: 10px;
  font-size: 14px;
  overflow-y: hidden;
}
</style>
