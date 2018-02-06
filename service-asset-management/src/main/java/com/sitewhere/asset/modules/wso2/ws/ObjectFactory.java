
package com.sitewhere.asset.modules.wso2.ws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.sitewhere.wso2.identity.ws package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _GetTenantIdofUserUsername_QNAME = new QName("http://service.ws.um.carbon.wso2.org", "username");
    private final static QName _UpdateUserListOfRoleRoleName_QNAME = new QName("http://service.ws.um.carbon.wso2.org", "roleName");
    private final static QName _GetUserClaimValuesProfileName_QNAME = new QName("http://service.ws.um.carbon.wso2.org", "profileName");
    private final static QName _GetUserClaimValuesUserName_QNAME = new QName("http://service.ws.um.carbon.wso2.org", "userName");
    private final static QName _GetUserClaimValueClaim_QNAME = new QName("http://service.ws.um.carbon.wso2.org", "claim");
    private final static QName _ListUsersFilter_QNAME = new QName("http://service.ws.um.carbon.wso2.org", "filter");
    private final static QName _PermissionDTOAction_QNAME = new QName("http://dao.service.ws.um.carbon.wso2.org/xsd", "action");
    private final static QName _PermissionDTOResourceId_QNAME = new QName("http://dao.service.ws.um.carbon.wso2.org/xsd", "resourceId");
    private final static QName _GetUserClaimValueResponseReturn_QNAME = new QName("http://service.ws.um.carbon.wso2.org", "return");
    private final static QName _DeleteUserClaimValueClaimURI_QNAME = new QName("http://service.ws.um.carbon.wso2.org", "claimURI");
    private final static QName _UpdateCredentialByAdminNewCredential_QNAME = new QName("http://service.ws.um.carbon.wso2.org", "newCredential");
    private final static QName _GetUserListClaimValue_QNAME = new QName("http://service.ws.um.carbon.wso2.org", "claimValue");
    private final static QName _GetUserListProfile_QNAME = new QName("http://service.ws.um.carbon.wso2.org", "profile");
    private final static QName _GetUserListClaimUri_QNAME = new QName("http://service.ws.um.carbon.wso2.org", "claimUri");
    private final static QName _ClaimDTOValue_QNAME = new QName("http://dao.service.ws.um.carbon.wso2.org/xsd", "value");
    private final static QName _ClaimDTODialectURI_QNAME = new QName("http://dao.service.ws.um.carbon.wso2.org/xsd", "dialectURI");
    private final static QName _ClaimDTORegEx_QNAME = new QName("http://dao.service.ws.um.carbon.wso2.org/xsd", "regEx");
    private final static QName _ClaimDTODisplayTag_QNAME = new QName("http://dao.service.ws.um.carbon.wso2.org/xsd", "displayTag");
    private final static QName _ClaimDTOClaimUri_QNAME = new QName("http://dao.service.ws.um.carbon.wso2.org/xsd", "claimUri");
    private final static QName _ClaimDTODescription_QNAME = new QName("http://dao.service.ws.um.carbon.wso2.org/xsd", "description");
    private final static QName _TenantCreatedDate_QNAME = new QName("http://api.user.carbon.wso2.org/xsd", "createdDate");
    private final static QName _TenantAdminFullName_QNAME = new QName("http://api.user.carbon.wso2.org/xsd", "adminFullName");
    private final static QName _TenantRealmConfig_QNAME = new QName("http://api.user.carbon.wso2.org/xsd", "realmConfig");
    private final static QName _TenantDomain_QNAME = new QName("http://api.user.carbon.wso2.org/xsd", "domain");
    private final static QName _TenantAdminFirstName_QNAME = new QName("http://api.user.carbon.wso2.org/xsd", "adminFirstName");
    private final static QName _TenantAdminName_QNAME = new QName("http://api.user.carbon.wso2.org/xsd", "adminName");
    private final static QName _TenantEmail_QNAME = new QName("http://api.user.carbon.wso2.org/xsd", "email");
    private final static QName _TenantAdminPassword_QNAME = new QName("http://api.user.carbon.wso2.org/xsd", "adminPassword");
    private final static QName _TenantAdminLastName_QNAME = new QName("http://api.user.carbon.wso2.org/xsd", "adminLastName");
    private final static QName _ClaimValueValue_QNAME = new QName("http://common.mgt.user.carbon.wso2.org/xsd", "value");
    private final static QName _ClaimValueClaimURI_QNAME = new QName("http://common.mgt.user.carbon.wso2.org/xsd", "claimURI");
    private final static QName _AddUserCredential_QNAME = new QName("http://service.ws.um.carbon.wso2.org", "credential");
    private final static QName _RealmConfigurationRealmClassName_QNAME = new QName("http://api.user.carbon.wso2.org/xsd", "realmClassName");
    private final static QName _RealmConfigurationAdminRoleName_QNAME = new QName("http://api.user.carbon.wso2.org/xsd", "adminRoleName");
    private final static QName _RealmConfigurationEveryOneRoleName_QNAME = new QName("http://api.user.carbon.wso2.org/xsd", "everyOneRoleName");
    private final static QName _RealmConfigurationAddAdmin_QNAME = new QName("http://api.user.carbon.wso2.org/xsd", "addAdmin");
    private final static QName _RealmConfigurationDescription_QNAME = new QName("http://api.user.carbon.wso2.org/xsd", "description");
    private final static QName _RealmConfigurationAuthorizationManagerClass_QNAME = new QName("http://api.user.carbon.wso2.org/xsd", "authorizationManagerClass");
    private final static QName _RealmConfigurationSecondaryRealmConfig_QNAME = new QName("http://api.user.carbon.wso2.org/xsd", "secondaryRealmConfig");
    private final static QName _RealmConfigurationPersistedTimestamp_QNAME = new QName("http://api.user.carbon.wso2.org/xsd", "persistedTimestamp");
    private final static QName _RealmConfigurationAdminUserName_QNAME = new QName("http://api.user.carbon.wso2.org/xsd", "adminUserName");
    private final static QName _RealmConfigurationUserStoreClass_QNAME = new QName("http://api.user.carbon.wso2.org/xsd", "userStoreClass");
    private final static QName _UpdateRoleNameNewRoleName_QNAME = new QName("http://service.ws.um.carbon.wso2.org", "newRoleName");
    private final static QName _RemoteUserStoreManagerServiceUserStoreExceptionUserStoreException_QNAME = new QName("http://service.ws.um.carbon.wso2.org", "UserStoreException");
    private final static QName _GetPropertiesTenant_QNAME = new QName("http://service.ws.um.carbon.wso2.org", "tenant");
    private final static QName _UpdateCredentialOldCredential_QNAME = new QName("http://service.ws.um.carbon.wso2.org", "oldCredential");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.sitewhere.wso2.identity.ws
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetTenantIdofUser }
     * 
     */
    public GetTenantIdofUser createGetTenantIdofUser() {
        return new GetTenantIdofUser();
    }

    /**
     * Create an instance of {@link AuthenticateResponse }
     * 
     */
    public AuthenticateResponse createAuthenticateResponse() {
        return new AuthenticateResponse();
    }

    /**
     * Create an instance of {@link GetTenantIdofUserResponse }
     * 
     */
    public GetTenantIdofUserResponse createGetTenantIdofUserResponse() {
        return new GetTenantIdofUserResponse();
    }

    /**
     * Create an instance of {@link GetUserClaimValue }
     * 
     */
    public GetUserClaimValue createGetUserClaimValue() {
        return new GetUserClaimValue();
    }

    /**
     * Create an instance of {@link ListUsers }
     * 
     */
    public ListUsers createListUsers() {
        return new ListUsers();
    }

    /**
     * Create an instance of {@link PermissionDTO }
     * 
     */
    public PermissionDTO createPermissionDTO() {
        return new PermissionDTO();
    }

    /**
     * Create an instance of {@link AddUserClaimValues }
     * 
     */
    public AddUserClaimValues createAddUserClaimValues() {
        return new AddUserClaimValues();
    }

    /**
     * Create an instance of {@link UpdateCredentialByAdmin }
     * 
     */
    public UpdateCredentialByAdmin createUpdateCredentialByAdmin() {
        return new UpdateCredentialByAdmin();
    }

    /**
     * Create an instance of {@link GetUserClaimValuesForClaims }
     * 
     */
    public GetUserClaimValuesForClaims createGetUserClaimValuesForClaims() {
        return new GetUserClaimValuesForClaims();
    }

    /**
     * Create an instance of {@link GetPropertiesResponse }
     * 
     */
    public GetPropertiesResponse createGetPropertiesResponse() {
        return new GetPropertiesResponse();
    }

    /**
     * Create an instance of {@link SetUserClaimValues }
     * 
     */
    public SetUserClaimValues createSetUserClaimValues() {
        return new SetUserClaimValues();
    }

    /**
     * Create an instance of {@link IsExistingUserResponse }
     * 
     */
    public IsExistingUserResponse createIsExistingUserResponse() {
        return new IsExistingUserResponse();
    }

    /**
     * Create an instance of {@link GetPasswordExpirationTime }
     * 
     */
    public GetPasswordExpirationTime createGetPasswordExpirationTime() {
        return new GetPasswordExpirationTime();
    }

    /**
     * Create an instance of {@link IsReadOnly }
     * 
     */
    public IsReadOnly createIsReadOnly() {
        return new IsReadOnly();
    }

    /**
     * Create an instance of {@link GetProfileNamesResponse }
     * 
     */
    public GetProfileNamesResponse createGetProfileNamesResponse() {
        return new GetProfileNamesResponse();
    }

    /**
     * Create an instance of {@link AddUser }
     * 
     */
    public AddUser createAddUser() {
        return new AddUser();
    }

    /**
     * Create an instance of {@link IsExistingRole }
     * 
     */
    public IsExistingRole createIsExistingRole() {
        return new IsExistingRole();
    }

    /**
     * Create an instance of {@link IsExistingUser }
     * 
     */
    public IsExistingUser createIsExistingUser() {
        return new IsExistingUser();
    }

    /**
     * Create an instance of {@link UpdateRoleName }
     * 
     */
    public UpdateRoleName createUpdateRoleName() {
        return new UpdateRoleName();
    }

    /**
     * Create an instance of {@link SetUserClaimValue }
     * 
     */
    public SetUserClaimValue createSetUserClaimValue() {
        return new SetUserClaimValue();
    }

    /**
     * Create an instance of {@link GetUserClaimValuesResponse }
     * 
     */
    public GetUserClaimValuesResponse createGetUserClaimValuesResponse() {
        return new GetUserClaimValuesResponse();
    }

    /**
     * Create an instance of {@link RemoteUserStoreManagerServiceUserStoreException }
     * 
     */
    public RemoteUserStoreManagerServiceUserStoreException createRemoteUserStoreManagerServiceUserStoreException() {
        return new RemoteUserStoreManagerServiceUserStoreException();
    }

    /**
     * Create an instance of {@link GetRoleNames }
     * 
     */
    public GetRoleNames createGetRoleNames() {
        return new GetRoleNames();
    }

    /**
     * Create an instance of {@link GetRoleListOfUserResponse }
     * 
     */
    public GetRoleListOfUserResponse createGetRoleListOfUserResponse() {
        return new GetRoleListOfUserResponse();
    }

    /**
     * Create an instance of {@link GetPasswordExpirationTimeResponse }
     * 
     */
    public GetPasswordExpirationTimeResponse createGetPasswordExpirationTimeResponse() {
        return new GetPasswordExpirationTimeResponse();
    }

    /**
     * Create an instance of {@link DeleteUserClaimValues }
     * 
     */
    public DeleteUserClaimValues createDeleteUserClaimValues() {
        return new DeleteUserClaimValues();
    }

    /**
     * Create an instance of {@link GetProperties }
     * 
     */
    public GetProperties createGetProperties() {
        return new GetProperties();
    }

    /**
     * Create an instance of {@link DeleteRole }
     * 
     */
    public DeleteRole createDeleteRole() {
        return new DeleteRole();
    }

    /**
     * Create an instance of {@link GetUserListResponse }
     * 
     */
    public GetUserListResponse createGetUserListResponse() {
        return new GetUserListResponse();
    }

    /**
     * Create an instance of {@link GetAllProfileNames }
     * 
     */
    public GetAllProfileNames createGetAllProfileNames() {
        return new GetAllProfileNames();
    }

    /**
     * Create an instance of {@link ListUsersResponse }
     * 
     */
    public ListUsersResponse createListUsersResponse() {
        return new ListUsersResponse();
    }

    /**
     * Create an instance of {@link UpdateUserListOfRole }
     * 
     */
    public UpdateUserListOfRole createUpdateUserListOfRole() {
        return new UpdateUserListOfRole();
    }

    /**
     * Create an instance of {@link GetUserClaimValues }
     * 
     */
    public GetUserClaimValues createGetUserClaimValues() {
        return new GetUserClaimValues();
    }

    /**
     * Create an instance of {@link GetAllProfileNamesResponse }
     * 
     */
    public GetAllProfileNamesResponse createGetAllProfileNamesResponse() {
        return new GetAllProfileNamesResponse();
    }

    /**
     * Create an instance of {@link GetUserClaimValueResponse }
     * 
     */
    public GetUserClaimValueResponse createGetUserClaimValueResponse() {
        return new GetUserClaimValueResponse();
    }

    /**
     * Create an instance of {@link GetUserListOfRole }
     * 
     */
    public GetUserListOfRole createGetUserListOfRole() {
        return new GetUserListOfRole();
    }

    /**
     * Create an instance of {@link AX2597UserStoreException }
     * 
     */
    public AX2597UserStoreException createAX2597UserStoreException() {
        return new AX2597UserStoreException();
    }

    /**
     * Create an instance of {@link UserStoreException }
     * 
     */
    public UserStoreException createUserStoreException() {
        return new UserStoreException();
    }

    /**
     * Create an instance of {@link DeleteUserClaimValue }
     * 
     */
    public DeleteUserClaimValue createDeleteUserClaimValue() {
        return new DeleteUserClaimValue();
    }

    /**
     * Create an instance of {@link GetUserList }
     * 
     */
    public GetUserList createGetUserList() {
        return new GetUserList();
    }

    /**
     * Create an instance of {@link IsReadOnlyResponse }
     * 
     */
    public IsReadOnlyResponse createIsReadOnlyResponse() {
        return new IsReadOnlyResponse();
    }

    /**
     * Create an instance of {@link GetUserIdResponse }
     * 
     */
    public GetUserIdResponse createGetUserIdResponse() {
        return new GetUserIdResponse();
    }

    /**
     * Create an instance of {@link GetTenantIdResponse }
     * 
     */
    public GetTenantIdResponse createGetTenantIdResponse() {
        return new GetTenantIdResponse();
    }

    /**
     * Create an instance of {@link ArrayOfString }
     * 
     */
    public ArrayOfString createArrayOfString() {
        return new ArrayOfString();
    }

    /**
     * Create an instance of {@link GetUserId }
     * 
     */
    public GetUserId createGetUserId() {
        return new GetUserId();
    }

    /**
     * Create an instance of {@link ClaimDTO }
     * 
     */
    public ClaimDTO createClaimDTO() {
        return new ClaimDTO();
    }

    /**
     * Create an instance of {@link Tenant }
     * 
     */
    public Tenant createTenant() {
        return new Tenant();
    }

    /**
     * Create an instance of {@link AddRole }
     * 
     */
    public AddRole createAddRole() {
        return new AddRole();
    }

    /**
     * Create an instance of {@link ClaimValue }
     * 
     */
    public ClaimValue createClaimValue() {
        return new ClaimValue();
    }

    /**
     * Create an instance of {@link GetHybridRolesResponse }
     * 
     */
    public GetHybridRolesResponse createGetHybridRolesResponse() {
        return new GetHybridRolesResponse();
    }

    /**
     * Create an instance of {@link DeleteUser }
     * 
     */
    public DeleteUser createDeleteUser() {
        return new DeleteUser();
    }

    /**
     * Create an instance of {@link RealmConfiguration }
     * 
     */
    public RealmConfiguration createRealmConfiguration() {
        return new RealmConfiguration();
    }

    /**
     * Create an instance of {@link IsExistingRoleResponse }
     * 
     */
    public IsExistingRoleResponse createIsExistingRoleResponse() {
        return new IsExistingRoleResponse();
    }

    /**
     * Create an instance of {@link AddUserClaimValue }
     * 
     */
    public AddUserClaimValue createAddUserClaimValue() {
        return new AddUserClaimValue();
    }

    /**
     * Create an instance of {@link GetUserClaimValuesForClaimsResponse }
     * 
     */
    public GetUserClaimValuesForClaimsResponse createGetUserClaimValuesForClaimsResponse() {
        return new GetUserClaimValuesForClaimsResponse();
    }

    /**
     * Create an instance of {@link Authenticate }
     * 
     */
    public Authenticate createAuthenticate() {
        return new Authenticate();
    }

    /**
     * Create an instance of {@link GetUserListOfRoleResponse }
     * 
     */
    public GetUserListOfRoleResponse createGetUserListOfRoleResponse() {
        return new GetUserListOfRoleResponse();
    }

    /**
     * Create an instance of {@link UpdateRoleListOfUser }
     * 
     */
    public UpdateRoleListOfUser createUpdateRoleListOfUser() {
        return new UpdateRoleListOfUser();
    }

    /**
     * Create an instance of {@link AX2604Tenant }
     * 
     */
    public AX2604Tenant createAX2604Tenant() {
        return new AX2604Tenant();
    }

    /**
     * Create an instance of {@link GetTenantId }
     * 
     */
    public GetTenantId createGetTenantId() {
        return new GetTenantId();
    }

    /**
     * Create an instance of {@link GetRoleNamesResponse }
     * 
     */
    public GetRoleNamesResponse createGetRoleNamesResponse() {
        return new GetRoleNamesResponse();
    }

    /**
     * Create an instance of {@link GetHybridRoles }
     * 
     */
    public GetHybridRoles createGetHybridRoles() {
        return new GetHybridRoles();
    }

    /**
     * Create an instance of {@link UpdateCredential }
     * 
     */
    public UpdateCredential createUpdateCredential() {
        return new UpdateCredential();
    }

    /**
     * Create an instance of {@link GetProfileNames }
     * 
     */
    public GetProfileNames createGetProfileNames() {
        return new GetProfileNames();
    }

    /**
     * Create an instance of {@link GetRoleListOfUser }
     * 
     */
    public GetRoleListOfUser createGetRoleListOfUser() {
        return new GetRoleListOfUser();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "username", scope = GetTenantIdofUser.class)
    public JAXBElement<String> createGetTenantIdofUserUsername(String value) {
        return new JAXBElement<String>(_GetTenantIdofUserUsername_QNAME, String.class, GetTenantIdofUser.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "roleName", scope = UpdateUserListOfRole.class)
    public JAXBElement<String> createUpdateUserListOfRoleRoleName(String value) {
        return new JAXBElement<String>(_UpdateUserListOfRoleRoleName_QNAME, String.class, UpdateUserListOfRole.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "profileName", scope = GetUserClaimValues.class)
    public JAXBElement<String> createGetUserClaimValuesProfileName(String value) {
        return new JAXBElement<String>(_GetUserClaimValuesProfileName_QNAME, String.class, GetUserClaimValues.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "userName", scope = GetUserClaimValues.class)
    public JAXBElement<String> createGetUserClaimValuesUserName(String value) {
        return new JAXBElement<String>(_GetUserClaimValuesUserName_QNAME, String.class, GetUserClaimValues.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "profileName", scope = GetUserClaimValue.class)
    public JAXBElement<String> createGetUserClaimValueProfileName(String value) {
        return new JAXBElement<String>(_GetUserClaimValuesProfileName_QNAME, String.class, GetUserClaimValue.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "userName", scope = GetUserClaimValue.class)
    public JAXBElement<String> createGetUserClaimValueUserName(String value) {
        return new JAXBElement<String>(_GetUserClaimValuesUserName_QNAME, String.class, GetUserClaimValue.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "claim", scope = GetUserClaimValue.class)
    public JAXBElement<String> createGetUserClaimValueClaim(String value) {
        return new JAXBElement<String>(_GetUserClaimValueClaim_QNAME, String.class, GetUserClaimValue.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "filter", scope = ListUsers.class)
    public JAXBElement<String> createListUsersFilter(String value) {
        return new JAXBElement<String>(_ListUsersFilter_QNAME, String.class, ListUsers.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dao.service.ws.um.carbon.wso2.org/xsd", name = "action", scope = PermissionDTO.class)
    public JAXBElement<String> createPermissionDTOAction(String value) {
        return new JAXBElement<String>(_PermissionDTOAction_QNAME, String.class, PermissionDTO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dao.service.ws.um.carbon.wso2.org/xsd", name = "resourceId", scope = PermissionDTO.class)
    public JAXBElement<String> createPermissionDTOResourceId(String value) {
        return new JAXBElement<String>(_PermissionDTOResourceId_QNAME, String.class, PermissionDTO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "return", scope = GetUserClaimValueResponse.class)
    public JAXBElement<String> createGetUserClaimValueResponseReturn(String value) {
        return new JAXBElement<String>(_GetUserClaimValueResponseReturn_QNAME, String.class, GetUserClaimValueResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "roleName", scope = GetUserListOfRole.class)
    public JAXBElement<String> createGetUserListOfRoleRoleName(String value) {
        return new JAXBElement<String>(_UpdateUserListOfRoleRoleName_QNAME, String.class, GetUserListOfRole.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "profileName", scope = DeleteUserClaimValue.class)
    public JAXBElement<String> createDeleteUserClaimValueProfileName(String value) {
        return new JAXBElement<String>(_GetUserClaimValuesProfileName_QNAME, String.class, DeleteUserClaimValue.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "claimURI", scope = DeleteUserClaimValue.class)
    public JAXBElement<String> createDeleteUserClaimValueClaimURI(String value) {
        return new JAXBElement<String>(_DeleteUserClaimValueClaimURI_QNAME, String.class, DeleteUserClaimValue.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "userName", scope = DeleteUserClaimValue.class)
    public JAXBElement<String> createDeleteUserClaimValueUserName(String value) {
        return new JAXBElement<String>(_GetUserClaimValuesUserName_QNAME, String.class, DeleteUserClaimValue.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "profileName", scope = AddUserClaimValues.class)
    public JAXBElement<String> createAddUserClaimValuesProfileName(String value) {
        return new JAXBElement<String>(_GetUserClaimValuesProfileName_QNAME, String.class, AddUserClaimValues.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "userName", scope = AddUserClaimValues.class)
    public JAXBElement<String> createAddUserClaimValuesUserName(String value) {
        return new JAXBElement<String>(_GetUserClaimValuesUserName_QNAME, String.class, AddUserClaimValues.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "newCredential", scope = UpdateCredentialByAdmin.class)
    public JAXBElement<String> createUpdateCredentialByAdminNewCredential(String value) {
        return new JAXBElement<String>(_UpdateCredentialByAdminNewCredential_QNAME, String.class, UpdateCredentialByAdmin.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "userName", scope = UpdateCredentialByAdmin.class)
    public JAXBElement<String> createUpdateCredentialByAdminUserName(String value) {
        return new JAXBElement<String>(_GetUserClaimValuesUserName_QNAME, String.class, UpdateCredentialByAdmin.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "claimValue", scope = GetUserList.class)
    public JAXBElement<String> createGetUserListClaimValue(String value) {
        return new JAXBElement<String>(_GetUserListClaimValue_QNAME, String.class, GetUserList.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "profile", scope = GetUserList.class)
    public JAXBElement<String> createGetUserListProfile(String value) {
        return new JAXBElement<String>(_GetUserListProfile_QNAME, String.class, GetUserList.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "claimUri", scope = GetUserList.class)
    public JAXBElement<String> createGetUserListClaimUri(String value) {
        return new JAXBElement<String>(_GetUserListClaimUri_QNAME, String.class, GetUserList.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "profileName", scope = GetUserClaimValuesForClaims.class)
    public JAXBElement<String> createGetUserClaimValuesForClaimsProfileName(String value) {
        return new JAXBElement<String>(_GetUserClaimValuesProfileName_QNAME, String.class, GetUserClaimValuesForClaims.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "userName", scope = GetUserClaimValuesForClaims.class)
    public JAXBElement<String> createGetUserClaimValuesForClaimsUserName(String value) {
        return new JAXBElement<String>(_GetUserClaimValuesUserName_QNAME, String.class, GetUserClaimValuesForClaims.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "profileName", scope = SetUserClaimValues.class)
    public JAXBElement<String> createSetUserClaimValuesProfileName(String value) {
        return new JAXBElement<String>(_GetUserClaimValuesProfileName_QNAME, String.class, SetUserClaimValues.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "userName", scope = SetUserClaimValues.class)
    public JAXBElement<String> createSetUserClaimValuesUserName(String value) {
        return new JAXBElement<String>(_GetUserClaimValuesUserName_QNAME, String.class, SetUserClaimValues.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "username", scope = GetPasswordExpirationTime.class)
    public JAXBElement<String> createGetPasswordExpirationTimeUsername(String value) {
        return new JAXBElement<String>(_GetTenantIdofUserUsername_QNAME, String.class, GetPasswordExpirationTime.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "username", scope = GetUserId.class)
    public JAXBElement<String> createGetUserIdUsername(String value) {
        return new JAXBElement<String>(_GetTenantIdofUserUsername_QNAME, String.class, GetUserId.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dao.service.ws.um.carbon.wso2.org/xsd", name = "value", scope = ClaimDTO.class)
    public JAXBElement<String> createClaimDTOValue(String value) {
        return new JAXBElement<String>(_ClaimDTOValue_QNAME, String.class, ClaimDTO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dao.service.ws.um.carbon.wso2.org/xsd", name = "dialectURI", scope = ClaimDTO.class)
    public JAXBElement<String> createClaimDTODialectURI(String value) {
        return new JAXBElement<String>(_ClaimDTODialectURI_QNAME, String.class, ClaimDTO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dao.service.ws.um.carbon.wso2.org/xsd", name = "regEx", scope = ClaimDTO.class)
    public JAXBElement<String> createClaimDTORegEx(String value) {
        return new JAXBElement<String>(_ClaimDTORegEx_QNAME, String.class, ClaimDTO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dao.service.ws.um.carbon.wso2.org/xsd", name = "displayTag", scope = ClaimDTO.class)
    public JAXBElement<String> createClaimDTODisplayTag(String value) {
        return new JAXBElement<String>(_ClaimDTODisplayTag_QNAME, String.class, ClaimDTO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dao.service.ws.um.carbon.wso2.org/xsd", name = "claimUri", scope = ClaimDTO.class)
    public JAXBElement<String> createClaimDTOClaimUri(String value) {
        return new JAXBElement<String>(_ClaimDTOClaimUri_QNAME, String.class, ClaimDTO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dao.service.ws.um.carbon.wso2.org/xsd", name = "description", scope = ClaimDTO.class)
    public JAXBElement<String> createClaimDTODescription(String value) {
        return new JAXBElement<String>(_ClaimDTODescription_QNAME, String.class, ClaimDTO.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.user.carbon.wso2.org/xsd", name = "createdDate", scope = Tenant.class)
    public JAXBElement<XMLGregorianCalendar> createTenantCreatedDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_TenantCreatedDate_QNAME, XMLGregorianCalendar.class, Tenant.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.user.carbon.wso2.org/xsd", name = "adminFullName", scope = Tenant.class)
    public JAXBElement<String> createTenantAdminFullName(String value) {
        return new JAXBElement<String>(_TenantAdminFullName_QNAME, String.class, Tenant.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RealmConfiguration }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.user.carbon.wso2.org/xsd", name = "realmConfig", scope = Tenant.class)
    public JAXBElement<RealmConfiguration> createTenantRealmConfig(RealmConfiguration value) {
        return new JAXBElement<RealmConfiguration>(_TenantRealmConfig_QNAME, RealmConfiguration.class, Tenant.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.user.carbon.wso2.org/xsd", name = "domain", scope = Tenant.class)
    public JAXBElement<String> createTenantDomain(String value) {
        return new JAXBElement<String>(_TenantDomain_QNAME, String.class, Tenant.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.user.carbon.wso2.org/xsd", name = "adminFirstName", scope = Tenant.class)
    public JAXBElement<String> createTenantAdminFirstName(String value) {
        return new JAXBElement<String>(_TenantAdminFirstName_QNAME, String.class, Tenant.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.user.carbon.wso2.org/xsd", name = "adminName", scope = Tenant.class)
    public JAXBElement<String> createTenantAdminName(String value) {
        return new JAXBElement<String>(_TenantAdminName_QNAME, String.class, Tenant.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.user.carbon.wso2.org/xsd", name = "email", scope = Tenant.class)
    public JAXBElement<String> createTenantEmail(String value) {
        return new JAXBElement<String>(_TenantEmail_QNAME, String.class, Tenant.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.user.carbon.wso2.org/xsd", name = "adminPassword", scope = Tenant.class)
    public JAXBElement<String> createTenantAdminPassword(String value) {
        return new JAXBElement<String>(_TenantAdminPassword_QNAME, String.class, Tenant.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.user.carbon.wso2.org/xsd", name = "adminLastName", scope = Tenant.class)
    public JAXBElement<String> createTenantAdminLastName(String value) {
        return new JAXBElement<String>(_TenantAdminLastName_QNAME, String.class, Tenant.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "roleName", scope = AddRole.class)
    public JAXBElement<String> createAddRoleRoleName(String value) {
        return new JAXBElement<String>(_UpdateUserListOfRoleRoleName_QNAME, String.class, AddRole.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://common.mgt.user.carbon.wso2.org/xsd", name = "value", scope = ClaimValue.class)
    public JAXBElement<String> createClaimValueValue(String value) {
        return new JAXBElement<String>(_ClaimValueValue_QNAME, String.class, ClaimValue.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://common.mgt.user.carbon.wso2.org/xsd", name = "claimURI", scope = ClaimValue.class)
    public JAXBElement<String> createClaimValueClaimURI(String value) {
        return new JAXBElement<String>(_ClaimValueClaimURI_QNAME, String.class, ClaimValue.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "profileName", scope = AddUser.class)
    public JAXBElement<String> createAddUserProfileName(String value) {
        return new JAXBElement<String>(_GetUserClaimValuesProfileName_QNAME, String.class, AddUser.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "credential", scope = AddUser.class)
    public JAXBElement<String> createAddUserCredential(String value) {
        return new JAXBElement<String>(_AddUserCredential_QNAME, String.class, AddUser.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "userName", scope = AddUser.class)
    public JAXBElement<String> createAddUserUserName(String value) {
        return new JAXBElement<String>(_GetUserClaimValuesUserName_QNAME, String.class, AddUser.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "roleName", scope = IsExistingRole.class)
    public JAXBElement<String> createIsExistingRoleRoleName(String value) {
        return new JAXBElement<String>(_UpdateUserListOfRoleRoleName_QNAME, String.class, IsExistingRole.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "userName", scope = DeleteUser.class)
    public JAXBElement<String> createDeleteUserUserName(String value) {
        return new JAXBElement<String>(_GetUserClaimValuesUserName_QNAME, String.class, DeleteUser.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "userName", scope = IsExistingUser.class)
    public JAXBElement<String> createIsExistingUserUserName(String value) {
        return new JAXBElement<String>(_GetUserClaimValuesUserName_QNAME, String.class, IsExistingUser.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.user.carbon.wso2.org/xsd", name = "realmClassName", scope = RealmConfiguration.class)
    public JAXBElement<String> createRealmConfigurationRealmClassName(String value) {
        return new JAXBElement<String>(_RealmConfigurationRealmClassName_QNAME, String.class, RealmConfiguration.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.user.carbon.wso2.org/xsd", name = "adminRoleName", scope = RealmConfiguration.class)
    public JAXBElement<String> createRealmConfigurationAdminRoleName(String value) {
        return new JAXBElement<String>(_RealmConfigurationAdminRoleName_QNAME, String.class, RealmConfiguration.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.user.carbon.wso2.org/xsd", name = "everyOneRoleName", scope = RealmConfiguration.class)
    public JAXBElement<String> createRealmConfigurationEveryOneRoleName(String value) {
        return new JAXBElement<String>(_RealmConfigurationEveryOneRoleName_QNAME, String.class, RealmConfiguration.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.user.carbon.wso2.org/xsd", name = "addAdmin", scope = RealmConfiguration.class)
    public JAXBElement<String> createRealmConfigurationAddAdmin(String value) {
        return new JAXBElement<String>(_RealmConfigurationAddAdmin_QNAME, String.class, RealmConfiguration.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.user.carbon.wso2.org/xsd", name = "adminPassword", scope = RealmConfiguration.class)
    public JAXBElement<String> createRealmConfigurationAdminPassword(String value) {
        return new JAXBElement<String>(_TenantAdminPassword_QNAME, String.class, RealmConfiguration.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.user.carbon.wso2.org/xsd", name = "description", scope = RealmConfiguration.class)
    public JAXBElement<String> createRealmConfigurationDescription(String value) {
        return new JAXBElement<String>(_RealmConfigurationDescription_QNAME, String.class, RealmConfiguration.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.user.carbon.wso2.org/xsd", name = "authorizationManagerClass", scope = RealmConfiguration.class)
    public JAXBElement<String> createRealmConfigurationAuthorizationManagerClass(String value) {
        return new JAXBElement<String>(_RealmConfigurationAuthorizationManagerClass_QNAME, String.class, RealmConfiguration.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RealmConfiguration }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.user.carbon.wso2.org/xsd", name = "secondaryRealmConfig", scope = RealmConfiguration.class)
    public JAXBElement<RealmConfiguration> createRealmConfigurationSecondaryRealmConfig(RealmConfiguration value) {
        return new JAXBElement<RealmConfiguration>(_RealmConfigurationSecondaryRealmConfig_QNAME, RealmConfiguration.class, RealmConfiguration.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.user.carbon.wso2.org/xsd", name = "persistedTimestamp", scope = RealmConfiguration.class)
    public JAXBElement<XMLGregorianCalendar> createRealmConfigurationPersistedTimestamp(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_RealmConfigurationPersistedTimestamp_QNAME, XMLGregorianCalendar.class, RealmConfiguration.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.user.carbon.wso2.org/xsd", name = "adminUserName", scope = RealmConfiguration.class)
    public JAXBElement<String> createRealmConfigurationAdminUserName(String value) {
        return new JAXBElement<String>(_RealmConfigurationAdminUserName_QNAME, String.class, RealmConfiguration.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.user.carbon.wso2.org/xsd", name = "userStoreClass", scope = RealmConfiguration.class)
    public JAXBElement<String> createRealmConfigurationUserStoreClass(String value) {
        return new JAXBElement<String>(_RealmConfigurationUserStoreClass_QNAME, String.class, RealmConfiguration.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "newRoleName", scope = UpdateRoleName.class)
    public JAXBElement<String> createUpdateRoleNameNewRoleName(String value) {
        return new JAXBElement<String>(_UpdateRoleNameNewRoleName_QNAME, String.class, UpdateRoleName.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "roleName", scope = UpdateRoleName.class)
    public JAXBElement<String> createUpdateRoleNameRoleName(String value) {
        return new JAXBElement<String>(_UpdateUserListOfRoleRoleName_QNAME, String.class, UpdateRoleName.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "profileName", scope = AddUserClaimValue.class)
    public JAXBElement<String> createAddUserClaimValueProfileName(String value) {
        return new JAXBElement<String>(_GetUserClaimValuesProfileName_QNAME, String.class, AddUserClaimValue.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "claimURI", scope = AddUserClaimValue.class)
    public JAXBElement<String> createAddUserClaimValueClaimURI(String value) {
        return new JAXBElement<String>(_DeleteUserClaimValueClaimURI_QNAME, String.class, AddUserClaimValue.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "claimValue", scope = AddUserClaimValue.class)
    public JAXBElement<String> createAddUserClaimValueClaimValue(String value) {
        return new JAXBElement<String>(_GetUserListClaimValue_QNAME, String.class, AddUserClaimValue.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "userName", scope = AddUserClaimValue.class)
    public JAXBElement<String> createAddUserClaimValueUserName(String value) {
        return new JAXBElement<String>(_GetUserClaimValuesUserName_QNAME, String.class, AddUserClaimValue.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "profileName", scope = SetUserClaimValue.class)
    public JAXBElement<String> createSetUserClaimValueProfileName(String value) {
        return new JAXBElement<String>(_GetUserClaimValuesProfileName_QNAME, String.class, SetUserClaimValue.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "claimURI", scope = SetUserClaimValue.class)
    public JAXBElement<String> createSetUserClaimValueClaimURI(String value) {
        return new JAXBElement<String>(_DeleteUserClaimValueClaimURI_QNAME, String.class, SetUserClaimValue.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "claimValue", scope = SetUserClaimValue.class)
    public JAXBElement<String> createSetUserClaimValueClaimValue(String value) {
        return new JAXBElement<String>(_GetUserListClaimValue_QNAME, String.class, SetUserClaimValue.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "userName", scope = SetUserClaimValue.class)
    public JAXBElement<String> createSetUserClaimValueUserName(String value) {
        return new JAXBElement<String>(_GetUserClaimValuesUserName_QNAME, String.class, SetUserClaimValue.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "credential", scope = Authenticate.class)
    public JAXBElement<String> createAuthenticateCredential(String value) {
        return new JAXBElement<String>(_AddUserCredential_QNAME, String.class, Authenticate.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "userName", scope = Authenticate.class)
    public JAXBElement<String> createAuthenticateUserName(String value) {
        return new JAXBElement<String>(_GetUserClaimValuesUserName_QNAME, String.class, Authenticate.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AX2597UserStoreException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "UserStoreException", scope = RemoteUserStoreManagerServiceUserStoreException.class)
    public JAXBElement<AX2597UserStoreException> createRemoteUserStoreManagerServiceUserStoreExceptionUserStoreException(AX2597UserStoreException value) {
        return new JAXBElement<AX2597UserStoreException>(_RemoteUserStoreManagerServiceUserStoreExceptionUserStoreException_QNAME, AX2597UserStoreException.class, RemoteUserStoreManagerServiceUserStoreException.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "userName", scope = UpdateRoleListOfUser.class)
    public JAXBElement<String> createUpdateRoleListOfUserUserName(String value) {
        return new JAXBElement<String>(_GetUserClaimValuesUserName_QNAME, String.class, UpdateRoleListOfUser.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "profileName", scope = DeleteUserClaimValues.class)
    public JAXBElement<String> createDeleteUserClaimValuesProfileName(String value) {
        return new JAXBElement<String>(_GetUserClaimValuesProfileName_QNAME, String.class, DeleteUserClaimValues.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "userName", scope = DeleteUserClaimValues.class)
    public JAXBElement<String> createDeleteUserClaimValuesUserName(String value) {
        return new JAXBElement<String>(_GetUserClaimValuesUserName_QNAME, String.class, DeleteUserClaimValues.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "roleName", scope = DeleteRole.class)
    public JAXBElement<String> createDeleteRoleRoleName(String value) {
        return new JAXBElement<String>(_UpdateUserListOfRoleRoleName_QNAME, String.class, DeleteRole.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AX2604Tenant }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "tenant", scope = GetProperties.class)
    public JAXBElement<AX2604Tenant> createGetPropertiesTenant(AX2604Tenant value) {
        return new JAXBElement<AX2604Tenant>(_GetPropertiesTenant_QNAME, AX2604Tenant.class, GetProperties.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "newCredential", scope = UpdateCredential.class)
    public JAXBElement<String> createUpdateCredentialNewCredential(String value) {
        return new JAXBElement<String>(_UpdateCredentialByAdminNewCredential_QNAME, String.class, UpdateCredential.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "userName", scope = UpdateCredential.class)
    public JAXBElement<String> createUpdateCredentialUserName(String value) {
        return new JAXBElement<String>(_GetUserClaimValuesUserName_QNAME, String.class, UpdateCredential.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "oldCredential", scope = UpdateCredential.class)
    public JAXBElement<String> createUpdateCredentialOldCredential(String value) {
        return new JAXBElement<String>(_UpdateCredentialOldCredential_QNAME, String.class, UpdateCredential.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "userName", scope = GetProfileNames.class)
    public JAXBElement<String> createGetProfileNamesUserName(String value) {
        return new JAXBElement<String>(_GetUserClaimValuesUserName_QNAME, String.class, GetProfileNames.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.um.carbon.wso2.org", name = "userName", scope = GetRoleListOfUser.class)
    public JAXBElement<String> createGetRoleListOfUserUserName(String value) {
        return new JAXBElement<String>(_GetUserClaimValuesUserName_QNAME, String.class, GetRoleListOfUser.class, value);
    }

}
