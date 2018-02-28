<template>
  <v-card class="asset white pa-2">
    <v-card-text>
      <span class="asset-logo" :style="logoStyle"></span>
      <span class="asset-qrcode" :style="qrCodeStyle"></span>
      <div class="asset-headers">
        <header-field label="Token">
          <clipboard-copy-field :field="asset.token"
            message="Token copied to clipboard">
          </clipboard-copy-field>
        </header-field>
        <header-field label="Name">
          <span>{{ asset.name }}</span>
        </header-field>
        <linked-header-field label="Asset Type"
          :text="asset.assetType.name"
          :url="'/assettypes/' + asset.assetType.token">
        </linked-header-field>
        <header-field label="Image URL">
          <span>{{ asset.imageUrl }}</span>
        </header-field>
        <header-field label="Created">
          <span>{{ formatDate(asset.createdDate) }}</span>
        </header-field>
        <header-field label="Updated">
          <span>{{ formatDate(asset.updatedDate) }}</span>
        </header-field>
      </div>
      <options-menu class="options-menu">
        <v-list slot="options">
          <v-list-tile>
            <v-btn block class="blue white--text"
              @click="onEditAsset">
              Edit Asset
              <v-spacer></v-spacer>
              <v-icon class="white--text pl-2">fa-edit</v-icon>
            </v-btn>
          </v-list-tile>
          <v-list-tile>
            <v-btn block class="red darken-2 white--text"
              @click="onDeleteAsset">
              Delete Asset
              <v-spacer></v-spacer>
              <v-icon class="white--text pl-2">fa-times</v-icon>
            </v-btn>
          </v-list-tile>
        </v-list>
      </options-menu>
    </v-card-text>
    <asset-update-dialog ref="update"
      :token="asset.token" @asseUpdated="onAssetUpdated">
    </asset-update-dialog>
    <asset-delete-dialog ref="delete"
      :token="asset.token" @assetDeleted="onAssetDeleted">
    </asset-delete-dialog>
  </v-card>
</template>

<script>
import Utils from '../common/Utils'
import HeaderField from '../common/HeaderField'
import LinkedHeaderField from '../common/LinkedHeaderField'
import ClipboardCopyField from '../common/ClipboardCopyField'
import OptionsMenu from '../common/OptionsMenu'
import AssetDeleteDialog from './AssetDeleteDialog'
import AssetUpdateDialog from './AssetUpdateDialog'
import {createCoreApiUrl} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
  }),

  props: ['asset'],

  components: {
    HeaderField,
    LinkedHeaderField,
    ClipboardCopyField,
    OptionsMenu,
    AssetDeleteDialog,
    AssetUpdateDialog
  },

  computed: {
    // Compute style of logo.
    logoStyle: function () {
      return {
        'background-image': 'url(' + this.asset.imageUrl + ')',
        'background-size': 'contain',
        'background-repeat': 'no-repeat',
        'background-position': '50% 50%',
        'border': '1px solid #eee'
      }
    },
    // Compute style for QR code URL.
    qrCodeStyle: function () {
      var tenant = this.$store.getters.selectedTenant
      return {
        'background-color': '#fff',
        'background-image': 'url(' + createCoreApiUrl(this.$store) +
          'assets/' + this.asset.token + '/symbol?tenantAuthToken=' +
          tenant.authenticationToken + ')',
        'background-size': 'contain',
        'background-repeat': 'no-repeat',
        'background-position': '50% 50%',
        'border': '1px solid #eee'
      }
    }
  },

  methods: {
    // Called to open asset edit dialog.
    onEditAsset: function () {
      this.$refs['update'].onOpenDialog()
    },
    // Called when asset type is updated.
    onAssetUpdated: function () {
      this.$emit('assetUpdated')
    },
    onDeleteAsset: function () {
      this.$refs['delete'].showDeleteDialog()
    },
    // Called when asset is deleted.
    onAssetDeleted: function () {
      this.$emit('assetDeleted')
    },
    // Format date.
    formatDate: function (date) {
      return Utils.formatDate(date)
    }
  }
}
</script>

<style scoped>
.asset {
  min-height: 200px;
  min-width: 920px;
  overflow-y: hidden;
}

.asset-logo {
  position: absolute;
  top: 10px;
  left: 7px;
  bottom: 7px;
  width: 180px;
}

.asset-qrcode {
  position: absolute;
  top: 10px;
  right: 7px;
  bottom: 7px;
  width: 180px;
}

.asset-headers {
  position: absolute;
  top: 20px;
  left: 200px;
  right: 200px;
}

.options-menu {
  position: absolute;
  top: 10px;
  right: 190px;
}
</style>
