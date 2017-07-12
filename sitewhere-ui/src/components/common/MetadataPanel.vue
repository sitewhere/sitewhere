<template>
  <v-card>
    <v-card-text>
      <v-data-table class="elevation-0" :headers="headers" :items="metadata"
        :rows-per-page-items="pagesize" no-data-text="No metadata has been assigned">
        <template slot="items" scope="props">
          <td width="250px" :title="props.item.name">
            {{ (props.item.name.length > 25) ? props.item.name.substring(0, 25) + "..." : props.item.name }}
          </td>
          <td width="370px" :title="props.item.value">
            {{ (props.item.value.length > 50) ? props.item.value.substring(0, 50) + "..." : props.item.value }}
          </td>
          <td width="20px">
            <v-btn icon @click.native="onDeleteItem(props.item.name)"
              v-tooltip:left="{ html: 'Delete Item' }">
              <v-icon class="grey--text">delete</v-icon>
            </v-btn>
          </td>
        </template>
      </v-data-table>
    </v-card-text>
    <v-alert error :value="true" class="ma-0" style="width: 100%" v-if="error">
      {{error}}
    </v-alert>
    <v-card-text class="blue darken-2 pa-0">
      <v-container fluid class="mr-4 pt-1 pb-0">
        <v-layout row>
          <v-flex xs4>
            <v-text-field dark label="Name" v-model="newItemName"></v-text-field>
          </v-flex>
          <v-flex xs7>
            <v-text-field dark label="Value" v-model="newItemValue"></v-text-field>
          </v-flex>
          <v-flex xs1 class="pt-3">
            <v-btn icon @click.native="onAddItem" v-tooltip:left="{ html: 'Add Item' }">
              <v-icon large class="white--text">add_circle</v-icon>
            </v-btn>
          </v-flex>
        </v-layout>
      </v-container>
    </v-card-text>
  </v-card>
</template>

<script>
export default {

  data: () => ({
    pagesize: [5],
    newItemName: '',
    newItemValue: '',
    headers: [
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
    ],
    error: null
  }),

  props: ['metadata'],

  created: function () {
    this.$data.newItemName = ''
    this.$data.newItemValue = ''
    this.$data.error = null
  },

  methods: {
    // Converts associative format to flat.
    buildFlatMetadata: function (input) {
      var result = []
      for (var key in input) {
        if (input.hasOwnProperty(key)) {
          result.push({name: key, value: input[key]})
        }
      }
      return result
    },

    // Converts flat format into associative.
    buildAssociativeMetadata: function (input) {
      var result = {}
      for (var i = 0; i < input.length; i++) {
        result[input.name] = input.value
      }
      return result
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
