<template>
  <v-card hover class="white">
    <v-card-text class="group">
      <div class="group-image" :style="backgroundImageStyle"></div>
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
    },
    // Create background image style.
    backgroundImageStyle: function () {
      return {
        'background-image': 'url(' + this.group.imageUrl + ')',
        'background-size': 'contain',
        'background-repeat': 'no-repeat',
        'background-position': '50% 50%'
      }
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
.group-image {
  position: absolute;
  top: 5px;
  left: 5px;
  bottom: 5px;
  width: 90px;
  background-color: #fff;
  border-right: 1px solid #eee;
}
.group-name {
  position: absolute;
  top: 8px;
  left: 110px;
  right: 10px;
}
.group-token {
  position: absolute;
  top: 35px;
  left: 110px;
  right: 10px;
}
.group-description {
  position: absolute;
  top: 63px;
  left: 110px;
  right: 10px;
}
.group-roles {
  position: absolute;
  top: 90px;
  left: 110px;
  right: 10px;
}
.ellipsis {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
