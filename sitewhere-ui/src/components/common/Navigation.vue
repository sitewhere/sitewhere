<template>
  <v-list v-if="sections" dense class="pt-0">
    <v-list-group v-for="navsect in sections" :value="navsect.active" :key="navsect.id">
      <v-list-tile v-if="isAuthForSection(navsect)"
        @click.native="onSectionClicked(navsect)" slot="item">
        <v-list-tile-action>
          <v-icon dark>{{ navsect.icon }}</v-icon>
        </v-list-tile-action>
        <v-list-tile-content>
          <v-list-tile-title>{{ navsect.title }}</v-list-tile-title>
        </v-list-tile-content>
        <v-list-tile-action v-if="navsect.subsections">
          <v-icon dark>keyboard_arrow_down</v-icon>
        </v-list-tile-action>
      </v-list-tile>
      <v-list-tile @click="onSectionClicked(navsub)"
        v-for="navsub in navsect.subsections" :key="navsub.id">
        <v-list-tile-content>
          <v-list-tile-title>{{ navsub.title }}</v-list-tile-title>
        </v-list-tile-content>
        <v-list-tile-action>
          <v-icon dark>{{ navsub.icon }}</v-icon>
        </v-list-tile-action>
      </v-list-tile>
    </v-list-group>
  </v-list>
</template>

<script>
import Utils from './Utils'

export default {

  data: () => ({
    sites: null,
    drawerEdit: true
  }),

  props: ['sections'],

  methods: {
    // Determines whether user is authorized for section.
    isAuthForSection: function (section) {
      if (section.requireAll) {
        return Utils.isAuthForAll(this, section.requireAll)
      }
      return true
    },

    onSectionClicked: function (section) {
      this.$emit('sectionSelected', section)
    }
  }
}
</script>

<style scoped>
.list__tile__action {
  min-width: 40px;
}
.list__tile__title {
  font-size: 16px;
  padding-top: 3px;
}
</style>
