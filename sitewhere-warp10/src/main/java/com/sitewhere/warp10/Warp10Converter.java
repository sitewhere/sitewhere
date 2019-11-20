/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.warp10;

import com.sitewhere.warp10.rest.GTSInput;
import com.sitewhere.warp10.rest.GTSOutput;

public interface Warp10Converter<I> {

    public GTSInput convert(I source);

    /**
     * Create the REST object from a GTS input.
     *
     * @param source
     * @return
     */
    public I convert(GTSOutput source);
}
