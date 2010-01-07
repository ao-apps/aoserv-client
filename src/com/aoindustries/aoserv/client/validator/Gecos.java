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

/**
 * Used for the various user-provided fields in the <code>/etc/passwd</code> file.
 *
 * {@link http://en.wikipedia.org/wiki/Gecos_field}
 *
 * @author  AO Industries, Inc.
 */
final public class Gecos implements Comparable<Gecos>, Serializable, ObjectInputValidation, BeanFactory<com.aoindustries.aoserv.client.beans.Gecos> {

    private static final long serialVersionUID = 1L;

    public static final int MAX_LENGTH = 100;

    /**
     * Determines if a name can be used as a GECOS field.  A GECOS field
     * is valid if it is between 1 and 100 characters in length and uses only
     * <code>[a-z,A-Z,0-9,-,_,@, ,.,#,=,/,$,%,^,&,*,(,),?,']</code> for each
     * character.<br>
     * <br>
     * Refer to <code>man 5 passwd</code>
     */
    public static void validate(String value) throws ValidationException {
        // Be non-null
        if(value==null) throw new ValidationException(ApplicationResources.accessor, "Gecos.validate.isNull");
        int len = value.length();
        if(len==0) throw new ValidationException(ApplicationResources.accessor, "Gecos.validate.isEmpty");
        if(len>MAX_LENGTH) throw new ValidationException(ApplicationResources.accessor, "Gecos.validate.tooLong", MAX_LENGTH, len);

        for (int c = 0; c < len; c++) {
            char ch = value.charAt(c);
            if (
                (ch < 'a' || ch > 'z')
                && (ch<'A' || ch>'Z')
                && (ch < '0' || ch > '9')
                && ch != '-'
                && ch != '_'
                && ch != '@'
                && ch != ' '
                && ch != '.'
                && ch != '#'
                && ch != '='
                && ch != '/'
                && ch != '$'
                && ch != '%'
                && ch != '^'
                && ch != '&'
                && ch != '*'
                && ch != '('
                && ch != ')'
                && ch != '?'
                && ch != '\''
                && ch != '+'
            ) throw new ValidationException(ApplicationResources.accessor, "Gecos.validate.invalidCharacter", ch);
        }
    }

    public static Gecos valueOf(String value) throws ValidationException {
        return new Gecos(value);
    }

    final private String value;

    private Gecos(String value) throws ValidationException {
        this.value = value;
        validate();
    }

    private void validate() throws ValidationException {
        validate(value);
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

    @Override
    public boolean equals(Object O) {
    	return
            O!=null
            && O instanceof Gecos
            && value.equals(((Gecos)O).value)
    	;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    public int compareTo(Gecos other) {
        return AOServObjectUtils.compareIgnoreCaseConsistentWithEquals(value, other.value);
    }

    @Override
    public String toString() {
        return value;
    }

    public String getValue() {
        return value;
    }

    public com.aoindustries.aoserv.client.beans.Gecos getBean() {
        return new com.aoindustries.aoserv.client.beans.Gecos(value);
    }
}
