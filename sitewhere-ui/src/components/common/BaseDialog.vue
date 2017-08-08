<template>
  <v-dialog v-model="visible" persistent :width="width">
    <v-card>
      <div class="create-dialog blue darken-2 white--text headline">
        {{title}}
      </div>
      <error-banner :error="error"></error-banner>
      <v-card-text>
        <slot>
            <div>Your content goes here!</div>
        </slot>
      </v-card-text>
      <v-card-actions v-if="!hideButtons">
        <v-spacer></v-spacer>
        <v-btn class="grey--text darken-1" flat="flat" @click.native="onCancelClicked">{{cancelLabel}}</v-btn>
        <v-btn :disabled="invalid" class="blue--text darken-1" flat="flat" @click.native="onCreateClicked">{{createLabel}}</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import ErrorBanner from '../common/ErrorBanner'

export default {

  data: () => ({
  }),

  components: {
    ErrorBanner
  },

  props: ['title', 'width', 'visible', 'createLabel', 'cancelLabel', 'error', 'hideButtons', 'invalid'],

  methods: {
    // Called when create button is clicked.
    onCancelClicked: function (e) {
      this.$emit('cancelClicked', e)
    },

    // Called when create button is clicked.
    onCreateClicked: function (e) {
      this.$emit('createClicked', e)
    }
  }
}
</script>

<style scoped>
.create-dialog {
  padding: 10px;
  width: 100%;
}
</style>
