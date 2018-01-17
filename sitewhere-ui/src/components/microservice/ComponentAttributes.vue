<template>
  <div v-if="currentContext && currentContext.groups && currentContext.groups.length">
    <v-form v-model="formValid" ref="form" lazy-validation>
      <v-card class="grey lighten-4 mb-3"
        v-for="group in currentContext.groups" :key="group.id">
        <v-divider></v-divider>
        <v-toolbar flat dark dense card class="primary">
          <v-icon small dark>fa-info-circle</v-icon>
          <v-toolbar-title class="white--text subheading">
            {{ group.id ? group.description : 'Component Attributes' }}
          </v-toolbar-title>
        </v-toolbar>
        <v-card-text class="subheading pa-0 pl-2">
          <v-container fluid>
            <attribute-field v-for="attribute in group.attributes"
              :key="attribute.name" :attribute="attribute"
              :attrValues="attrValues" :readOnly="readOnly"
              @valueUpdated="onAttributeValueUpdated">
            </attribute-field>
          </v-container>
        </v-card-text>
      </v-card>
    </v-form>
  </div>
</template>

<script>
import Utils from '../common/Utils'
import AttributeField from './AttributeField'

export default {

  data: () => ({
    attrValues: {},
    formValid: true
  }),

  props: ['currentContext', 'readOnly', 'dirty'],

  components: {
    AttributeField
  },

  watch: {
    // Compute initial attribute values for current context.
    currentContext: function (context) {
      this.updateFromContext()
    },
    // Track dirty flag to allow for updates.
    dirty: function (value) {
      this.updateFromContext()
    }
  },

  methods: {
    // Update attribute values from context.
    updateFromContext: function () {
      this.$data.attrValues = {}
      if (this.currentContext && this.currentContext['config']) {
        var attributes = this.currentContext['config'].attributes
        this.$data.attrValues = Utils.arrayToMetadata(attributes)
      }
    },

    // Called when attribute value is updated.
    onAttributeValueUpdated: function (update) {
      this.$data.attrValues[update.attribute.localName] = update.newValue
      this.$emit('valuesUpdated', this.$data.attrValues)
    }
  }
}
</script>

<style scoped>
</style>
