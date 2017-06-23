<template>
  <v-card class="elevation-1 grey lighten-4 black--text pa-0 mb-3">
    <v-container class="ma-0 pa-0">
      <v-layout row wrap>
        <v-flex xs2 offset-xs7>
          <v-subheader class="ma-0 pt-0 pr-0 right">Rows per page</v-subheader>
        </v-flex>
        <v-flex xs1 style="margin-top: 8px; height: 10px;">
          <v-select class="ma-0 pt-0 pr-4" :items="pageSizes" v-model="pageSize"
            dark single-line></v-select>
        </v-flex>
        <v-flex xs2>
          <v-subheader class="ma-0 pt-0 right">1-10 of 1200</v-subheader>
        </v-flex>
      </v-layout>
    </v-container>
  </v-card>
</template>

<script>
export default {

  data: () => ({
    pageSize: null,
    defaultPageSizes: [
      {
        text: '25',
        value: 25
      }, {
        text: '50',
        value: 50
      }, {
        text: '100',
        value: 100
      }
    ]
  }),

  props: ['paging', 'pageSizes'],

  computed: {
    // Calculate number of pages.
    pageCount: function () {
      var paging = this.$data.paging
      if (paging) {
        var total = paging.total
        var size = paging.pageSize
        var mod = (total % size)
        var count = (total / size)
        count += (mod > 0) ? 1 : 0
        return count
      }
      return 0
    },

    // Get list of available page sizes.
    pageSizes: function () {
      var paging = this.paging
      if (paging) {
        return this.paging.pageSizes || this.$data.defaultPageSizes
      }
      return this.$data.defaultPageSizes
    }
  },

  methods: {
  }
}
</script>

<style scoped>
</style>
