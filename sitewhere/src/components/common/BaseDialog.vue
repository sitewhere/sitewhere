<template>
  <v-dialog v-model="visible" persistent :width="width">
    <v-card class="create-dialog">
      <v-card-row class="create-dialog">
        <span class="blue darken-2 white--text">{{title}}</span>
      </v-card-row>
      <v-card-row slot="error" v-if="error">
        <v-alert class="ma-0" error v-bind:value="true" style="width: 100%">
          {{error}}
        </v-alert>
      </v-card-row>
      <v-card-row>
        <slot>
            <div>Your content goes here!</div>
        </slot>
      </v-card-row>
      <v-card-row actions>
        <v-btn class="grey--text darken-1" flat="flat" @click.native="onCancelClicked">{{cancelLabel}}</v-btn>
        <v-btn class="blue--text darken-1" flat="flat" @click.native="onCreateClicked">{{createLabel}}</v-btn>
      </v-card-row>
    </v-card>
  </v-dialog>
</template>

<script>
export default {

  data: () => ({
  }),

  props: ['title', 'width', 'visible', 'createLabel', 'cancelLabel', 'error'],

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
.create-dialog span {
  padding: 10px 10px 0px;
  font-size: 30px;
  width: 100%;
}
</style>
