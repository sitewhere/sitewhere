<template>
  <div>
    <base-dialog title="Create Site" buttonTooltip="Add Site" width="600"
      @createClicked="onCreateClicked" @cancelClicked="onCancelClicked"
      :visible="dialogVisible">
      <v-tabs light v-model="active">
        <v-tabs-bar slot="activators" class="blue darken-2">
          <v-tabs-slider></v-tabs-slider>
          <v-tabs-item key="1" href="#tab1">
            Site Details
          </v-tabs-item>
          <v-tabs-item key="2" href="#tab2">
            Map Information
          </v-tabs-item>
          <v-tabs-item key="3" href="#tab3">
            Metadata
          </v-tabs-item>
        </v-tabs-bar>
        <v-tabs-content key="1" id="tab1">
          <v-card flat>
            <v-card-row>
              <v-card-text>
                <v-text-field label="Site name" v-model="site.name" required></v-text-field>
                <v-text-field label="Description" v-model="site.description" required></v-text-field>
                <v-text-field label="Image URL" v-model="site.imageUrl" required></v-text-field>
              </v-card-text>
            </v-card-row>
          </v-card>
        </v-tabs-content>
        <v-tabs-content key="2" id="tab2">
          <v-card flat>
            <v-card-text>XXX</v-card-text>
          </v-card>
        </v-tabs-content>
        <v-tabs-content key="3" id="tab3">
          <metadata-panel :metadata="metadata"
            @itemDeleted="onMetadataDeleted" @itemAdded="onMetadataAdded"/>
        </v-tabs-content>
      </v-tabs>
    </base-dialog>
    <v-btn slot="activator" floating class="add-button red darken-1"
      v-tooltip:top="{ html: 'Add Site' }" @click.native.stop="onOpenDialog">
      <v-icon light>add</v-icon>
    </v-btn>
  </div>
</template>

<script>
import BaseDialog from '../common/BaseDialog'
import MetadataPanel from '../common/MetadataPanel'

export default {

  data: () => ({
    active: null,
    dialogVisible: false,
    site: {
      name: '',
      description: '',
      imageUrl: ''
    },
    metadata: [
      {
        'name': 'derek',
        'value': 'developer'
      }, {
        'name': 'bobby',
        'value': 'engineer'
      }
    ]
  }),

  components: {
    BaseDialog,
    MetadataPanel
  },

  methods: {
    // Called to open the dialog.
    onOpenDialog: function (e) {
      this.$data.dialogVisible = true
    },

    // Called after create button is clicked.
    onCreateClicked: function (e) {
      this.$data.dialogVisible = false
    },

    // Called after cancel button is clicked.
    onCancelClicked: function (e) {
      this.$data.dialogVisible = false
    },

    // Called when a metadata entry has been deleted.
    onMetadataDeleted: function (name) {
      var metadata = this.$data.metadata
      for (var i = 0; i < metadata.length; i++) {
        if (metadata[i].name === name) {
          metadata.splice(i, 1)
        }
      }
    },

    // Called when a metadata entry has been added.
    onMetadataAdded: function (entry) {
      var metadata = this.$data.metadata
      metadata.push(entry)
    }
  }
}
</script>

<style scoped>
.add-button {
  position: absolute;
  bottom: 16px;
  right: 16px;
}
</style>
