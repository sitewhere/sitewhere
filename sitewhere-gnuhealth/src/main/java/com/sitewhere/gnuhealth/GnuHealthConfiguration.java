/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.gnuhealth;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.WebClient;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitewhere.spi.SiteWhereException;

/**
 * Common configuration options for accessing GNU Health.
 * 
 * @author Derek
 */
public class GnuHealthConfiguration {

	/** Default base URL Tryton JSON-RPC responder */
	private static final String DEFAULT_URL = "http://sitewhere-aws:8000/gnuhealth";

	/** Default Tryton username */
	private static final String DEFAULT_USERNAME = "admin";

	/** Default Tryton password */
	private static final String DEFAULT_PASSWORD = "gnuadmin";

	/** Base URL for contacting GNU Health */
	private String baseUrl = DEFAULT_URL;

	/** Username for GH login */
	private String username = DEFAULT_USERNAME;

	/** Password for GH login */
	private String password = DEFAULT_PASSWORD;

	/** User id returned by login */
	private int userId;

	/** Cookie returned by login */
	private String cookie;

	/** Constant for HTTP POST method */
	public static final String METHOD_POST = "POST";

	/** Jackson JSON mapper */
	private ObjectMapper mapper = new ObjectMapper();

	/** Handler for accessing data */
	private GnuHealthData gnuHealthData;

	/**
	 * Lazy load the underlying data cache.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public GnuHealthData getGnuHealthData() throws SiteWhereException {
		if (gnuHealthData == null) {
			gnuHealthData = new GnuHealthData(this);
			gnuHealthData.start();
		}
		return gnuHealthData;
	}

	/**
	 * Attempt login to GNU Health server.
	 * 
	 * @throws SiteWhereException
	 */
	protected void login(WebClient client) throws SiteWhereException {
		JsonCall login =
				new JsonCall("common.server.login", 1, new Object[] { getUsername(), getPassword() });
		Response response = client.invoke(METHOD_POST, login);
		JsonNode json = null;
		try {
			json = mapper.readTree((InputStream) response.getEntity());
		} catch (JsonParseException e) {
			throw new SiteWhereException("Unable to parse login response.", e);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to read login response.", e);
		}
		JsonNode resultJson = json.get("result");
		if (resultJson.isBoolean()) {
			throw new SiteWhereException("GNU Health login failed.");
		}
		if (!resultJson.isArray()) {
			throw new SiteWhereException("Unexpected login response from GNU Health: "
					+ resultJson.toString());
		}
		this.userId = resultJson.get(0).asInt();
		this.cookie = resultJson.get(1).asText();
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getCookie() {
		return cookie;
	}

	public void setCookie(String cookie) {
		this.cookie = cookie;
	}

	/**
	 * Wrapper object for calling a GH method.
	 * 
	 * @author Derek
	 */
	public static class JsonCall {

		/** Method name */
		private String method;

		/** Call id */
		private int id;

		/** Method parameters */
		private Object[] params;

		public JsonCall(String method, int id, Object[] params) {
			this.method = method;
			this.id = id;
			this.params = params;
		}

		public String getMethod() {
			return method;
		}

		public void setMethod(String method) {
			this.method = method;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public Object[] getParams() {
			return params;
		}

		public void setParams(Object[] params) {
			this.params = params;
		}
	}
}