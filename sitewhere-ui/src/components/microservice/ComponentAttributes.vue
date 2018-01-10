<template>
  <div v-if="currentContext.groups && currentContext.groups.length">
    <v-card class="grey lighten-4 mb-3"
      v-for="group in currentContext.groups"
      :key="group.id">
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
            :attrValues="attributeValues" :readOnly="readOnly">
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
  }),

  props: ['currentContext', 'readOnly'],

  components: {
    AttributeField
  },

  computed: {
    // Compute attribute values for current context.
    attributeValues: function () {
      if (this.currentContext) {
        var attributes = this.currentContext['config'].attributes
        return Utils.arrayToMetadata(attributes)
      }
      return {}
    }
  },

  methods: {
  }
}
</script>

<style scoped>
</style>
