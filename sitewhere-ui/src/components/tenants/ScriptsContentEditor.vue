<template>
  <v-card v-if="selectedVersion">
    <v-card-text>
      <v-toolbar dense color="grey lighten-2">
        <v-toolbar-title class="subheading">
          {{ scriptTitle }}
        </v-toolbar-title>
        <v-spacer></v-spacer>
        <v-tooltip left>
          <v-btn icon slot="activator" @click="saveContent">
            <v-icon color="blue darken-1">
              fa-cloud-upload
            </v-icon>
          </v-btn>
          <span>Upload Changes</span>
        </v-tooltip>
      </v-toolbar>
      <v-card>
        <codemirror v-model="content" :options="options">
        </codemirror>
      </v-card>
    </v-card-text>
  </v-card>
</template>

<script>
import { codemirror } from 'vue-codemirror-lite'
import 'codemirror/lib/codemirror.css'
import 'codemirror/mode/groovy/groovy'
import Utils from '../common/Utils'
import {
  _getTenantScriptContent,
  _updateTenantScript
} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    selectedScript: null,
    selectedVersion: null,
    content: '',
    options: {
      mode: 'groovy'
    }
  }),

  props: ['script', 'version', 'tenantId'],

  components: {
    codemirror
  },

  computed: {
    scriptTitle: function () {
      if (this.$data.selectedScript && this.$data.selectedVersion) {
        return this.$data.selectedScript.id + '.' +
          this.$data.selectedScript.type + ' (' +
          this.formatDate(this.$data.selectedVersion.createdDate) + ')'
      }
      return null
    }
  },

  watch: {
    script: function (updated) {
      this.$data.selectedScript = updated
    },
    version: function (updated) {
      this.$data.selectedVersion = updated
    },
    selectedVersion: function (updated) {
      this.updateContent()
    }
  },

  methods: {
    // Format date.
    formatDate: function (date) {
      return Utils.formatDate(date)
    },

    // Update content.
    updateContent: function () {
      var component = this
      _getTenantScriptContent(this.$store, this.tenantId,
        this.$data.selectedScript.id, this.$data.selectedVersion.versionId)
        .then(function (response) {
          component.$data.content = response.data
        }).catch(function (e) {
          console.log(e)
        })
    },

    // Save editor content.
    saveContent: function () {
      var component = this
      var script = this.$data.selectedScript
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
          component.$emit('saved')
        }).catch(function (e) {
          console.log(e)
        })
    }
  }
}
</script>

<style scoped>
</style>
