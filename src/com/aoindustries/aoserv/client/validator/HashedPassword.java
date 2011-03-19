/*
 * Copyright 2010-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.validator;

import com.aoindustries.aoserv.client.*;
import com.aoindustries.util.Base64Coder;
import com.aoindustries.util.WrappedException;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Represents a hashed password.  May be any of:
 * <ul>
 *   <li>Must not be null</li>
 *   <li>* - will not match anything</li>
 *   <li>Result of SHA1 hash (preferred) - 28 characters</li>
 *   <li>Result of crypt (deprecated) - 13 characters</li>
 * </ul>
 *
 * @author  AO Industries, Inc.
 */
final public class HashedPassword implements Serializable, ObjectInputValidation, DtoFactory<com.aoindustries.aoserv.client.dto.HashedPassword> {

    private static final long serialVersionUID = 6198625525641344394L;

    /**
     * Indicates that no password is set.
     */
    public static final String NO_PASSWORD = "*";

    public static ValidationResult validate(String hashedPassword) {
        // May be null
        if(hashedPassword==null) return new InvalidResult(ApplicationResources.accessor, "HashedPassword.validate.isNull");
        // Be non-empty
        int len = hashedPassword.length();
        if(len==0) return new InvalidResult(ApplicationResources.accessor, "HashedPassword.validate.empty");
        // May be *
        if(!NO_PASSWORD.equals(hashedPassword)) {
            // SHA1
            if(len!=28) {
                // Crypt
                if(len!=13) {
                    return new InvalidResult(ApplicationResources.accessor, "HashedPassword.validate.wrongLength");
                }
            }
        }
        return ValidResult.getInstance();
    }

    /**
     * Performs a one-way hash of the plaintext value using SHA-1.
     *
     * @exception  WrappedException  if any problem occurs.
     */
    public static String hash(String plaintext) throws WrappedException {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(plaintext.getBytes("UTF-8"));
            return new String(Base64Coder.encode(md.digest()));
        } catch(NoSuchAlgorithmException err) {
            throw new WrappedException(err);
        } catch(UnsupportedEncodingException err) {
            throw new WrappedException(err);
        }
    }

    public static HashedPassword valueOf(String hashedPassword) throws ValidationException {
        return new HashedPassword(hashedPassword);
    }

    final private String hashedPassword;

    private HashedPassword(String hashedPassword) throws ValidationException {
        this.hashedPassword = hashedPassword;
        validate();
    }

    private void validate() throws ValidationException {
        ValidationResult result = validate(hashedPassword);
        if(!result.isValid()) throw new ValidationException(result);
    }

    /**
     * Perform same validation as constructor on readObject.
     */
    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        validateObject();
    }

    @Override
    public void validateObject() throws InvalidObjectException {
        try {
            validate();
        } catch(ValidationException err) {
            InvalidObjectException newErr = new InvalidObjectException(err.getMessage());
            newErr.initCause(err);
            throw newErr;
        }
    }

    @Override
    public boolean equals(Object O) {
    	return
            (O instanceof HashedPassword)
            && hashedPassword.equals(((HashedPassword)O).hashedPassword)
    	;
    }

    @Override
    public int hashCode() {
        return hashedPassword.hashCode();
    }

    @Override
    public String toString() {
        return hashedPassword;
    }

    @Override
    public com.aoindustries.aoserv.client.dto.HashedPassword getDto() {
        return new com.aoindustries.aoserv.client.dto.HashedPassword(hashedPassword);
    }

    @SuppressWarnings("deprecation")
    public boolean passwordMatches(String plaintext) {
        if(hashedPassword.length()==28) return hash(plaintext).equals(hashedPassword);
        if(hashedPassword.length()==13) return com.aoindustries.util.UnixCrypt.crypt(plaintext, hashedPassword.substring(0,2)).equals(hashedPassword);
    	return false;
    }
}
