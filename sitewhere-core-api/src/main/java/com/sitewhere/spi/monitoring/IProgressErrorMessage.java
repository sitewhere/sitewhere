/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.monitoring;

import com.sitewhere.spi.error.ErrorLevel;

/**
 * Extends {@link IProgressMessage} to add error information.
 */
public interface IProgressErrorMessage extends IProgressMessage {

    /**
     * Get error level associated with message.
     * 
     * @return
     */
    public ErrorLevel getLevel();
}