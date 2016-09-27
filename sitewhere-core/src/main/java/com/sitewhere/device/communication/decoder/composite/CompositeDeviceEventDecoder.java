package com.sitewhere.device.communication.decoder.composite;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.communication.EventDecodeException;
import com.sitewhere.spi.device.communication.ICompositeDeviceEventDecoder;
import com.sitewhere.spi.device.communication.IDecodedDeviceRequest;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Basic logic for a composite decoder. Extracts metadata from initial payload,
 * looks up device management data to build a context, then loops through a list
 * of potential decoders, returning the first one that applies to the context.
 * 
 * @author Derek
 *
 * @param <T>
 */
public abstract class CompositeDeviceEventDecoder<T> extends TenantLifecycleComponent
	implements ICompositeDeviceEventDecoder<T> {

    public CompositeDeviceEventDecoder() {
	super(LifecycleComponentType.DeviceEventDecoder);
    }

    /**
     * Build an {@link IDeviceContext} based on metadata parsed from the initial
     * payload.
     * 
     * @param metadata
     * @return
     * @throws SiteWhereException
     */
    public abstract IDeviceContext<T> buildContext(IMessageMetadata<T> metadata) throws SiteWhereException;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.communication.IDeviceEventDecoder#decode(java.
     * lang.Object, java.util.Map)
     */
    @Override
    public List<IDecodedDeviceRequest<?>> decode(T payload, Map<String, String> eventSourceMetadata)
	    throws EventDecodeException {

	// Parse metadata from payload.
	IMessageMetadata<T> metadata = getMetadataExtractor().extractMetadata(payload, eventSourceMetadata);
	getLogger().info("Extracted payload metadata: HardwareId: " + metadata.getHardwareId() + " Payload: "
		+ metadata.getPayload().toString());

	try {
	    IDeviceContext<T> context = buildContext(metadata);
	    getLogger().info("Built context: Device: " + context.getDevice() + " Specification: "
		    + context.getDeviceSpecification());

	    // Loop through choices and use first one that applies.
	    for (ICompositeDeviceEventDecoder.IDecoderChoice<T> choice : getDecoderChoices()) {

		if (choice.appliesTo(context)) {
		    return choice.getDeviceEventDecoder().decode(context.getPayload(), eventSourceMetadata);
		}
	    }
	} catch (SiteWhereException e) {
	    throw new EventDecodeException("Unable to build context from extracted metadata.", e);
	}

	// Handle case where no choices apply.
	return new ArrayList<IDecodedDeviceRequest<?>>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
     */
    @Override
    public void start() throws SiteWhereException {
	if (getMetadataExtractor() == null) {
	    throw new SiteWhereException("Composite decoder has no metadata extractor configured.");
	}
	if (getDecoderChoices().size() < 1) {
	    throw new SiteWhereException("Composite decoder has no chocies configured.");
	}

	startNestedComponent(getMetadataExtractor(), "Composite decoder metadata extractor startup failed.", true);
	for (ICompositeDeviceEventDecoder.IDecoderChoice<T> choice : getDecoderChoices()) {
	    startNestedComponent(choice, "Composite decoder delegate startup failed.", true);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
     */
    @Override
    public void stop() throws SiteWhereException {
	if (getMetadataExtractor() != null) {
	    getMetadataExtractor().lifecycleStop();
	}
	for (ICompositeDeviceEventDecoder.IDecoderChoice<T> choice : getDecoderChoices()) {
	    choice.lifecycleStop();
	}
    }
}