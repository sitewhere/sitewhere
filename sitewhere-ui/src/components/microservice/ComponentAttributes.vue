<template>
  <div v-if="currentContext && currentContext.groups && currentContext.groups.length">
    <v-card class="grey lighten-4 mb-3"
      v-for="group in currentContext.groups" :key="group.id">
      <v-card-text
        class="subheading blue darken-2 white--text pa-2">
        <strong>
          {{ group.id ? group.description : 'Component Attributes' }}
        </strong>
      </v-card-text>
      <v-card-text class="subheading pa-0 pl-2">
        <v-container fluid>
          <attribute-field
            v-for="attribute in group.attributes"
            :key="attribute.name" :attribute="attribute"
            :attrValue="attrValues[attribute.localName]"
            :readOnly="readOnly" @valueUpdated="onAttributeValueUpdated">
          </attribute-field>
        </v-container>
      </v-card-text>
    </v-card>
  </div>
</template>

<script>
import Utils from '../common/Utils'
import AttributeField from './AttributeField'

export default {

  data: () => ({
    attrValues: {}
  }),

  props: ['currentContext', 'readOnly'],

  components: {
    AttributeField
  },

  watch: {
    // Compute initial attribute values for current context.
    currentContext: function (context) {
      this.$data.attrValues = {}
      if (context && context['config']) {
        var attributes = context['config'].attributes
        this.$data.attrValues = Utils.arrayToMetadata(attributes)
      }
    }
  },

  methods: {
    // Get updated values for attributes.
    getValues: function () {
      return this.$data.values
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
