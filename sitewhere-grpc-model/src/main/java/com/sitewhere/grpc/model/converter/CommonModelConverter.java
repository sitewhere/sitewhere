package com.sitewhere.grpc.model.converter;

import java.util.Date;

import com.google.protobuf.Timestamp;
import com.sitewhere.grpc.model.CommonModel;
import com.sitewhere.grpc.model.CommonModel.GEntityInformation;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.common.IMetadataProviderEntity;

/**
 * Convert between SiteWhere API model and GRPC model.
 * 
 * @author Derek
 */
public class CommonModelConverter {

    /**
     * Convert date to GRPC value.
     * 
     * @param date
     * @return
     * @throws SiteWhereException
     */
    public static Timestamp asGrpcTimestamp(Date date) throws SiteWhereException {
	if (date == null) {
	    return null;
	}
	Timestamp.Builder grpc = Timestamp.newBuilder();
	long millis = date.getTime();
	grpc.setSeconds(millis / 1000);
	grpc.setNanos((int) (millis % 1000) * 1000000);
	return grpc.build();
    }

    public static GEntityInformation asGrpcEntityInformation(IMetadataProviderEntity api) throws SiteWhereException {
	GEntityInformation.Builder grpc = CommonModel.GEntityInformation.newBuilder();
	grpc.setCreatedBy(api.getCreatedBy());
	grpc.setCreatedDate(CommonModelConverter.asGrpcTimestamp(api.getCreatedDate()));
	grpc.setUpdatedBy(api.getUpdatedBy());
	grpc.setUpdatedDate(CommonModelConverter.asGrpcTimestamp(api.getUpdatedDate()));
	grpc.setDeleted(api.isDeleted());
	return grpc.build();
    }
}