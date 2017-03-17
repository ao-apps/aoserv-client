/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2010-2013, 2016, 2017  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aoserv-client.
 *
 * aoserv-client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aoserv-client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aoserv-client.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.aoserv.client.validator;

import com.aoindustries.dto.DtoFactory;
import com.aoindustries.util.Internable;
import com.aoindustries.validation.InvalidResult;
import com.aoindustries.validation.ValidResult;
import com.aoindustries.validation.ValidationException;
import com.aoindustries.validation.ValidationResult;
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
final public class GroupId implements
	Comparable<GroupId>,
	Serializable,
	ObjectInputValidation,
	DtoFactory<com.aoindustries.aoserv.client.dto.GroupId>,
	Internable<GroupId>
{

	private static final long serialVersionUID = 5758732021942097608L;

	public static final int MAX_LENGTH=255;

	/**
	 * Validates a group id.
	 */
	public static ValidationResult validate(String id) {
		if(id==null) return new InvalidResult(ApplicationResources.accessor, "GroupId.validate.isNull");
		int len = id.length();
		if(len==0) return new InvalidResult(ApplicationResources.accessor, "GroupId.validate.isEmpty");
		if(len > MAX_LENGTH) return new InvalidResult(ApplicationResources.accessor, "GroupId.validate.tooLong", MAX_LENGTH, len);

		// The first character must be [a-z]
		char ch = id.charAt(0);
		if(ch < 'a' || ch > 'z') return new InvalidResult(ApplicationResources.accessor, "GroupId.validate.startAToZ");

		// The rest may have additional characters
		for (int c = 1; c < len; c++) {
			ch = id.charAt(c);
			if(ch==' ') return new InvalidResult(ApplicationResources.accessor, "GroupId.validate.noSpace");
			if(ch<=0x21 || ch>0x7f) return new InvalidResult(ApplicationResources.accessor, "GroupId.validate.specialCharacter");
			if(ch>='A' && ch<='Z') return new InvalidResult(ApplicationResources.accessor, "GroupId.validate.noCapital");
			switch(ch) {
				case ',' : return new InvalidResult(ApplicationResources.accessor, "GroupId.validate.comma");
				case ':' : return new InvalidResult(ApplicationResources.accessor, "GroupId.validate.colon");
				case '(' : return new InvalidResult(ApplicationResources.accessor, "GroupId.validate.leftParen");
				case ')' : return new InvalidResult(ApplicationResources.accessor, "GroupId.validate.rightParen");
				case '[' : return new InvalidResult(ApplicationResources.accessor, "GroupId.validate.leftSquare");
				case ']' : return new InvalidResult(ApplicationResources.accessor, "GroupId.validate.rightSquare");
				case '\'' : return new InvalidResult(ApplicationResources.accessor, "GroupId.validate.apostrophe");
				case '"' : return new InvalidResult(ApplicationResources.accessor, "GroupId.validate.quote");
				case '|' : return new InvalidResult(ApplicationResources.accessor, "GroupId.validate.verticalBar");
				case '&' : return new InvalidResult(ApplicationResources.accessor, "GroupId.validate.ampersand");
				case ';' : return new InvalidResult(ApplicationResources.accessor, "GroupId.validate.semicolon");
				case '\\' : return new InvalidResult(ApplicationResources.accessor, "GroupId.validate.backslash");
				case '/' : return new InvalidResult(ApplicationResources.accessor, "GroupId.validate.slash");
				case '@' : return new InvalidResult(ApplicationResources.accessor, "GroupId.validate.at");
			}
		}
		return ValidResult.getInstance();
	}

	private static final ConcurrentMap<String,GroupId> interned = new ConcurrentHashMap<>();

	public static GroupId valueOf(String id) throws ValidationException {
		//GroupId existing = interned.get(id);
		//return existing!=null ? existing : new GroupId(id);
		return new GroupId(id);
	}

	final private String id;

	private GroupId(String id) throws ValidationException {
		this.id = id;
		validate();
	}

	private void validate() throws ValidationException {
		ValidationResult result = validate(id);
		if(!result.isValid()) throw new ValidationException(result);
	}

	/**
	 * Perform same validation as constructor on readObject.
	 */
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
