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
 * The unique identifier for a business.  Accounting codes must:
 * <ul>
 *   <li>Be non-null</li>
 *   <li>Be non-empty</li>
 *   <li>Be between 2 and 32 characters</li>
 *   <li>Must start with <code>[A-Z]</code></li>
 *   <li>Must end with <code>[A-Z] [0-9]</code></li>
 *   <li>Must contain only <code>[A-Z] [0-9] and underscore(_)</code></li>
 *   <li>May not have consecutive underscores</li>
 * </ul>
 *
 * @see Email#validate(java.lang.String)
 *
 * @author  AO Industries, Inc.
 */
final public class AccountingCode implements Comparable<AccountingCode>, Serializable, ObjectInputValidation, DtoFactory<com.aoindustries.aoserv.client.dto.AccountingCode>, Internable<AccountingCode> {

    private static final long serialVersionUID = -4701364475901418693L;

    public static final int MIN_LENGTH = 2;

    public static final int MAX_LENGTH = 32;

    /**
     * Validates an accounting code.
     */
    public static void validate(String accounting) throws ValidationException {
        if(accounting==null) throw new ValidationException(ApplicationResources.accessor, "AccountingCode.validate.isNull");
        int len=accounting.length();

        if(len<MIN_LENGTH) throw new ValidationException(ApplicationResources.accessor, "AccountingCode.validate.tooShort", MIN_LENGTH, len);
        if(len>MAX_LENGTH) throw new ValidationException(ApplicationResources.accessor, "AccountingCode.validate.tooLong", MAX_LENGTH, len);

        char ch=accounting.charAt(0);
        if(ch<'A' || ch>'Z') throw new ValidationException(ApplicationResources.accessor, "AccountingCode.validate.mustStartAlpha");

        ch=accounting.charAt(len-1);
        if(
            (ch<'A' || ch>'Z')
            && (ch<'0' || ch>'9')
    	) throw new ValidationException(ApplicationResources.accessor, "AccountingCode.validate.mustEndAlphanumeric");

        for(int pos=1;pos<(len-1);pos++) {
            ch=accounting.charAt(pos);
            if(ch=='_') {
                if(accounting.charAt(pos-1)=='_') throw new ValidationException(ApplicationResources.accessor, "AccountingCode.validate.consecutiveUnderscores", pos-1);
            } else if(
                (ch<'A' || ch>'Z')
                && (ch<'0' || ch>'9')
            ) throw new ValidationException(ApplicationResources.accessor, "AccountingCode.validate.invalidCharacter", ch, pos);
        }
    }

    private static final ConcurrentMap<String,AccountingCode> interned = new ConcurrentHashMap<String,AccountingCode>();

    /**
     * If accounting is null, then returns is null.
     */
    public static AccountingCode valueOf(String accounting) throws ValidationException {
        if(accounting==null) return null;
        AccountingCode existing = interned.get(accounting);
        return existing!=null ? existing : new AccountingCode(accounting);
    }

    /*
    public static AccountingCode valueOfInterned(String accounting) throws ValidationException {
        AccountingCode existing = interned.get(accounting);
        return existing!=null ? existing : new AccountingCode(accounting).intern();
    }*/

    final private String accounting;

    private AccountingCode(String accounting) throws ValidationException {
        this.accounting = accounting;
        validate();
    }

    private void validate() throws ValidationException {
        validate(accounting);
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
        AccountingCode existing = interned.get(accounting);
        return existing!=null ? existing : this;
    }

    @Override
    public boolean equals(Object O) {
    	return
            O!=null
            && O instanceof AccountingCode
            && accounting.equals(((AccountingCode)O).accounting)
    	;
    }

    @Override
    public int hashCode() {
        return accounting.hashCode();
    }

    @Override
    public int compareTo(AccountingCode other) {
        return this==other ? 0 : accounting.compareTo(other.accounting);
    }

    @Override
    public String toString() {
        return accounting;
    }

    /**
     * Interns this accounting code much in the same fashion as <code>String.intern()</code>.
     *
     * @see  String#intern()
     */
    @Override
    public AccountingCode intern() {
        try {
            AccountingCode existing = interned.get(accounting);
            if(existing==null) {
                String internedAccounting = accounting.intern();
                AccountingCode addMe = accounting==internedAccounting ? this : new AccountingCode(internedAccounting);
                existing = interned.putIfAbsent(internedAccounting, addMe);
                if(existing==null) existing = addMe;
            }
            return existing;
        } catch(ValidationException err) {
            // Should not fail validation since original object passed
            throw new AssertionError(err.getMessage());
        }
    }

    @Override
    public com.aoindustries.aoserv.client.dto.AccountingCode getDto() {
        return new com.aoindustries.aoserv.client.dto.AccountingCode(accounting);
    }
}
