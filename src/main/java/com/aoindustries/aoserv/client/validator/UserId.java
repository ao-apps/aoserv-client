/*
 * Copyright 2010-2013, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.validator;

import com.aoindustries.aoserv.client.DtoFactory;
import com.aoindustries.io.FastExternalizable;
import com.aoindustries.io.FastObjectInput;
import com.aoindustries.io.FastObjectOutput;
import com.aoindustries.util.Internable;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectInputValidation;
import java.io.ObjectOutput;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Represents a user ID that may be used by certain types of accounts.  User ids must:
 * <ul>
 *   <li>Be non-null</li>
 *   <li>Be non-empty</li>
 *   <li>Be between 1 and 255 characters</li>
 *   <li>Must start with <code>[a-z]</code></li>
 *   <li>Uses only ASCII 0x21 through 0x7f, excluding <code>space , : ( ) [ ] ' " | & ; A-Z \ /</code></li>
 *   <li>
 *     If contains any @ symbol, must also be a valid email address.  Please note that the
 *     reverse is not implied - email addresses may exist that are not valid user ids.
 *   </li>
 *   <li>May not start with cyrus@</li>
 * </ul>
 *
 * @see Email#validate(java.lang.String)
 *
 * @author  AO Industries, Inc.
 */
final public class UserId implements
	Comparable<UserId>,
	FastExternalizable,
	ObjectInputValidation,
	DtoFactory<com.aoindustries.aoserv.client.dto.UserId>,
	Internable<UserId>
{

	public static final int MAX_LENGTH=255;

	/**
	 * Validates a user id.
	 */
	public static ValidationResult validate(String id) {
		if(id==null) return new InvalidResult(ApplicationResources.accessor, "UserId.validate.isNull");
		int len = id.length();
		if(len==0) return new InvalidResult(ApplicationResources.accessor, "UserId.validate.isEmpty");
		if(len > MAX_LENGTH) return new InvalidResult(ApplicationResources.accessor, "UserId.validate.tooLong", MAX_LENGTH, len);

		// The first character must be [a-z]
		char ch = id.charAt(0);
		if(ch < 'a' || ch > 'z') return new InvalidResult(ApplicationResources.accessor, "UserId.validate.startAToZ");

		// The rest may have additional characters
		boolean hasAt = false;
		for (int c = 1; c < len; c++) {
			ch = id.charAt(c);
			if(ch==' ') return new InvalidResult(ApplicationResources.accessor, "UserId.validate.noSpace");
			if(ch<=0x21 || ch>0x7f) return new InvalidResult(ApplicationResources.accessor, "UserId.validate.specialCharacter");
			if(ch>='A' && ch<='Z') return new InvalidResult(ApplicationResources.accessor, "UserId.validate.noCapital");
			switch(ch) {
				case ',' : return new InvalidResult(ApplicationResources.accessor, "UserId.validate.comma");
				case ':' : return new InvalidResult(ApplicationResources.accessor, "UserId.validate.colon");
				case '(' : return new InvalidResult(ApplicationResources.accessor, "UserId.validate.leftParen");
				case ')' : return new InvalidResult(ApplicationResources.accessor, "UserId.validate.rightParen");
				case '[' : return new InvalidResult(ApplicationResources.accessor, "UserId.validate.leftSquare");
				case ']' : return new InvalidResult(ApplicationResources.accessor, "UserId.validate.rightSquare");
				case '\'' : return new InvalidResult(ApplicationResources.accessor, "UserId.validate.apostrophe");
				case '"' : return new InvalidResult(ApplicationResources.accessor, "UserId.validate.quote");
				case '|' : return new InvalidResult(ApplicationResources.accessor, "UserId.validate.verticalBar");
				case '&' : return new InvalidResult(ApplicationResources.accessor, "UserId.validate.ampersand");
				case ';' : return new InvalidResult(ApplicationResources.accessor, "UserId.validate.semicolon");
				case '\\' : return new InvalidResult(ApplicationResources.accessor, "UserId.validate.backslash");
				case '/' : return new InvalidResult(ApplicationResources.accessor, "UserId.validate.slash");
				case '@' : hasAt = true; break;
			}
		}

		// More strict at sign control is required for user@domain structure in Cyrus virtdomains.
		if(hasAt) {
			// Must also be a valid email address
			ValidationResult result = Email.validate(id);
			if(!result.isValid()) return result;
			if(id.startsWith("cyrus@")) return new InvalidResult(ApplicationResources.accessor, "UserId.validate.startWithCyrusAt");
		}
		return ValidResult.getInstance();
	}

	private static final ConcurrentMap<String,UserId> interned = new ConcurrentHashMap<>();

	/**
	 * If id is null, returns null.
	 */
	public static UserId valueOf(String id) throws ValidationException {
		if(id==null) return null;
		//UserId existing = interned.get(id);
		//return existing!=null ? existing : new UserId(id);
		return new UserId(id);
	}

	/*
	public static UserId valueOfInterned(String id) throws ValidationException {
		UserId existing = interned.get(id);
		return existing!=null ? existing : new UserId(id).intern();
	}*/

	private String id;

	private UserId(String id) throws ValidationException {
		this.id = id;
		validate();
	}

	private void validate() throws ValidationException {
		ValidationResult result = validate(id);
		if(!result.isValid()) throw new ValidationException(result);
	}

	@Override
	public boolean equals(Object O) {
		return
			O!=null
			&& O instanceof UserId
			&& id.equals(((UserId)O).id)
		;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public int compareTo(UserId other) {
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
	public UserId intern() {
		try {
			UserId existing = interned.get(id);
			if(existing==null) {
				String internedId = id.intern();
				UserId addMe = id==internedId ? this : new UserId(internedId);
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
	public com.aoindustries.aoserv.client.dto.UserId getDto() {
		return new com.aoindustries.aoserv.client.dto.UserId(id);
	}

	// <editor-fold defaultstate="collapsed" desc="FastExternalizable">
	private static final long serialVersionUID = -837866431257794645L;

	public UserId() {
	}

	@Override
	public long getSerialVersionUID() {
		return serialVersionUID;
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		FastObjectOutput fastOut = FastObjectOutput.wrap(out);
		try {
			fastOut.writeFastUTF(id);
		} finally {
			fastOut.unwrap();
		}
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		if(id!=null) throw new IllegalStateException();
		FastObjectInput fastIn = FastObjectInput.wrap(in);
		try {
			id = fastIn.readFastUTF();
		} finally {
			fastIn.unwrap();
		}
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
	// </editor-fold>
}
