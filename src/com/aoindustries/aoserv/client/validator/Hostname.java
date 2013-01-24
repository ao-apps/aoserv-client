/*
 * Copyright 2010-2013 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.validator;

import com.aoindustries.aoserv.client.DtoFactory;
import com.aoindustries.lang.ObjectUtils;
import com.aoindustries.util.Internable;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Represents a hostname as either a <code>DomainName</code> or an <code>InetAddress</code>.
 * To not allow the IP address representation, use <code>DomainName</code> instead.
 * No DNS lookups are performed during validation.
 * 
 * @author  AO Industries, Inc.
 */
final public class Hostname implements
    Comparable<Hostname>,
    Serializable,
    ObjectInputValidation,
    DtoFactory<com.aoindustries.aoserv.client.dto.Hostname>,
    Internable<Hostname>
{

    private static final long serialVersionUID = -6323326583709666966L;

    private static boolean isIp(String hostname) {
        if(hostname==null) return false;
        int len = hostname.length();
        if(len==0) return false;
        // If contains all digits and periods, or contains any colon, then is an IP
        boolean allDigitsAndPeriods = true;
        for(int c=0;c<len;c++) {
            char ch = hostname.charAt(c);
            if(ch==':') return true;
            if(
                (ch<'0' || ch>'9')
                && ch!='.'
            ) {
                allDigitsAndPeriods = false;
                // Still need to look for any colons
            }
        }
        return allDigitsAndPeriods;
    }

    /**
     * Validates a hostname, must be either a valid domain name or a valid IP address.
     */
    public static ValidationResult validate(String hostname) {
        if(isIp(hostname)) return InetAddress.validate(hostname);
        else return DomainName.validate(hostname);
    }

    private static final ConcurrentMap<DomainName,Hostname> internedByDomainName = new ConcurrentHashMap<DomainName,Hostname>();

    private static final ConcurrentMap<InetAddress,Hostname> internedByInetAddress = new ConcurrentHashMap<InetAddress,Hostname>();

    /**
     * If hostname is null, returns null.
     */
    public static Hostname valueOf(String hostname) throws ValidationException {
        if(hostname==null) return null;
        return
            isIp(hostname)
            ? valueOf(InetAddress.valueOf(hostname))
            : valueOf(DomainName.valueOf(hostname))
        ;
    }

    /**
     * If domainName is null, returns null.
     */
    public static Hostname valueOf(DomainName domainName) {
        if(domainName==null) return null;
        //Hostname existing = internedByDomainName.get(domainName);
        //return existing!=null ? existing : new Hostname(domainName);
        return new Hostname(domainName);
    }

    /**
     * If ip is null, returns null.
     */
    public static Hostname valueOf(InetAddress ip) {
        if(ip==null) return null;
        //Hostname existing = internedByInetAddress.get(ip);
        //return existing!=null ? existing : new Hostname(ip);
        return new Hostname(ip);
    }

    final private DomainName domainName;
    final private InetAddress inetAddress;

    private Hostname(DomainName domainName) {
        this.domainName = domainName;
        this.inetAddress = null;
    }

    private Hostname(InetAddress inetAddress) {
        this.domainName = null;
        this.inetAddress = inetAddress;
    }

    private void validate() throws ValidationException {
        if(domainName==null && inetAddress==null) throw new ValidationException(new InvalidResult(ApplicationResources.accessor, "Hostname.validate.bothNull"));
        if(domainName!=null && inetAddress!=null) throw new ValidationException(new InvalidResult(ApplicationResources.accessor, "Hostname.validate.bothNonNull"));
    }

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
        if(!(O instanceof Hostname)) return false;
        Hostname other = (Hostname)O;
    	return
            ObjectUtils.equals(domainName, other.domainName)
            && ObjectUtils.equals(inetAddress, other.inetAddress)
    	;
    }

    @Override
    public int hashCode() {
        return domainName!=null ? domainName.hashCode() : inetAddress.hashCode();
    }

    /**
     * Sorts IP addresses before domain names.
     */
    @Override
    public int compareTo(Hostname other) {
        if(this==other) return 0;
        if(domainName!=null) {
            if(other.domainName!=null) return domainName.compareTo(other.domainName);
            else return 1;
        } else {
            if(other.domainName!=null) return -1;
            else return inetAddress.compareTo(other.inetAddress);
        }
    }

    @Override
    public String toString() {
        return domainName!=null ? domainName.toString() : inetAddress.toString();
    }

    /**
     * Interns this hostname much in the same fashion as <code>String.intern()</code>.
     *
     * @see  String#intern()
     */
    @Override
    public Hostname intern() {
        if(domainName!=null) {
            Hostname existing = internedByDomainName.get(domainName);
            if(existing==null) {
                DomainName internedDomainName = domainName.intern();
                Hostname addMe = domainName==internedDomainName ? this : new Hostname(internedDomainName);
                existing = internedByDomainName.putIfAbsent(internedDomainName, addMe);
                if(existing==null) existing = addMe;
            }
            return existing;
        } else {
            Hostname existing = internedByInetAddress.get(inetAddress);
            if(existing==null) {
                InetAddress internedInetAddress = inetAddress.intern();
                Hostname addMe = inetAddress==internedInetAddress ? this : new Hostname(internedInetAddress);
                existing = internedByInetAddress.putIfAbsent(internedInetAddress, addMe);
                if(existing==null) existing = addMe;
            }
            return existing;
        }
    }

    public DomainName getDomainName() {
        return domainName;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    @Override
    public com.aoindustries.aoserv.client.dto.Hostname getDto() {
        return new com.aoindustries.aoserv.client.dto.Hostname(toString());
    }
}
