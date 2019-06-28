/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice;

import com.brsanthu.googleanalytics.GoogleAnalytics;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.IMicroserviceAnalytics;

/**
 * Implementation of {@link IMicroserviceAnalytics} that sends information to
 * Google Analytics based on microservice lifecycle and other events.
 */
public class MicroserviceAnalytics implements IMicroserviceAnalytics {

    /** Google Analytics tracking id for runtime measurements */
    private static final String SITEWHERE_RUNTIME_TRACKING = "UA-122806506-2";

    /** Analytics instance */
    private GoogleAnalytics googleAnalytics;

    public MicroserviceAnalytics() {
	googleAnalytics = GoogleAnalytics.builder().withTrackingId(SITEWHERE_RUNTIME_TRACKING).build();
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.IMicroserviceAnalytics#sendMicroserviceStarted
     * (com.sitewhere.spi.microservice.IMicroservice)
     */
    @Override
    public void sendMicroserviceStarted(IMicroservice<?> microservice) throws SiteWhereException {
	// try {
	// getGoogleAnalytics().event().eventCategory("Microservice_" +
	// microservice.getIdentifier().getShortName())
	// .eventAction("Started").eventLabel(microservice.getVersion().getVersionIdentifier()).send();
	// } catch (Throwable t) {
	// microservice.getLogger().warn("Unable to send Google Analytics started
	// event.", t);
	// }
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.IMicroserviceAnalytics#sendMicroserviceUptime(
     * com.sitewhere.spi.microservice.IMicroservice)
     */
    @Override
    public void sendMicroserviceUptime(IMicroservice<?> microservice) throws SiteWhereException {
	// try {
	// getGoogleAnalytics().event().eventCategory("Microservice_" +
	// microservice.getIdentifier().getShortName())
	// .eventAction("Uptime").eventLabel(microservice.getVersion().getVersionIdentifier())
	// .eventValue((int) microservice.getCurrentState().getUptime()).send();
	// } catch (Throwable t) {
	// microservice.getLogger().warn("Unable to send Google Analytics uptime
	// event.", t);
	// }
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.IMicroserviceAnalytics#sendMicroserviceStopped
     * (com.sitewhere.spi.microservice.IMicroservice)
     */
    @Override
    public void sendMicroserviceStopped(IMicroservice<?> microservice) throws SiteWhereException {
	// try {
	// getGoogleAnalytics().event().eventCategory("Microservice_" +
	// microservice.getIdentifier().getShortName())
	// .eventAction("Stopped").eventLabel(microservice.getVersion().getVersionIdentifier()).send();
	// } catch (Throwable t) {
	// microservice.getLogger().warn("Unable to send Google Analytics stopped
	// event.", t);
	// }
    }

    protected GoogleAnalytics getGoogleAnalytics() {
	return googleAnalytics;
    }
}