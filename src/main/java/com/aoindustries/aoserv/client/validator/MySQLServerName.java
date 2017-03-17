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
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Represents a name that may be used for a MySQL installation.  Names must:
 * <ul>
 *   <li>Be non-null</li>
 *   <li>Be non-empty</li>
 *   <li>Be between 1 and 31 characters</li>
 *   <li>Must start with <code>[a-z]</code> or <code>[0-9]</code></li>
 *   <li>The rest of the characters may contain [a-z], [0-9], period (.), and underscore (_)</li>
 * </ul>
 *
 * @author  AO Industries, Inc.
 */
final public class MySQLServerName implements
	Comparable<MySQLServerName>,
	Serializable,
	ObjectInputValidation,
	DtoFactory<com.aoindustries.aoserv.client.dto.MySQLServerName>,
	Internable<MySQLServerName>
{

	private static final long serialVersionUID = 6148467549389988813L;

	public static final int MAX_LENGTH=255;

	/**
	 * Validates a MySQL server name.
	 */
	public static ValidationResult validate(String name) {
		if(name==null) return new InvalidResult(ApplicationResources.accessor, "MySQLServerName.validate.isNull");
		int len = name.length();
		if(len==0) return new InvalidResult(ApplicationResources.accessor, "MySQLServerName.validate.isEmpty");
		if(len > MAX_LENGTH) return new InvalidResult(ApplicationResources.accessor, "MySQLServerName.validate.tooLong", MAX_LENGTH, len);

		// The first character must be [a-z] or [0-9]
		char ch = name.charAt(0);
		if(
			(ch < 'a' || ch > 'z')
			&& (ch<'0' || ch>'9')
		) return new InvalidResult(ApplicationResources.accessor, "MySQLServerName.validate.startAtoZor0to9");

		// The rest may have additional characters
		for (int c = 1; c < len; c++) {
			ch = name.charAt(c);
			if (
				(ch<'a' || ch>'z')
				&& (ch<'0' || ch>'9')
				&& ch!='.'
				&& ch!='_'
			) return new InvalidResult(ApplicationResources.accessor, "MySQLServerName.validate.illegalCharacter");
		}
		return ValidResult.getInstance();
	}

	private static final ConcurrentMap<String,MySQLServerName> interned = new ConcurrentHashMap<>();

	public static MySQLServerName valueOf(String name) throws ValidationException {
		//MySQLServerName existing = interned.get(name);
		//return existing!=null ? existing : new MySQLServerName(name);
		return new MySQLServerName(name);
	}

	final private String name;

	private MySQLServerName(String name) throws ValidationException {
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
			O!=null
			&& O instanceof MySQLServerName
			&& name.equals(((MySQLServerName)O).name)
		;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public int compareTo(MySQLServerName other) {
		return this==other ? 0 : name.compareTo(other.name);
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
	public MySQLServerName intern() {
		try {
			MySQLServerName existing = interned.get(name);
			if(existing==null) {
				String internedName = name.intern();
				MySQLServerName addMe = name==internedName ? this : new MySQLServerName(internedName);
				existing = interned.putIfAbsent(internedName, addMe);
				if(existing==null) existing = addMe;
			}
			return existing;
		} catch(ValidationException err) {
			// Should not fail validation since original object passed
			throw new AssertionError(err.getMessage());
		}
	}

	@Override
	public com.aoindustries.aoserv.client.dto.MySQLServerName getDto() {
		return new com.aoindustries.aoserv.client.dto.MySQLServerName(name);
	}
}
