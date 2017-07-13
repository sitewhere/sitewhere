<template>
  <v-dialog v-model="visible" persistent :width="width">
    <v-card>
      <div class="create-dialog blue darken-2 white--text">
        {{title}}
      </div>
      <v-card-text slot="error" v-if="error">
        <v-alert class="ma-0" error v-bind:value="true" style="width: 100%">
          {{error.message}}
        </v-alert>
      </v-card-text>
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
export default {

  data: () => ({
  }),

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
  font-size: 26px;
  width: 100%;
}
</style>
