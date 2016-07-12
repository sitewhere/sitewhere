// Create default tenant if does not exist.
def tenant = tenantBuilder.getTenant 'default'
if (tenant == null) {
	tenant = tenantBuilder.newTenant 'default', 'Default Tenant', 'sitewhere1234567890',
		'https://s3.amazonaws.com/sitewhere-demo/sitewhere-small.png'
	tenant.withAuthorizedUserId 'admin' 
	tenant.withAuthorizedUserId 'noadmin'
	tenant = tenantBuilder.persist tenant

	logger.info "[Create Tenant] ${tenant.id}"
}

// Create default tenant group if does not exist and add elements.
def group = tenantBuilder.getTenantGroup 'default'
if (group == null) {
	group = tenantBuilder.newTenantGroup 'default', 'Default Tenant Group'
	group.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/sitewhere-small.png'
	group = tenantBuilder.persist group
	
	def elements = []
	elements << tenantBuilder.newTenantGroupElement(group.getId(), tenant.getId())
	tenantBuilder.persist group, elements
	
	logger.info "[Create Tenant Group] ${group.id}"
}
