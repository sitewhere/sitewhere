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
package com.sitewhere.web.rest.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ExceptionDetail {

    /** Error message */
    private String message;

    /** Error code */
    private Long errorCode;

    /** Error description */
    private String errorDescription;

    public String getMessage() {
	return message;
    }

    public void setMessage(String message) {
	this.message = message;
    }

    public Long getErrorCode() {
	return errorCode;
    }

    public void setErrorCode(Long errorCode) {
	this.errorCode = errorCode;
    }

    public String getErrorDescription() {
	return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
	this.errorDescription = errorDescription;
    }
}
