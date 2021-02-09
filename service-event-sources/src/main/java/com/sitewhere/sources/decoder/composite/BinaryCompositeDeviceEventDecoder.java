/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.sources.decoder.composite;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.sources.spi.ICompositeDeviceEventDecoder;
import com.sitewhere.spi.SiteWhereException;

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

	IDeviceManagement devices = getDeviceManagement();
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

    private IDeviceManagement getDeviceManagement() {
	return null;
    }
}