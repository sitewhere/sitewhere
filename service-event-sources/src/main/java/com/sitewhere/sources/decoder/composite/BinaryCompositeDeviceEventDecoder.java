package com.sitewhere.sources.decoder.composite;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.sources.SiteWhere;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceManagement;
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
    public IDeviceContext<byte[]> buildContext(IMessageMetadata<byte[]> metadata) throws SiteWhereException {
	BinaryDeviceContext context = new BinaryDeviceContext();

	IDeviceManagement devices = SiteWhere.getServer().getDeviceManagement(getTenant());
	context.setDevice(devices.getDeviceByHardwareId(metadata.getHardwareId()));
	if (context.getDevice() == null) {
	    throw new SiteWhereException(
		    "Unable to build device context. Device not found for hardware id: " + metadata.getHardwareId());
	}

	context.setDeviceSpecification(
		devices.getDeviceSpecificationByToken(context.getDevice().getSpecificationToken()));

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