<template>
  <v-card @click="onOpenSite(site.token)" class="site white pa-2">
    <v-card-text>
      <div class="site-logo"
        v-bind:style="{ 'background': 'url(' + site.imageUrl + ')', 'background-size': 'cover', 'background-repeat': 'no-repeat', 'background-position': '50% 50%'}">
      </div>
      <div class="site-token">
        Token: {{site.token}}
        <v-btn small light icon v-clipboard="copyData"
          @success="onTokenCopied" @error="onTokenCopyFailed">
          <v-icon>content_copy</v-icon>
        </v-btn>
      </div>
      <div class="site-name">{{site.name}}</div>
      <div class="site-desc">{{site.description}}</div>
      <div class="site-right">
        <div class="site-created-label">Created:</div>
        <div class="site-created">{{ formatDate(site.createdDate) }}</div>
        <div class="site-updated-label">Updated:</div>
        <div class="site-updated">{{ formatDate(site.udpatedDate) }}</div>
        <site-delete-dialog :token="site.token" class="site-delete" @siteDeleted="onSiteDeleted"></site-delete-dialog>
      </div>
      <div class="site-divider"></div>
    </v-card-text>
    <v-snackbar :timeout="3000" success v-model="showTokenCopied">Token copied to clipboard</v-snackbar>
  </v-card>
</template>

<script>
import Utils from '../common/utils'
import SiteDeleteDialog from './SiteDeleteDialog'

export default {

  data: () => ({
    copyData: null,
    showTokenCopied: false
  }),

  props: ['site'],

  components: {
    SiteDeleteDialog
  },

  created: function () {
    this.$data.copyData = this.site.token
  },

  methods: {
    // Called when a site is deleted.
    onSiteDeleted: function () {
      this.$emit('siteDeleted')
    },

    // Called after token is copied.
    onTokenCopied: function (e) {
      this.$data.showTokenCopied = true
      console.log('Token copied.')
    },

    // Called if unable to copy token.
    onTokenCopyFailed: function (e) {
      console.log('Token copy failed.')
    },

    // Format date.
    formatDate: function (date) {
      return Utils.formatDate(date)
    }
  }
}
</script>

<style scoped>
.site {
  min-height: 180px;
  min-width: 800px;
  overflow-y: hidden;
}

.site-logo {
  position: absolute;
  top: 0px;
  left: 0px;
  bottom: 0px;
  width: 140px;
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

.site-token {
  position: absolute;
  top: 40px;
  left: 158px;
  right: 300px;
  font-size: 16px;
  white-space: nowrap;
  overflow-x: hidden;
}

.site-desc {
  position: absolute;
  top: 80px;
  left: 160px;
  right: 300px;
  bottom: 10px;
  font-size: 14px;
  overflow-y: hidden;
}

.site-divider {
  position: absolute;
  top: 10px;
  right: 280px;
  bottom: 10px;
  border-left: 1px solid #eee;
}

.site-right {
  position: absolute;
  top: 10px;
  right: 10px;
  bottom: 10px;
  width: 260px;
}

.site-created-label {
  position: absolute;
  top: 10px;
  left: 10px;
  font-size: 14px;
}

.site-created {
  position: absolute;
  top: 10px;
  left: 100px;
  font-size: 14px;
  white-space: nowrap;
}

.site-updated-label {
  position: absolute;
  top: 35px;
  left: 10px;
  font-size: 14px;
}

.site-updated {
  position: absolute;
  top: 35px;
  left: 100px;
  font-size: 14px;
  white-space: nowrap;
}

.site-delete {
  position: absolute;
  bottom: 0px;
  right: 10px;
}
</style>
