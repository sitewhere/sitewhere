import com.sitewhere.spi.user.*

// Use default system authorities tree.
def authIds = [];
def allAuthorities = SiteWhereAuthority.values();
allAuthorities.each { authority ->
	// Add any authorities not already present.
	if (!userBuilder.hasAuthority(authority.name)) {
		def newAuth = userBuilder.newGrantedAuthority authority.name
		newAuth.withDescription authority.description 
		newAuth.withParent authority.parent 
		newAuth.makeGroup authority.group
		newAuth = userBuilder.persist newAuth
		logger.info "[Create Authority] ${newAuth.authority}"
	}
	if (!authority.group) {
		authIds << authority.name
	}
}

// Only add users if no data in system.
if (!userBuilder.hasUsers()) {
	// Create the primary admin user.
	def adminUser = userBuilder.newUser 'admin', 'password', 'Admin', 'User'
	adminUser.withStatus(AccountStatus.Active) 
	adminUser.withAuthorities authIds
	adminUser = userBuilder.persist adminUser
	logger.info "[Create User] ${adminUser.username}"
	
	// Remove non-admin authorities
	authIds -= SiteWhereAuthority.ViewServerInfo.name
	authIds -= SiteWhereAuthority.AdminTenants.name
	authIds -= SiteWhereAuthority.AdminUsers.name
	
	
	// Create the default non-admin user.
	def nonAdminUser = userBuilder.newUser 'noadmin', 'noadmin', 'Non-Admin', 'User'
	nonAdminUser.withStatus(AccountStatus.Active) 
	nonAdminUser.withAuthorities authIds
	nonAdminUser = userBuilder.persist nonAdminUser
	logger.info "[Create User] ${nonAdminUser.username}"
}