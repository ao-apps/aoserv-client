/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.validator;

import com.aoindustries.aoserv.client.BeanFactory;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Represents a 48-bit MAC address in HH:HH:HH:HH:HH:HH format.  Parses case insensitive, produces uppercase.
 *
 * {@link http://en.wikipedia.org/wiki/MAC_address}
 *
 * @author  AO Industries, Inc.
 */
final public class MacAddress implements Comparable<MacAddress>, Serializable, ObjectInputValidation, BeanFactory<com.aoindustries.aoserv.client.beans.MacAddress> {

    private static final long serialVersionUID = 1L;

    private static void checkHexValue(char ch) throws ValidationException {
        if(
            (ch<'0' || ch>'9')
            && (ch<'A' || ch>'F')
            && (ch<'a' || ch>'f')
        ) throw new ValidationException(ApplicationResources.accessor, "MacAddress.checkHexValue.badCharacter", ch);
    }

    /**
     * Checks if the address is valid.
     */
    public static void validate(String address) throws ValidationException {
        // Be non-null
        if(address==null) throw new ValidationException(ApplicationResources.accessor, "MacAddress.validate.isNull");
        // If found in interned, it is valid
        if(!interned.containsKey(address)) {
        // Be non-empty
        int len = address.length();
        if(len!=17) throw new ValidationException(ApplicationResources.accessor, "MacAddress.parse.incorrectLength", len);
            checkHexValue(address.charAt(0));
            checkHexValue(address.charAt(1));
            if(address.charAt(2)!=':') throw new ValidationException(ApplicationResources.accessor, "MacAddress.parse.notColon", 2);
            checkHexValue(address.charAt(3));
            checkHexValue(address.charAt(4));
            if(address.charAt(5)!=':') throw new ValidationException(ApplicationResources.accessor, "MacAddress.parse.notColon", 5);
            checkHexValue(address.charAt(6));
            checkHexValue(address.charAt(7));
            if(address.charAt(8)!=':') throw new ValidationException(ApplicationResources.accessor, "MacAddress.parse.notColon", 8);
            checkHexValue(address.charAt(9));
            checkHexValue(address.charAt(10));
            if(address.charAt(11)!=':') throw new ValidationException(ApplicationResources.accessor, "MacAddress.parse.notColon", 11);
            checkHexValue(address.charAt(12));
            checkHexValue(address.charAt(13));
            if(address.charAt(14)!=':') throw new ValidationException(ApplicationResources.accessor, "MacAddress.parse.notColon", 14);
            checkHexValue(address.charAt(15));
            checkHexValue(address.charAt(16));
        }
    }

    private static final ConcurrentMap<String,MacAddress> interned = new ConcurrentHashMap<String,MacAddress>();

    public static MacAddress valueOf(String address) throws ValidationException {
        MacAddress existing = interned.get(address);
        return existing!=null ? existing : new MacAddress(address);
    }

    final private String address;

    private MacAddress(String address) throws ValidationException {
        this.address = address;
        validate();
    }

    private void validate() throws ValidationException {
        validate(address);
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
        MacAddress existing = interned.get(address);
        return existing!=null ? existing : this;
    }

    @Override
    public boolean equals(Object O) {
        return
            (O instanceof MacAddress)
            && address.equals(((MacAddress)O).address)
        ;
    }

    @Override
    public int hashCode() {
        return address.hashCode();
    }

    public int compareTo(MacAddress other) {
        return address.compareTo(other.address);
    }

    @Override
    public String toString() {
        return address;
    }

    /**
     * Interns this IP much in the same fashion as <code>String.intern()</code>.
     *
     * @see  String#intern()
     */
    public MacAddress intern() {
        try {
            MacAddress existing = interned.get(address);
            if(existing==null) {
                String internedAddress = address.intern();
                MacAddress addMe = address==internedAddress ? this : new MacAddress(internedAddress);
                existing = interned.putIfAbsent(internedAddress, addMe);
                if(existing==null) existing = addMe;
            }
            return existing;
        } catch(ValidationException err) {
            // Should not fail validation since original object passed
            throw new AssertionError(err.getMessage());
        }
    }

    public String getAddress() {
        return address;
    }

    public com.aoindustries.aoserv.client.beans.MacAddress getBean() {
        return new com.aoindustries.aoserv.client.beans.MacAddress(address);
    }

    public boolean isBroadcast() {
        return address.equals("FF:FF:FF:FF:FF:FF");
    }
}
