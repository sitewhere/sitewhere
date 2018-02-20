<template>
  <v-card class="areatype white pa-2">
    <v-card-text>
      <span class="areatype-logo">
        <v-icon :style="iconStyle">{{ areaType.icon }}</v-icon>
      </span>
      <span class="areatype-qrcode" :style="qrCodeStyle"></span>
      <div class="areatype-headers">
        <header-field label="Token">
          <clipboard-copy-field :field="areaType.token"
            message="Token copied to clipboard">
          </clipboard-copy-field>
        </header-field>
        <header-field label="Name">
          <span>{{ areaType.name }}</span>
        </header-field>
        <header-field label="Description">
          <span>{{ areaType.description }}</span>
        </header-field>
        <header-field label="Created">
          <span>{{ formatDate(areaType.createdDate) }}</span>
        </header-field>
        <header-field label="Updated">
          <span>{{ formatDate(areaType.updatedDate) }}</span>
        </header-field>
      </div>
      <options-menu class="options-menu">
        <v-list slot="options">
          <v-list-tile>
            <v-btn block class="blue white--text"
              @click="onEditAreaType">
              Edit Area Type
              <v-spacer></v-spacer>
              <v-icon class="white--text pl-2">fa-edit</v-icon>
            </v-btn>
          </v-list-tile>
          <v-list-tile>
            <v-btn block class="red darken-2 white--text"
              @click="onDeleteAreaType">
              Delete Area Type
              <v-spacer></v-spacer>
              <v-icon class="white--text pl-2">fa-times</v-icon>
            </v-btn>
          </v-list-tile>
        </v-list>
      </options-menu>
    </v-card-text>
    <area-type-update-dialog ref="update"
      :token="areaType.token" :areaTypes="areaTypes"
      @areaUpdated="onAreaTypeUpdated">
    </area-type-update-dialog>
    <area-type-delete-dialog ref="delete" :token="areaType.token"
      @areaDeleted="onAreaTypeDeleted">
    </area-type-delete-dialog>
  </v-card>
</template>

<script>
import Utils from '../common/Utils'
import HeaderField from '../common/HeaderField'
import ClipboardCopyField from '../common/ClipboardCopyField'
import OptionsMenu from '../common/OptionsMenu'
import AreaTypeDeleteDialog from './AreaTypeDeleteDialog'
import AreaTypeUpdateDialog from './AreaTypeUpdateDialog'
import {createCoreApiUrl} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
  }),

  props: ['areaType', 'areaTypes'],

  components: {
    HeaderField,
    ClipboardCopyField,
    OptionsMenu,
    AreaTypeDeleteDialog,
    AreaTypeUpdateDialog
  },

  computed: {
    // Compute style for icon.
    iconStyle: function () {
      return {
        'font-size': '80px',
        'padding': '40px',
        'border': '1px solid #eee'
      }
    },
    // Compute style for QR code URL.
    qrCodeStyle: function () {
      var tenant = this.$store.getters.selectedTenant
      return {
        'background-color': '#fff',
        'background-image': 'url(' + createCoreApiUrl(this.$store) +
          'areatypes/' + this.areaType.token + '/symbol?tenantAuthToken=' +
          tenant.authenticationToken + ')',
        'background-size': 'contain',
        'background-repeat': 'no-repeat',
        'background-position': '50% 50%',
        'border': '1px solid #eee'
      }
    }
  },

  methods: {
    // Called to open area type edit dialog.
    onEditAreaType: function () {
      this.$refs['update'].onOpenDialog()
    },
    // Called when area type is updated.
    onAreaTypeUpdated: function () {
      this.$emit('areaTypeUpdated')
    },
    onDeleteAreaType: function () {
      this.$refs['delete'].showDeleteDialog()
    },
    // Called when area type is deleted.
    onAreaTypeDeleted: function () {
      this.$emit('areaTypeDeleted')
    },
    // Format date.
    formatDate: function (date) {
      return Utils.formatDate(date)
    }
  }
}
</script>

<style scoped>
.areatype {
  min-height: 180px;
  min-width: 920px;
  overflow-y: hidden;
}

.areatype-logo {
  position: absolute;
  top: 10px;
  left: 7px;
  bottom: 7px;
  width: 180px;
}

.areatype-qrcode {
  position: absolute;
  top: 10px;
  right: 7px;
  bottom: 7px;
  width: 180px;
}

.areatype-headers {
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
