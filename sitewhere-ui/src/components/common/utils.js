import moment from 'moment'

export default {

  /**
   * Format date in YYYY-MM-DD H:mm:ss format. N/A for null.
   */
  formatDate: function (date) {
    if (!date) {
      return 'N/A'
    }
    return moment(date).format('YYYY-MM-DD H:mm:ss')
  },

  // Rounds to four decimal places
  fourDecimalPlaces: function (val) {
    return Number(Math.round(val + 'e4') + 'e-4').toFixed(4)
  },

  // Converts metadata object into array.
  metadataToArray: function (meta) {
    var flat = []
    if (meta) {
      for (var key in meta) {
        if (meta.hasOwnProperty(key)) {
          flat.push({name: key, value: meta[key]})
        }
      }
    }
    return flat
  },

  // Converts array to metadata object.
  arrayToMetadata: function (arrayMeta) {
    var metadata = {}
    for (var i = 0; i < arrayMeta.length; i++) {
      metadata[arrayMeta[i].name] = arrayMeta[i].value
    }
    return metadata
  },

  /**
   * Routes to a applicaton-relative URL.
   */
  routeTo: function (component, url) {
    var tenant = component.$store.getters.selectedTenant
    if (tenant) {
      component.$router.push('/admin/' + tenant.id + url)
    }
  }
}
