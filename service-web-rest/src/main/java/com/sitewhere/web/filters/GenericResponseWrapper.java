/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.filters;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class GenericResponseWrapper extends HttpServletResponseWrapper {

    private ByteArrayOutputStream output;
    private int contentLength;
    private String contentType;

    public GenericResponseWrapper(HttpServletResponse response) {
	super(response);

	output = new ByteArrayOutputStream();
    }

    public byte[] getData() {
	return output.toByteArray();
    }

    public ServletOutputStream getOutputStream() {
	return new FilterServletOutputStream(output);
    }

    public PrintWriter getWriter() {
	return new PrintWriter(getOutputStream(), true);
    }

    public void setContentLength(int length) {
	this.contentLength = length;
	super.setContentLength(length);
    }

    public int getContentLength() {
	return contentLength;
    }

    public void setContentType(String type) {
	this.contentType = type;
	super.setContentType(type);
    }

    public String getContentType() {
	return contentType;
    }
}