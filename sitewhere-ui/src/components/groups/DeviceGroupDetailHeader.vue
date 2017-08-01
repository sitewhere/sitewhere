<template>
  <v-card v-if="group" class="white">
    <v-card-text class="group">
      <v-icon class="group-icon grey--text">view_module</v-icon>
      <div class="group-name ellipsis title">
        {{ group.name }}
      </div>
      <div class="group-token ellipsis subheading">
        {{ group.token }}
      </div>
      <div class="group-description ellipsis">
        {{ group.description }}
      </div>
      <div class="group-roles ellipsis">
        Roles: <strong>{{ rolesView  }}</strong>
      </div>
    </v-card-text>
  </v-card>
</template>

<script>
export default {

  data: function () {
    return {
    }
  },

  components: {
  },

  props: ['group'],

  computed: {
    rolesView: function () {
      return this.group.roles.join(', ')
    }
  },

  methods: {
    // Fire event to have parent refresh content.
    refresh: function () {
      this.$emit('refresh')
    }
  }
}
</script>

<style scoped>
.group {
  position: relative;
  min-height: 120px;
  overflow-x: hidden;
}
.group-icon {
  position: absolute;
  top: 0px;
  left: 0px;
  bottom: 0px;
  width: 90px;
  font-size: 70px;
  background-color: #eee;
  border-right: 1px solid #ddd;
}
.group-name {
  position: absolute;
  top: 8px;
  left: 100px;
  right: 10px;
}
.group-token {
  position: absolute;
  top: 35px;
  left: 100px;
  right: 10px;
}
.group-description {
  position: absolute;
  top: 63px;
  left: 100px;
  right: 10px;
}
.group-roles {
  position: absolute;
  top: 90px;
  left: 100px;
  right: 10px;
}
.ellipsis {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
