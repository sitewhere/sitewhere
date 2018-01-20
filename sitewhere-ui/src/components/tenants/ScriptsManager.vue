<template>
  <v-container fluid fill-height style="border: 1px solid #ccc;" class="pa-0">
    <v-layout row>
      <v-flex xs4 style="border-right: 1px solid #ccc;">
        <scripts-manager-toolbar title="Scripts">
          <v-spacer></v-spacer>
          <scripts-create-dialog :tenantId="tenantId">
          </scripts-create-dialog>
          <v-tooltip right>
            <v-btn icon slot="activator">
              <v-icon color="green darken-1" @click="refresh">fa-refresh</v-icon>
            </v-btn>
            <span>Refresh Scripts</span>
          </v-tooltip>
        </scripts-manager-toolbar>
        <v-list dense two-line style="min-height: 350px;">
          <template v-for="script in scripts">
            <v-list-tile v-bind:key="script.id"
              @click="onScriptClicked(script)">
              <v-list-tile-content>
                <v-list-tile-title v-html="script.name"></v-list-tile-title>
                <v-list-tile-sub-title v-html="script.description"></v-list-tile-sub-title>
              </v-list-tile-content>
            </v-list-tile>
          </template>
        </v-list>
        <v-divider></v-divider>
        <scripts-manager-toolbar title="Versions"></scripts-manager-toolbar>
        <v-list dense two-line style="min-height: 350px;">
          <template v-for="version in versions">
            <v-list-tile v-bind:key="version.versionId"
              @click="onVersionClicked(version)">
              <v-list-tile-content>
                <v-list-tile-title>{{ formatDate(version.createdDate) }}</v-list-tile-title>
                <v-list-tile-sub-title v-html="version.comment"></v-list-tile-sub-title>
              </v-list-tile-content>
            </v-list-tile>
          </template>
        </v-list>
      </v-flex>
      <v-flex xs8>
        <v-card v-if="selectedVersion" height="100%">
          <scripts-manager-toolbar
            :title="selectedScript.name + '.' + selectedScript.type + ' (' + formatDate(selectedVersion.createdDate) + ')'">
            <v-spacer></v-spacer>
            <v-tooltip left>
              <v-btn icon slot="activator" @click="saveContent">
                <v-icon color="blue darken-1">
                  fa-cloud-upload
                </v-icon>
              </v-btn>
              <span>Upload Changes</span>
            </v-tooltip>
          </scripts-manager-toolbar>
          <codemirror v-model="content" :options="options">
          </codemirror>
        </v-card>
      </v-flex>
    </v-layout>
    <v-snackbar :timeout="2000" success v-model="showMessage">{{ message }}
      <v-btn dark flat @click.native="showMessage = false">Close</v-btn>
    </v-snackbar>
  </v-container>
</template>

<script>
import { codemirror } from 'vue-codemirror-lite'
import 'codemirror/lib/codemirror.css'
import 'codemirror/mode/groovy/groovy'
import Utils from '../common/Utils'
import ScriptsManagerToolbar from './ScriptsManagerToolbar'
import ScriptsCreateDialog from './ScriptsCreateDialog'
import {
  _listTenantScriptMetadata,
  _getTenantScriptContent,
  _updateTenantScript
} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    scripts: null,
    selectedScript: null,
    versions: null,
    selectedVersion: null,
    content: '',
    options: {
      mode: 'groovy'
    },
    message: null,
    showMessage: false
  }),

  props: ['tenantId'],

  components: {
    codemirror,
    ScriptsManagerToolbar,
    ScriptsCreateDialog
  },

  created: function () {
    this.refresh()
  },

  methods: {
    // Refresh list of scripts.
    refresh: function () {
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

    // Called when a script is clicked.
    onScriptClicked: function (script) {
      this.$data.selectedScript = script
      this.$data.versions = script.versions
    },

    // Called when a version is clicked.
    onVersionClicked: function (version) {
      var component = this
      this.$data.selectedVersion = version
      _getTenantScriptContent(this.$store, this.tenantId,
        this.$data.selectedScript.id, version.versionId)
        .then(function (response) {
          component.$data.content = response.data
        }).catch(function (e) {
          console.log(e)
        })
    },

    // Save editor content.
    saveContent: function () {
      var script = this.$data.selectedScript
      console.log(this.$data.content)
      var updated = {
        'id': script.id,
        'name': script.name,
        'description': script.description,
        'type': script.type,
        'content': btoa(this.$data.content)
      }
      _updateTenantScript(this.$store, this.tenantId,
        this.$data.selectedScript.id, this.$data.selectedVersion.versionId,
        updated)
        .then(function (response) {
          this.showMessage('Content Saved Successfully.')
        }).catch(function (e) {
          console.log(e)
        })
    },

    // Show snackbar message.
    showMesssage: function (message) {
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
