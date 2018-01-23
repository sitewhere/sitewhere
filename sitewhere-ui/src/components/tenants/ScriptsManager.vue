<template>
  <div>
    <v-card class="pa-2">
      <v-card>
        <v-card-text>
          <span v-if="scripts && scripts.length > 0">
            <v-menu offset-y>
              <v-btn outline color="primary" dark slot="activator">
                Script:<span style="color: #333; margin-left: 10px;">{{ selectedScript.name }}</span>
              </v-btn>
              <v-list dense two-line>
                <template v-for="script in scripts">
                  <v-list-tile v-bind:key="script.id"
                    @click="onScriptClicked(script)">
                    <v-list-tile-content>
                      <v-list-tile-title class="subheading" v-html="script.name"></v-list-tile-title>
                      <v-list-tile-sub-title v-html="script.description"></v-list-tile-sub-title>
                    </v-list-tile-content>
                  </v-list-tile>
                  <v-divider></v-divider>
                </template>
              </v-list>
            </v-menu>
            <v-menu offset-y>
              <v-btn outline color="primary" dark slot="activator">
                Version:<span style="color: #333; margin-left: 10px;">{{ selectedVersion.versionId }}</span>
              </v-btn>
              <v-list dense two-line>
                <template v-for="version in versions">
                  <v-list-tile v-bind:key="version.versionId"
                    @click="onVersionClicked(version)">
                    <v-list-tile-content>
                      <v-list-tile-title class="subheading">
                        {{ formatDate(version.createdDate) }}
                      </v-list-tile-title>
                      <v-list-tile-sub-title v-html="version.comment"></v-list-tile-sub-title>
                    </v-list-tile-content>
                  </v-list-tile>
                  <v-divider></v-divider>
                </template>
              </v-list>
            </v-menu>
          </span>
          <span v-else class="subheading">
            No scripts have been configured.
          </span>
          <v-tooltip top>
            <v-btn dark color="primary"
              @click="onScriptCreate" slot="activator">
              <v-icon left>fa-plus</v-icon>
              Create
            </v-btn>
            <span>Create Script</span>
          </v-tooltip>
          <v-tooltip top>
            <v-btn dark color="green darken-2" @click="refresh"
              slot="activator">
              <v-icon left>fa-refresh</v-icon>
              Refresh
            </v-btn>
            <span>Refresh Scripts</span>
          </v-tooltip>
        </v-card-text>
      </v-card>
    </v-card>
    <scripts-content-editor :script="selectedScript"
      :version="selectedVersion" :tenantId="tenantId" @saved="onContentSaved">
    </scripts-content-editor>
    <v-snackbar :timeout="2000" success v-model="showMessage">{{ message }}
      <v-btn dark flat @click.native="showMessage = false">Close</v-btn>
    </v-snackbar>
    <scripts-create-dialog ref="create" :tenantId="tenantId"
      @scriptAdded="refresh">
    </scripts-create-dialog>
  </div>
</template>

<script>
import Utils from '../common/Utils'
import ScriptsContentEditor from './ScriptsContentEditor'
import ScriptsCreateDialog from './ScriptsCreateDialog'
import {
  _listTenantScriptMetadata
} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    scripts: null,
    selectedScript: null,
    versions: null,
    selectedVersion: null,
    message: null,
    showMessage: false
  }),

  props: ['tenantId'],

  components: {
    ScriptsContentEditor,
    ScriptsCreateDialog
  },

  created: function () {
    this.refresh()
  },

  methods: {
    // Refresh list of scripts.
    refresh: function () {
      console.log('refresh scripts')
      var component = this
      _listTenantScriptMetadata(this.$store, this.tenantId)
        .then(function (response) {
          let scripts = response.data
          component.$data.scripts = scripts
          if (scripts.length > 0) {
            component.onScriptClicked(scripts[0])
          }
        }).catch(function (e) {
        })
    },

    // Called when content is saved successfully.
    onContentSaved: function () {
      this.displaySnackbarMessage('Content Saved Successfully.')
    },

    // Called when script create button is pressed.
    onScriptCreate: function () {
      let createDialog = this.$refs['create']
      createDialog.onOpenDialog()
    },

    // Called when a script is clicked.
    onScriptClicked: function (script) {
      this.$data.selectedScript = script
      this.$data.versions = script.versions
      if (script.versions.length > 0) {
        this.$data.selectedVersion = script.versions[0]
      }
    },

    // Called when a version is clicked.
    onVersionClicked: function (version) {
      this.$data.selectedVersion = version
    },

    // Show snackbar message.
    displaySnackbarMessage: function (message) {
      this.$data.message = message
      this.$data.showMessage = true
    },

    // Format date.
    formatDate: function (date) {
      return Utils.formatDate(date)
    }
  }
}
</script>

<style scoped>
.CodeMirror {
  border: 1px solid #eee;
  height: auto;
}
</style>
