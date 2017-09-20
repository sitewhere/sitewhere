package com.sitewhere.grpc.model.converter;

import java.util.Date;

import com.google.protobuf.Timestamp;
import com.sitewhere.grpc.model.CommonModel;
import com.sitewhere.grpc.model.CommonModel.GEntityInformation;
import com.sitewhere.rest.model.common.MetadataProviderEntity;
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

    /**
     * Convert GRPC timestamp to date.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static Date asDate(Timestamp grpc) throws SiteWhereException {
	long millis = grpc.getSeconds() * 1000;
	millis += (grpc.getNanos() / 1000);
	return new Date(millis);
    }

    /**
     * Craete {@link GEntityInformation} from API entity information.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GEntityInformation asGrpcEntityInformation(IMetadataProviderEntity api) throws SiteWhereException {
	GEntityInformation.Builder grpc = CommonModel.GEntityInformation.newBuilder();
	grpc.setCreatedBy(api.getCreatedBy());
	grpc.setCreatedDate(CommonModelConverter.asGrpcTimestamp(api.getCreatedDate()));
	grpc.setUpdatedBy(api.getUpdatedBy());
	grpc.setUpdatedDate(CommonModelConverter.asGrpcTimestamp(api.getUpdatedDate()));
	grpc.setDeleted(api.isDeleted());
	return grpc.build();
    }

    /**
     * Set API entity information from {@link GEntityInformation}.
     * 
     * @param api
     * @param grpc
     * @throws SiteWhereException
     */
    public static void setEntityInformation(MetadataProviderEntity api, GEntityInformation grpc)
	    throws SiteWhereException {
	if (grpc != null) {
	    api.setCreatedBy(grpc.getCreatedBy());
	    api.setCreatedDate(CommonModelConverter.asDate(grpc.getCreatedDate()));
	    api.setUpdatedBy(grpc.getUpdatedBy());
	    api.setUpdatedDate(CommonModelConverter.asDate(grpc.getUpdatedDate()));
	    api.setDeleted(grpc.getDeleted());
	}
    }
}