<template>
  <v-container fluid fill-height style="border: 1px solid #ccc;" class="pa-0">
    <v-layout row>
      <v-flex xs4 style="border-right: 1px solid #ccc;">
        <scripts-manager-toolbar title="Scripts"></scripts-manager-toolbar>
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
          <v-toolbar flat dense color="grey lighten-3">
            <scripts-manager-toolbar
              :title="selectedScript.name + '.' + selectedScript.type">
            </scripts-manager-toolbar>
          </v-toolbar>
          <codemirror style="min-height: 650px;"
            :value="content" :options="options">
          </codemirror>
        </v-card>
      </v-flex>
    </v-layout>
  </v-container>
</template>

<script>
import { codemirror } from 'vue-codemirror-lite'
import 'codemirror/lib/codemirror.css'
import 'codemirror/mode/groovy/groovy'
import Utils from '../common/Utils'
import ScriptsManagerToolbar from './ScriptsManagerToolbar'
import {
  _listTenantScriptMetadata,
  _getTenantScriptContent
} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    scripts: null,
    selectedScript: null,
    versions: null,
    selectedVersion: null,
    content: null,
    options: {
      mode: 'groovy'
    }
  }),

  props: ['tenantId'],

  components: {
    codemirror,
    ScriptsManagerToolbar
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

    // Format date.
    formatDate: function (date) {
      return Utils.formatDate(date)
    }
  }
}
</script>

<style scoped>
</style>
