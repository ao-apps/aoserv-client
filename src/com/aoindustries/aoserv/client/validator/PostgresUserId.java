/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.validator;

import com.aoindustries.aoserv.client.BeanFactory;
import com.aoindustries.aoserv.client.PostgresServer;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Represents a PostgreSQL user ID.  PostgreSQL user ids must:
 * <ul>
 *   <li>Be non-null</li>
 *   <li>Be non-empty</li>
 *   <li>Be between 1 and 31 characters</li>
 *   <li>Must start with <code>[a-z]</code></li>
 *   <li>The rest of the characters may contain [a-z], [0-9], and underscore (_)</li>
 *   <li>Must not be a PostgreSQL reserved word</li>
 *   <li>Must be a valid <code>UserId</code> - this is implied by the above rules</li>
 * </ul>
 *
 * @author  AO Industries, Inc.
 */
final public class PostgresUserId implements Comparable<PostgresUserId>, Serializable, ObjectInputValidation, BeanFactory<com.aoindustries.aoserv.client.beans.PostgresUserId> {

    private static final long serialVersionUID = 1L;

    /**
     * The maximum length of a PostgreSQL username.
     */
    public static final int MAX_LENGTH = 31;

    /**
     * Validates a user id.
     */
    public static void validate(String id) throws ValidationException {
        if(id==null) throw new ValidationException(ApplicationResources.accessor, "PostgresUserId.validate.isNull");
    	int len = id.length();
        if(len==0) throw new ValidationException(ApplicationResources.accessor, "PostgresUserId.validate.isEmpty");
        if(len > MAX_LENGTH) throw new ValidationException(ApplicationResources.accessor, "PostgresUserId.validate.tooLong", MAX_LENGTH, len);

        // The first character must be [a-z] or [0-9]
        char ch = id.charAt(0);
        if(
            (ch < 'a' || ch > 'z')
            && (ch<'0' || ch>'9')
        ) throw new ValidationException(ApplicationResources.accessor, "PostgresUserId.validate.startAtoZor0to9");

        // The rest may have additional characters
        for (int c = 1; c < len; c++) {
            ch = id.charAt(c);
            if (
                (ch<'a' || ch>'z')
                && (ch<'0' || ch>'9')
                && ch!='_'
            ) throw new ValidationException(ApplicationResources.accessor, "PostgresUserId.validate.illegalCharacter");
    	}
        if(
            id.equals("sameuser")
            || id.equals("samegroup")
            || id.equals("all")
            || PostgresServer.ReservedWord.isReservedWord(id)
        ) throw new ValidationException(ApplicationResources.accessor, "PostgresUserId.validate.reservedWord");
    }

    private static final ConcurrentMap<String,PostgresUserId> interned = new ConcurrentHashMap<String,PostgresUserId>();

    public static PostgresUserId valueOf(String id) throws ValidationException {
        PostgresUserId existing = interned.get(id);
        return existing!=null ? existing : new PostgresUserId(id);
    }

    final private String id;

    private PostgresUserId(String id) throws ValidationException {
        this.id = id;
        validate();
    }

    private void validate() throws ValidationException {
        validate(id);
    }

    /**
     * Perform same validation as constructor on readObject.
     */
    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.registerValidation(this, 0);
        ois.defaultReadObject();
    }

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
        PostgresUserId existing = interned.get(id);
        return existing!=null ? existing : this;
    }

    @Override
    public boolean equals(Object O) {
    	return
            O!=null
            && O instanceof PostgresUserId
            && id.equals(((PostgresUserId)O).id)
    	;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public int compareTo(PostgresUserId other) {
        return id.compareTo(other.id);
    }

    @Override
    public String toString() {
        return id;
    }

    /**
     * Interns this id much in the same fashion as <code>String.intern()</code>.
     *
     * @see  String#intern()
     */
    public PostgresUserId intern() {
        // Interning implies interning the eqvilalent UserId
        getUserId().intern();
        try {
            PostgresUserId existing = interned.get(id);
            if(existing==null) {
                String internedId = id.intern();
                PostgresUserId addMe = id==internedId ? this : new PostgresUserId(internedId);
                existing = interned.putIfAbsent(internedId, addMe);
                if(existing==null) existing = addMe;
            }
            return existing;
        } catch(ValidationException err) {
            // Should not fail validation since original object passed
            throw new AssertionError(err.getMessage());
        }
    }

    public String getId() {
        return id;
    }

    public com.aoindustries.aoserv.client.beans.PostgresUserId getBean() {
        return new com.aoindustries.aoserv.client.beans.PostgresUserId(id);
    }

    /**
     * A PostgresUserId is always a valid UserId.
     */
    public UserId getUserId() {
        try {
            return UserId.valueOf(id);
        } catch(ValidationException err) {
            throw new AssertionError(err.getMessage());
        }
    }
}
