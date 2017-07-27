<template>
  <v-card class="spec white pa-2">
    <v-card-text>
      <div class="spec-logo" :style="logoStyle">
      </div>
      <div class="spec-token">
        Token: {{specification.token}}
        <v-btn style="position: relative;"
          v-tooltip:right="{ html: 'Copy to Clipboard' }" class="mt-0"
          light icon v-clipboard="copyData"
          @success="onTokenCopied" @error="onTokenCopyFailed">
          <v-icon fa class="fa-lg">clipboard</v-icon>
        </v-btn>
      </div>
      <div class="spec-name">{{specification.name}}</div>
      <div class="spec-desc">{{specification.asset.description}}</div>
      <div class="spec-right">
        <div class="spec-created-label">Created:</div>
        <div class="spec-created">{{ formatDate(specification.createdDate) }}</div>
        <div class="spec-updated-label">Updated:</div>
        <div class="spec-updated">{{ formatDate(specification.updatedDate) }}</div>
        <specification-update-dialog :token="specification.token" class="spec-update"
          @specificationUpdated="onUpdated"></specification-update-dialog>
        <specification-delete-dialog :token="specification.token" class="spec-delete"
          @specificationDeleted="onDeleted"></specification-delete-dialog>
      </div>
      <div class="spec-divider"></div>
    </v-card-text>
    <v-snackbar :timeout="2000" success v-model="showTokenCopied">Token copied to clipboard
      <v-btn dark flat @click.native="showTokenCopied = false">Close</v-btn>
    </v-snackbar>
  </v-card>
</template>

<script>
import Utils from '../common/Utils'
import SpecificationDeleteDialog from './SpecificationDeleteDialog'
import SpecificationUpdateDialog from './SpecificationUpdateDialog'

export default {

  data: () => ({
    copyData: null,
    showTokenCopied: false
  }),

  props: ['specification'],

  components: {
    SpecificationDeleteDialog,
    SpecificationUpdateDialog
  },

  created: function () {
    this.$data.copyData = this.specification.token
  },

  computed: {
    // Compute style of logo.
    logoStyle: function () {
      return {
        'background-image': 'url(' + this.specification.assetImageUrl + ')',
        'background-size': 'contain',
        'background-repeat': 'no-repeat',
        'background-position': '50% 50%'
      }
    }
  },

  methods: {
    // Called when deleted.
    onDeleted: function () {
      this.$emit('specificationDeleted')
    },

    // Called when updated.
    onUpdated: function () {
      this.$emit('specificationUpdated')
    },

    // Called after token is copied.
    onTokenCopied: function (e) {
      this.$data.showTokenCopied = true
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
.spec {
  min-height: 180px;
  min-width: 800px;
  overflow-y: hidden;
}

.spec-logo {
  position: absolute;
  top: 0px;
  left: 0px;
  bottom: 0px;
  width: 140px;
}

.spec-name {
  position: absolute;
  top: 5px;
  left: 158px;
  right: 10px;
  font-size: 24px;
  font-weight: 400;
  white-space: nowrap;
  overflow-x: hidden;
}

.spec-token {
  position: absolute;
  top: 40px;
  left: 158px;
  right: 300px;
  font-size: 16px;
  white-space: nowrap;
  overflow-x: hidden;
}

.spec-desc {
  position: absolute;
  top: 83px;
  left: 160px;
  right: 300px;
  bottom: 10px;
  font-size: 14px;
  overflow-y: hidden;
}

.spec-divider {
  position: absolute;
  top: 10px;
  right: 280px;
  bottom: 10px;
  border-left: 1px solid #eee;
}

.spec-right {
  position: absolute;
  top: 10px;
  right: 10px;
  bottom: 10px;
  width: 260px;
}

.spec-created-label {
  position: absolute;
  top: 10px;
  left: 10px;
  font-size: 14px;
}

.spec-created {
  position: absolute;
  top: 10px;
  left: 100px;
  font-size: 14px;
  white-space: nowrap;
}

.spec-updated-label {
  position: absolute;
  top: 35px;
  left: 10px;
  font-size: 14px;
}

.spec-updated {
  position: absolute;
  top: 35px;
  left: 100px;
  font-size: 14px;
  white-space: nowrap;
}

.spec-update {
  position: absolute;
  bottom: 0px;
  left: 7px;
}

.spec-delete {
  position: absolute;
  bottom: 0px;
  left: 42px;
}
</style>
