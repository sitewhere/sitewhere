/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates methods and parameters with cross-cutting concerns.
 * 
 * @author Derek
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.PARAMETER })
public @interface Concerns {

    public enum ConcernType {

	/** Paging configuration */
	Paging("Paged Results", "paging"),

	/** Forced delete */
	ForceDelete("Delete Policies", "overview-delete");

	/** Title shown for parameters */
	private String title;

	/** In-document link into overview section */
	private String link;

	private ConcernType(String title, String link) {
	    this.title = title;
	    this.link = link;
	}

	public String getTitle() {
	    return title;
	}

	public void setTitle(String title) {
	    this.title = title;
	}

	public String getLink() {
	    return link;
	}

	public void setLink(String link) {
	    this.link = link;
	}
    }

    /** Concern types */
    ConcernType[] values();
}