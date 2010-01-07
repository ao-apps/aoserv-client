/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.validator;

import com.aoindustries.aoserv.client.AOServObjectUtils;
import com.aoindustries.aoserv.client.BeanFactory;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Represents a DNS domain label (a single part of a domain name between dots).  Domain labels must:
 * <ul>
 *   <li>Be non-null</li>
 *   <li>Be non-empty</li>
 *   <li>
 *     Confirm to definition in {@link http://en.wikipedia.org/wiki/DNS_label#Parts_of_a_domain_name}
 *   </li>
 * </ul>
 *
 * 
 * @author  AO Industries, Inc.
 */
final public class DomainLabel implements Comparable<DomainLabel>, Serializable, ObjectInputValidation, BeanFactory<com.aoindustries.aoserv.client.beans.DomainLabel> {

    private static final long serialVersionUID = 1L;

    public static final int MAX_LENGTH = 63;

    /**
     * Validates a domain name label.
     */
    public static void validate(String label) throws ValidationException {
        if(label==null) throw new ValidationException(ApplicationResources.accessor, "DomainLabel.validate.isNull");
        int len = label.length();
        if(len==0) throw new ValidationException(ApplicationResources.accessor, "DomainLabel.validate.empty");
        if(len>MAX_LENGTH) throw new ValidationException(ApplicationResources.accessor, "DomainLabel.validate.tooLong", MAX_LENGTH, len);
        boolean foundNonDigit = false;
        for(int pos=0; pos<len; pos++) {
            char ch = label.charAt(pos);
            if(ch=='-') {
                foundNonDigit = true;
                if(pos==0) throw new ValidationException(ApplicationResources.accessor, "DomainLabel.validate.startsDash");
                if(pos==(len-1)) throw new ValidationException(ApplicationResources.accessor, "DomainLabel.validate.endsDash");
            } else if(
                (ch>='a' && ch<='z')
                || (ch>='A' && ch<='Z')
            ) {
                foundNonDigit = true;
            } else if(ch<'0' || ch>'9') throw new ValidationException(ApplicationResources.accessor, "DomainLabel.validate.invalidCharacter", ch, pos);
        }
        if(!foundNonDigit) throw new ValidationException(ApplicationResources.accessor, "DomainLabel.validate.allDigits");
    }

    private static final ConcurrentMap<String,DomainLabel> interned = new ConcurrentHashMap<String,DomainLabel>();

    public static DomainLabel valueOf(String label) throws ValidationException {
        DomainLabel existing = interned.get(label);
        return existing!=null ? existing : new DomainLabel(label);
    }

    final private String label;

    private DomainLabel(String label) throws ValidationException {
        this.label = label;
        validate();
    }

    private void validate() throws ValidationException {
        validate(label);
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
        DomainLabel existing = interned.get(label);
        return existing!=null ? existing : this;
    }

    @Override
    public boolean equals(Object O) {
    	return
            O!=null
            && O instanceof DomainLabel
            && label.equals(((DomainLabel)O).label)
    	;
    }

    @Override
    public int hashCode() {
        return label.hashCode();
    }

    public int compareTo(DomainLabel other) {
        return AOServObjectUtils.compareIgnoreCaseConsistentWithEquals(label, other.label);
    }

    @Override
    public String toString() {
        return label;
    }

    /**
     * Interns this label much in the same fashion as <code>String.intern()</code>.
     *
     * @see  String#intern()
     */
    public DomainLabel intern() {
        try {
            DomainLabel existing = interned.get(label);
            if(existing==null) {
                String internedLabel = label.intern();
                DomainLabel addMe = label==internedLabel ? this : new DomainLabel(internedLabel);
                existing = interned.putIfAbsent(internedLabel, addMe);
                if(existing==null) existing = addMe;
            }
            return existing;
        } catch(ValidationException err) {
            // Should not fail validation since original object passed
            throw new AssertionError(err.getMessage());
        }
    }

    public String getLabel() {
        return label;
    }

    public com.aoindustries.aoserv.client.beans.DomainLabel getBean() {
        return new com.aoindustries.aoserv.client.beans.DomainLabel(label);
    }
}
