/*
 * Copyright 2010-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.validator;

import com.aoindustries.aoserv.client.*;
import com.aoindustries.util.Internable;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Represents a name that may be used for a PostgreSQL installation.  Names must:
 * <ul>
 *   <li>Be non-null</li>
 *   <li>Be non-empty</li>
 *   <li>Be between 1 and 31 characters</li>
 *   <li>Must start with <code>[a-z]</code> or <code>[0-9]</code></li>
 *   <li>The rest of the characters may contain [a-z], [0-9], period (.), and underscore (_)</li>
 * </ul>
 *
 * @author  AO Industries, Inc.
 */
final public class PostgresServerName implements Comparable<PostgresServerName>, Serializable, ObjectInputValidation, DtoFactory<com.aoindustries.aoserv.client.dto.PostgresServerName>, Internable<PostgresServerName> {

    private static final long serialVersionUID = 7935268259991524802L;

    public static final int MAX_LENGTH=255;

    /**
     * Validates a PostgreSQL server name.
     */
    public static void validate(String name) throws ValidationException {
        if(name==null) throw new ValidationException(ApplicationResources.accessor, "PostgresServerName.validate.isNull");
    	int len = name.length();
        if(len==0) throw new ValidationException(ApplicationResources.accessor, "PostgresServerName.validate.isEmpty");
        if(len > MAX_LENGTH) throw new ValidationException(ApplicationResources.accessor, "PostgresServerName.validate.tooLong", MAX_LENGTH, len);

        // The first character must be [a-z] or [0-9]
        char ch = name.charAt(0);
        if(
            (ch < 'a' || ch > 'z')
            && (ch<'0' || ch>'9')
        ) throw new ValidationException(ApplicationResources.accessor, "PostgresServerName.validate.startAtoZor0to9");

        // The rest may have additional characters
        for (int c = 1; c < len; c++) {
            ch = name.charAt(c);
            if (
                (ch<'a' || ch>'z')
                && (ch<'0' || ch>'9')
                && ch!='.'
                && ch!='_'
            ) throw new ValidationException(ApplicationResources.accessor, "PostgresServerName.validate.illegalCharacter");
    	}
    }

    private static final ConcurrentMap<String,PostgresServerName> interned = new ConcurrentHashMap<String,PostgresServerName>();

    public static PostgresServerName valueOf(String name) throws ValidationException {
        PostgresServerName existing = interned.get(name);
        return existing!=null ? existing : new PostgresServerName(name);
    }

    final private String name;

    private PostgresServerName(String name) throws ValidationException {
        this.name = name;
        validate();
    }

    private void validate() throws ValidationException {
        validate(name);
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
            O!=null
            && O instanceof PostgresServerName
            && name.equals(((PostgresServerName)O).name)
    	;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public int compareTo(PostgresServerName other) {
        return this==other ? 0 : name.compareTo(other.name);
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Interns this name much in the same fashion as <code>String.intern()</code>.
     *
     * @see  String#intern()
     */
    @Override
    public PostgresServerName intern() {
        try {
            PostgresServerName existing = interned.get(name);
            if(existing==null) {
                String internedName = name.intern();
                PostgresServerName addMe = name==internedName ? this : new PostgresServerName(internedName);
                existing = interned.putIfAbsent(internedName, addMe);
                if(existing==null) existing = addMe;
            }
            return existing;
        } catch(ValidationException err) {
            // Should not fail validation since original object passed
            throw new AssertionError(err.getMessage());
        }
    }

    @Override
    public com.aoindustries.aoserv.client.dto.PostgresServerName getDto() {
        return new com.aoindustries.aoserv.client.dto.PostgresServerName(name);
    }
}
