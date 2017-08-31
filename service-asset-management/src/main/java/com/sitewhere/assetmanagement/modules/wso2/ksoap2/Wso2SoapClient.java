/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.assetmanagement.modules.wso2.ksoap2;

import java.io.IOException;
import java.net.Authenticator;
import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpsTransportSE;
import org.ksoap2.transport.ServiceConnection;
import org.xmlpull.v1.XmlPullParserException;

import com.sitewhere.spi.SiteWhereException;

/**
 * Client for WSO2 remote user store manager service using ksoap2 for lightweight SOAP
 * container.
 * 
 * @author Derek
 */
public class Wso2SoapClient {

	/** Namespace used by WSO2 services */
	public static final String CARBON_NAMESPACE = "http://service.ws.um.carbon.wso2.org";

	/** Hostname for WSO2 Identity Server */
	public static final String WSO2_HOST = "wso2is.sitewhere.org";

	/** Hostname for WSO2 Identity Server */
	public static final int WSO2_PORT = 9443;

	/** Service URL for WSO2 Identity Server */
	public static final String WSO2_SERVICE = "/services/RemoteUserStoreManagerService";

	/** Host for WSO2 IS instance */
	private String host = WSO2_HOST;

	/** Port for WSO2 IS instance */
	private int port = WSO2_PORT;

	public Wso2SoapClient() {
		this(WSO2_HOST, WSO2_PORT);
	}

	public Wso2SoapClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

	/**
	 * Authenticate with the given username and password.
	 * 
	 * @param username
	 * @param credentials
	 * @return
	 * @throws SiteWhereException
	 */
	public boolean authenticate(String username, String credentials) throws SiteWhereException {
		SoapObject body = new SoapObject(CARBON_NAMESPACE, "authenticate");
		body.addProperty("userName", username);
		body.addProperty("credential", credentials);
		Object response = send("urn:authenticate", body);
		return getPrimitiveBoolean(response);
	}

	public List<String> getRoleListOfUser(String username) throws SiteWhereException {
		SoapObject body = new SoapObject(CARBON_NAMESPACE, "getRoleListOfUser");
		body.addProperty("userName", username);
		Object response = send("urn:getRoleListOfUser", body);
		return getPrimitiveStringList(response);
	}

	/**
	 * Send a SOAP message.
	 * 
	 * @param action
	 * @param body
	 * @return
	 * @throws SiteWhereException
	 */
	protected Object send(String action, SoapObject body) throws SiteWhereException {
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.bodyOut = body;

		HttpsTransportSE transport = new HttpsTransportSE(getHost(), getPort(), WSO2_SERVICE, 2000);
		try {
			addBasicAuthentication(transport.getServiceConnection(), "admin", "admin");
			transport.call(action, envelope);
			return envelope.getResponse();
		} catch (HttpResponseException e) {
			throw new SiteWhereException(e);
		} catch (IOException e) {
			throw new SiteWhereException(e);
		} catch (XmlPullParserException e) {
			throw new SiteWhereException(e);
		}
	}

	/**
	 * Add basic authentication.
	 * 
	 * @param conn
	 * @param username
	 * @param password
	 * @throws SiteWhereException
	 */
	public void addBasicAuthentication(ServiceConnection conn, String username, String password)
			throws SiteWhereException {
		StringBuffer buffer = new StringBuffer(username);
		buffer.append(':').append(password);
		byte[] bytes = buffer.toString().getBytes();
		buffer.setLength(0);
		buffer.append("Basic ");
		buffer.append(new String(Base64.encodeBase64(bytes)));
		try {
			conn.setRequestProperty("Authorization", buffer.toString());
		} catch (IOException e) {
			throw new SiteWhereException("Error adding basic auth request property.", e);
		}
	}

	/**
	 * Parse out a primitive boolean response.
	 * 
	 * @param response
	 * @return
	 * @throws SiteWhereException
	 */
	protected boolean getPrimitiveBoolean(Object response) throws SiteWhereException {
		if ((response != null) && (response instanceof SoapPrimitive)) {
			String value = (String) ((SoapPrimitive) response).getValue();
			return "true".equalsIgnoreCase(value);
		}
		throw new SiteWhereException("Invalid response from SOAP service.");
	}

	/**
	 * Parse out a primitive String or a primitive String List response.
	 * 
	 * @param response
	 * @return
	 * @throws SiteWhereException
	 */
	@SuppressWarnings("unchecked")
	protected List<String> getPrimitiveStringList(Object response) throws SiteWhereException {
		LinkedList<String> values = new LinkedList<String>();

		if ((response != null) && (response instanceof SoapPrimitive)) {
			String value = (String) ((SoapPrimitive) response).getValue();
			values.add(value);
		} else if ((response != null) && (response instanceof List)) {
			List<SoapPrimitive> primitiveValues = (List<SoapPrimitive>) response;
			for (SoapPrimitive primitiveValue : primitiveValues) {
				values.add((String) primitiveValue.getValue());
			}
		}

		return values;
	}

	/**
	 * Call to turn off SSL certificate checking. This should only be done for testing!
	 */
	public void turnOffSSLVerification() {

		// Send basic authentication header with web service calls.
		Authenticator.setDefault(new java.net.Authenticator() {

			@Override
			protected java.net.PasswordAuthentication getPasswordAuthentication() {
				return new java.net.PasswordAuthentication("admin", "admin".toCharArray());
			}
		});
		// Code below circumvents SSL certificated validation since cert is self-signed.
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}
		} };

		try {
			// Install the all-trusting trust manager
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

			// Install the all-trusting host verifier
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}