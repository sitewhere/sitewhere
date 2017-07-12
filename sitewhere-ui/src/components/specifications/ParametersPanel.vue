<template>
  <v-card>
    <v-card-text>
      <v-data-table class="elevation-0" :headers="headers" :items="parameters"
        :rows-per-page-items="pagesize" no-data-text="Command has no existing parameters">
        <template slot="items" scope="props">
          <td width="250px" :title="props.item.name">
            {{ (props.item.name.length > 25) ? props.item.name.substring(0, 25) + "..." : props.item.name }}
          </td>
          <td width="350px" :title="props.item.type">
            {{ (props.item.type.length > 25) ? props.item.type.substring(0, 25) + "..." : props.item.type }}
          </td>
          <td width="200px">
            <span v-if="props.item.required">
              <v-icon fa class="grey--text fa-2x">check-circle</v-icon>
            </span>
          </td>
          <td width="20px">
            <v-btn icon @click.native="onDeleteParameter(props.item.name)"
              v-tooltip:top="{ html: 'Delete Parameter' }">
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
            <v-text-field dark label="Name" v-model="newParamName"></v-text-field>
          </v-flex>
          <v-flex xs4>
            <v-select dark :items="types" v-model="newParamType"
              item-text="text" item-value="datatype" label="Parameter Type">
            </v-select>
          </v-flex>
          <v-flex xs3>
            <v-checkbox dark label="Required" v-model="newParamRequired" light>
            </v-checkbox>
          </v-flex>
          <v-flex xs1 class="pt-2">
            <v-btn icon @click.native="onAddParameter" v-tooltip:top="{ html: 'Add Parameter' }">
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
    newParamName: null,
    newParamType: null,
    newParamRequired: null,
    headers: [
      {
        align: 'left',
        sortable: false,
        text: 'Parameter Name',
        value: 'name'
      }, {
        align: 'left',
        sortable: false,
        text: 'Parameter Type',
        value: 'type'
      }, {
        align: 'left',
        sortable: false,
        text: 'Required',
        value: 'required'
      }, {
        align: 'left',
        sortable: false,
        text: '',
        value: 'delete'
      }
    ],
    types: [
      {
        text: 'String',
        datatype: 'String'
      }, {
        text: 'Double',
        datatype: 'Double'
      }, {
        text: 'Float',
        datatype: 'Float'
      }, {
        text: 'Boolean',
        datatype: 'Bool'
      }, {
        text: 'Int32',
        datatype: 'Int32'
      }, {
        text: 'Int64',
        datatype: 'Int64'
      }, {
        text: 'UInt32',
        datatype: 'UInt32'
      }, {
        text: 'UInt64',
        datatype: 'UInt64'
      }, {
        text: 'SInt32',
        datatype: 'SInt32'
      }, {
        text: 'SInt64',
        datatype: 'SInt64'
      }, {
        text: 'Fixed32',
        datatype: 'Fixed32'
      }, {
        text: 'Fixed64',
        datatype: 'Fixed64'
      }, {
        text: 'SFixed32',
        datatype: 'SFixed32'
      }, {
        text: 'SFixed64',
        datatype: 'SFixed64'
      }],
    error: null
  }),

  props: ['parameters'],

  created: function () {
    this.$data.newParamName = null
    this.$data.newParamType = null
    this.$data.newParamRequired = false
    this.$data.error = null
  },

  methods: {
    // Called when a parameter is deleted.
    onDeleteParameter: function (name) {
      this.$emit('parameterDeleted', name)
    },

    // Called when a parameter is added.
    onAddParameter: function () {
      var parameter = {}
      parameter.name = this.$data.newParamName
      parameter.type = this.$data.newParamType
      parameter.required = this.$data.newParamRequired
      var error = null

      // Check for empty.
      if (parameter.name.length === 0) {
        error = 'Name must not be empty.'
      }

      // Check for bad characters.
      var regex = /^[\w-]+$/
      if (!error && !regex.test(parameter.name)) {
        error = 'Name contains invalid characters.'
      }

      if (!error) {
        this.$emit('parameterAdded', parameter)
        this.$data.newParamName = null
        this.$data.newParamType = null
        this.$data.newParamRequired = false
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
