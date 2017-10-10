package com.sitewhere.grpc.model.converter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.protobuf.Timestamp;
import com.sitewhere.grpc.model.CommonModel;
import com.sitewhere.grpc.model.CommonModel.GDateRangeSearchCriteria;
import com.sitewhere.grpc.model.CommonModel.GEntityInformation;
import com.sitewhere.grpc.model.CommonModel.GLocation;
import com.sitewhere.grpc.model.CommonModel.GOptionalDouble;
import com.sitewhere.grpc.model.CommonModel.GPaging;
import com.sitewhere.grpc.model.CommonModel.GUserReference;
import com.sitewhere.rest.model.common.Location;
import com.sitewhere.rest.model.common.MetadataProviderEntity;
import com.sitewhere.rest.model.search.DateRangeSearchCriteria;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.common.ILocation;
import com.sitewhere.spi.common.IMetadataProviderEntity;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.spi.search.ISearchCriteria;

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
     * Convert location from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static Location asApiLocation(GLocation grpc) throws SiteWhereException {
	Location api = new Location();
	api.setLatitude(grpc.hasLatitude() ? grpc.getLatitude().getValue() : 0.0);
	api.setLongitude(grpc.hasLongitude() ? grpc.getLongitude().getValue() : 0.0);
	api.setElevation(grpc.hasElevation() ? grpc.getElevation().getValue() : 0.0);
	return api;
    }

    /**
     * Convert locations from GRPC to API.
     * 
     * @param grpcs
     * @return
     * @throws SiteWhereException
     */
    public static List<Location> asApiLocations(List<GLocation> grpcs) throws SiteWhereException {
	List<Location> api = new ArrayList<Location>();
	for (GLocation grpc : grpcs) {
	    api.add(CommonModelConverter.asApiLocation(grpc));
	}
	return api;
    }

    /**
     * Convert location from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GLocation asGrpcLocation(ILocation api) throws SiteWhereException {
	GLocation.Builder grpc = GLocation.newBuilder();
	if (api.getLatitude() != null) {
	    grpc.setLatitude(GOptionalDouble.newBuilder().setValue(api.getLatitude()).build());
	}
	if (api.getLongitude() != null) {
	    grpc.setLongitude(GOptionalDouble.newBuilder().setValue(api.getLongitude()).build());
	}
	if (api.getElevation() != null) {
	    grpc.setElevation(GOptionalDouble.newBuilder().setValue(api.getElevation()).build());
	}
	return grpc.build();
    }

    /**
     * Convert locations from API to GRPC.
     * 
     * @param apis
     * @return
     * @throws SiteWhereException
     */
    public static List<GLocation> asGrpcLocations(List<ILocation> apis) throws SiteWhereException {
	List<GLocation> grpcs = new ArrayList<GLocation>();
	for (ILocation api : apis) {
	    grpcs.add(CommonModelConverter.asGrpcLocation(api));
	}
	return grpcs;
    }

    /**
     * Convert GRPC paging block into generic search criteria.
     * 
     * @param paging
     * @return
     * @throws SiteWhereException
     */
    public static SearchCriteria asApiSearchCriteria(GPaging paging) throws SiteWhereException {
	return new SearchCriteria(paging.getPageNumber(), paging.getPageSize());
    }

    /**
     * Convert date range search criteria from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DateRangeSearchCriteria asDateRangeSearchCriteria(GDateRangeSearchCriteria grpc)
	    throws SiteWhereException {
	return new DateRangeSearchCriteria(grpc.getPageNumber(), grpc.getPageSize(),
		CommonModelConverter.asDate(grpc.getStartDate()), CommonModelConverter.asDate(grpc.getEndDate()));
    }

    /**
     * Convert date range search criteria from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDateRangeSearchCriteria asGrpcDateRangeSearchCriteria(IDateRangeSearchCriteria api)
	    throws SiteWhereException {
	GDateRangeSearchCriteria.Builder grpc = GDateRangeSearchCriteria.newBuilder();
	grpc.setPageNumber(api.getPageNumber());
	grpc.setPageSize(api.getPageSize());
	if (api.getStartDate() != null) {
	    grpc.setStartDate(CommonModelConverter.asGrpcTimestamp(api.getStartDate()));
	}
	if (api.getEndDate() != null) {
	    grpc.setEndDate(CommonModelConverter.asGrpcTimestamp(api.getEndDate()));
	}
	return grpc.build();
    }

    /**
     * Convert paging information from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GPaging asGrpcPaging(ISearchCriteria api) throws SiteWhereException {
	GPaging.Builder grpc = GPaging.newBuilder();
	grpc.setPageNumber(api.getPageNumber());
	grpc.setPageSize(api.getPageSize());
	return grpc.build();
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
	if (api.getCreatedBy() != null) {
	    GUserReference.Builder ref = GUserReference.newBuilder();
	    ref.setUsername(api.getCreatedBy());
	    grpc.setCreatedBy(ref);
	}
	if (api.getCreatedDate() != null) {
	    grpc.setCreatedDate(CommonModelConverter.asGrpcTimestamp(api.getCreatedDate()));
	}
	if (api.getUpdatedBy() != null) {
	    GUserReference.Builder ref = GUserReference.newBuilder();
	    ref.setUsername(api.getUpdatedBy());
	    grpc.setUpdatedBy(ref);
	}
	if (api.getUpdatedDate() != null) {
	    grpc.setUpdatedDate(CommonModelConverter.asGrpcTimestamp(api.getUpdatedDate()));
	}
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
	    api.setCreatedBy(grpc.hasCreatedBy() ? grpc.getCreatedBy().getUsername() : null);
	    api.setCreatedDate(grpc.hasCreatedDate() ? CommonModelConverter.asDate(grpc.getCreatedDate()) : null);
	    api.setUpdatedBy(grpc.hasUpdatedBy() ? grpc.getUpdatedBy().getUsername() : null);
	    api.setUpdatedDate(grpc.hasUpdatedDate() ? CommonModelConverter.asDate(grpc.getUpdatedDate()) : null);
	    api.setDeleted(grpc.getDeleted());
	}
    }
}