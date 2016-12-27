package com.sitewhere.spring.handler;

/**
 * Enumerates elements used by globals parser.
 * 
 * @author Derek
 */
public interface IGlobalsParser {

    /**
     * Expected child elements.
     * 
     * @author Derek
     */
    public static enum Elements {

	/** Global Hazelcast configuration */
	@Deprecated
	HazelcastConfiguration("hazelcast-configuration"),

	/** Global Solr configuration */
	SolrConfiguration("solr-configuration"),

	/** Global Groovy configuration */
	@Deprecated
	GroovyConfiguration("groovy-configuration");

	/** Event code */
	private String localName;

	private Elements(String localName) {
	    this.localName = localName;
	}

	public static Elements getByLocalName(String localName) {
	    for (Elements value : Elements.values()) {
		if (value.getLocalName().equals(localName)) {
		    return value;
		}
	    }
	    return null;
	}

	public String getLocalName() {
	    return localName;
	}

	public void setLocalName(String localName) {
	    this.localName = localName;
	}
    }
}