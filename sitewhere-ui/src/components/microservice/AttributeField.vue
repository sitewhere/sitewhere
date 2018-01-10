<template>
  <v-layout row wrap class="mb-2">
    <v-flex xs4 class="text-xs-right subheading mt-1">
      <strong>{{ attribute.name }}</strong>:
    </v-flex>
    <v-flex xs1>
    </v-flex>
    <v-flex xs6>
      <span v-if="readOnly">
        {{attrValue}}
      </span>
      <v-select v-else-if="attribute.choices"
        :items="attribute.choices" hide-details v-model="attrValue"
        item-text="name" item-value="value">
      </v-select>
      <v-text-field v-else-if="attribute.type === 'String'"
        :required="attribute.required" v-model="attrValue"
        hide-details single-line>
      </v-text-field>
      <v-text-field v-else-if="attribute.type === 'Integer'"
        :required="attribute.required" type="number"
        class="mt-1" v-model="attrValue" hide-details single-line>
      </v-text-field>
      <v-checkbox v-else-if="attribute.type === 'Boolean'"
        :label="attribute.name" v-model="attrValue">
      </v-checkbox>
    </v-flex>
    <v-flex xs1>
      <v-tooltip left>
        <v-icon class="blue--text text--darken-2" slot="activator">
          fa-question-circle
        </v-icon>
        <span>{{ attribute.description }}</span>
      </v-tooltip>
    </v-flex>
  </v-layout>
</template>

<script>
export default {

  data: () => ({
  }),

  props: ['attribute', 'attrValue', 'readOnly'],

  watch: {
    // Send update if value changed.
    attrValue: function (newValue) {
      var updated = {}
      updated.attribute = this.attribute
      updated.oldValue = this.$data.attrValue
      updated.newValue = newValue
      this.$emit('valueUpdated', updated)
    }
  },

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
