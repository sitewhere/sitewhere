<template>
  <v-card>
    <v-card-text>
      <v-data-table class="elevation-0" :headers="headers" :items="metadata"
        :rows-per-page-items="pagesize" :no-data-text="noDataText">
        <template slot="items" slot-scope="props">
          <td width="250px" :title="props.item.name">
            {{ (props.item.name.length > 25) ? props.item.name.substring(0, 25) + "..." : props.item.name }}
          </td>
          <td width="370px" :title="props.item.value">
            {{ (props.item.value.length > 50) ? props.item.value.substring(0, 50) + "..." : props.item.value }}
          </td>
          <td v-if="!readOnly" width="20px">
            <v-tooltip left>
              <v-btn icon @click="onDeleteItem(props.item.name)" slot="activator">
                <v-icon class="grey--text">delete</v-icon>
              </v-btn>
              <span>Delete Item</span>
            </v-tooltip>
          </td>
        </template>
      </v-data-table>
    </v-card-text>
    <v-alert error :value="true" class="ma-0" style="width: 100%" v-if="error">
      {{error}}
    </v-alert>
    <v-card-text v-if="!readOnly" class="grey lighten-3 pa-0">
      <v-container fluid class="mr-4 pt-1 pb-0">
        <v-layout row>
          <v-flex xs4>
            <v-text-field light label="Name" v-model="newItemName"></v-text-field>
          </v-flex>
          <v-flex xs7>
            <v-text-field light label="Value" v-model="newItemValue"></v-text-field>
          </v-flex>
          <v-flex xs1 class="pt-3">
            <v-tooltip left>
              <v-btn icon @click.native="onAddItem" slot="activator">
                <v-icon large class="blue--text text--darken-2">add_circle</v-icon>
              </v-btn>
              <span>Add Item</span>
            </v-tooltip>
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
    newItemName: '',
    newItemValue: '',
    noDataText: 'No metadata has been assigned',
    error: null
  }),

  props: ['metadata', 'readOnly', 'noDataMessage'],

  created: function () {
    this.$data.newItemName = ''
    this.$data.newItemValue = ''
    this.$data.error = null

    if (this.noDataMessage) {
      this.$data.noDataText = this.noDataMessage
    }
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
    onDeleteItem: function (name) {
      this.$emit('itemDeleted', name)
    },

    // Let owner know an item was added.
    onAddItem: function () {
      var item = {}
      item['name'] = this.$data.newItemName
      item['value'] = this.$data.newItemValue
      var error = null

      // Check for empty.
      if (item.name.length === 0) {
        error = 'Name must not be empty.'
      }

      // Check for bad characters.
      var regex = /^[\w-]+$/
      if (!error && !regex.test(item.name)) {
        error = 'Name contains invalid characters.'
      }

      if (!error) {
        this.$emit('itemAdded', item)
        this.$data.newItemName = ''
        this.$data.newItemValue = ''
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
