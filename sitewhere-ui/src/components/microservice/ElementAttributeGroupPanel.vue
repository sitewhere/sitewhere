<template>
  <v-card class="mb-3">
    <v-card-text class="subheading blue darken-2 white--text pa-2">
      <strong>{{ group.id ? group.description : 'Component Attributes' }}</strong>
    </v-card-text>
    <v-container fluid>
      <attribute-field v-for="attribute in group.attributes"
        :key="attribute.name" :attribute="attribute"
        :attrValues="attrValues">
      </attribute-field>
    </v-container>
  </v-card>
</template>

<script>
import AttributeField from './AttributeField'

export default {

  data: () => ({
  }),

  components: {
    AttributeField
  },

  props: ['group', 'attrValues'],

  methods: {
  }
}
</script>

<style scoped>
</style>
