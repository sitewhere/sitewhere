<template>
  <v-card class="elevation-1 grey lighten-4 black--text pa-0 mb-3">
    <v-container class="ma-0 pa-0">
      <v-layout row wrap>
        <v-flex xs2>
          <v-subheader class="ma-0 pt-0 pr-0 right">Rows per page</v-subheader>
        </v-flex>
        <v-flex xs3 style="margin-top: 6px; height: 10px;">
          <v-btn-toggle :options="pageSizesWithDefaults" v-model="pageSize"></v-btn-toggle>
        </v-flex>
        <v-flex xs4>
          <v-btn :disabled="!previousEnabled" icon dark class="ml-0 mr-0"
            v-tooltip:top="{ html: 'First Page' }" @click.native="onFirstPage">
            <v-icon light>skip_previous</v-icon>
          </v-btn>
          <v-btn :disabled="!previousEnabled" icon dark class="ml-0 mr-0"
            v-tooltip:top="{ html: 'Previous Page' }" @click.native="onPreviousPage">
            <v-icon light>keyboard_arrow_left</v-icon>
          </v-btn>
          <v-btn :disabled="!nextEnabled" icon dark class="ml-0 mr-0"
            v-tooltip:top="{ html: 'Next Page' }" @click.native="onNextPage">
            <v-icon light>keyboard_arrow_right</v-icon>
          </v-btn>
          <v-btn :disabled="!nextEnabled" icon dark class="ml-0 mr-0"
            v-tooltip:top="{ html: 'Last Page' }" @click.native="onLastPage">
            <v-icon light>skip_next</v-icon>
          </v-btn>
        </v-flex>
        <v-flex xs3>
          <v-subheader class="ma-0 pt-0 right">{{ description }}</v-subheader>
        </v-flex>
      </v-layout>
    </v-container>
  </v-card>
</template>

<script>
export default {

  data: () => ({
    page: 1,
    pageSize: null,
    defaultResults: {
      numResults: 0,
      results: []
    },
    defaultPageSizes: [
      {
        text: '10',
        value: 10
      }, {
        text: '25',
        value: 25
      }, {
        text: '50',
        value: 50
      }
    ]
  }),

  props: ['results', 'pageSizes'],

  created: function () {
    var pageSize = this.$data.pageSize
    if (!pageSize) {
      var pageSizes = this.pageSizesWithDefaults
      this.$data.pageSize = pageSizes[0].value
    }
  },

  computed: {
    // Results with defaults fallback.
    resultsWithDefaults: function () {
      return this.results || this.$data.defaultResults
    },

    // Total record count.
    total: function () {
      return this.resultsWithDefaults.numResults
    },

    // Description.
    description: function () {
      var size = this.$data.pageSize
      var total = this.total
      var page = this.$data.page
      var first = (size * (page - 1)) + 1
      var last = Math.min(total, first + size - 1)
      return '' + first + '-' + last + ' of ' + total
    },

    // Calculate number of pages.
    pageCount: function () {
      var results = this.resultsWithDefaults
      var total = results.numResults
      var size = this.$data.pageSize
      var mod = (total % size)
      var count = (total / size | 0)
      count += (mod > 0) ? 1 : 0
      return count
    },

    // Get list of available page sizes with fallback defaults.
    pageSizesWithDefaults: function () {
      return this.pageSizes || this.$data.defaultPageSizes
    },

    // Indicates if 'first' button should be enabled.
    previousEnabled: function () {
      var page = this.$data.page
      return page > 1
    },

    // Indicates if 'first' button should be enabled.
    nextEnabled: function () {
      var page = this.$data.page
      var count = this.pageCount
      return page < count
    }
  },

  watch: {
    pageSize: function (newSize) {
      this.$data.page = 1
      this.onPagingUpdated()
    }
  },

  methods: {
    // Called to move to first page.
    onFirstPage: function () {
      var page = this.$data.page
      if (page !== 1) {
        this.$data.page = 1
        this.onPagingUpdated()
      }
    },
    // Called to move to previous page.
    onPreviousPage: function () {
      var page = this.$data.page
      if (page > 1) {
        this.$data.page = (--page)
        this.onPagingUpdated()
      }
    },
    // Called to move to next page.
    onNextPage: function () {
      var page = this.$data.page
      var count = this.pageCount
      if (page < count) {
        this.$data.page = (++page)
        this.onPagingUpdated()
      }
    },
    // Called to move to last page.
    onLastPage: function () {
      var page = this.$data.page
      var count = this.pageCount
      if (page < count) {
        this.$data.page = count
        this.onPagingUpdated()
      }
    },
    // Called when paging values are updated.
    onPagingUpdated: function () {
      var page = this.$data.page
      var pageSize = this.$data.pageSize
      var paging = {
        page: page,
        pageSize: pageSize,
        query: 'page=' + page + '&pageSize=' + pageSize
      }
      this.$emit('pagingUpdated', paging)
    }
  }
}
</script>

<style scoped>
</style>
