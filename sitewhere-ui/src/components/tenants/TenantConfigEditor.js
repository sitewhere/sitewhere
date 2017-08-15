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
  editorContexts: [],

  /**
   * Reset context stack.
   */
  resetContexts: function () {
    this.editorContexts = []
  },

  /**
   * Reset the wizard
   */
  reset: function () {
    this.resetContexts()
    return this.addRootContext()
  },

  /**
   * Get most recent context (last on stack).
   */
  getLastContext: function () {
    if (this.editorContexts.length > 0) {
      return this.editorContexts[this.editorContexts.length - 1]
    }
    return null
  },

  /** Add the root context */
  addRootContext: function () {
    var configNode = this.findConfigNodeByName(
      this.config, 'tenant-configuration')
    var modelNode = this.findModelNodeByName(
      this.configModel, 'tenant-configuration')
    return this.pushContext(configNode, modelNode)
  },

  /**
   * Get context relative to current.
   */
  getRelativeContext: function (name) {
    var context = this.getLastContext()
    var configNode = this.findConfigNodeByName(
      context.config, name)
    var modelNode = this.findModelNodeByName(
      context.model, name)
    return {
      'config': configNode,
      'model': modelNode
    }
  },

  /** Push context on the stack */
  pushRelativeContext: function (name) {
    var context = this.getRelativeContext(name)
    return this.pushContext(context.config, context.model)
  },

  /** Push context on the stack */
  pushContext: function (configNode, modelNode) {
    var context = {
      'config': configNode,
      'model': modelNode
    }

    // Build metadata for attributes.
    let groups = buildAttributeGroups(context)
    context.groups = groups

    // Build metadata for content.
    let content = buildContent(context)
    context.content = content

    this.editorContexts.push(context)
    return this.editorContexts
  },

  /** Pop elements off the stack until the given element name is found */
  popToContext: function (elementName) {
    var context = this.getLastContext()
    var config = context['config']
    if ((config.name === elementName) || (this.editorContexts.length === 1)) {
      return this.editorContexts
    } else {
      // Pop the top item and recurse.
      wizard.editorContexts.pop()
      return this.popToContext(elementName)
    }
  },

  /** Pop up one level */
  popOne: function () {
    if (this.editorContexts.length > 1) {
      var context = this.editorContexts[this.editorContexts.length - 2]
      var config = context['config']
      return this.popToContext(config.name)
    }
    return this.editorContexts
  },

  /** Update current context attributes */
  onUpdateCurrent: function (attributes) {
    var context = wizard.getLastContext()
    let config = context['config']
    config.attributes = attributes
    context.groups = buildAttributeGroups(context)
    context.content = buildContent(context)
    return this.editorContexts
  },

  /** Add child to current context */
  onAddChild: function (name, attributes) {
    var context = wizard.getLastContext()
    var model = context['model']
    let config = context['config']

    // Create new config element based on selected model.
    var childModel = wizard.findModelNodeByName(model, name)
    var childConfig = {
      'name': childModel.localName,
      'id': generateUniqueId(),
      'attributes': attributes
    }
    if (childModel.namespace) {
      childConfig.namespace = childModel.namespace
    }

    if (!config.children) {
      config.children = []
    }
    config.children.push(childConfig)
    fixChildOrder(model, config)
    context.content = buildContent(context)
    return this.pushRelativeContext(name)
  },

  /** Delete the given element */
  onDeleteChild: function (id) {
    let context = wizard.getLastContext()
    let config = context['config']
    let childConfig = wizard.findConfigNodeById(config, id)

    for (var i = 0; i < config.children.length; i++) {
      if (config.children[i].id === childConfig.id) {
        config.children.splice(i, 1)
        break
      }
    }
    context.content = buildContent(context)
    return this.editorContexts
  },

  /** Find closest element with the given localName */
  findConfigNodeByName: function (root, name) {
    if (root.name === name) {
      return root
    } else {
      var found
      if (root.children) {
        for (var i = 0; i < root.children.length; i++) {
          found = this.findConfigNodeByName(root.children[i], name)
          if (found) {
            return found
          }
        }
      }
    }
    return null
  },

  /** Find closest element with the given uuid */
  findConfigNodeById: function (root, uuid) {
    if (root.id === uuid) {
      return root
    } else {
      var found
      if (root.children) {
        for (var i = 0; i < root.children.length; i++) {
          found = this.findConfigNodeById(root.children[i], uuid)
          if (found) {
            return found
          }
        }
      }
    }
    return null
  },

  /** Find a child model based on config element name */
  findModelNodeByName: function (model, name) {
    if (model.localName === name) {
      return model
    } else {
      let role = this.roles[model.role]
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

/**
 * Hash configuration node attributes by name.
 */
function hashConfigNodeAttributesByName (config) {
  let configByName = {}
  if (config.attributes) {
    for (var i = 0; i < config.attributes.length; i++) {
      configByName[config.attributes[i].name] =
        config.attributes[i].value
    }
  }
  return configByName
}

/** Build attribute groups for context */
function buildAttributeGroups (context) {
  let configNode = context['config']
  let modelNode = context['model']
  let configByName = hashConfigNodeAttributesByName(configNode)

  let groups = []
  let attributes = []
  let currentGroup = null
  if (modelNode.attributes) {
    for (let i = 0; i < modelNode.attributes.length; i++) {
      var modelAttr = modelNode.attributes[i]
      if (!currentGroup || currentGroup.id !== modelAttr.group) {
        currentGroup = {}
        currentGroup.id = modelAttr.group
        if (modelAttr.group) {
          currentGroup.description =
            modelNode.attributeGroups[modelAttr.group]
        }
        attributes = []
        currentGroup.attributes = attributes
        groups.push(currentGroup)
      }
      let value = configByName[modelAttr.localName]
      if (value) {
        if ((modelAttr.type === 'SiteReference') && wizard.sites) {
          value = getSiteNamesByToken()[value]
        } else if ((modelAttr.type === 'SpecificationReference') && wizard.specifications) {
          value = getSpecificationNamesByToken()[value]
        }
      } else if (modelAttr.defaultValue) {
        value = modelAttr.defaultValue
      }
      attributes.push({
        'localName': modelAttr.localName,
        'name': modelAttr.name,
        'icon': modelAttr.icon,
        'description': modelAttr.description,
        'value': value,
        'required': modelAttr.required
      })
    }
  }
  return groups
}

/** Build content */
function buildContent (context) {
  let configNode = context['config']
  let modelNode = context['model']
  let children = {}

  let childrenByRole = getConfigChildrenByRole(modelNode, configNode)
  let role = wizard.roles[modelNode.role]
  if (!role) {
    return children
  }

  // Loop through role children in order.
  let elements = []
  let childRoles = getSpecializedRoleChildren(role, modelNode)
  for (let i = 0; i < childRoles.length; i++) {
    let childRoleName = childRoles[i]
    let childRole = wizard.roles[childRoleName]
    let childrenWithRole = childrenByRole[childRoleName]
    let availableForRole = findModelChildrenInRole(childRoleName)

    if (childrenWithRole.length === 0) {
      let placeholder = buildPlaceholder(childRole)
      placeholder.options = availableForRole
      placeholder.hasContent = false
      elements.push(placeholder)
    } else {
      for (let j = 0; j < childrenWithRole.length; j++) {
        let childContext = childrenWithRole[j]
        let childModel = childContext['model']
        let childConfig = childContext['config']
        let element = buildChild(childModel, childConfig, childRoleName,
          childRole, childrenWithRole)
        element.options = availableForRole
        element.hasContent = true
        elements.push(element)
      }
      if (childRole.multiple) {
        let placeholder = buildPlaceholder(childRole)
        placeholder.options = availableForRole
        placeholder.hasContent = false
        elements.push(placeholder)
      }
    }
  }
  children.elements = elements

  // Add children that do not have a model.
  var noModel = childrenByRole['?']
  var unknown = []
  for (let i = 0; i < noModel.length; i++) {
    unknown.push(noModel[i])
  }
  children.unknown = unknown
  return children
}

/** Build placeholder entry */
function buildPlaceholder (childRole) {
  let placeholder = {}
  placeholder.name = childRole.name
  placeholder.multiple = childRole.multiple
  placeholder.optional = childRole.optional
  placeholder.permanent = childRole.permanent
  placeholder.reorderable = childRole.reorderable
  return placeholder
}

/** Build child entry */
function buildChild (childModel, childConfig, childRoleName, childRole,
  childrenWithRole) {
  let child = {}
  child.id = childConfig.id
  child.name = childModel.name
  child.localName = childModel.localName
  child.icon = childModel.icon
  child.multiple = childRole.multiple
  child.optional = childRole.optional
  child.permanent = childRole.permanent
  child.reorderable = childRole.reorderable
  child.deprecated = childModel.deprecated
  if (childModel.indexAttribute) {
    child.indexAttribute = childModel.indexAttribute
    child.resolvedIndexAttribute =
      resolveIndexAttribute(childModel, childConfig)
    if (childrenWithRole.length === 0) {
      if (childRole.optional) {
        child.missingOptional = true
      } else {
        child.missingRequired = true
      }
    }
  }
  return child
}

/** Resolve an index attribute */
function resolveIndexAttribute (childModel, childConfig) {
  if (childModel.indexAttribute) {
    let modelAttr = null
    for (let i = 0; i < childModel.attributes.length; i++) {
      if (childModel.indexAttribute === childModel.attributes[i].localName) {
        modelAttr = childModel.attributes[i]
        break
      }
    }
    for (let j = 0; j < childConfig.attributes.length; j++) {
      let attrName = childConfig.attributes[j].name
      if (childModel.indexAttribute === attrName) {
        if (modelAttr && ((modelAttr.type === 'SiteReference') && wizard.sites)) {
          let siteName = getSiteNamesByToken()[childConfig.attributes[j].value]
          return siteName || childConfig.attributes[j].value
        } else if (modelAttr && ((modelAttr.type === 'SpecificationReference') && wizard.specifications)) {
          let specName = getSpecificationNamesByToken()[childConfig.attributes[j].value]
          return specName || childConfig.attributes[j].value
        } else {
          return childConfig.attributes[j].value
        }
      }
    }
  } else {
    return null
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

/** Delete the given element */
export function onChildDeleteClicked (childName, childId) {
  let top = wizard.getLastContext()
  let topModel = top['model']
  let topConfig = top['config']
  let childModel = wizard.findModelNodeByName(topModel, childName)
  let childConfig = wizard.findConfigNodeById(topConfig, childId)
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
    }
  })
}

/** Open a child page in the wizard */
export function onChildOpenClicked (childName, childId) {
  let top = wizard.getLastContext()
  let topModel = top['model']
  let topConfig = top['config']
  let childModel = wizard.findModelNodeByName(topModel, childName)
  let childConfig = wizard.findConfigNodeById(topConfig, childId)
  if (childModel && childConfig) {
    wizard.pushContext(childConfig, childModel)
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
          let childModel = wizard.findModelNodeByName(modelNode, childConfig.name)
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
