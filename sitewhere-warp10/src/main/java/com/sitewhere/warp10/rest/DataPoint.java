/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.warp10.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DataPoint {
    private Long msTimestamp;
    private Double latitude;
    private Double longitude;
    private Double elevation;
    private String value;

    public DataPoint(Long msTimestamp, Double latitude, Double longitude, Double elevation, String value) {
        this.msTimestamp = msTimestamp;
        this.latitude = latitude;
        this.longitude = longitude;
        this.elevation = elevation;
        this.value = value;
    }

    private DataPoint(Long msTimestamp, String value) {
        this.msTimestamp = msTimestamp;
        this.value = value;
    }

    private DataPoint() {

    }

    public static DataPoint empty() {
        return new DataPoint();
    }

    public boolean isEmpty() {
        return msTimestamp == null && value == null;
    }

    public static DataPoint of(String value, Long msTimeStamp) {
        return new DataPoint(msTimeStamp, value);
    }

    public DataPoint atLatitude(double lat) {
        latitude = lat;
        return this;
    }

    public DataPoint atLongitude(double lgt) {
        longitude = lgt;
        return this;
    }

    public DataPoint atElevation(Double elev) {
        elevation = elev;
        return this;
    }

    public String getValue() {
        return value;
    }

    public Long getMsTimestamp() {
        return msTimestamp;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getElevation() {
        return elevation;
    }

    public static List<DataPoint> extractDataPoint(String source) {

        String[] values = source.replaceAll("\\[\\[", "[").replaceAll("]]", "]").split("],");

        List<DataPoint> allPoints = new ArrayList<>();

        for (String item : values) {
            String[] data = item.replaceAll("\\[", "").replaceAll("]", "").split(",");

            if (data.length > 0) {
                DataPoint dp = DataPoint.empty();
                Long msTimestamp = Long.parseLong(data[0]);

                if (data.length == 2) {
                    dp = DataPoint.of(stripExtraQuotes(data[1]), msTimestamp);

                } else if (data.length == 3) {
                    dp = DataPoint.of(stripExtraQuotes(data[2]), msTimestamp)
                     .atElevation(Double.parseDouble(data[1]));

                } else if (data.length == 4) {
                    dp = DataPoint.of(stripExtraQuotes(data[3]), msTimestamp)
                     .atLatitude(Double.parseDouble(data[1]))
                     .atLongitude(Double.parseDouble(data[2]));

                } else if (data.length == 5) {
                    dp = DataPoint.of(stripExtraQuotes(data[4]), msTimestamp)
                     .atLatitude(Double.parseDouble(data[1]))
                     .atLongitude(Double.parseDouble(data[2]))
                     .atElevation(Double.parseDouble(data[3]));

                }
                allPoints.add(dp);
            }
        }
        return allPoints;
    }

    private static String stripExtraQuotes(String string) {
        if (string != null) {
            return string.replaceAll("\"", "");
        }
        return "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataPoint dataPoint = (DataPoint) o;
        return Objects.equals(msTimestamp, dataPoint.msTimestamp) &&
         Objects.equals(latitude, dataPoint.latitude) &&
         Objects.equals(longitude, dataPoint.longitude) &&
         Objects.equals(elevation, dataPoint.elevation) &&
         Objects.equals(value, dataPoint.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(msTimestamp, latitude, longitude, elevation, value);
    }
}
