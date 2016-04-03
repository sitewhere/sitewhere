if (!tenantBuilder.hasTenant('default')) {
	def tenant = tenantBuilder.newTenant 'default', 'Default Tenant', 'sitewhere1234567890',
		'https://s3.amazonaws.com/sitewhere-demo/sitewhere-small.png'
	tenant.withAuthorizedUserId 'admin' 
	tenant.withAuthorizedUserId 'noadmin'
	tenant = tenantBuilder.persist tenant

	logger.info "[Create Tenant] ${tenant.id}"
}