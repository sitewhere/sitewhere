<template>
  <v-layout row wrap class="mb-2">
    <v-flex xs4 class="text-xs-right subheading mt-1">
      <strong>{{ attribute.name }}</strong>:
    </v-flex>
    <v-flex xs1>
    </v-flex>
    <v-flex xs6>
      <span v-if="readOnly">
        {{attrValues[attribute.localName]}}
      </span>
      <v-text-field v-else-if="attribute.type === 'String'"
        :required="attribute.required"
        v-model="attrValues[attribute.localName]"
        hide-details single-line>
      </v-text-field>
      <v-text-field v-else-if="attribute.type === 'Integer'"
        :required="attribute.required" type="number"
        class="mt-1"
        v-model="attrValues[attribute.localName]"
        hide-details single-line>
      </v-text-field>
      <v-checkbox v-else-if="attribute.type === 'Boolean'"
        :label="attribute.name"
        v-model="attrValues[attribute.localName]">
      </v-checkbox>
    </v-flex>
    <v-flex xs1>
      <div v-tooltip:left="{ html: attribute.description }" >
        <v-icon fa class="fa-lg blue--text text--darken-2">question-circle</v-icon>
      </div>
    </v-flex>
  </v-layout>
</template>

<script>
export default {

  data: () => ({
  }),

  props: ['attribute', 'attrValues', 'readOnly'],

  methods: {
  }
}
</script>

<style scoped>
.input-group {
  padding: 0;
  margin: 0;
}
</style>
