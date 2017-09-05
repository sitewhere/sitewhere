/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.wso2.identity.test;

import java.net.Authenticator;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.junit.Before;
import org.junit.Test;

import com.sitewhere.rest.client.SiteWhereClient;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.search.DeviceAssignmentSearchResults;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.spi.ISiteWhereClient;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.wso2.identity.ksoap2.Wso2SoapClient;
import com.sitewhere.wso2.identity.ws.RemoteUserStoreManagerService;
import com.sitewhere.wso2.identity.ws.RemoteUserStoreManagerServicePortType;

public class SoapApiTests {

	/** Default username for authentication */
	public static final String USERNAME = "dadams";

	/** Default password for authentication */
	public static final String PASSWORD = "sitewhere";

	@Before
	public void setup() {
		System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
		System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
		System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump", "true");
		System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", "true");

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

	@Test
	public void testAuthentication() throws Exception {
		URL wsdl = new URL("https://wso2is:9443//services/RemoteUserStoreManagerService?wsdl");
		RemoteUserStoreManagerService service = new RemoteUserStoreManagerService(wsdl);
		RemoteUserStoreManagerServicePortType wso2 =
				service.getRemoteUserStoreManagerServiceHttpsSoap11Endpoint();
		boolean authenticated = wso2.authenticate(USERNAME, PASSWORD);
		System.out.println("Authenticated " + USERNAME + ": " + authenticated);

		ISiteWhereClient sitewhere = new SiteWhereClient();
		DeviceAssignmentSearchResults results =
				sitewhere.getAssignmentsForAsset("bb105f8d-3150-41f5-b9d1-db04965668d3", "wso2", "dadams",
						DeviceAssignmentStatus.Active, new SearchCriteria(1, 0));
		System.out.println("Found " + results.getNumResults() + " devices assigned");
		for (DeviceAssignment current : results.getResults()) {
			DeviceAssignment assignment = sitewhere.getDeviceAssignmentByToken(current.getToken());
			System.out.println("Device: " + assignment.getDevice().getAssetName());
		}
	}

	@Test
	public void testKSoapAuthentication() throws Exception {
		Wso2SoapClient client = new Wso2SoapClient();
		boolean authenticated = client.authenticate(USERNAME, PASSWORD);
		System.out.println("Authenticated " + USERNAME + ": " + authenticated);
	}
}