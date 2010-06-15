/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.validator;

import com.aoindustries.aoserv.client.BeanFactory;
import com.aoindustries.aoserv.client.PostgresServer;
import com.aoindustries.util.Internable;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Represents a name that may be used for a PostgreSQL database.  Database names must:
 * <ul>
 *   <li>Be non-null</li>
 *   <li>Be non-empty</li>
 *   <li>Be between 1 and 31 characters</li>
 *   <li>Must start with <code>[a-z]</code></li>
 *   <li>The rest of the characters may contain [a-z], [0-9], and underscore (_)</li>
 *   <li>Must not be a PostgreSQL reserved word</li>
 * </ul>
 *
 * @author  AO Industries, Inc.
 */
final public class PostgresDatabaseName implements Comparable<PostgresDatabaseName>, Serializable, ObjectInputValidation, BeanFactory<com.aoindustries.aoserv.client.beans.PostgresDatabaseName>, Internable<PostgresDatabaseName> {

    private static final long serialVersionUID = 1L;

    /**
     * The name of a database is limited by the internal data type of
     * the <code>pg_database</code> table.  The type is <code>name</code>
     * which has a maximum length of 31 characters.
     */
    public static final int MAX_LENGTH=31;

    /**
     * Validates a PostgreSQL database name.
     */
    public static void validate(String name) throws ValidationException {
        if(name==null) throw new ValidationException(ApplicationResources.accessor, "PostgresDatabaseName.validate.isNull");
    	int len = name.length();
        if(len==0) throw new ValidationException(ApplicationResources.accessor, "PostgresDatabaseName.validate.isEmpty");
        if(len > MAX_LENGTH) throw new ValidationException(ApplicationResources.accessor, "PostgresDatabaseName.validate.tooLong", MAX_LENGTH, len);

        // The first character must be [a-z] or [0-9]
        char ch = name.charAt(0);
        if(
            (ch < 'a' || ch > 'z')
            && (ch<'0' || ch>'9')
        ) throw new ValidationException(ApplicationResources.accessor, "PostgresDatabaseName.validate.startAtoZor0to9");

        // The rest may have additional characters
        for (int c = 1; c < len; c++) {
            ch = name.charAt(c);
            if (
                (ch<'a' || ch>'z')
                && (ch<'0' || ch>'9')
                && ch!='_'
            ) throw new ValidationException(ApplicationResources.accessor, "PostgresDatabaseName.validate.illegalCharacter");
    	}
        if(PostgresServer.ReservedWord.isReservedWord(name)) throw new ValidationException(ApplicationResources.accessor, "PostgresDatabaseName.validate.reservedWord");
    }

    private static final ConcurrentMap<String,PostgresDatabaseName> interned = new ConcurrentHashMap<String,PostgresDatabaseName>();

    public static PostgresDatabaseName valueOf(String name) throws ValidationException {
        PostgresDatabaseName existing = interned.get(name);
        return existing!=null ? existing : new PostgresDatabaseName(name);
    }

    final private String name;

    private PostgresDatabaseName(String name) throws ValidationException {
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
        ois.registerValidation(this, 0);
        ois.defaultReadObject();
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

    /**
     * Automatically uses previously interned values on deserialization.
     */
    private Object readResolve() throws InvalidObjectException {
        PostgresDatabaseName existing = interned.get(name);
        return existing!=null ? existing : this;
    }

    @Override
    public boolean equals(Object O) {
    	return
            O!=null
            && O instanceof PostgresDatabaseName
            && name.equals(((PostgresDatabaseName)O).name)
    	;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public int compareTo(PostgresDatabaseName other) {
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
    public PostgresDatabaseName intern() {
        try {
            PostgresDatabaseName existing = interned.get(name);
            if(existing==null) {
                String internedName = name.intern();
                PostgresDatabaseName addMe = name==internedName ? this : new PostgresDatabaseName(internedName);
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
    public com.aoindustries.aoserv.client.beans.PostgresDatabaseName getBean() {
        return new com.aoindustries.aoserv.client.beans.PostgresDatabaseName(name);
    }
}
