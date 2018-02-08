<template>
  <v-container fluid>
    <v-layout row wrap>
      <v-flex xs12>
        <v-icon>info</v-icon>
        <span v-if="roles.length" class="ml-2 subheading">Roles: </span>
        <span v-else class="ml-2">No roles currently assigned</span>
        <v-chip close v-for="role in editedRoles" :key="role"
          class="grey white--text" @input="onRoleDeleted(role)">
          {{ role }}
        </v-chip>
      </v-flex>
      <v-flex xs12>
        <v-card class="ml-4 mt-3">
          <v-container fluid>
            <v-layout row wrap>
              <v-flex xs11>
                <v-text-field label="Add role" hide-details
                  v-model="newRole">
                </v-text-field>
              </v-flex>
              <v-flex xs1>
                <v-tooltip left>
                  <v-btn icon @click="onRoleAdded"  slot="activator">
                    <v-icon class="blue--text text--darken-2">
                      fa-plus
                    </v-icon>
                  </v-btn>
                  <span>Add Role</span>
                </v-tooltip>
              </v-flex>
            </v-layout>
          </v-container>
        </v-card>
      </v-flex>
    </v-layout>
  </v-container>
</template>

<script>
export default {

  data: () => ({
    editedRoles: [],
    newRole: null
  }),

  props: ['roles'],

  watch: {
    roles: function (value) {
      this.$data.editedRoles = this.roles
    }
  },

  methods: {
    // Called when a role is added.
    onRoleAdded: function (e) {
      let roles = this.$data.newRole.split(' ')
      roles.forEach((role) => this.$data.editedRoles.push(role))
      this.$data.newRole = null
    },

    // Called when a role is deleted.
    onRoleDeleted: function (role) {
      let roles = this.$data.editedRoles
      let index = roles.indexOf(role)
      if (index !== -1) {
        roles.splice(index, 1)
      }
    },

    // Called when roles are added or deleted.
    onRolesUpdated: function () {
      this.$emit('rolesUpdated', this.$data.editedRoles)
    }
  }
}
</script>

<style scoped>
</style>
