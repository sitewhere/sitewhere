/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.warp10.rest;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GTSOutput {


    private String className;
    private Map<String, String> labels;
    private Map<String, String> attributes;
    private String id;
    private List<DataPoint> points;

    public static List<GTSOutput> fromOutputFormat(String output) {

        if (output == null || output.equals("")) return new ArrayList<>();

        final String regex = "\\{(\"c\"):(?<c>.*?),(\"l\"):(?<l>\\{.*?\\}),(\"a\"):(?<a>\\{.*?\\}),((\"i\"):(?<i>\".*?\"),)?(\"la\"):(?<la>.*?),(\"v\"):(?<v>\\[\\[.*?]])\\}";


        Gson gson = new Gson();

        Matcher matcher = Pattern.compile(regex, Pattern.MULTILINE).matcher(output);

        List<GTSOutput> outputs = new ArrayList<>();

        while (matcher.find()) {
            GTSOutput gts = new GTSOutput();

            gts.className = stripExtraQuotes(matcher.group("c"));
            gts.labels = populateMap(matcher.group("l"));
            gts.attributes = populateMap(matcher.group("a"));
            gts.id = stripExtraQuotes(matcher.group("i"));
            gts.points = DataPoint.extractDataPoint(matcher.group("v"));

            outputs.add(gts);
        }

        return outputs;
    }

    private static String stripExtraQuotes(String string) {
        if (string != null) {
            return string.replaceAll("\"", "");
        }
        return "";
    }

    private static Map<String, String> populateMap(String source) {
        source = source.replace("{", "").replace("}", "");

        String[] groups = source.split(",");

        if (!source.equals("") && groups.length > 0) {
            return Stream.of(groups).collect(Collectors.toMap(GTSOutput::extractMapKey, GTSOutput::extractMapValue));
        }
        return new HashMap<>();
    }

    private static String extractMapValue(String item) {
        return stripExtraQuotes(item.split(":")[1]);
    }

    private static String extractMapKey(String item) {
        return stripExtraQuotes(item.split(":")[0]);
    }

    public String getClassName() {
        return className;
    }

    public Map<String, String> getLabels() {
        return labels;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public String getId() {
        return id;
    }

    public List<DataPoint> getPoints() {
        return points;
    }
}
