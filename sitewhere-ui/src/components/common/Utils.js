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

  /**
   * Format date in YYYY-MM-DD H:mm:ss format.
   */
  formatIso8601: function (date) {
    if (!date) {
      return null
    }
    return moment(date).toISOString()
  },

  /**
   * Parse date in YYYY-MM-DD H:mm:ss format.
   */
  parseIso8601: function (value) {
    if (!value) {
      return null
    }
    return new Date(moment(value))
  },

  // Tests whether a string is blank.
  isBlank: function (str) {
    return (!str || /^\s*$/.test(str))
  },

  // Short string with ellipsis if necessary.
  ellipsis: function (val, max) {
    return (val.length > max) ? (val.substring(0, max) + '...') : val
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
    if (arrayMeta) {
      for (var i = 0; i < arrayMeta.length; i++) {
        metadata[arrayMeta[i].name] = arrayMeta[i].value
      }
    }
    return metadata
  },

  // Indicates if logged-in user is authorized for all auths in list.
  isAuthForAll: function (component, list) {
    let user = component.$store.getters.user
    if (!user) {
      console.log('No user for permissions check.')
      return false
    }
    return list.every(auth => user.authorities.indexOf(auth) > -1)
  },

  /**
   * Routes to a applicaton-relative URL.
   */
  routeTo: function (component, url) {
    var tenant = component.$store.getters.selectedTenant
    if (tenant) {
      component.$router.push('/tenants/' + tenant.token + url)
    }
  },

  /**
   * Routes to device page for hardware id.
   */
  routeToDevice: function (component, hardwareId) {
    this.routeTo(component, '/devices/' + hardwareId)
  },

  // Returns paging value for all results.
  pagingForAllResults: function () {
    return 'page=1&pageSize=0'
  }
}
