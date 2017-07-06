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
  }
}
