<template>
  <v-card v-if="unsaved" class="yellow lighten-4 elevation-1">
    <v-card-text class="subtitle">
      <v-icon large class="yellow--text text--darken-3">warning</v-icon>
      The microservice configuation has been updated.
      <v-btn class="green white--text" @click.native="onSave">
        Save Changes <v-icon fa class="white--text pl-2">cloud-upload</v-icon>
      </v-btn>
      <v-btn class="red white--text" @click.native="onRevert">
        Revert Changes <v-icon fa class="white--text pl-2">times</v-icon>
      </v-btn>
    </v-card-text>
  </v-card>
</template>

<script>
export default {

  data: () => ({
  }),

  props: ['unsaved'],

  methods: {
    onSave: function () {
      this.$emit('save')
    },
    onRevert: function () {
      this.$emit('revert')
    }
  }
}
</script>

<style scoped>
</style>
