export default {

  // Style a card based on assignment status.
  styleForAssignmentStatus: function (assignment) {
    return {
      'background-color': this.assignmentBackgroundColor(assignment),
      'border': '1px solid ' + this.assignmentBorderColor(assignment),
      'border-top': '5px solid ' + this.assignmentHeaderColor(assignment)
    }
  },

  // Get background color for panel.
  assignmentBackgroundColor: function (assignment) {
    if (assignment.status === 'Active') {
      return '#f5fff5'
    } else if (assignment.status === 'Missing') {
      return '#fff5f5'
    } else {
      return '#f0f0f0'
    }
  },

  // Get border color for panel.
  assignmentBorderColor: function (assignment) {
    if (assignment.status === 'Active') {
      return '#99cc99'
    } else if (assignment.status === 'Missing') {
      return '#cc9999'
    } else {
      return '#dcdcdc'
    }
  },

  // Get header color for panel.
  assignmentHeaderColor: function (assignment) {
    if (assignment.status === 'Active') {
      return '#007700'
    } else if (assignment.status === 'Missing') {
      return '#dc0000'
    } else {
      return '#333333'
    }
  }
}
