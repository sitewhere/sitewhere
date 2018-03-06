<template>
  <div>
    <div v-if="group">
      <v-card-text>
        {{ chosenText }}
      </v-card-text>
      <v-list two-line>
        <v-list-tile avatar :key="group.token">
          <v-list-tile-avatar>
            <v-icon large>view_module</v-icon>
          </v-list-tile-avatar>
          <v-list-tile-content>
            <v-list-tile-title v-html="group.name"></v-list-tile-title>
            <v-list-tile-sub-title v-html="group.description"></v-list-tile-sub-title>
          </v-list-tile-content>
          <v-list-tile-action>
            <v-btn icon ripple @click.native.stop="onGroupRemoved">
              <v-icon class="grey--text">remove_circle</v-icon>
            </v-btn>
          </v-list-tile-action>
        </v-list-tile>
      </v-list>
    </div>
    <div v-else>
      <v-card-text>
        {{ notChosenText }}
      </v-card-text>
      <v-list v-if="groups" class="group-list" two-line>
        <template v-for="group in groups">
          <v-list-tile avatar :key="group.token"
            @click.native.stop="onGroupChosen(group)">
            <v-list-tile-avatar>
              <v-icon large>view_module</v-icon>
            </v-list-tile-avatar>
            <v-list-tile-content>
              <v-list-tile-title v-html="group.name"></v-list-tile-title>
              <v-list-tile-sub-title v-html="group.description">
              </v-list-tile-sub-title>
            </v-list-tile-content>
          </v-list-tile>
        </template>
      </v-list>
    </div>
  </div>
</template>

<script>
import Lodash from 'lodash'
import {_listDeviceGroups} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    group: null,
    groups: []
  }),

  props: ['value', 'chosenText', 'notChosenText'],

  // Initially load list of all sites.
  created: function () {
    var component = this
    _listDeviceGroups(component.$store)
      .then(function (response) {
        component.groups = response.data.results
        if (component.value) {
          component.onGroupChosen(component.selected)
        }
      }).catch(function (e) {
      })
  },

  watch: {
    value: function (updated) {
      let group = Lodash.find(this.groups, { 'token': updated })
      if (group) {
        this.onGroupChosen(group, false)
      } else {
        this.onGroupRemoved(false)
      }
    }
  },

  methods: {
    // Called when a group is chosen.
    onGroupChosen: function (group) {
      this.$data.group = group
      this.$emit('input', group.token)
      this.$emit('groupUpdated', group)
    },

    // Allow another group to be chosen.
    onGroupRemoved: function () {
      this.$data.group = null
      this.$emit('input', null)
      this.$emit('groupUpdated', null)
    }
  }
}
</script>

<style scoped>
.group-list {
  max-height: 300px;
  overflow-y: auto;
}
</style>
