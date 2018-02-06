<template>
  <v-chip :color="backgroundColor" :text-color="foregroundColor">
    <v-menu v-if="tenantRuntime.lifecycleErrorStack"
      open-on-hover offset-y :nudge-width="700">
      <div slot="activator">
        <v-avatar :class="statusClass">
          <v-icon :class="statusIconClass">{{ statusIcon }}</v-icon>
        </v-avatar>
        {{ tenantRuntime.microservice.hostname }} ({{ tenantRuntime.lifecycleStatus }})
        <v-icon right :class="identClass">
          fa-{{ tenantRuntime.microservice.icon }}
        </v-icon>
      </div>
      <v-card>
        <v-list dense>
          <template v-for="error in tenantRuntime.lifecycleErrorStack">
            <v-list-tile>
              <v-icon class="red--text text--darken-3 pr-3">
                fa-exclamation-circle
              </v-icon>
              <v-list-tile-content>
                {{ error }}
              </v-list-tile-content>
            </v-list-tile>
            <v-divider></v-divider>
          </template>
        </v-list>
      </v-card>
    </v-menu>
    <div v-else>
      <v-avatar :class="statusClass">
        <v-icon :class="statusIconClass">{{ statusIcon }}</v-icon>
      </v-avatar>
      {{ tenantRuntime.microservice.hostname }} ({{ tenantRuntime.lifecycleStatus }})
      <v-icon right :class="identClass">
        fa-{{ tenantRuntime.microservice.icon }}
      </v-icon>
    </div>
  </v-chip>
</template>

<script>
export default {

  data: () => ({
  }),

  props: ['tenantRuntime'],

  computed: {
    backgroundColor: function () {
      var status = this.tenantRuntime.lifecycleStatus
      if (status === 'Initializing') {
        return 'green'
      } else if (status === 'InitializationError') {
        return 'red'
      } else if (status === 'Starting') {
        return 'green'
      } else if (status === 'Started') {
        return 'green'
      } else if (status === 'Stopping') {
        return 'grey'
      } else if (status === 'Stopped') {
        return 'grey'
      } else if (status === 'Terminating') {
        return 'grey'
      } else if (status === 'Terminated') {
        return 'grey'
      } else if (status === 'LifecycleError') {
        return 'red'
      } else {
        return 'grey'
      }
    },
    foregroundColor: function () {
      var status = this.tenantRuntime.lifecycleStatus
      if (status === 'Initializing') {
        return 'white'
      } else if (status === 'InitializationError') {
        return 'white'
      } else if (status === 'Starting') {
        return 'white'
      } else if (status === 'Started') {
        return 'white'
      } else if (status === 'Stopping') {
        return 'black'
      } else if (status === 'Stopped') {
        return 'black'
      } else if (status === 'Terminating') {
        return 'black'
      } else if (status === 'Terminated') {
        return 'black'
      } else if (status === 'LifecycleError') {
        return 'white'
      } else {
        return 'black'
      }
    },
    identClass: function () {
      var status = this.tenantRuntime.lifecycleStatus
      if (status === 'Initializing') {
        return ['green--text', 'text--darken-3', 'pr-2']
      } else if (status === 'InitializationError') {
        return ['red--text', 'text--darken-3', 'pr-2']
      } else if (status === 'Starting') {
        return ['green--text', 'text--darken-3', 'pr-2']
      } else if (status === 'Started') {
        return ['green--text', 'text--darken-3', 'pr-2']
      } else if (status === 'Stopping') {
        return ['grey--text', 'text--darken-3', 'pr-2']
      } else if (status === 'Stopped') {
        return ['grey--text', 'text--darken-3', 'pr-2']
      } else if (status === 'Terminating') {
        return ['grey--text', 'text--darken-3', 'pr-2']
      } else if (status === 'Terminated') {
        return ['grey--text', 'text--darken-3', 'pr-2']
      } else if (status === 'LifecycleError') {
        return ['red--text', 'text--darken-3', 'pr-2']
      } else {
        return ['grey--text', 'text--darken-3', 'pr-2']
      }
    },
    statusClass: function () {
      var status = this.tenantRuntime.lifecycleStatus
      if (status === 'Initializing') {
        return ['green', 'darken-2']
      } else if (status === 'InitializationError') {
        return ['red', 'darken-3']
      } else if (status === 'Starting') {
        return ['green', 'darken-2']
      } else if (status === 'Started') {
        return ['green', 'darken-2']
      } else if (status === 'Stopping') {
        return ['grey', 'darken-3']
      } else if (status === 'Stopped') {
        return ['grey', 'darken-3']
      } else if (status === 'Terminating') {
        return ['grey', 'darken-3']
      } else if (status === 'Terminated') {
        return ['grey', 'darken-3']
      } else if (status === 'LifecycleError') {
        return ['red', 'darken-3']
      } else {
        return ['grey', 'darken-3']
      }
    },
    statusIcon: function () {
      var status = this.tenantRuntime.lifecycleStatus
      if (status === 'Initializing') {
        return 'fa-gear'
      } else if (status === 'InitializationError') {
        return 'fa-exclamation-circle'
      } else if (status === 'Starting') {
        return 'fa-gear'
      } else if (status === 'Started') {
        return 'fa-gear'
      } else if (status === 'Stopping') {
        return 'fa-gear'
      } else if (status === 'Stopped') {
        return 'fa-stop'
      } else if (status === 'Terminating') {
        return 'fa-times'
      } else if (status === 'Terminated') {
        return 'fa-times'
      } else if (status === 'LifecycleError') {
        return 'fa-bolt'
      } else {
        return 'fa-question'
      }
    },
    statusIconClass: function () {
      var status = this.tenantRuntime.lifecycleStatus
      if (status === 'Initializing') {
        return ['fa-spin', 'green--text']
      } else if (status === 'InitializationError') {
        return ['red--text', 'text--lighten-4']
      } else if (status === 'Starting') {
        return ['fa-spin', 'green--text']
      } else if (status === 'Started') {
        return ['green--text']
      } else if (status === 'Stopping') {
        return ['grey--text', 'text--lighten-4']
      } else if (status === 'Stopped') {
        return ['grey--text', 'text--lighten-4']
      } else if (status === 'Terminating') {
        return ['grey--text', 'text--lighten-4']
      } else if (status === 'Terminated') {
        return ['grey--text', 'text--lighten-4']
      } else if (status === 'LifecycleError') {
        return ['red--text', 'text--lighten-4']
      } else {
        return ['grey--text', 'text--lighten-4']
      }
    }
  },

  methods: {
  }
}
</script>

<style scoped>
</style>
