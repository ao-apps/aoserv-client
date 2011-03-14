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
 * Represents a group ID that may be used by certain types of groups.  Group ids must:
 * <ul>
 *   <li>Be non-null</li>
 *   <li>Be non-empty</li>
 *   <li>Be between 1 and 255 characters</li>
 *   <li>Must start with <code>[a-z]</code></li>
 *   <li>Uses only ASCII 0x21 through 0x7f, excluding <code>space , : ( ) [ ] ' " | & ; A-Z \ / @</code></li>
 * </ul>
 *
 * @author  AO Industries, Inc.
 */
final public class GroupId implements Comparable<GroupId>, Serializable, ObjectInputValidation, DtoFactory<com.aoindustries.aoserv.client.dto.GroupId>, Internable<GroupId> {

    private static final long serialVersionUID = 5758732021942097608L;

    public static final int MAX_LENGTH=255;

    /**
     * Validates a group id.
     */
    public static void validate(String id) throws ValidationException {
        if(id==null) throw new ValidationException(ApplicationResources.accessor, "GroupId.validate.isNull");
        if(!interned.containsKey(id)) { // Is valid if already interned
            int len = id.length();
            if(len==0) throw new ValidationException(ApplicationResources.accessor, "GroupId.validate.isEmpty");
            if(len > MAX_LENGTH) throw new ValidationException(ApplicationResources.accessor, "GroupId.validate.tooLong", MAX_LENGTH, len);

            // The first character must be [a-z]
            char ch = id.charAt(0);
            if(ch < 'a' || ch > 'z') throw new ValidationException(ApplicationResources.accessor, "GroupId.validate.startAToZ");

            // The rest may have additional characters
            for (int c = 1; c < len; c++) {
                ch = id.charAt(c);
                if(ch==' ') throw new ValidationException(ApplicationResources.accessor, "GroupId.validate.noSpace");
                if(ch<=0x21 || ch>0x7f) throw new ValidationException(ApplicationResources.accessor, "GroupId.validate.specialCharacter");
                if(ch>='A' && ch<='Z') throw new ValidationException(ApplicationResources.accessor, "GroupId.validate.noCapital");
                switch(ch) {
                    case ',' : throw new ValidationException(ApplicationResources.accessor, "GroupId.validate.comma");
                    case ':' : throw new ValidationException(ApplicationResources.accessor, "GroupId.validate.colon");
                    case '(' : throw new ValidationException(ApplicationResources.accessor, "GroupId.validate.leftParen");
                    case ')' : throw new ValidationException(ApplicationResources.accessor, "GroupId.validate.rightParen");
                    case '[' : throw new ValidationException(ApplicationResources.accessor, "GroupId.validate.leftSquare");
                    case ']' : throw new ValidationException(ApplicationResources.accessor, "GroupId.validate.rightSquare");
                    case '\'' : throw new ValidationException(ApplicationResources.accessor, "GroupId.validate.apostrophe");
                    case '"' : throw new ValidationException(ApplicationResources.accessor, "GroupId.validate.quote");
                    case '|' : throw new ValidationException(ApplicationResources.accessor, "GroupId.validate.verticalBar");
                    case '&' : throw new ValidationException(ApplicationResources.accessor, "GroupId.validate.ampersand");
                    case ';' : throw new ValidationException(ApplicationResources.accessor, "GroupId.validate.semicolon");
                    case '\\' : throw new ValidationException(ApplicationResources.accessor, "GroupId.validate.backslash");
                    case '/' : throw new ValidationException(ApplicationResources.accessor, "GroupId.validate.slash");
                    case '@' : throw new ValidationException(ApplicationResources.accessor, "GroupId.validate.at");
                }
            }
        }
    }

    private static final ConcurrentMap<String,GroupId> interned = new ConcurrentHashMap<String,GroupId>();

    public static GroupId valueOf(String id) throws ValidationException {
        GroupId existing = interned.get(id);
        return existing!=null ? existing : new GroupId(id);
    }

    final private String id;

    private GroupId(String id) throws ValidationException {
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
        GroupId existing = interned.get(id);
        return existing!=null ? existing : this;
    }

    @Override
    public boolean equals(Object O) {
    	return
            O!=null
            && O instanceof GroupId
            && id.equals(((GroupId)O).id)
    	;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public int compareTo(GroupId other) {
        return this==other ? 0 : id.compareTo(other.id);
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
    @Override
    public GroupId intern() {
        try {
            GroupId existing = interned.get(id);
            if(existing==null) {
                String internedId = id.intern();
                GroupId addMe = id==internedId ? this : new GroupId(internedId); // Using identity String comparison to see if already interned
                existing = interned.putIfAbsent(internedId, addMe);
                if(existing==null) existing = addMe;
            }
            return existing;
        } catch(ValidationException err) {
            // Should not fail validation since original object passed
            throw new AssertionError(err.getMessage());
        }
    }

    @Override
    public com.aoindustries.aoserv.client.dto.GroupId getDto() {
        return new com.aoindustries.aoserv.client.dto.GroupId(id);
    }
}
