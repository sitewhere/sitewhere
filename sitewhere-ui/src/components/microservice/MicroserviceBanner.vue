<template>
  <div>
    <v-breadcrumbs divider="/">
      <v-breadcrumbs-item v-for="context in wizardContexts"
        :key="context.model.localName"
        @click.native="onPopToContext(context.model.localName)">
        {{ context.model.name }}
      </v-breadcrumbs-item>
    </v-breadcrumbs>
    <v-card class="mb-3">
      <v-toolbar flat dark class="primary">
        <v-icon dark fa class="fa-lg">{{currentContext.model.icon}}</v-icon>
        <v-toolbar-title class="white--text">{{currentContext.model.name}}</v-toolbar-title>
        <v-spacer></v-spacer>
        <v-btn icon class="ml-0" v-if="wizardContexts.length > 1"
          @click.native="onPopContext">
          <v-icon fa class="fa-lg">arrow-up</v-icon>
        </v-btn>
        <v-btn icon class="ml-0" v-if="currentContext.model.attributes"
          @click.native="onConfigureCurrent">
          <v-icon fa class="fa-lg">gear</v-icon>
        </v-btn>
        <v-btn icon class="ml-0" v-if="currentContext.model.role.optional"
          @click.native="onDeleteCurrent">
          <v-icon fa class="fa-lg">times</v-icon>
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
