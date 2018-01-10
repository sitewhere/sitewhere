<template>
  <div>
    <v-breadcrumbs divider="/">
      <v-breadcrumbs-item v-for="context in wizardContexts"
        :key="context.model.localName"
        @click="onPopToContext(context.model.localName)">
        {{ context.model.name }}
      </v-breadcrumbs-item>
    </v-breadcrumbs>
    <v-card class="mb-3">
      <v-toolbar flat dark card class="primary">
        <v-icon dark>fa-{{currentContext.model.icon}}</v-icon>
        <v-toolbar-title class="white--text">{{currentContext.model.name}}</v-toolbar-title>
        <v-spacer></v-spacer>
        <v-btn icon class="ml-0" v-if="wizardContexts.length > 1"
          @click="onPopContext">
          <v-icon class="fa-lg">fa-arrow-up</v-icon>
        </v-btn>
        <v-btn icon class="ml-0" v-if="currentContext.model.attributes"
          @click="onConfigureCurrent">
          <v-icon class="fa-lg">fa-gear</v-icon>
        </v-btn>
        <v-btn icon class="ml-0" v-if="currentContext.model.role.optional"
          @click="onDeleteCurrent">
          <v-icon class="fa-lg">fa-times</v-icon>
        </v-btn>
      </v-toolbar>
      <v-card-text v-html="currentContext.model.description"></v-card-text>
    </v-card>
  </div>
</template>

<script>
export default {

  data: () => ({
  }),

  props: ['currentContext', 'wizardContexts'],

  methods: {
    // Pop context from stack.
    onPopContext: function () {
      this.$emit('popContext')
    },
    // Pop context from stack.
    onConfigureCurrent: function () {
      this.$emit('configureCurrent')
    },
    // Pop context from stack.
    onDeleteCurrent: function () {
      this.$emit('deleteCurrent')
    }
  }
}
</script>

<style scoped>
</style>
