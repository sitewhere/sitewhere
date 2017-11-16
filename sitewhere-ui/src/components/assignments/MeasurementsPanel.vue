<template>
  <v-card>
    <v-card-text>
      <v-data-table class="elevation-0" :headers="headers" :items="mxs"
        :rows-per-page-items="pagesize" no-data-text="No measurements have been added">
        <template slot="items" slot-scope="props">
          <td width="250px" :title="props.item.name">
            {{ (props.item.name.length > 25) ? props.item.name.substring(0, 25) + "..." : props.item.name }}
          </td>
          <td width="370px" :title="props.item.value">
            {{ props.item.value }}
          </td>
          <td v-if="!readOnly" width="20px">
            <v-btn icon @click.native="onDeleteMx(props.item.name)"
              v-tooltip:left="{ html: 'Delete Measurement' }">
              <v-icon class="grey--text">delete</v-icon>
            </v-btn>
          </td>
        </template>
      </v-data-table>
    </v-card-text>
    <v-alert error :value="true" class="ma-0" style="width: 100%" v-if="error">
      {{error}}
    </v-alert>
    <v-card-text v-if="!readOnly" class="blue darken-2 pa-0">
      <v-container fluid class="mr-4 pt-1 pb-0">
        <v-layout row>
          <v-flex xs4>
            <v-text-field dark label="Name" v-model="newMxName"></v-text-field>
          </v-flex>
          <v-flex xs7>
            <v-text-field type="number" dark label="Value"
              v-model="newMxValue">
            </v-text-field>
          </v-flex>
          <v-flex xs1 class="pt-3">
            <v-btn icon @click.native="onAddMx"
              v-tooltip:left="{ html: 'Add Measurement' }">
              <v-icon large class="white--text">add_circle</v-icon>
            </v-btn>
          </v-flex>
        </v-layout>
      </v-container>
    </v-card-text>
  </v-card>
</template>

<script>
import Utils from '../common/Utils'

export default {

  data: () => ({
    pagesize: [5],
    newMxName: '',
    newMxValue: 0.0,
    error: null
  }),

  props: ['mxs', 'readOnly'],

  created: function () {
    this.$data.newMxName = ''
    this.$data.newMxValue = 0.0
    this.$data.error = null
  },

  computed: {
    headers: function () {
      if (!this.readOnly) {
        return [
          {
            align: 'left',
            sortable: false,
            text: 'Name',
            value: 'name'
          }, {
            align: 'left',
            sortable: false,
            text: 'Value',
            value: 'value'
          }, {
            align: 'left',
            sortable: false,
            text: 'Delete',
            value: 'value'
          }
        ]
      } else {
        return [
          {
            align: 'left',
            sortable: false,
            text: 'Name',
            value: 'name'
          }, {
            align: 'left',
            sortable: false,
            text: 'Value',
            value: 'value'
          }
        ]
      }
    }
  },

  methods: {
    // Converts associative format to flat.
    buildFlatMetadata: function (input) {
      return Utils.metadataToArray(input)
    },

    // Converts flat format into associative.
    buildAssociativeMetadata: function (input) {
      return Utils.arrayToMetadata(input)
    },

    // Let owner know an item was deleted.
    onDeleteMx: function (name) {
      this.$emit('itemDeleted', name)
    },

    // Let owner know an item was added.
    onAddMx: function () {
      var mx = {}
      mx['name'] = this.$data.newMxName
      mx['value'] = this.$data.newMxValue
      var error = null

      // Check for empty.
      if (mx.name.length === 0) {
        error = 'Name must not be empty.'
      }

      // Check for bad characters.
      var regex = /^[\w-\\.]+$/
      if (!error && !regex.test(mx.name)) {
        error = 'Name contains invalid characters.'
      }

      if (!error) {
        this.$emit('mxAdded', mx)
        this.$data.newMxName = ''
        this.$data.newMxValue = ''
        this.$data.error = null
      } else {
        this.$data.error = error
      }
    }
  }
}
</script>

<style scoped>
</style>
