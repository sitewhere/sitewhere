<template>
  <v-card class="assettype white pa-2">
    <v-card-text>
      <span class="assettype-logo" :style="logoStyle"></span>
      <span class="assettype-qrcode" :style="qrCodeStyle"></span>
      <div class="assettype-headers">
        <header-field label="Token">
          <clipboard-copy-field :field="assetType.token"
            message="Token copied to clipboard">
          </clipboard-copy-field>
        </header-field>
        <header-field label="Name">
          <span>{{ assetType.name }}</span>
        </header-field>
        <header-field label="Description">
          <span>{{ assetType.description }}</span>
        </header-field>
        <header-field label="Created">
          <span>{{ formatDate(assetType.createdDate) }}</span>
        </header-field>
        <header-field label="Updated">
          <span>{{ formatDate(assetType.updatedDate) }}</span>
        </header-field>
      </div>
      <options-menu class="options-menu">
        <v-list slot="options">
          <v-list-tile>
            <v-btn block class="blue white--text"
              @click="onEditAssetType">
              Edit Asset Type
              <v-spacer></v-spacer>
              <v-icon class="white--text pl-2">fa-edit</v-icon>
            </v-btn>
          </v-list-tile>
          <v-list-tile>
            <v-btn block class="red darken-2 white--text"
              @click="onDeleteAssetType">
              Delete Asset Type
              <v-spacer></v-spacer>
              <v-icon class="white--text pl-2">fa-times</v-icon>
            </v-btn>
          </v-list-tile>
        </v-list>
      </options-menu>
    </v-card-text>
    <asset-type-update-dialog ref="update"
      :token="assetType.token" @assetTypeUpdated="onAssetTypeUpdated">
    </asset-type-update-dialog>
    <asset-type-delete-dialog ref="delete"
      :token="assetType.token" @assetTypeDeleted="onAssetTypeDeleted">
    </asset-type-delete-dialog>
  </v-card>
</template>

<script>
import Utils from '../common/Utils'
import HeaderField from '../common/HeaderField'
import ClipboardCopyField from '../common/ClipboardCopyField'
import OptionsMenu from '../common/OptionsMenu'
import AssetTypeDeleteDialog from './AssetTypeDeleteDialog'
import AssetTypeUpdateDialog from './AssetTypeUpdateDialog'
import {createCoreApiUrl} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
  }),

  props: ['assetType'],

  components: {
    HeaderField,
    ClipboardCopyField,
    OptionsMenu,
    AssetTypeDeleteDialog,
    AssetTypeUpdateDialog
  },

  computed: {
    // Compute style of logo.
    logoStyle: function () {
      return {
        'background-image': 'url(' + this.assetType.imageUrl + ')',
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
          'areatypes/' + this.assetType.token + '/symbol?tenantAuthToken=' +
          tenant.authenticationToken + ')',
        'background-size': 'contain',
        'background-repeat': 'no-repeat',
        'background-position': '50% 50%',
        'border': '1px solid #eee'
      }
    }
  },

  methods: {
    // Called to open asset type edit dialog.
    onEditAssetType: function () {
      this.$refs['update'].onOpenDialog()
    },
    // Called when asset type is updated.
    onAssetTypeUpdated: function () {
      this.$emit('assetTypeUpdated')
    },
    onDeleteAssetType: function () {
      this.$refs['delete'].showDeleteDialog()
    },
    // Called when asset type is deleted.
    onAssetTypeDeleted: function () {
      this.$emit('assetTypeDeleted')
    },
    // Format date.
    formatDate: function (date) {
      return Utils.formatDate(date)
    }
  }
}
</script>

<style scoped>
.assettype {
  min-height: 180px;
  min-width: 920px;
  overflow-y: hidden;
}

.assettype-logo {
  position: absolute;
  top: 10px;
  left: 7px;
  bottom: 7px;
  width: 180px;
}

.assettype-qrcode {
  position: absolute;
  top: 10px;
  right: 7px;
  bottom: 7px;
  width: 180px;
}

.assettype-headers {
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
