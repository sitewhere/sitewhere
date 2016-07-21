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
def group = tenantBuilder.getTenantGroup 'a6b9d032-cc43-4885-a8fe-789bc8cdebab'
if (group == null) {
	group = tenantBuilder.newTenantGroup 'a6b9d032-cc43-4885-a8fe-789bc8cdebab', 'Default Tenant Group'
	group.withDescription 'Tenant group used as default container for all tenants included in test dataset.'
	group.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/sitewhere-small.png'
	group = tenantBuilder.persist group
	
	def elements = []
	elements << tenantBuilder.newTenantGroupElement(tenant.getId())
	tenantBuilder.persist group, elements
	
	logger.info "[Create Tenant Group] ${group.token}"
}
