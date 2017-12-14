<template>
  <div>
    <v-card v-if="tenant" color="white" class="grey--text">
      <v-layout class="pa-2" row>
        <v-flex xs3>
          <v-card-media :src="tenant.logoUrl" height="60px" contain>
          </v-card-media>
        </v-flex>
        <v-flex xs9>
          <v-card-title class="pa-3" primary-title>
            <div class="headline">{{ tenant.name }}</div>
            <v-spacer></v-spacer>
            <slot name="buttons"></slot>
          </v-card-title>
        </v-flex>
      </v-layout>
    </v-card>
  </div>
</template>

<script>
import HeaderField from '../common/HeaderField'

export default {

  data: () => ({
    copyData: null,
    showIdCopied: false
  }),

  props: ['tenant'],

  components: {
    HeaderField
  },

  methods: {
  }
}
</script>

<style scoped>
</style>
