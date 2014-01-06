/*
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.mvc;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.sitewhere.security.LoginManager;
import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.ISite;
import com.sitewhere.version.VersionHelper;

/**
 * Spring MVC controller for SiteWhere web application.
 * 
 * @author dadams
 */
@Controller
public class SiteWhereController {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(SiteWhereController.class);

	/**
	 * Display the "login" page.
	 * 
	 * @return
	 */
	@RequestMapping("/")
	public ModelAndView login() {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("version", VersionHelper.getVersion());
		return new ModelAndView("login", data);
	}

	/**
	 * Display the "login" page after failed login.
	 * 
	 * @return
	 */
	@RequestMapping("/loginFailed")
	public ModelAndView loginFailed() {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("version", VersionHelper.getVersion());
		data.put("loginFailed", true);
		return new ModelAndView("login", data);
	}

	/**
	 * Display the "list sites" page.
	 * 
	 * @return
	 */
	@RequestMapping("/sites/list")
	public ModelAndView listSites() {
		try {
			Map<String, Object> data = createBaseData();
			return new ModelAndView("sites/list", data);
		} catch (SiteWhereException e) {
			LOGGER.error(e);
			return showError(e.getMessage());
		}
	}

	/**
	 * Display the "site detail" page.
	 * 
	 * @param siteToken
	 * @return
	 */
	@RequestMapping("/sites/detail")
	public ModelAndView siteDetail(@RequestParam("siteToken") String siteToken) {
		if (siteToken != null) {
			try {
				Map<String, Object> data = createBaseData();
				IDeviceManagement management = SiteWhereServer.getInstance().getDeviceManagement();
				ISite site = management.getSiteByToken(siteToken);
				if (site != null) {
					data.put("site", site);
					return new ModelAndView("sites/detail", data);
				}
				return showError("Site for token '" + siteToken + "' not found.");
			} catch (SiteWhereException e) {
				LOGGER.error(e);
				return showError(e.getMessage());
			}
		}
		return showError("No site token passed.");
	}

	/**
	 * Display the "assignment detail" page.
	 * 
	 * @param token
	 * @return
	 */
	@RequestMapping("/assignments/detail")
	public ModelAndView assignmentDetail(@RequestParam("token") String token) {
		if (token != null) {
			try {
				Map<String, Object> data = createBaseData();
				IDeviceManagement management = SiteWhereServer.getInstance().getDeviceManagement();
				IDeviceAssignment assignment = management.getDeviceAssignmentByToken(token);
				if (assignment != null) {
					data.put("assignment", assignment);
					return new ModelAndView("assignments/detail", data);
				}
				return showError("Assignment for token '" + token + "' not found.");
			} catch (SiteWhereException e) {
				LOGGER.error(e);
				return showError(e.getMessage());
			}
		}
		return showError("No assignment token passed.");
	}

	/**
	 * Display the "assignment emulator" page.
	 * 
	 * @param token
	 * @return
	 */
	@RequestMapping("/assignments/emulator")
	public ModelAndView assignmentEmulator(@RequestParam("token") String token) {
		if (token != null) {
			try {
				Map<String, Object> data = createBaseData();
				IDeviceManagement management = SiteWhereServer.getInstance().getDeviceManagement();
				IDeviceAssignment assignment = management.getDeviceAssignmentByToken(token);
				if (assignment != null) {
					data.put("assignment", assignment);
					return new ModelAndView("assignments/emulator", data);
				}
				return showError("Assignment for token '" + token + "' not found.");
			} catch (SiteWhereException e) {
				LOGGER.error(e);
				return showError(e.getMessage());
			}
		}
		return showError("Assignment token was not passed.");
	}

	/**
	 * Display the "list devices" page.
	 * 
	 * @return
	 */
	@RequestMapping("/devices/list")
	public ModelAndView listDevices() {
		try {
			Map<String, Object> data = createBaseData();
			return new ModelAndView("devices/list", data);
		} catch (SiteWhereException e) {
			LOGGER.error(e);
			return showError(e.getMessage());
		}
	}

	/**
	 * Display the "device detail" page.
	 * 
	 * @param hardwareId
	 * @return
	 */
	@RequestMapping("/devices/detail")
	public ModelAndView deviceDetail(@RequestParam("hardwareId") String hardwareId) {
		if (hardwareId != null) {
			try {
				Map<String, Object> data = createBaseData();
				IDeviceManagement management = SiteWhereServer.getInstance().getDeviceManagement();
				IDevice device = management.getDeviceByHardwareId(hardwareId);
				if (device != null) {
					data.put("device", device);
					return new ModelAndView("devices/detail", data);
				}
				return showError("Device for hardware id '" + hardwareId + "' not found.");
			} catch (SiteWhereException e) {
				LOGGER.error(e);
				return showError(e.getMessage());
			}
		}
		return showError("No hardware id token passed.");
	}

	/**
	 * Display the "list users" page.
	 * 
	 * @return
	 */
	@RequestMapping("/users/list")
	public ModelAndView listUsers() {
		try {
			Map<String, Object> data = createBaseData();
			return new ModelAndView("users/list", data);
		} catch (SiteWhereException e) {
			LOGGER.error(e);
			return showError(e.getMessage());
		}
	}

	/**
	 * Returns a {@link ModelAndView} that will display an error message.
	 * 
	 * @param message
	 * @return
	 */
	protected ModelAndView showError(String message) {
		try {
			Map<String, Object> data = createBaseData();
			data.put("message", message);
			return new ModelAndView("error", data);
		} catch (SiteWhereException e) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("message", e.getMessage());
			return new ModelAndView("error", data);
		}
	}

	/**
	 * Create data structure and common objects passed to pages.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	protected Map<String, Object> createBaseData() throws SiteWhereException {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("version", VersionHelper.getVersion());
		data.put("currentUser", LoginManager.getCurrentlyLoggedInUser());
		return data;
	}
}