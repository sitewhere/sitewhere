<template>
  <v-card class="grey lighten-3 black--text pa-0">
    <slot  v-if="results && results.numResults === 0" name="noresults"></slot>
    <v-divider></v-divider>
    <v-container v-if="results && results.numResults > 0" class="ma-0 pa-0">
      <v-layout row wrap>
        <v-flex xs2>
          <v-subheader class="ma-0 pt-0 pr-0">Rows per page</v-subheader>
        </v-flex>
        <v-flex xs3>
          <v-btn-toggle v-model="pageSize" class="mt-1">
            <v-btn flat :value="entry.value"
              v-for="entry in pageSizesWithDefaults" :key="entry.value">
              {{ entry.text }}
            </v-btn>
          </v-btn-toggle>
        </v-flex>
        <v-flex xs4>
          <v-tooltip top>
            <v-btn :disabled="!previousEnabled" icon light class="ml-0 mr-0"
              @click="onFirstPage" slot="activator">
              <v-icon light>fa-fast-backward</v-icon>
            </v-btn>
            <span>First Page</span>
          </v-tooltip>
          <v-tooltip top>
            <v-btn :disabled="!previousEnabled" icon light class="ml-0 mr-0"
              @click="onPreviousPage" slot="activator">
              <v-icon light>fa-backward</v-icon>
            </v-btn>
            <span>Previous Page</span>
          </v-tooltip>
          <v-tooltip top>
            <v-btn icon light class="ml-0 mr-0"
              @click="onRefresh" slot="activator">
              <v-icon light>fa-refresh</v-icon>
            </v-btn>
            <span>Refresh</span>
          </v-tooltip>
          <v-tooltip top>
            <v-btn :disabled="!nextEnabled" icon light class="ml-0 mr-0"
              @click="onNextPage" slot="activator">
              <v-icon light>fa-forward</v-icon>
            </v-btn>
            <span>Next Page</span>
          </v-tooltip>
          <v-tooltip top>
            <v-btn :disabled="!nextEnabled" icon light class="ml-0 mr-0"
              @click="onLastPage" slot="activator">
              <v-icon light>fa-fast-forward</v-icon>
            </v-btn>
            <span>Last Page</span>
          </v-tooltip>
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
    // Called to refresh data.
    onRefresh: function () {
      this.onPagingUpdated()
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
