/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.mvc.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.sitewhere.SiteWhere;
import com.sitewhere.Tracer;
import com.sitewhere.common.MarshalUtils;
import com.sitewhere.device.marshaling.DeviceAssignmentMarshalHelper;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.security.LoginManager;
import com.sitewhere.spi.ServerStartupException;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetCategory;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceSpecification;
import com.sitewhere.spi.device.ISite;
import com.sitewhere.spi.device.batch.IBatchOperation;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.group.IDeviceGroup;
import com.sitewhere.spi.device.request.IBatchCommandInvocationRequest;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.server.ISiteWhereTenantEngineState;
import com.sitewhere.spi.server.debug.TracerCategory;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.spi.user.IUser;
import com.sitewhere.spi.user.SiteWhereRoles;
import com.sitewhere.version.VersionHelper;
import com.sitewhere.web.configuration.TenantConfigurationModel;
import com.sitewhere.web.configuration.TokenNamePair;
import com.sitewhere.web.configuration.model.ElementRole;
import com.sitewhere.web.mvc.AuthoritiesHelper;
import com.sitewhere.web.mvc.MvcController;
import com.sitewhere.web.mvc.NoTenantException;

/**
 * Spring MVC controller for SiteWhere administrative application.
 * 
 * @author dadams
 */
@Controller
@RequestMapping
public class AdminInterfaceController extends MvcController {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(AdminInterfaceController.class);

	/**
	 * Display the "login" page.
	 * 
	 * @return
	 */
	@RequestMapping("/")
	public ModelAndView login() {
		Tracer.start(TracerCategory.AdminUserInterface, "login", LOGGER);
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put(DATA_VERSION, VersionHelper.getVersion());
			if (SiteWhere.getServer().getLifecycleStatus() == LifecycleStatus.Started) {
				return new ModelAndView("login", data);
			} else {
				ServerStartupException failure = SiteWhere.getServer().getServerStartupError();
				data.put("subsystem", failure.getDescription());
				data.put("component", failure.getComponent().getLifecycleError().getMessage());
				return new ModelAndView("noserver", data);
			}
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Display page for choosing tenant.
	 * 
	 * @param redirect
	 * @param servletRequest
	 * @return
	 */
	@RequestMapping("/tenant")
	public ModelAndView showTenantChoices(@RequestParam(required = false) String redirect,
			HttpServletRequest servletRequest) {
		Tracer.start(TracerCategory.AdminUserInterface, "showTenantChoices", LOGGER);
		try {
			if ((SecurityContextHolder.getContext() == null)
					|| (SecurityContextHolder.getContext().getAuthentication() == null)) {
				return login();
			}

			// Find tenants the logged in user is able to view.
			IUser user = LoginManager.getCurrentlyLoggedInUser();
			AuthoritiesHelper auths = new AuthoritiesHelper(user);
			List<ITenant> matches = SiteWhere.getServer().getAuthorizedTenants(user.getUsername(), true);

			// Create standard data objects, but do not require tenant.
			Map<String, Object> data = createBaseData(servletRequest);

			// Handle cases where there are no tenants or exactly one.
			if (matches.size() == 0) {
				return handleNoRunningTenants(auths, data);
			} else if (matches.size() == 1) {
				return chooseTenant(matches.get(0).getId(), redirect, servletRequest);
			}

			// Multiple tenants available. Allow user to choose.
			return new ModelAndView("tenant", data);
		} catch (SiteWhereException e) {
			return showError(e);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Handle case where user either has no tenants or has no running tenants.
	 * 
	 * @param auths
	 * @return
	 */
	protected ModelAndView handleNoRunningTenants(AuthoritiesHelper auths, Map<String, Object> data) {
		if (auths.isAdministerTenants()) {
			return new ModelAndView("tenants/list", data);
		} else {
			return showError("User is not authorized to access any of the available tenants.");
		}
	}

	@RequestMapping("/tenant/{tenantId}")
	public ModelAndView chooseTenant(@PathVariable("tenantId") String tenantId,
			@RequestParam(required = false) String redirect, HttpServletRequest servletRequest) {
		Tracer.start(TracerCategory.AdminUserInterface, "chooseTenant", LOGGER);
		try {
			if ((SecurityContextHolder.getContext() == null)
					|| (SecurityContextHolder.getContext().getAuthentication() == null)) {
				return login();
			}

			// If no redirect specified, show server info page.
			IUser user = LoginManager.getCurrentlyLoggedInUser();
			// AuthoritiesHelper auths = new AuthoritiesHelper(user);
			if ((redirect == null) || (redirect.length() == 0)) {
				redirect = "/admin/" + tenantId + "/sites/list.html";
			}

			// Find tenants the logged in user is able to view.
			List<ITenant> matches = SiteWhere.getServer().getAuthorizedTenants(user.getUsername(), true);
			ITenant chosen = null;
			for (ITenant tenant : matches) {
				if (tenant.getId().equals(tenantId)) {
					chosen = tenant;
				}
			}

			// Trying to choose an invalid or unauthorized tenant.
			if (chosen == null) {
				return showError("Invalid tenant choice.");
			}

			return new ModelAndView("redirect:" + redirect);
		} catch (SiteWhereException e) {
			return showError(e);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Display the "login" page after failed login.
	 * 
	 * @return
	 */
	@RequestMapping("/loginFailed")
	public ModelAndView loginFailed() {
		Tracer.start(TracerCategory.AdminUserInterface, "loginFailed", LOGGER);
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put(DATA_VERSION, VersionHelper.getVersion());
			data.put("loginFailed", true);
			return new ModelAndView("login", data);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Show SiteWhere server information.
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/server")
	@Secured({ SiteWhereRoles.VIEW_SERVER_INFO })
	public ModelAndView serverInfo(HttpServletRequest request) {
		Tracer.start(TracerCategory.AdminUserInterface, "serverInfo", LOGGER);
		try {
			Map<String, Object> data = createBaseData(request);
			return new ModelAndView("server/server", data);
		} catch (NoTenantException e) {
			return showTenantChoices(getUrl(request), request);
		} catch (SiteWhereException e) {
			return showError(e);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Display the "list sites" page.
	 * 
	 * @param tenantId
	 * @param request
	 * @return
	 */
	@RequestMapping("/{tenantId}/sites/list")
	public ModelAndView listSites(@PathVariable("tenantId") String tenantId, HttpServletRequest request) {
		Tracer.start(TracerCategory.AdminUserInterface, "listSites", LOGGER);
		try {
			Map<String, Object> data = createTenantPageBaseData(tenantId, request);
			return new ModelAndView("sites/list", data);
		} catch (NoTenantException e) {
			return showTenantChoices(getUrl(request), request);
		} catch (SiteWhereException e) {
			return showError(e);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Display the "site detail" page.
	 * 
	 * @param tenantId
	 * @param siteToken
	 * @param request
	 * @return
	 */
	@RequestMapping("/{tenantId}/sites/{siteToken}")
	public ModelAndView siteDetail(@PathVariable("tenantId") String tenantId,
			@PathVariable("siteToken") String siteToken, HttpServletRequest request) {
		Tracer.start(TracerCategory.AdminUserInterface, "siteDetail", LOGGER);
		try {
			Map<String, Object> data = createTenantPageBaseData(tenantId, request);
			ITenant tenant = (ITenant) data.get(DATA_TENANT);
			IDeviceManagement management = SiteWhere.getServer().getDeviceManagement(tenant);
			ISite site = management.getSiteByToken(siteToken);
			if (site != null) {
				data.put("site", site);
				return new ModelAndView("sites/detail", data);
			}
			return showError("Site for token '" + siteToken + "' not found.");
		} catch (NoTenantException e) {
			return showTenantChoices(getUrl(request), request);
		} catch (SiteWhereException e) {
			return showError(e);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Display the "assignment detail" page.
	 * 
	 * @param tenantId
	 * @param token
	 * @param request
	 * @return
	 */
	@RequestMapping("/{tenantId}/assignments/{token}")
	public ModelAndView assignmentDetail(@PathVariable("tenantId") String tenantId, @PathVariable("token") String token,
			HttpServletRequest request) {
		Tracer.start(TracerCategory.AdminUserInterface, "assignmentDetail", LOGGER);
		try {
			Map<String, Object> data = createTenantPageBaseData(tenantId, request);
			ITenant tenant = (ITenant) data.get(DATA_TENANT);
			IDeviceManagement management = SiteWhere.getServer().getDeviceManagement(tenant);
			IDeviceAssignment assignment = management.getDeviceAssignmentByToken(token);
			if (assignment != null) {
				DeviceAssignmentMarshalHelper helper = new DeviceAssignmentMarshalHelper(tenant);
				helper.setIncludeDevice(true);
				assignment = helper.convert(assignment, SiteWhere.getServer().getAssetModuleManager(tenant));
				data.put("assignment", assignment);
				return new ModelAndView("assignments/detail", data);
			}
			return showError("Assignment for token '" + token + "' not found.");
		} catch (NoTenantException e) {
			return showTenantChoices(getUrl(request), request);
		} catch (SiteWhereException e) {
			return showError(e);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Display the "assignment emulator" page.
	 * 
	 * @param tenantId
	 * @param token
	 * @param request
	 * @return
	 */
	@RequestMapping("/{tenantId}/assignments/{token}/emulator")
	public ModelAndView assignmentEmulator(@PathVariable("tenantId") String tenantId,
			@PathVariable("token") String token, HttpServletRequest request) {
		Tracer.start(TracerCategory.AdminUserInterface, "assignmentEmulator", LOGGER);
		try {
			Map<String, Object> data = createTenantPageBaseData(tenantId, request);
			ITenant tenant = (ITenant) data.get(DATA_TENANT);
			IDeviceManagement management = SiteWhere.getServer().getDeviceManagement(tenant);
			IDeviceAssignment assignment = management.getDeviceAssignmentByToken(token);
			if (assignment != null) {
				data.put("assignment", assignment);
				return new ModelAndView("assignments/emulator", data);
			}
			return showError("Assignment for token '" + token + "' not found.");
		} catch (NoTenantException e) {
			return showTenantChoices(getUrl(request), request);
		} catch (SiteWhereException e) {
			return showError(e);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Display the "list device specifications" page.
	 * 
	 * @param tenantId
	 * @param request
	 * @return
	 */
	@RequestMapping("/{tenantId}/specifications/list")
	public ModelAndView listSpecifications(@PathVariable("tenantId") String tenantId, HttpServletRequest request) {
		Tracer.start(TracerCategory.AdminUserInterface, "listSpecifications", LOGGER);
		try {
			Map<String, Object> data = createTenantPageBaseData(tenantId, request);
			return new ModelAndView("specifications/list", data);
		} catch (NoTenantException e) {
			return showTenantChoices(getUrl(request), request);
		} catch (SiteWhereException e) {
			return showError(e);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Display the "specification detail" page.
	 * 
	 * @param tenantId
	 * @param token
	 * @param request
	 * @return
	 */
	@RequestMapping("/{tenantId}/specifications/{token}")
	public ModelAndView specificationDetail(@PathVariable("tenantId") String tenantId,
			@PathVariable("token") String token, HttpServletRequest request) {
		Tracer.start(TracerCategory.AdminUserInterface, "specificationDetail", LOGGER);
		try {
			Map<String, Object> data = createTenantPageBaseData(tenantId, request);
			ITenant tenant = (ITenant) data.get(DATA_TENANT);
			IDeviceManagement management = SiteWhere.getServer().getDeviceManagement(tenant);
			IDeviceSpecification spec = management.getDeviceSpecificationByToken(token);
			if (spec != null) {
				data.put("specification", spec);
				return new ModelAndView("specifications/detail", data);
			}
			return showError("Specification for token '" + token + "' not found.");
		} catch (NoTenantException e) {
			return showTenantChoices(getUrl(request), request);
		} catch (SiteWhereException e) {
			return showError(e);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Display the "list devices" page.
	 * 
	 * @param tenantId
	 * @param specification
	 * @param group
	 * @param groupsWithRole
	 * @param site
	 * @param dateRange
	 * @param beforeDate
	 * @param afterDate
	 * @param excludeAssigned
	 * @param request
	 * @return
	 */
	@RequestMapping("/{tenantId}/devices/list")
	public ModelAndView listDevices(@PathVariable("tenantId") String tenantId,
			@RequestParam(required = false) String specification, @RequestParam(required = false) String group,
			@RequestParam(required = false) String groupsWithRole, @RequestParam(required = false) String site,
			@RequestParam(required = false) String dateRange, @RequestParam(required = false) String beforeDate,
			@RequestParam(required = false) String afterDate, @RequestParam(required = false) boolean excludeAssigned,
			HttpServletRequest request) {
		Tracer.start(TracerCategory.AdminUserInterface, "listDevices", LOGGER);
		try {
			Map<String, Object> data = createTenantPageBaseData(tenantId, request);
			ITenant tenant = (ITenant) data.get(DATA_TENANT);

			// Look up specification that will be used for filtering.
			if (specification != null) {
				IDeviceSpecification found = SiteWhere.getServer().getDeviceManagement(tenant)
						.getDeviceSpecificationByToken(specification);
				if (found == null) {
					throw new SiteWhereException("Specification token was not valid.");
				}
				data.put("specification", found);
			}

			// Look up device group that will be used for filtering.
			if (group != null) {
				IDeviceGroup found = SiteWhere.getServer().getDeviceManagement(tenant).getDeviceGroup(group);
				if (found == null) {
					throw new SiteWhereException("Device group token was not valid.");
				}
				data.put("group", found);
			}

			if (groupsWithRole != null) {
				data.put("groupsWithRole", groupsWithRole);
			}

			// Look up device group that will be used for filtering.
			if (site != null) {
				ISite found = SiteWhere.getServer().getDeviceManagement(tenant).getSiteByToken(site);
				if (found == null) {
					throw new SiteWhereException("Site token was not valid.");
				}
				data.put("site", found);
			}

			data.put("dateRange", dateRange);
			data.put("beforeDate", beforeDate);
			data.put("afterDate", afterDate);
			data.put("excludeAssigned", excludeAssigned);
			return new ModelAndView("devices/list", data);
		} catch (NoTenantException e) {
			return showTenantChoices(getUrl(request), request);
		} catch (SiteWhereException e) {
			return showError(e);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Display the "device detail" page.
	 * 
	 * @param tenantId
	 * @param hardwareId
	 * @param request
	 * @return
	 */
	@RequestMapping("/{tenantId}/devices/{hardwareId}")
	public ModelAndView deviceDetail(@PathVariable("tenantId") String tenantId,
			@PathVariable("hardwareId") String hardwareId, HttpServletRequest request) {
		Tracer.start(TracerCategory.AdminUserInterface, "deviceDetail", LOGGER);
		try {
			Map<String, Object> data = createTenantPageBaseData(tenantId, request);
			ITenant tenant = (ITenant) data.get(DATA_TENANT);
			IDeviceManagement management = SiteWhere.getServer().getDeviceManagement(tenant);
			IDevice device = management.getDeviceByHardwareId(hardwareId);
			if (device != null) {
				IDeviceSpecification specification = management
						.getDeviceSpecificationByToken(device.getSpecificationToken());
				data.put("device", device);
				data.put("specification", specification);
				return new ModelAndView("devices/detail", data);
			}
			return showError("Device for hardware id '" + hardwareId + "' not found.");
		} catch (NoTenantException e) {
			return showTenantChoices(getUrl(request), request);
		} catch (SiteWhereException e) {
			return showError(e);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Display the "list device groups" page.
	 * 
	 * @param tenantId
	 * @param request
	 * @return
	 */
	@RequestMapping("/{tenantId}/groups/list")
	public ModelAndView listDeviceGroups(@PathVariable("tenantId") String tenantId, HttpServletRequest request) {
		Tracer.start(TracerCategory.AdminUserInterface, "listDeviceGroups", LOGGER);
		try {
			Map<String, Object> data = createTenantPageBaseData(tenantId, request);
			return new ModelAndView("groups/list", data);
		} catch (NoTenantException e) {
			return showTenantChoices(getUrl(request), request);
		} catch (SiteWhereException e) {
			return showError(e);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Display the "device group detail" page.
	 * 
	 * @param tenantId
	 * @param groupToken
	 * @param request
	 * @return
	 */
	@RequestMapping("/{tenantId}/groups/{groupToken}")
	public ModelAndView deviceGroupDetail(@PathVariable("tenantId") String tenantId,
			@PathVariable("groupToken") String groupToken, HttpServletRequest request) {
		Tracer.start(TracerCategory.AdminUserInterface, "deviceGroupDetail", LOGGER);
		try {
			Map<String, Object> data = createTenantPageBaseData(tenantId, request);
			ITenant tenant = (ITenant) data.get(DATA_TENANT);
			IDeviceManagement management = SiteWhere.getServer().getDeviceManagement(tenant);
			IDeviceGroup group = management.getDeviceGroup(groupToken);
			if (group != null) {
				data.put("group", group);
				return new ModelAndView("groups/detail", data);
			}
			return showError("Device group for token '" + groupToken + "' not found.");
		} catch (NoTenantException e) {
			return showTenantChoices(getUrl(request), request);
		} catch (SiteWhereException e) {
			return showError(e);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Display the "asset categories" page.
	 * 
	 * @param tenantId
	 * @param request
	 * @return
	 */
	@RequestMapping("/{tenantId}/assets/categories")
	public ModelAndView listAssetCategories(@PathVariable("tenantId") String tenantId, HttpServletRequest request) {
		Tracer.start(TracerCategory.AdminUserInterface, "listAssetCategories", LOGGER);
		try {
			Map<String, Object> data = createTenantPageBaseData(tenantId, request);
			return new ModelAndView("assets/categories", data);
		} catch (NoTenantException e) {
			return showTenantChoices(getUrl(request), request);
		} catch (SiteWhereException e) {
			return showError(e);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Display the "category person assets" page.
	 * 
	 * @param tenantId
	 * @param categoryId
	 * @param request
	 * @return
	 */
	@RequestMapping("/{tenantId}/assets/categories/{categoryId}")
	public ModelAndView listCategoryAssets(@PathVariable("tenantId") String tenantId,
			@PathVariable("categoryId") String categoryId, HttpServletRequest request) {
		Tracer.start(TracerCategory.AdminUserInterface, "listCategoryAssets", LOGGER);
		try {
			Map<String, Object> data = createTenantPageBaseData(tenantId, request);
			ITenant tenant = (ITenant) data.get(DATA_TENANT);
			IAssetCategory category = SiteWhere.getServer().getAssetManagement(tenant).getAssetCategory(categoryId);
			data.put("category", category);
			return new ModelAndView("assets/categoryAssets", data);
		} catch (NoTenantException e) {
			return showTenantChoices(getUrl(request), request);
		} catch (SiteWhereException e) {
			return showError(e);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Display the "list batch operations" page.
	 * 
	 * @param tenantId
	 * @param request
	 * @return
	 */
	@RequestMapping("/{tenantId}/batch/list")
	public ModelAndView listBatchOperations(@PathVariable("tenantId") String tenantId, HttpServletRequest request) {
		Tracer.start(TracerCategory.AdminUserInterface, "listBatchOperations", LOGGER);
		try {
			Map<String, Object> data = createTenantPageBaseData(tenantId, request);
			return new ModelAndView("batch/list", data);
		} catch (NoTenantException e) {
			return showTenantChoices(getUrl(request), request);
		} catch (SiteWhereException e) {
			return showError(e);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * View details about a batch command invocation.
	 * 
	 * @param tenantId
	 * @param batchToken
	 * @param request
	 * @return
	 */
	@RequestMapping("/{tenantId}/batch/command/{token}")
	public ModelAndView batchCommandInvocationDetail(@PathVariable("tenantId") String tenantId,
			@PathVariable("token") String batchToken, HttpServletRequest request) {
		Tracer.start(TracerCategory.AdminUserInterface, "batchCommandInvocationDetail", LOGGER);
		try {
			Map<String, Object> data = createTenantPageBaseData(tenantId, request);
			ITenant tenant = (ITenant) data.get(DATA_TENANT);
			IDeviceManagement management = SiteWhere.getServer().getDeviceManagement(tenant);
			IBatchOperation operation = management.getBatchOperation(batchToken);
			if (operation != null) {
				data.put("operation", operation);
				String commandToken = operation.getParameters().get(IBatchCommandInvocationRequest.PARAM_COMMAND_TOKEN);
				if (commandToken == null) {
					return showError("No command token set for batch operation.");
				}
				IDeviceCommand command = management.getDeviceCommandByToken(commandToken);
				data.put("command", new String(MarshalUtils.marshalJsonAsString(command)));
				return new ModelAndView("batch/command", data);
			}
			return showError("Batch operation for token '" + batchToken + "' not found.");
		} catch (NoTenantException e) {
			return showTenantChoices(getUrl(request), request);
		} catch (SiteWhereException e) {
			return showError(e);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Display the "list schedules" page.
	 * 
	 * @param tenantId
	 * @param request
	 * @return
	 */
	@RequestMapping("/{tenantId}/schedules/list")
	public ModelAndView listSchedules(@PathVariable("tenantId") String tenantId, HttpServletRequest request) {
		Tracer.start(TracerCategory.AdminUserInterface, "listSchedules", LOGGER);
		try {
			Map<String, Object> data = createTenantPageBaseData(tenantId, request);
			return new ModelAndView("schedules/list", data);
		} catch (NoTenantException e) {
			return showTenantChoices(getUrl(request), request);
		} catch (SiteWhereException e) {
			return showError(e);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Display the "list scheduled jobs" page.
	 * 
	 * @param tenantId
	 * @param request
	 * @return
	 */
	@RequestMapping("/{tenantId}/jobs/list")
	public ModelAndView listScheduledjobs(@PathVariable("tenantId") String tenantId, HttpServletRequest request) {
		Tracer.start(TracerCategory.AdminUserInterface, "listScheduledjobs", LOGGER);
		try {
			Map<String, Object> data = createTenantPageBaseData(tenantId, request);
			return new ModelAndView("jobs/list", data);
		} catch (NoTenantException e) {
			return showTenantChoices(getUrl(request), request);
		} catch (SiteWhereException e) {
			return showError(e);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Display the "list users" page.
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/users/list")
	public ModelAndView listUsers(HttpServletRequest request) {
		Tracer.start(TracerCategory.AdminUserInterface, "listUsers", LOGGER);
		try {
			Map<String, Object> data = createBaseData(request);
			return new ModelAndView("users/list", data);
		} catch (SiteWhereException e) {
			return showError(e);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Display the "list tenants" page.
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/tenants/list")
	@Secured({ SiteWhereRoles.ADMINISTER_TENANTS })
	public ModelAndView listTenants(HttpServletRequest request) {
		Tracer.start(TracerCategory.AdminUserInterface, "listTenants", LOGGER);
		try {
			Map<String, Object> data = createBaseData(request);
			return new ModelAndView("tenants/list", data);
		} catch (SiteWhereException e) {
			return showError(e);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * View tenant details.
	 * 
	 * @param tenantId
	 * @param request
	 * @return
	 */
	@RequestMapping("/tenants/{tenantId}")
	public ModelAndView viewTenant(@PathVariable("tenantId") String tenantId, HttpServletRequest request) {
		Tracer.start(TracerCategory.AdminUserInterface, "viewTenant", LOGGER);
		try {
			Map<String, Object> data = createBaseData(request);

			// Pass JSON representation of tenant configuration model.
			TenantConfigurationModel configModel = new TenantConfigurationModel();
			String strConfigModel = MarshalUtils.marshalJsonAsString(configModel);
			data.put("configModel", strConfigModel);

			// Pass JSON representation of component roles.
			ElementRole[] roles = ElementRole.values();
			Map<String, ElementRole> rolesById = new HashMap<String, ElementRole>();
			for (ElementRole role : roles) {
				rolesById.put(role.name(), role);
			}
			String strRoles = MarshalUtils.marshalJsonAsString(rolesById);
			data.put("roles", strRoles);

			ITenant tenant = SiteWhere.getServer().getTenantManagement().getTenantById(tenantId);
			if (tenant == null) {
				showError("Invalid tenant id.");
			}
			data.put("selected", tenant);

			// Add data from tenant.
			addTenantData(tenant, data);

			return new ModelAndView("tenants/detail", data);
		} catch (SiteWhereException e) {
			return showError(e);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Display the "list tenant groups" page.
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/tgroups/list")
	@Secured({ SiteWhereRoles.ADMINISTER_TENANTS })
	public ModelAndView listTenantGroups(HttpServletRequest request) {
		Tracer.start(TracerCategory.AdminUserInterface, "listTenantGroups", LOGGER);
		try {
			Map<String, Object> data = createBaseData(request);
			return new ModelAndView("tgroups/list", data);
		} catch (SiteWhereException e) {
			return showError(e);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Add site and specification data if tenant is active.
	 * 
	 * @param tenant
	 * @param data
	 * @throws SiteWhereException
	 */
	protected void addTenantData(ITenant tenant, Map<String, Object> data) throws SiteWhereException {
		ISiteWhereTenantEngineState state = SiteWhere.getServer().getTenantEngine(tenant.getId()).getEngineState();

		// Only attempt to load data if engine is started.
		if (state.getLifecycleStatus() == LifecycleStatus.Started) {
			// Pass JSON representation of sites list.
			ISearchResults<ISite> sites = SiteWhere.getServer().getDeviceManagement(tenant)
					.listSites(new SearchCriteria(1, 0));
			List<TokenNamePair> sitesList = new ArrayList<TokenNamePair>();
			for (ISite site : sites.getResults()) {
				TokenNamePair pair = new TokenNamePair();
				pair.setToken(site.getToken());
				pair.setName(site.getName());
				sitesList.add(pair);
			}
			Collections.sort(sitesList);
			String strSites = MarshalUtils.marshalJsonAsString(sitesList);
			data.put("sites", strSites);

			// Pass JSON representation of specifications list.
			ISearchResults<IDeviceSpecification> specs = SiteWhere.getServer().getDeviceManagement(tenant)
					.listDeviceSpecifications(false, new SearchCriteria(1, 0));
			List<TokenNamePair> specsList = new ArrayList<TokenNamePair>();
			for (IDeviceSpecification spec : specs.getResults()) {
				TokenNamePair pair = new TokenNamePair();
				pair.setToken(spec.getToken());
				pair.setName(spec.getName());
				specsList.add(pair);
			}
			Collections.sort(specsList);
			String strSpecs = MarshalUtils.marshalJsonAsString(specsList);
			data.put("specifications", strSpecs);
		} else {
			data.put("sites", "null");
			data.put("specifications", "null");
		}
	}
}