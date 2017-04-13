/*
 * aoserv-client - Java client for the AOServ Platform.
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
import com.aoindustries.util.ComparatorUtils;
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
 * Used for the various user-provided fields in the <code>/etc/passwd</code> file.
 *
 * {@link http://en.wikipedia.org/wiki/Gecos_field}
 *
 * @author  AO Industries, Inc.
 */
final public class Gecos implements
	Comparable<Gecos>,
	Serializable,
	ObjectInputValidation,
	DtoFactory<com.aoindustries.aoserv.client.dto.Gecos>,
	Internable<Gecos> {

	private static final long serialVersionUID = -117164942375352467L;

	public static final int MAX_LENGTH = 100;

	/**
	 * Determines if a name can be used as a GECOS field.  A GECOS field
	 * is valid if it is between 1 and 100 characters in length and uses only
	 * <code>[a-z,A-Z,0-9,-,_,@, ,.,#,=,/,$,%,^,&,*,(,),?,']</code> for each
	 * character.<br>
	 * <br>
	 * Refer to <code>man 5 passwd</code>
	 */
	public static ValidationResult validate(String value) {
		// Be non-null
		if(value==null) return new InvalidResult(ApplicationResources.accessor, "Gecos.validate.isNull");
		int len = value.length();
		if(len==0) return new InvalidResult(ApplicationResources.accessor, "Gecos.validate.isEmpty");
		if(len>MAX_LENGTH) return new InvalidResult(ApplicationResources.accessor, "Gecos.validate.tooLong", MAX_LENGTH, len);

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
			) return new InvalidResult(ApplicationResources.accessor, "Gecos.validate.invalidCharacter", ch);
		}
		return ValidResult.getInstance();
	}

	private static final ConcurrentMap<String,Gecos> interned = new ConcurrentHashMap<>();

	/**
	 * @param value  when {@code null}, returns {@code null}
	 */
	public static Gecos valueOf(String value) throws ValidationException {
		if(value == null) return null;
		//Gecos existing = interned.get(value);
		//return existing!=null ? existing : new Gecos(value);
		return new Gecos(value);
	}

	final private String value;

	private Gecos(String value) throws ValidationException {
		this.value = value;
		validate();
	}

	private void validate() throws ValidationException {
		ValidationResult result = validate(value);
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
			&& O instanceof Gecos
			&& value.equals(((Gecos)O).value)
		;
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

	@Override
	public int compareTo(Gecos other) {
		return this==other ? 0 : ComparatorUtils.compareIgnoreCaseConsistentWithEquals(value, other.value);
	}

	@Override
	public String toString() {
		return value;
	}

	/**
	 * Interns this id much in the same fashion as <code>String.intern()</code>.
	 *
	 * @see  String#intern()
	 */
	@Override
	public Gecos intern() {
		try {
			Gecos existing = interned.get(value);
			if(existing==null) {
				String internedValue = value.intern();
				Gecos addMe = value==internedValue ? this : new Gecos(internedValue); // Using identity String comparison to see if already interned
				existing = interned.putIfAbsent(internedValue, addMe);
				if(existing==null) existing = addMe;
			}
			return existing;
		} catch(ValidationException err) {
			// Should not fail validation since original object passed
			throw new AssertionError(err.getMessage());
		}
	}

	@Override
	public com.aoindustries.aoserv.client.dto.Gecos getDto() {
		return new com.aoindustries.aoserv.client.dto.Gecos(value);
	}
}
