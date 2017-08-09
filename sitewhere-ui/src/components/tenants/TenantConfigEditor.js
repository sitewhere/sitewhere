import $ from 'jquery'

/**
 * Wizard configuration.
 */
export var wizard = {

  /**
   * Configuration being edited.
   */
  config: null,

  /**
   * Tenant configuration model.
   */
  configModel: null,

  /**
   * Tenant configuration roles.
   */
  roles: null,

  /**
   * List of sites.
   */
  sites: null,

  /**
   * List of specifications.
   */
  specifications: null,

  /**
   * Stack of editor contexts.
   */
  editorContexts: []
}

/** Reset the wizard */
export function resetWizard () {
  wizard.editorContexts = []
  return addRootPanel()
}

/** Edit a component */
export function ceComponentEdit (topModel, topConfig, onCurrentEdited) {
  console.log('Editing a component')
}

/** Create a component */
export function ceComponentCreate (childModel, childConfig, onChildAdded) {
  console.log('Creating a component')
}

/** Show confirmation dialog */
export function swConfirm (dialogTitle, dialogMessage, callback) {
  console.log('Show confirmation dialog')
}

/** Add the root panel */
function addRootPanel () {
  var configNode = findConfigNodeByName(wizard.config, 'tenant-configuration')
  var modelNode = findModelNodeByName(wizard.configModel, 'tenant-configuration')
  return pushContext(configNode, modelNode)
}

/** Push context on the stack */
function pushContext (configNode, modelNode) {
  var context = {
    'config': configNode,
    'model': modelNode
  }
  wizard.editorContexts.push(context)
  return showPanelFor(context)
}

/** Pop elements off the stack until the given element name is found */
function popToContext (elementName) {
  var context = wizard.editorContexts[wizard.editorContexts.length - 1]
  var config = context['config']
  if ((config.name === elementName) || (wizard.editorContexts.length === 1)) {
    showPanelFor(context)
  } else {
    // Pop the top item and recurse.
    wizard.editorContexts.pop()
    popToContext(elementName)
  }
}

/** Pop up one level */
export function popOne () {
  if (wizard.editorContexts.length > 1) {
    var context = wizard.editorContexts[wizard.editorContexts.length - 2]
    var config = context['config']
    popToContext(config.name)
  }
}

/** Show panel for a given context */
function showPanelFor (context) {
  var configNode = context['config']
  var modelNode = context['model']

  var panel = '<div>'

  // Add breadcrumbs for quickly navigating to parent nodes.
  panel += addBreadcrumbs()

  panel += '<div class=\'wz-header\'>'
  panel += '<i class=\'wz-header-icon fa fa-' + modelNode.icon + ' fa-white\'></i>'
  panel += '<h1>' + modelNode.name + '</h1>'

  // Create buttons for various node actions.
  panel += createActionButtons(modelNode, configNode)

  panel += '<h2>' + modelNode.description + '</h2>'
  panel += '</div>'
  panel += '<div class=\'wz-divider\'>'

  // If model node has attributes, add form.
  if (modelNode.attributes) {
    panel += addAttributesForm(configNode, modelNode)
  }

  /** If children are configured, add navigation */
  panel += addChildElements(configNode, modelNode)

  panel += '</div>'
  panel += '</div>'

  return panel
}

/** Refresh contents of current panel */
function refresh () {
  return showPanelFor(wizard.editorContexts[wizard.editorContexts.length - 1])
}

/** Add buttons that allow actions to be taken on the current node */
function createActionButtons (modelNode, configNode) {
  let panel = ''

  // Do not allow actions at the root level.
  if (wizard.editorContexts.length > 1) {
    panel += '<div style=\'float: right;\' class=\'btn-group\'>'
    panel += '<a onclick=\'popOne()\' title=\'Up One Level\' class=\'btn\' href=\'javascript:void(0)\'>'
    panel += '<i class=\'fa fa-arrow-up sw-button-icon\'></i>'
    panel += '</a>'

    // Only allow configuration if there are configurable attributes.
    if (modelNode.attributes) {
      panel += '<a onclick=\'configureCurrent()\' title=\'Configure ' + modelNode.name +
        '\' class=\'btn\' href=\'javascript:void(0)\'>'
      panel += '<i class=\'fa fa-gear sw-button-icon\'></i>'
      panel += '</a>'
    }

    // Only allow optional elements to be deleted.
    if (modelNode.role.optional) {
      panel += '<a onclick=\'deleteCurrent()\' style=\'color: #900;\' title=\'Delete ' + modelNode.name +
        '\' class=\'btn\' href=\'javascript:void(0)\'>'
      panel += '<i class=\'fa fa-times sw-button-icon\'></i>'
      panel += '</a>'
    }
    panel += '</div>'
  }
  return panel
}

/** Configure the current element */
export function configureCurrent () {
  let top = wizard.editorContexts[wizard.editorContexts.length - 1]
  let topModel = top['model']
  let topConfig = top['config']
  ceComponentEdit(topModel, topConfig, onCurrentEdited)
}

/** Called after editing is complete */
function onCurrentEdited (updated) {
  refresh()
}

/** Add breadcrumbs to all access to parent nodes */
function addBreadcrumbs () {
  var bc = '<ol class=\'breadcrumb wz-breadcrumb\' style=\'margin-top: 8px;\' role=\'group\'>'
  for (var i = 0; i < wizard.editorContexts.length; i++) {
    var modelNode = wizard.editorContexts[i]['model']
    var configNode = wizard.editorContexts[i]['config']
    var active = (i === (wizard.editorContexts.length - 1))
    if (active) {
      bc += '<li class=\'active\'>' + modelNode.name + '</li>'
    } else {
      bc += '<li><a href=\'javacsript:void(0);\' onclick=\'popToContext("' + configNode.name + '")\'>' +
        modelNode.name + '</a><span class=\'divider\'>/</span></li>'
    }
  }
  bc += '</ol>'
  return bc
}

/** Add attributes form for panel */
function addAttributesForm (configNode, modelNode) {
  let section = '<form class=\'form-horizontal\'>'
  let valuesByName = []
  if (configNode.attributes) {
    for (var i = 0; i < configNode.attributes.length; i++) {
      valuesByName[configNode.attributes[i].name] = configNode.attributes[i].value
    }
  }
  let lastGroup
  for (let i = 0; i < modelNode.attributes.length; i++) {
    var attr = modelNode.attributes[i]
    if (attr.group !== lastGroup) {
      var groupName = modelNode.attributeGroups[attr.group]
      if (lastGroup) {
        section += '</div>'
      }
      section += '<div class=\'sw-attribute-group\'><h1>' + groupName + '</h1>'
      lastGroup = attr.group
    }
    section += '<div class=\'control-group\'>'
    section += '  <label class=\'control-label sw-control-label\' style=\'width: 275px;\' for=\'tc-' +
      attr.localName + '\'>' + attr.name + '<i class=\'fa fa-info-circle fa-white\' title=\'' +
      attr.description + '\'></i></label>'
    section += '  <div class=\'controls sw-controls\' style=\'margin-left: 300px;\'>'
    if (valuesByName[attr.localName]) {
      if ((attr.type === 'SiteReference') && wizard.sites) {
        var siteName = getSiteNamesByToken()[valuesByName[attr.localName]]
        section += '    ' + (siteName || valuesByName[attr.localName])
      } else if ((attr.type === 'SpecificationReference') && wizard.specifications) {
        var specName = getSpecificationNamesByToken()[valuesByName[attr.localName]]
        section += '    ' + (specName || valuesByName[attr.localName])
      } else {
        section += '    ' + valuesByName[attr.localName]
      }
    } else if (attr.defaultValue) {
      section += '    (defaulted to \'' + attr.defaultValue + '\')'
    }
    section += '  </div>'
    section += '</div>'
  }
  if (lastGroup) {
    section += '</div>'
  }
  section += '</form>'
  return section
}

/** Create lookup of site names by token */
function getSiteNamesByToken () {
  var mapped = {}
  for (var i = 0; i < wizard.sites.length; i++) {
    mapped[wizard.sites[i].token] = wizard.sites[i].name
  }
  return mapped
}

/** Create lookup of site names by token */
function getSpecificationNamesByToken () {
  var mapped = {}
  for (var i = 0; i < wizard.specifications.length; i++) {
    mapped[wizard.specifications[i].token] = wizard.specifications[i].name
  }
  return mapped
}

/** Add child element navigation for panel */
function addChildElements (configNode, modelNode) {
  let section = ''
  let childrenByRole = getConfigChildrenByRole(modelNode, configNode)
  let role = wizard.roles[modelNode.role]
  if (!role) {
    return section
  }
  let childRoles = getSpecializedRoleChildren(role, modelNode)

  // Loop through role children in order.
  for (let i = 0; i < childRoles.length; i++) {
    var childRoleName = childRoles[i]
    var childRole = wizard.roles[childRoleName]
    var childrenWithRole = childrenByRole[childRoleName]

    // Add children.
    if (childRole.multiple) {
      section += addSortableRoleChildren(childRoleName, childRole, childrenWithRole)
    } else {
      section += addNonSortableRoleChildren(childRoleName, childRole, childrenWithRole)
    }
  }

  // Show children that do not have a model.
  var noModel = childrenByRole['?']
  for (let i = 0; i < noModel.length; i++) {
    section += '<div class=\'wz-child\'>'
    section += '<h1>Unknown model element: ' + noModel[i] + '</h1>'
    section += '</div>'
  }

  // Add script that allows drag-and-drop sorting.
  section += '<scr' + 'ipt>'
  section += '$(\'.wz-sortable\').sortable({'
  section += '  placeholder: \'wz-sortable-placeholder\', '
  section += '  stop: function(event,ui) { childOrderChanged(\'' + childRoleName + '\'); }, '
  section += '  items : \'li.wz-sortable-item\' '
  section += '});'
  section += '</scr' + 'ipt>'

  return section
}

/** Called when drag-and-drop sorting is done */
export function childOrderChanged (roleName) {
  var ids = []
  $('.wz-child').each(function () {
    ids.push($(this).attr('id'))
  })
  var context = wizard.editorContexts[wizard.editorContexts.length - 1]
  var config = context['config']
  var reordered = []
  for (var i = 0; i < ids.length; i++) {
    var child = findConfigNodeById(config, ids[i])
    if (child) {
      reordered.push(child)
    }
  }
  config.children = reordered
}

/** Add html for child icon and name */
function addChildFields (childModel, childConfig, childRole) {
  let section = '<i class=\'wz-child-icon fa fa-' + childModel.icon + ' fa-white\'></i>'
  section += '<h1 class=\'wz-child-name\'>'
  if (childModel.deprecated) {
    section += '<i>' + childModel.name + ' [Deprecated]</i>'
  } else {
    section += childModel.name
  }

  // Show index value if specified.
  if (childModel.indexAttribute) {
    var modelAttr = null
    for (let i = 0; i < childModel.attributes.length; i++) {
      if (childModel.indexAttribute === childModel.attributes[i].localName) {
        modelAttr = childModel.attributes[i]
        break
      }
    }
    for (let j = 0; j < childConfig.attributes.length; j++) {
      var attrName = childConfig.attributes[j].name
      if (childModel.indexAttribute === attrName) {
        if (modelAttr && ((modelAttr.type === 'SiteReference') && wizard.sites)) {
          var siteName = getSiteNamesByToken()[childConfig.attributes[j].value]
          section += ' (' + (siteName || childConfig.attributes[j].value) + ')'
        } else if (modelAttr &&
          ((modelAttr.type === 'SpecificationReference') &&
          wizard.specifications)) {
          var specName = getSpecificationNamesByToken()[childConfig.attributes[j].value]
          section += ' (' + (specName || childConfig.attributes[j].value) + ')'
        } else {
          section += ' (' + childConfig.attributes[j].value + ')'
        }
      }
    }
  }

  section += '</h1>'

  section += '<a class=\'wz-child-nav btn\' title=\'Open\' '
  section += '  style=\'color: #060;\' href=\'javascript:void(0)\' '
  section += '  onclick=\'onChildOpenClicked("' + childConfig.name + '", "' + childConfig.id + '")\'>'
  section += '  <i class=\'fa fa-chevron-right fa-white\'></i>'
  section += '</a>'

  if (!childRole.permanent) {
    section += '<a class=\'wz-child-nav btn\' title=\'Delete\' '
    section += '  style=\'color: #900;\' href=\'javascript:void(0)\' '
    section += '  onclick=\'onChildDeleteClicked("' + childConfig.name + '", "' + childConfig.id + '")\'>'
    section += '  <i class=\'fa fa-times fa-white\'></i>'
    section += '</a>'
  }
  return section
}

/** Add children that are in a fixed format */
function addNonSortableRoleChildren (childRoleName, childRole, childrenWithRole) {
  let section = ''
  let roleClasses = 'wz-role'
  let roleLabelClasses = 'wz-role-label'
  let childClasses = 'wz-child'
  let missingRequired = false
  let missingOptional = false
  if (!childRole.optional) {
    if (childrenWithRole.length === 0) {
      missingRequired = true
      roleClasses += ' wz-role-missing'
      roleLabelClasses += ' wz-role-label-required'
    } else {
      roleClasses += ' wz-role-required'
      roleLabelClasses += ' wz-role-label-required'
      childClasses += ' wz-child-required'
    }
  } else {
    if (childrenWithRole.length === 0) {
      missingOptional = true
      roleClasses += ' wz-role-missing-optional'
    }
  }
  if (!childRole.permanent) {
    section += '<div class=\'' + roleClasses + '\'><div class=\'' + roleLabelClasses +
      '\'>' + childRole.name + '</div>'
  }

  if (missingRequired) {
    section += addMissingRequired(childRoleName, childRole)
  } else if (missingOptional) {
    section += addMissingOptional(childRoleName, childRole)
  } else {
    for (var j = 0; j < childrenWithRole.length; j++) {
      var childContext = childrenWithRole[j]
      var childModel = childContext['model']
      var childConfig = childContext['config']

      section += '<div class=\'' + childClasses + '\' id=\'' + childConfig.id + '\'>'

      // Adds icon, name, and navigation.
      section += addChildFields(childModel, childConfig, childRole)

      section += '</div>'
    }
  }
  if (!childRole.permanent) {
    section += '</div>'
  }
  return section
}

/** Add placeholder for missing required field */
function addMissingRequired (roleName, role) {
  var modelsForRole = findModelChildrenInRole(roleName)

  var section = ''
  section += '<div class=\'wz-child wz-child-missing\'>'
  section += '<i class=\'wz-child-icon fa fa-warning fa-white\'></i>'
  section += '<h1 class=\'wz-child-name\'>' + role.name + ' is Required</h1>'

  section += '<div class=\'wz-child-nav btn-group dropup\' style=\'padding: 0; margin-top: 5px; margin-right: 5px;\'>'
  section += '<a class=\'btn dropdown-toggle\' title=\'Add Component\' data-toggle=\'dropdown\'>'
  section += 'Add Component<span class=\'caret\'style=\'margin-left: 5px\'></span></a>'
  section += '<ul class=\'dropdown-menu pull-right\'>'

  // Add item in dropdown for each component in the given role.
  for (let i = 0; i < modelsForRole.length; i++) {
    let roleModel = modelsForRole[i]
    section += '<li><a href=\'#\' onclick=\'onAddChild(event, "' + roleModel.localName +
      '")\'><i class=\'dd-icon fa fa-' + roleModel.icon + '\'></i>' + roleModel.name + '</a></li>'
  }

  section += '</ul>'
  section += '</div>'

  section += '</div>'
  return section
}

/** Add placeholder for missing optional field */
function addMissingOptional (roleName, role) {
  var modelsForRole = findModelChildrenInRole(roleName)

  var section = ''
  section += '<div class=\'wz-child wz-child-missing\'>'
  section += '<i class=\'wz-child-icon fa fa-plus fa-white\'></i>'
  section += '<h1 class=\'wz-child-name\'>Add ' + role.name + '</h1>'

  section += '<div class=\'wz-child-nav btn-group dropup\' style=\'padding: 0; margin-top: 5px; margin-right: 5px;\'>'
  section += '<a class=\'btn dropdown-toggle\' title=\'Add Component\' data-toggle=\'dropdown\'>'
  section += 'Add Component<span class=\'caret\'style=\'margin-left: 5px\'></span></a>'
  section += '<ul class=\'dropdown-menu pull-right\'>'

  // Add item in dropdown for each component in the given role.
  for (var i = 0; i < modelsForRole.length; i++) {
    var roleModel = modelsForRole[i]
    if (roleModel.deprecated) {
      section += '<li><a href=\'#\'><i class=\'dd-icon fa fa-' + roleModel.icon +
        '\'></i><span style=\'text-decoration: line-through;\'>' + roleModel.name + '</span></a></li>'
    } else {
      section += '<li><a href=\'#\' onclick=\'onAddChild(event, "' + roleModel.localName +
        '")\'><i class=\'dd-icon fa fa-' + roleModel.icon + '\'></i>' + roleModel.name + '</a></li>'
    }
  }

  section += '</ul>'
  section += '</div>'

  section += '</div>'
  return section
}

/** Add child of * */
export function onAddChild (event, name) {
  event.preventDefault()

  var context = wizard.editorContexts[wizard.editorContexts.length - 1]
  var model = context['model']

  // Create new config element based on selected model.
  var childModel = findModelNodeByName(model, name)
  var childConfig = {
    'name': childModel.localName,
    'id': generateUniqueId()
  }
  if (childModel.namespace) {
    childConfig.namespace = childModel.namespace
  }

  // If the new model element has no attributes, there is nothing to
  // configure.
  if (childModel.attributes) {
    ceComponentCreate(childModel, childConfig, onChildAdded)
  } else {
    onChildAdded(childConfig)
  }
}

/** Generate a unique id */
function generateUniqueId () {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
    let r = crypto.getRandomValues(new Uint8Array(1))[0] % 16 | 0
    let v = c === 'x' ? r : (r & 0x3 | 0x8)
    return v.toString(16)
  })
}

/** Add newly created config element */
function onChildAdded (newConfig) {
  let context = wizard.editorContexts[wizard.editorContexts.length - 1]
  let topModel = context['model']
  let topConfig = context['config']
  if (!topConfig.children) {
    topConfig.children = []
  }
  topConfig.children.push(newConfig)
  fixChildOrder(topModel, topConfig)
  refresh()
}

/** Fix order of children to match model */
function fixChildOrder (modelNode, configNode) {
  let childrenByRole = getConfigChildrenByRole(modelNode, configNode)
  let role = wizard.roles[modelNode.role]
  let childRoles = getSpecializedRoleChildren(role, modelNode)

  var updated = []
  if (childRoles) {
    for (let i = 0; i < childRoles.length; i++) {
      var childrenInRole = childrenByRole[childRoles[i]]
      if (childrenInRole) {
        for (let j = 0; j < childrenInRole.length; j++) {
          var childConfig = childrenInRole[j].config
          updated.push(childConfig)
        }
      }
    }
  }
  configNode.children = updated
}

/** Add children that are in a sortable format */
function addSortableRoleChildren (childRoleName, childRole, childrenWithRole) {
  let modelsForRole = findModelChildrenInRole(childRoleName)

  let section = '<ul class=\'wz-role wz-sortable\'><div class=\'wz-role-label\'>' + childRole.name + '</div>'
  for (let j = 0; j < childrenWithRole.length; j++) {
    var childContext = childrenWithRole[j]
    var childModel = childContext['model']
    var childConfig = childContext['config']

    section += '<li class=\'wz-child wz-sortable-item\' draggable=\'true\' id=\'' + childConfig.id + '\'>'
    section += '<i class=\'wz-drag-icon fa fa-bars fa-white\'></i>'

    // Adds icon, name, and navigation.
    section += addChildFields(childModel, childConfig, childRole)

    section += '</li>'
  }

  // Separator.
  if (childrenWithRole.length > 0) {
    section += "<li style='padding-bottom: 10px; border-bottom: 1px dashed #aaa; margin-bottom: 10px; "
    section += "margin-left: 20px; margin-right: 20px; list-style-type: none; list-style-position: inside;'></li>"
  }

  // Non-draggable item for creating new children.
  section += '<li class=\'wz-child\'><i class=\'wz-child-icon fa fa-plus fa-white\'></i>'
  section += '<h1 class=\'wz-child-name\'>Add ' + childRole.name + '</h1>'
  section += '<div class=\'wz-child-nav btn-group dropup\' style=\'padding: 0; margin-top: 5px; margin-right: 5px;\'>'
  section += '<a class=\'btn dropdown-toggle\' title=\'Add Component\' data-toggle=\'dropdown\'>'
  section += 'Add Component<span class=\'caret\'style=\'margin-left: 5px\'></span></a>'
  section += '<ul class=\'dropdown-menu pull-right\'>'

  // Add item in dropdown for each component in the given role.
  if (modelsForRole) {
    for (var i = 0; i < modelsForRole.length; i++) {
      var roleModel = modelsForRole[i]
      section += '<li><a href=\'#\' onclick=\'onAddChild(event, "' + roleModel.localName +
        '")\'><i class=\'dd-icon fa fa-' + roleModel.icon + '\'></i>' + roleModel.name + '</a></li>'
    }
  }

  section += '</ul>'
  section += '</div>'
  section += '</li>'

  section += '</ul>'
  return section
}

/** Delete the given element */
export function onChildDeleteClicked (childName, childId) {
  let top = wizard.editorContexts[wizard.editorContexts.length - 1]
  let topModel = top['model']
  let topConfig = top['config']
  let childModel = findModelNodeByName(topModel, childName)
  let childConfig = findConfigNodeById(topConfig, childId)
  let childRole = wizard.roles[childModel.role]

  let dialogTitle = 'Delete Component?'
  let dialogMessage = 'Are you sure that you want to delete \'' + childModel.name + '\'?'
  if (!childRole.optional) {
    dialogTitle = 'Delete Required Component?'
    dialogMessage = 'Are you sure that you want to delete \'' + childModel.name + '\'? ' +
      'This component is required by \'' + topModel.name + '\'.'
  }

  // Confirm delete, then remove the element and reload the panel.
  swConfirm(dialogTitle, dialogMessage, function (result) {
    if (result) {
      for (var i = 0; i < topConfig.children.length; i++) {
        if (topConfig.children[i].id === childConfig.id) {
          topConfig.children.splice(i, 1)
          break
        }
      }
      refresh()
    }
  })
}

/** Open a child page in the wizard */
export function onChildOpenClicked (childName, childId) {
  let top = wizard.editorContexts[wizard.editorContexts.length - 1]
  let topModel = top['model']
  let topConfig = top['config']
  let childModel = findModelNodeByName(topModel, childName)
  let childConfig = findConfigNodeById(topConfig, childId)
  if (childModel && childConfig) {
    pushContext(childConfig, childModel)
  }
}

/** Find children of a model node with the given role */
function findModelChildrenInRole (roleName) {
  var role = wizard.roles[roleName]
  var all = []
  all.push.apply(all, wizard.configModel.elementsByRole[roleName])

  // Also matches of subtypes.
  if (role.subtypes) {
    for (var i = 0; i < role.subtypes.length; i++) {
      var subtypeName = role.subtypes[i]
      all.push.apply(all, wizard.configModel.elementsByRole[subtypeName])
    }
  }
  all.sort(function (a, b) {
    if (a.name < b.name) {
      return -1
    } else if (a.name > b.name) {
      return 1
    } else {
      return 0
    }
  })
  return all
}

/** Get configuration children grouped by role */
function getConfigChildrenByRole (modelNode, configNode) {
  let role = wizard.roles[modelNode.role]
  let result = {}

  // Get child roles with constraints.
  let childRoles = getSpecializedRoleChildren(role, modelNode)
  let modelNotFound = []
  for (let i = 0; i < childRoles.length; i++) {
    var childRoleName = childRoles[i]
    var childRole = wizard.roles[childRoleName]
    var roleSubtypes = []
    roleSubtypes.push(childRoleName)
    if (childRole.subtypes) {
      roleSubtypes.push.apply(roleSubtypes, childRole.subtypes)
    }

    let matches = []
    result[childRoleName] = matches
    if (configNode.children) {
      for (let j = 0; j < configNode.children.length; j++) {
        let childConfig = configNode.children[j]
        if (modelNotFound.indexOf(childConfig.name) === -1) {
          let childModel = findModelNodeByName(modelNode, childConfig.name)
          if (!childModel) {
            modelNotFound.push(childConfig.name)
          } else if (roleSubtypes.indexOf(childModel.role) !== -1) {
            let childContext = {}
            childContext['model'] = childModel
            childContext['config'] = childConfig
            matches.push(childContext)
          }
        }
      }
    }
  }
  result['?'] = modelNotFound
  return result
}

/** Get child roles, taking into account model specializations */
function getSpecializedRoleChildren (role, modelNode) {
  var specialized = []
  var childRoles = role.children
  for (var i = 0; i < childRoles.length; i++) {
    var childRole = childRoles[i]
    if (modelNode.specializes) {
      var match = modelNode.specializes[childRole]
      if (match) {
        specialized.push(match)
      } else {
        specialized.push(childRole)
      }
    } else {
      specialized.push(childRole)
    }
  }
  return specialized
}

/** Find closest element with the given localName */
function findConfigNodeByName (root, name) {
  if (root.name === name) {
    return root
  } else {
    var found
    if (root.children) {
      for (var i = 0; i < root.children.length; i++) {
        found = findConfigNodeByName(root.children[i], name)
        if (found) {
          return found
        }
      }
    }
  }
  return null
}

/** Find closest element with the given uuid */
function findConfigNodeById (root, uuid) {
  if (root.id === uuid) {
    return root
  } else {
    var found
    if (root.children) {
      for (var i = 0; i < root.children.length; i++) {
        found = findConfigNodeById(root.children[i], uuid)
        if (found) {
          return found
        }
      }
    }
  }
  return null
}

/** Find a child model based on config element name */
function findModelNodeByName (model, name) {
  if (model.localName === name) {
    return model
  } else {
    let role = wizard.roles[model.role]
    let childRoles = getSpecializedRoleChildren(role, model)

    // Loop through all possible child roles for model.
    for (let i = 0; i < childRoles.length; i++) {
      let potential = findModelChildrenInRole(childRoles[i])
      if (potential) {
        for (let j = 0; j < potential.length; j++) {
          if (name === potential[j].localName) {
            return potential[j]
          }
        }
      }
    }
  }
  return null
}
