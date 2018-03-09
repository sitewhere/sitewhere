/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.label;

import com.sitewhere.spi.label.ILabel;

/**
 * Model object for label information.
 * 
 * @author Derek
 */
public class Label implements ILabel {

    /** Label binary (image) content */
    private byte[] content;

    /*
     * @see com.sitewhere.spi.label.ILabel#getContent()
     */
    @Override
    public byte[] getContent() {
	return content;
    }

    public void setContent(byte[] content) {
	this.content = content;
    }
}