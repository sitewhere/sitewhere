/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.label;

import com.google.protobuf.ByteString;
import com.sitewhere.grpc.model.LabelGenerationModel.GLabel;
import com.sitewhere.rest.model.label.Label;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.label.ILabel;

/**
 * Convert label generation entities between SiteWhere API model and GRPC model.
 * 
 * @author Derek
 */
public class LabelGenerationModelConverter {

    /**
     * Convert label from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static Label asApiLabel(GLabel grpc) throws SiteWhereException {
	Label label = new Label();
	label.setContent(grpc.getContent().toByteArray());
	return label;
    }

    /**
     * Convert label from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GLabel asGrpcLabel(ILabel api) throws SiteWhereException {
	GLabel.Builder grpc = GLabel.newBuilder();
	grpc.setContent(ByteString.copyFrom(api.getContent()));
	return grpc.build();
    }
}