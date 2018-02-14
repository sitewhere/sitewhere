<template>
  <v-card class="area white pa-2">
    <v-card-text>
      <div class="area-logo" :style="logoStyle">
      </div>
      <div class="area-token">
        Token: {{area.token}}
        <v-tooltip top>
          <v-btn light icon class="grey--text"
            v-clipboard="copyData" @click.stop="onOpenEdit" slot="activator"
            @success="onTokenCopied" @error="onTokenCopyFailed">
            <v-icon>fa-clipboard</v-icon>
          </v-btn>
          <span>Copy to Clipboard</span>
        </v-tooltip>
      </div>
      <div class="area-name">{{area.name}}</div>
      <div class="area-desc">{{area.description}}</div>
      <div class="area-right">
        <div class="area-created-label">Created:</div>
        <div class="area-created">{{ formatDate(area.createdDate) }}</div>
        <div class="area-updated-label">Updated:</div>
        <div class="area-updated">{{ formatDate(area.updatedDate) }}</div>
        <area-update-dialog :token="area.token" class="area-update" @areaUpdated="onAreaUpdated"></area-update-dialog>
        <area-delete-dialog :token="area.token" class="area-delete" @areaDeleted="onAreaDeleted"></area-delete-dialog>
      </div>
      <div class="area-divider"></div>
    </v-card-text>
    <v-snackbar :timeout="2000" success v-model="showTokenCopied">Token copied to clipboard
      <v-btn dark flat @click.native="showTokenCopied = false">Close</v-btn>
    </v-snackbar>
  </v-card>
</template>

<script>
import Utils from '../common/Utils'
import AreaDeleteDialog from './AreaDeleteDialog'
import AreaUpdateDialog from './AreaUpdateDialog'

export default {

  data: () => ({
    copyData: null,
    showTokenCopied: false
  }),

  props: ['area'],

  components: {
    AreaDeleteDialog,
    AreaUpdateDialog
  },

  computed: {
    // Compute style of logo.
    logoStyle: function () {
      return {
        'background-image': 'url(' + this.area.imageUrl + ')',
        'background-size': 'cover',
        'background-repeat': 'no-repeat',
        'background-position': '50% 50%'
      }
    }
  },

  created: function () {
    this.$data.copyData = this.area.token
  },

  methods: {
    // Called when area is deleted.
    onAreaDeleted: function () {
      this.$emit('areaDeleted')
    },

    // Called when area is updated.
    onAreaUpdated: function () {
      this.$emit('areaUpdated')
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
.area {
  min-height: 180px;
  min-width: 800px;
  overflow-y: hidden;
}

.area-logo {
  position: absolute;
  top: 0px;
  left: 0px;
  bottom: 0px;
  width: 140px;
}

.area-name {
  position: absolute;
  top: 5px;
  left: 158px;
  right: 10px;
  font-size: 24px;
  font-weight: 400;
  white-space: nowrap;
  overflow-x: hidden;
}

.area-token {
  position: absolute;
  top: 40px;
  left: 158px;
  right: 300px;
  font-size: 16px;
  white-space: nowrap;
  overflow-x: hidden;
}

.area-desc {
  position: absolute;
  top: 80px;
  left: 160px;
  right: 300px;
  bottom: 10px;
  font-size: 14px;
  overflow-y: hidden;
}

.area-divider {
  position: absolute;
  top: 10px;
  right: 280px;
  bottom: 10px;
  border-left: 1px solid #eee;
}

.area-right {
  position: absolute;
  top: 10px;
  right: 10px;
  bottom: 10px;
  width: 260px;
}

.area-created-label {
  position: absolute;
  top: 10px;
  left: 10px;
  font-size: 14px;
}

.area-created {
  position: absolute;
  top: 10px;
  left: 100px;
  font-size: 14px;
  white-space: nowrap;
}

.area-updated-label {
  position: absolute;
  top: 35px;
  left: 10px;
  font-size: 14px;
}

.area-updated {
  position: absolute;
  top: 35px;
  left: 100px;
  font-size: 14px;
  white-space: nowrap;
}

.area-update {
  position: absolute;
  bottom: 0px;
  left: 7px;
}

.area-delete {
  position: absolute;
  bottom: 0px;
  left: 42px;
}
</style>
