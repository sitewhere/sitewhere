<template>
  <v-card class="area white pa-2">
    <v-card-text>
      <span class="area-logo" :style="logoStyle"></span>
      <span class="area-qrcode" :style="qrCodeStyle"></span>
      <div class="area-headers">
        <header-field label="Token">
          <clipboard-copy-field :field="area.token"
            message="Token copied to clipboard">
          </clipboard-copy-field>
        </header-field>
        <linked-header-field label="Area Type" :text="area.areaType.name"
          :url="'/areatypes/' + area.areaType.token">
        </linked-header-field>
        <header-field label="Name">
          <span>{{ area.name }}</span>
        </header-field>
        <header-field label="Description">
          <span>{{ area.description }}</span>
        </header-field>
        <header-field label="Created">
          <span>{{ formatDate(area.createdDate) }}</span>
        </header-field>
        <header-field label="Updated">
          <span>{{ formatDate(area.updatedDate) }}</span>
        </header-field>
      </div>
      <options-menu class="options-menu">
        <v-list slot="options">
          <v-list-tile>
            <v-btn block class="blue white--text" @click="onEditArea">
              Edit Area
              <v-spacer></v-spacer>
              <v-icon class="white--text pl-2">fa-edit</v-icon>
            </v-btn>
          </v-list-tile>
          <v-list-tile>
            <v-btn block class="red darken-2 white--text" @click="onDeleteArea">
              Delete Area
              <v-spacer></v-spacer>
              <v-icon class="white--text pl-2">fa-times</v-icon>
            </v-btn>
          </v-list-tile>
        </v-list>
      </options-menu>
    </v-card-text>
    <area-update-dialog ref="update" :token="area.token"
      @areaUpdated="onAreaUpdated">
    </area-update-dialog>
    <area-delete-dialog ref="delete" :token="area.token"
      @areaDeleted="onAreaDeleted">
    </area-delete-dialog>
  </v-card>
</template>

<script>
import Utils from '../common/Utils'
import HeaderField from '../common/HeaderField'
import LinkedHeaderField from '../common/LinkedHeaderField'
import ClipboardCopyField from '../common/ClipboardCopyField'
import OptionsMenu from '../common/OptionsMenu'
import AreaDeleteDialog from './AreaDeleteDialog'
import AreaUpdateDialog from './AreaUpdateDialog'
import {createCoreApiUrl} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
  }),

  props: ['area'],

  components: {
    HeaderField,
    LinkedHeaderField,
    ClipboardCopyField,
    OptionsMenu,
    AreaDeleteDialog,
    AreaUpdateDialog
  },

  computed: {
    // Compute style of logo.
    logoStyle: function () {
      return {
        'background-color': '#fff',
        'background-image': 'url(' + this.area.imageUrl + ')',
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
        'background-image': 'url(' + createCoreApiUrl(this.$store) + 'areas/' +
          this.area.token + '/symbol?tenantAuthToken=' +
          tenant.authenticationToken + ')',
        'background-size': 'contain',
        'background-repeat': 'no-repeat',
        'background-position': '50% 50%',
        'border': '1px solid #eee'
      }
    }
  },

  methods: {
    // Called to open area edit dialog.
    onEditArea: function () {
      this.$refs['update'].onOpenDialog()
    },
    // Called when area is updated.
    onAreaUpdated: function () {
      this.$emit('areaUpdated')
    },
    onDeleteArea: function () {
      this.$refs['delete'].showDeleteDialog()
    },
    // Called when area is deleted.
    onAreaDeleted: function () {
      this.$emit('areaDeleted')
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
  min-height: 210px;
  min-width: 920px;
  overflow-y: hidden;
}

.area-logo {
  position: absolute;
  top: 10px;
  left: 7px;
  bottom: 7px;
  width: 180px;
}

.area-qrcode {
  position: absolute;
  top: 10px;
  right: 7px;
  bottom: 7px;
  width: 180px;
}

.area-headers {
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
