/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.sources.spi;

import com.sitewhere.spi.SiteWhereException;

/**
 * Exception thrown when an event payload can not be decoded.
 */
public class EventDecodeException extends SiteWhereException {

    /** Serial version UID */
    private static final long serialVersionUID = 1994831211720002160L;

    public EventDecodeException() {
    }

    public EventDecodeException(String message, Throwable cause) {
	super(message, cause);
    }

    public EventDecodeException(String message) {
	super(message);
    }

    public EventDecodeException(Throwable cause) {
	super(cause);
    }
}