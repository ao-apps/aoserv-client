/*
 * Copyright 2010-2011 by AO Industries, Inc.,
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

/**
 * Represents a name that may be used for a MySQL table.  Table names must:
 * <ul>
 *   <li>Be non-null</li>
 *   <li>Be non-empty</li>
 *   <li>Be between 1 and 64 characters</li>
 *   <li>Must start with <code>[a-z], [A-Z], or _</code></li>
 *   <li>The rest of the characters may contain [a-z], [A-Z], [0-9], underscore (_), and hyphen (-)</li>
 * </ul>
 *
 * @author  AO Industries, Inc.
 */
final public class MySQLTableName implements Comparable<MySQLTableName>, Serializable, ObjectInputValidation, DtoFactory<com.aoindustries.aoserv.client.dto.MySQLTableName> {

    private static final long serialVersionUID = 1L;

    /**
     * The longest name allowed for a MySQL table name.
     */
    public static final int MAX_LENGTH = 64;

    /**
     * Validates a MySQL table name.
     */
    public static void validate(String name) throws ValidationException {
        if(name==null) throw new ValidationException(ApplicationResources.accessor, "MySQLTableName.validate.isNull");
    	int len = name.length();
        if(len==0) throw new ValidationException(ApplicationResources.accessor, "MySQLTableName.validate.isEmpty");
        if(len > MAX_LENGTH) throw new ValidationException(ApplicationResources.accessor, "MySQLTableName.validate.tooLong", MAX_LENGTH, len);

        // The first character must be [a-z], [A-Z], [0-9], or _
        char ch = name.charAt(0);
        if(
               (ch < 'a' || ch > 'z')
            && (ch < 'A' || ch > 'Z')
            && (ch < '0' || ch > '9')
            && ch != '_'
        ) throw new ValidationException(ApplicationResources.accessor, "MySQLTableName.validate.badFirstCharacter");

        // The rest may have additional characters
        for (int c = 1; c < len; c++) {
            ch = name.charAt(c);
            if (
                   (ch<'a' || ch>'z')
                && (ch<'A' || ch>'Z')
                && (ch<'0' || ch>'9')
                && ch != '_'
                && ch != '-'
            ) throw new ValidationException(ApplicationResources.accessor, "MySQLTableName.validate.illegalCharacter");
    	}
    }

    public static MySQLTableName valueOf(String name) throws ValidationException {
        return new MySQLTableName(name);
    }

    final private String name;

    private MySQLTableName(String name) throws ValidationException {
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

    @Override
    public boolean equals(Object O) {
    	return
            O!=null
            && O instanceof MySQLTableName
            && name.equals(((MySQLTableName)O).name)
    	;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public int compareTo(MySQLTableName other) {
        return this==other ? 0 : name.compareTo(other.name);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public com.aoindustries.aoserv.client.dto.MySQLTableName getDto() {
        return new com.aoindustries.aoserv.client.dto.MySQLTableName(name);
    }
}
