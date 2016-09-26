package com.sitewhere.device.communication.decoder.composite;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.SiteWhere;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.communication.EventDecodeException;
import com.sitewhere.spi.device.communication.ICompositeDeviceEventDecoder;

/**
 * Concrete implementation of {@link ICompositeDeviceEventDecoder} for binary
 * data.
 * 
 * @author Derek
 */
public class BinaryCompositeDeviceEventDecoder extends CompositeDeviceEventDecoder<byte[]> {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

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
    public IDeviceContext<byte[]> buildContext(IMessageMetadata<byte[]> metadata) throws EventDecodeException {
	BinaryDeviceContext context = new BinaryDeviceContext();

	try {
	    context.setDevice(SiteWhere.getServer().getDeviceManagement(getTenant())
		    .getDeviceByHardwareId(metadata.getHardwareId()));
	} catch (SiteWhereException e) {
	    throw new EventDecodeException("Device not found for hardwareId: " + metadata.getHardwareId(), e);
	}

	try {
	    context.setDeviceSpecification(SiteWhere.getServer().getDeviceManagement(getTenant())
		    .getDeviceSpecificationByToken(context.getDevice().getSpecificationToken()));
	} catch (SiteWhereException e) {
	    throw new EventDecodeException("Device specification not found: " + metadata.getHardwareId(), e);
	}

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

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }
}