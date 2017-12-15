<template>
  <div>
    <v-card v-if="tenant" color="white" class="grey--text">
      <v-layout class="pt-2 pb-2 pl-1 pr-1" row>
        <v-flex xs3>
          <v-card-media :src="tenant.logoUrl" height="100%" contain>
          </v-card-media>
        </v-flex>
        <v-flex xs9>
          <v-card-title class="pa-0" primary-title>
            <div class="headline pt-1">{{ tenant.name }}</div>
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
