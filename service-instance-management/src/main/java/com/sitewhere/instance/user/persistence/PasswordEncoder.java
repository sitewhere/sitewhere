/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.instance.user.persistence;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.RandomIvGenerator;

/**
 * Used for password encode/decode.
 */
public class PasswordEncoder {

    /** Encryptor */
    private StandardPBEStringEncryptor encryptor;

    public PasswordEncoder() {
	StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
	encryptor.setPassword("jasypt"); // TODO: Pass this from Helm.
	encryptor.setAlgorithm("PBEWithHMACSHA512AndAES_256");
	encryptor.setIvGenerator(new RandomIvGenerator());
	this.encryptor = encryptor;
    }

    public String encrypt(String password) {
	return encryptor.encrypt(password);
    }

    public String decrypt(String encrypted) {
	return encryptor.decrypt(encrypted);
    }
}
