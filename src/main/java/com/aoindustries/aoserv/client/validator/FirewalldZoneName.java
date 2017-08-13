/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2017  AO Industries, Inc.
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
 * Represents a name that may be used for a Firewalld zone.  Zones names must:
 * <ul>
 *   <li>Be non-null</li>
 *   <li>Be non-empty</li>
 *   <li>Be between 1 and 17 characters</li>
 *   <li>Contain the characters [a-z], [A-Z], [0-9], underscore (_), hyphen (-), and slash (/)</li>
 *   <li>Not begin with a slash (/)</li>
 *   <li>Not end with a slash (/)</li>
 *   <li>Not contain more than one slash (/)</li>
 * </ul>
 * <p>
 *   We're unable to find well-defined rules for valid zone names.  The rules above are based on the source code
 *   for firewalld included with CentOS 7.
 * </p>
 * <ol>
 *   <li>See <code>/usr/lib/python2.7/site-packages/firewall/core/io/zone.py</code>, <code>check_name</code>.</li>
 *   <li>See <code>/usr/lib/python2.7/site-packages/firewall/core/io/io_object.py</code>, <code>check_name</code>.</li>
 *   <li>See <code>/usr/lib/python2.7/site-packages/firewall/functions.py</code>, <code>max_zone_name_len</code>.</li>
 * </ol>
 * <p>
 * Additionally, we tried creating a new zone with some UTF-8 characters, specifically Japanese,
 * and firewalld-cmd just stalled, not even responding to Ctrl-C.  We are implementing with a
 * strict ASCII-compatible definition of "alphanumeric".
 * </p>
 *
 * @author  AO Industries, Inc.
 */
final public class FirewalldZoneName implements
	Comparable<FirewalldZoneName>,
	Serializable,
	ObjectInputValidation,
	DtoFactory<com.aoindustries.aoserv.client.dto.FirewalldZoneName>,
	Internable<FirewalldZoneName>
{

	private static final long serialVersionUID = 1L;

	/**
	 * The longest name allowed for a Firewalld Zone.
	 */
	public static final int MAX_LENGTH = 17;

	/**
	 * Validates a Firewalld Zone name.
	 */
	public static ValidationResult validate(String name) {
		if(name == null) return new InvalidResult(ApplicationResources.accessor, "FirewalldZoneName.validate.isNull");
		int len = name.length();
		if(len == 0) return new InvalidResult(ApplicationResources.accessor, "FirewalldZoneName.validate.isEmpty");
		if(len > MAX_LENGTH) return new InvalidResult(ApplicationResources.accessor, "FirewalldZoneName.validate.tooLong", MAX_LENGTH, len);

		// Contain the characters [a-z], [A-Z], [0-9], underscore (_), hyphen (-), and slash (/)
		for (int c = 0; c < len; c++) {
			char ch = name.charAt(c);
			if (
				(ch<'a' || ch>'z')
				&& (ch < 'A' || ch > 'Z')
				&& (ch < '0' || ch > '9')
				&& ch != '_'
				&& ch != '-'
				&& ch != '/'
			) return new InvalidResult(ApplicationResources.accessor, "FirewalldZoneName.validate.illegalCharacter");
		}
		// Not begin with a slash (/)
		if(name.charAt(0) == '/') return new InvalidResult(ApplicationResources.accessor, "FirewalldZoneName.validate.startsWithSlash");
		// Not end with a slash (/)
		if(name.charAt(len - 1) == '/') return new InvalidResult(ApplicationResources.accessor, "FirewalldZoneName.validate.endsWithSlash");
		// Not contain more than one slash (/)
		int slashPos = name.indexOf('/');
		if(slashPos != -1 && name.indexOf('/', slashPos + 1) != -1) return new InvalidResult(ApplicationResources.accessor, "FirewalldZoneName.validate.moreThanOneSlash");
		return ValidResult.getInstance();
	}

	private static final ConcurrentMap<String,FirewalldZoneName> interned = new ConcurrentHashMap<>();

	/**
	 * @param name  when {@code null}, returns {@code null}
	 */
	public static FirewalldZoneName valueOf(String name) throws ValidationException {
		if(name == null) return null;
		//FirewalldZoneName existing = interned.get(name);
		//return existing!=null ? existing : new FirewalldZoneName(name);
		return new FirewalldZoneName(name);
	}

	final private String name;

	private FirewalldZoneName(String name) throws ValidationException {
		this.name = name;
		validate();
	}

	private void validate() throws ValidationException {
		ValidationResult result = validate(name);
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
			O != null
			&& O instanceof FirewalldZoneName
			&& name.equals(((FirewalldZoneName)O).name)
		;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public int compareTo(FirewalldZoneName other) {
		return (this == other) ? 0 : name.compareTo(other.name);
	}

	@Override
	public String toString() {
		return name;
	}

	/**
	 * Interns this name much in the same fashion as <code>String.intern()</code>.
	 *
	 * @see  String#intern()
	 */
	@Override
	public FirewalldZoneName intern() {
		try {
			FirewalldZoneName existing = interned.get(name);
			if(existing == null) {
				String internedName = name.intern();
				FirewalldZoneName addMe = (name == internedName) ? this : new FirewalldZoneName(internedName);
				existing = interned.putIfAbsent(internedName, addMe);
				if(existing == null) existing = addMe;
			}
			return existing;
		} catch(ValidationException err) {
			// Should not fail validation since original object passed
			throw new AssertionError(err.getMessage());
		}
	}

	@Override
	public com.aoindustries.aoserv.client.dto.FirewalldZoneName getDto() {
		return new com.aoindustries.aoserv.client.dto.FirewalldZoneName(name);
	}
}
