package com.sitewhere.spring.handler;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

/**
 * Utility methods for parser functionality used in multiple areas.
 * 
 * @author Derek
 */
public class ParserUtils {

    /**
     * Common parser logic for MongoDB attributes.
     * 
     * @param element
     * @param context
     * @param client
     */
    public static void parseMongoAttributes(Element element, ParserContext context, BeanDefinitionBuilder client) {
	Attr hostname = element.getAttributeNode("hostname");
	if (hostname != null) {
	    client.addPropertyValue("hostname", hostname.getValue());
	}
	Attr port = element.getAttributeNode("port");
	if (port != null) {
	    client.addPropertyValue("port", port.getValue());
	}
	Attr databaseName = element.getAttributeNode("databaseName");
	if (databaseName != null) {
	    client.addPropertyValue("databaseName", databaseName.getValue());
	}

	// Determine if username and password are supplied.
	Attr username = element.getAttributeNode("username");
	Attr password = element.getAttributeNode("password");
	if ((username != null) && ((password == null))) {
	    throw new RuntimeException("If username is specified for MongoDB, password must be specified as well.");
	}
	if ((username == null) && ((password != null))) {
	    throw new RuntimeException("If password is specified for MongoDB, username must be specified as well.");
	}
	if ((username != null) && (password != null)) {
	    client.addPropertyValue("username", username.getValue());
	    client.addPropertyValue("password", password.getValue());
	}

	Attr authDatabaseName = element.getAttributeNode("authDatabaseName");
	if (authDatabaseName != null) {
	    client.addPropertyValue("authDatabaseName", authDatabaseName.getValue());
	}

	// Set replica set name if specified.
	Attr replicaSetName = element.getAttributeNode("replicaSetName");
	if (replicaSetName != null) {
	    client.addPropertyValue("replicaSetName", replicaSetName.getValue());
	}

	// Determine if replication set should be created if does not exist.
	Attr autoConfigureReplication = element.getAttributeNode("autoConfigureReplication");
	if (autoConfigureReplication != null) {
	    client.addPropertyValue("autoConfigureReplication", autoConfigureReplication.getValue());
	}
    }
}