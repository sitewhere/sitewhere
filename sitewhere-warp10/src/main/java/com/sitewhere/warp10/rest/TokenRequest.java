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

public class TokenRequest {

    private String id;

    private String tokenSecret;

    private TokenType tokenType;

    private String application;

    private String owner;

    private String producer;

    private String issuance = " NOW 1 ms";

    private String expiry = " NOW 30 d + 1 ms ";

    private String ttl;

    private List<String> labels = new ArrayList();

    private List<String> attributes = new ArrayList();

    //  Only for READ tokens
    private List<String> owners = new ArrayList();

    private List<String> producers = new ArrayList();

    private List<String> applications = new ArrayList();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTokenSecret() {
        return tokenSecret;
    }

    public void setTokenSecret(String tokenSecret) {
        this.tokenSecret = tokenSecret;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public String getIssuance() {
        return issuance;
    }

    public void setIssuance(String issuance) {
        this.issuance = issuance;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public String getTtl() {
        return ttl;
    }

    public void setTtl(String ttl) {
        this.ttl = ttl;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }

    public List<String> getOwners() {
        return owners;
    }

    public void setOwners(List<String> owners) {
        this.owners = owners;
    }

    public List<String> getProducers() {
        return producers;
    }

    public void setProducers(List<String> producers) {
        this.producers = producers;
    }

    public List<String> getApplications() {
        return applications;
    }

    public void setApplications(List<String> applications) {
        this.applications = applications;
    }

    @Override
    public String toString() {
        return "{ \n" +
         " 'id' '" + id  +  "' \n" +
         " 'type' '" + tokenType.name() + "' \n" +
         " 'application' '" + application + "' \n" +
         " 'owner' '" + owner + "' \n" +
         " 'producer' '" + producer + "' \n" +
         " 'issuance' " + issuance + " / \n " +
         " 'expiry' " + expiry + " / \n" +
         " 'ttl' " + ttl + " / \n" +
         " 'labels' " + getLabelsToString() + "\n" +
         " 'attributes' " + getAttributesString() + "\n" +
         " 'owners' " + owners + "\n" +
         " 'producers' " + producers  + "\n" +
         " 'applications' " + getApplicationsToString() + "\n }" + " '" + tokenSecret + "' TOKENGEN"  ;
    }

    private String getAttributesString() {
        String attributesString = "{";

        for (String attribute: this.attributes) {
            //attributesString
        }

        attributesString = attributesString + "}";
        return attributesString;
    }

    private String getLabelsToString() {
        String labelsString = "{";

        for (String label: this.labels) {
            //attributesString
        }

        labelsString = labelsString + "}";
        return labelsString;
    }

    private String getApplicationsToString() {
        String applicationsString = "[ ";

        for (String application: this.applications) {
            applicationsString = applicationsString + "'" + application + "'";
        }

        applicationsString = applicationsString + " ]";
        return applicationsString;
    }
}
