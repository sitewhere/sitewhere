/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.decoder.composite;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.sources.spi.ICompositeDeviceEventDecoder;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Concrete implementation of {@link ICompositeDeviceEventDecoder} for binary
 * data.
 */
public class BinaryCompositeDeviceEventDecoder extends CompositeDeviceEventDecoder<byte[]> {

    /** Metadata extractor implementation */
    private IMessageMetadataExtractor<byte[]> metadataExtractor;

    /** List of decoder choices */
    private List<ICompositeDeviceEventDecoder.IDecoderChoice<byte[]>> decoderChoices = new ArrayList<ICompositeDeviceEventDecoder.IDecoderChoice<byte[]>>();

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.device.communication.decoder.composite.
     * CompositeDeviceEventDecoder#buildContext(com.sitewhere.spi.device.
     * communication.ICompositeDeviceEventDecoder.IMessageMetadata)
     */
    @Override
    public IDeviceContext<byte[]> buildContext(IMessageMetadata<byte[]> metadata) throws SiteWhereException {
	BinaryDeviceContext context = new BinaryDeviceContext();

	IDeviceManagement devices = getDeviceManagement(getTenantEngine().getTenant());
	context.setDevice(devices.getDeviceByToken(metadata.getDeviceToken()));
	if (context.getDevice() == null) {
	    throw new SiteWhereException(
		    "Unable to build device context. Device not found for token: " + metadata.getDeviceToken());
	}

	context.setDeviceType(devices.getDeviceType(context.getDevice().getDeviceTypeId()));

	context.setPayload(metadata.getPayload());
	return context;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.ICompositeDeviceEventDecoder#
     * getMetadataExtractor()
     */
    public IMessageMetadataExtractor<byte[]> getMetadataExtractor() {
	return metadataExtractor;
    }

    public void setMetadataExtractor(IMessageMetadataExtractor<byte[]> metadataExtractor) {
	this.metadataExtractor = metadataExtractor;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.ICompositeDeviceEventDecoder#
     * getDecoderChoices()
     */
    public List<ICompositeDeviceEventDecoder.IDecoderChoice<byte[]>> getDecoderChoices() {
	return decoderChoices;
    }

    public void setDecoderChoices(List<ICompositeDeviceEventDecoder.IDecoderChoice<byte[]>> decoderChoices) {
	this.decoderChoices = decoderChoices;
    }

    private IDeviceManagement getDeviceManagement(ITenant tenant) {
	return null;
    }
}