<template>
  <span>
    <v-dialog v-model="visible" persistent width="500">
      <v-card>
        <div class="stage-dialog blue darken-2 white--text headline">
          Stage Tenant Updates
        </div>
        <v-card-text>
          Are you sure that you want to stage the current updates? These will be applied
          the next time the tenant is restarted.
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn class="grey--text darken-1" flat="flat" @click.native="onCancelClicked">Cancel</v-btn>
          <v-btn class="blue--text darken-1" flat="flat" @click.native="onStageClicked">Stage Updates</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <floating-action-button label="Stage Updates" icon="cloud_upload"
      @action="openDialog">
    </floating-action-button>
  </span>
</template>

<script>
import {_stageTenantUpdates} from '../../http/sitewhere-api-wrapper'
import FloatingActionButton from '../common/FloatingActionButton'

export default {

  data: () => ({
    visible: false
  }),

  components: {
    FloatingActionButton
  },

  props: ['tenantId', 'json'],

  methods: {
    // Called to open the dialog.
    openDialog: function () {
      this.$data.visible = true
    },

    // Called to open the dialog.
    closeDialog: function () {
      this.$data.visible = false
    },

    // Called to show an error message.
    showError: function (error) {
      this.$data.error = error
    },

    // Called after stage button is clicked.
    onStageClicked: function (e) {
      var component = this
      _stageTenantUpdates(this.$store, this.tenantId, this.json)
        .then(function (response) {
          component.closeDialog()
          component.$emit('staged')
        }).catch(function (e) {
        })
    },

    // Called after cancel button is clicked.
    onCancelClicked: function (e) {
      this.$data.visible = false
    }
  }
}
</script>

<style scoped>
.stage-dialog {
  padding: 10px;
  width: 100%;
}
</style>
