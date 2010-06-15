/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.validator;

import com.aoindustries.aoserv.client.AOServObjectUtils;
import com.aoindustries.aoserv.client.BeanFactory;
import com.aoindustries.util.Internable;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Represents a DNS domain name.  Domain names must:
 * <ul>
 *   <li>Be non-null</li>
 *   <li>Be non-empty</li>
 *   <li>May not be "default" (case-insensitive)</li>
 *   <li>
 *     Confirm to definition in {@link http://en.wikipedia.org/wiki/Hostname#Internet_hostnames}
 *     and {@link http://en.wikipedia.org/wiki/DNS_label#Parts_of_a_domain_name}
 *   </li>
 *   <li>Not end with a period (.)</li>
 * </ul>
 *
 * 
 * @author  AO Industries, Inc.
 */
final public class DomainName implements Comparable<DomainName>, Serializable, ObjectInputValidation, BeanFactory<com.aoindustries.aoserv.client.beans.DomainName>, Internable<DomainName> {

    private static final long serialVersionUID = 1L;

    public static final int MAX_LENGTH = 253;

    /**
     * Validates a domain name, but doesn't allow an ending period.
     *
     * @see DomainLabel#validate(java.lang.String)
     */
    public static void validate(String domain) throws ValidationException {
        if(domain==null) throw new ValidationException(ApplicationResources.accessor, "DomainName.validate.isNull");
        int len = domain.length();
        if(len==0) throw new ValidationException(ApplicationResources.accessor, "DomainName.validate.empty");
        if("default".equalsIgnoreCase(domain)) throw new ValidationException(ApplicationResources.accessor, "DomainName.validate.isDefault");
        if(len>MAX_LENGTH) throw new ValidationException(ApplicationResources.accessor, "DomainName.validate.tooLong", MAX_LENGTH, len);
        int labelStart = 0;
        for(int pos=0; pos<len; pos++) {
            if(domain.charAt(pos)=='.') {
                DomainLabel.validate(domain.substring(labelStart, pos));
                labelStart = pos+1;
            }
        }
        DomainLabel.validate(domain.substring(labelStart, len));
    }

    private static final ConcurrentMap<String,DomainName> interned = new ConcurrentHashMap<String,DomainName>();

    public static DomainName valueOf(String domain) throws ValidationException {
        DomainName existing = interned.get(domain);
        return existing!=null ? existing : new DomainName(domain);
    }

    final private String domain;

    private DomainName(String domain) throws ValidationException {
        this.domain = domain;
        validate();
    }

    private void validate() throws ValidationException {
        validate(domain);
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
        DomainName existing = interned.get(domain);
        return existing!=null ? existing : this;
    }

    @Override
    public boolean equals(Object O) {
    	return
            O!=null
            && O instanceof DomainName
            && domain.equals(((DomainName)O).domain)
    	;
    }

    @Override
    public int hashCode() {
        return domain.hashCode();
    }

    /**
     * Sorts by top level domain, then subdomain, then sub-subdomain, ...
     */
    @Override
    public int compareTo(DomainName other) {
        if(this==other) return 0;
        String domain1 = domain;
        String domain2 = other.domain;
        if(domain1==domain2) return 0; // Shortcut for interned
        while(domain1.length()>0 && domain2.length()>0) {
            int pos=domain1.lastIndexOf('.');
            String section1;
            if(pos==-1) {
                section1=domain1;
                domain1="";
            } else {
                section1=domain1.substring(pos+1);
                domain1=domain1.substring(0, pos);
            }

            pos=domain2.lastIndexOf('.');
            String section2;
            if(pos==-1) {
                section2=domain2;
                domain2="";
            } else {
                section2=domain2.substring(pos+1);
                domain2=domain2.substring(0, pos);
            }

            int diff=AOServObjectUtils.compareIgnoreCaseConsistentWithEquals(section1, section2);
            if(diff!=0) return diff;
        }
        return AOServObjectUtils.compareIgnoreCaseConsistentWithEquals(domain1, domain2);
    }

    @Override
    public String toString() {
        return domain;
    }

    /**
     * Interns this domain much in the same fashion as <code>String.intern()</code>.
     *
     * @see  String#intern()
     */
    @Override
    public DomainName intern() {
        try {
            DomainName existing = interned.get(domain);
            if(existing==null) {
                String internedDomain = domain.intern();
                DomainName addMe = domain==internedDomain ? this : new DomainName(internedDomain);
                existing = interned.putIfAbsent(internedDomain, addMe);
                if(existing==null) existing = addMe;
            }
            return existing;
        } catch(ValidationException err) {
            // Should not fail validation since original object passed
            throw new AssertionError(err.getMessage());
        }
    }

    @Override
    public com.aoindustries.aoserv.client.beans.DomainName getBean() {
        return new com.aoindustries.aoserv.client.beans.DomainName(domain);
    }
}
