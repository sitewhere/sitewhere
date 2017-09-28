package com.sitewhere.microservice.security;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.sitewhere.rest.model.user.User;
import com.sitewhere.spi.SiteWhereException;

public class TokenManagementTest {

    /** Username */
    private static String USERNAME = "dadams";

    /** Granted authorities */
    private static List<String> AUTHS = Arrays.asList(new String[] { "this", "that", "other" });

    @Test
    public void testGenerateToken() throws SiteWhereException {
	TokenManagement tokens = new TokenManagement();
	User user = new User();
	user.setUsername(USERNAME);
	user.setAuthorities(AUTHS);
	String token = tokens.generateToken(user);
	String username = tokens.getUsernameFromToken(token);
	assertEquals(username, USERNAME);
    }
}