/*
 * Copyright 2000-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.validator;

import com.aoindustries.aoserv.client.*;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * Several resources on a <code>Server</code> require a server-wide
 * unique identifier.  All of the possible identifiers are represented
 * by <code>LinuxID</code>s.
 *
 * @author  AO Industries, Inc.
 */
final public class LinuxID implements Comparable<LinuxID>, Serializable, ObjectInputValidation, DtoFactory<com.aoindustries.aoserv.client.dto.LinuxID> {

    private static final long serialVersionUID = 1L;

    public static void validate(int id) throws ValidationException {
        if(id<0) throw new ValidationException(ApplicationResources.accessor, "LinuxID.validate.lessThanZero", id);
        if(id>65535) throw new ValidationException(ApplicationResources.accessor, "LinuxID.validate.greaterThan64k", id);
    }

    private static final AtomicReferenceArray<LinuxID> cache = new AtomicReferenceArray<LinuxID>(65536);

    public static LinuxID valueOf(int id) throws ValidationException {
        validate(id);
        while(true) {
            LinuxID existing = cache.get(id);
            if(existing!=null) return existing;
            LinuxID newObj = new LinuxID(id);
            if(cache.compareAndSet(id, null, newObj)) return newObj;
        }
    }

    final private int id;

    private LinuxID(int id) throws ValidationException {
        this.id=id;
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

    private Object readResolve() throws InvalidObjectException {
        try {
            return valueOf(id);
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
            && O instanceof LinuxID
            && ((LinuxID)O).id==id
    	;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public int compareTo(LinuxID other) {
        return this==other ? 0 : AOServObjectUtils.compare(id, other.id);
    }

    @Override
    public String toString() {
        return Integer.toString(id);
    }

    public int getId() {
    	return id;
    }

    public boolean isSystem() {
        return id<500 || id==65534 || id==65535;
    }

    @Override
    public com.aoindustries.aoserv.client.dto.LinuxID getDto() {
        return new com.aoindustries.aoserv.client.dto.LinuxID(id);
    }
}
