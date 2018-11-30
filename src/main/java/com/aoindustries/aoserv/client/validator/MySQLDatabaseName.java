/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2010-2013, 2016, 2017, 2018  AO Industries, Inc.
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

import com.aoindustries.aoserv.client.mysql.MySQLServer;
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
 * Represents a name that may be used for a MySQL database.  Database names must:
 * <ul>
 *   <li>Be non-null</li>
 *   <li>Be non-empty</li>
 *   <li>Be between 1 and 64 characters</li>
 *   <li>Must start with <code>[a-z], [A-Z], or [0-9]</code></li>
 *   <li>The rest of the characters may contain [a-z], [A-Z], [0-9], and underscore (_)</li>
 *   <li>Must not be a MySQL reserved word</li>
 * </ul>
 *
 * @author  AO Industries, Inc.
 */
final public class MySQLDatabaseName implements
	Comparable<MySQLDatabaseName>,
	Serializable,
	ObjectInputValidation,
	DtoFactory<com.aoindustries.aoserv.client.dto.MySQLDatabaseName>,
	Internable<MySQLDatabaseName>
{

	private static final long serialVersionUID = 1495532864586195961L;

	/**
	 * The longest name allowed for a MySQL database.
	 */
	public static final int MAX_LENGTH = 64;

	/**
	 * Validates a MySQL database name.
	 */
	public static ValidationResult validate(String name) {
		if(name==null) return new InvalidResult(ApplicationResources.accessor, "MySQLDatabaseName.validate.isNull");
		int len = name.length();
		if(len==0) return new InvalidResult(ApplicationResources.accessor, "MySQLDatabaseName.validate.isEmpty");
		if(len > MAX_LENGTH) return new InvalidResult(ApplicationResources.accessor, "MySQLDatabaseName.validate.tooLong", MAX_LENGTH, len);

		// The first character must be [a-z],  or [0-9]
		char ch = name.charAt(0);
		if(
			(ch < 'a' || ch > 'z')
			&& (ch < 'A' || ch > 'Z')
			&& (ch < '0' || ch > '9')
		) return new InvalidResult(ApplicationResources.accessor, "MySQLDatabaseName.validate.startAtoZor0to9");

		// The rest may have additional characters
		for (int c = 1; c < len; c++) {
			ch = name.charAt(c);
			if (
				(ch<'a' || ch>'z')
				&& (ch < 'A' || ch > 'Z')
				&& (ch < '0' || ch > '9')
				&& ch != '_'
			) return new InvalidResult(ApplicationResources.accessor, "MySQLDatabaseName.validate.illegalCharacter");
		}
		if(MySQLServer.ReservedWord.isReservedWord(name)) return new InvalidResult(ApplicationResources.accessor, "MySQLDatabaseName.validate.reservedWord");
		return ValidResult.getInstance();
	}

	private static final ConcurrentMap<String,MySQLDatabaseName> interned = new ConcurrentHashMap<>();

	/**
	 * @param name  when {@code null}, returns {@code null}
	 */
	public static MySQLDatabaseName valueOf(String name) throws ValidationException {
		if(name == null) return null;
		//MySQLDatabaseName existing = interned.get(name);
		//return existing!=null ? existing : new MySQLDatabaseName(name);
		return new MySQLDatabaseName(name, true);
	}

	final private String name;

	private MySQLDatabaseName(String name, boolean validate) throws ValidationException {
		this.name = name;
		if(validate) validate();
	}

	/**
	 * @param  name  Does not validate, should only be used with a known valid value.
	 */
	private MySQLDatabaseName(String name) {
		ValidationResult result;
		assert (result = validate(name)).isValid() : result.toString();
		this.name = name;
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
			&& O instanceof MySQLDatabaseName
			&& name.equals(((MySQLDatabaseName)O).name)
		;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public int compareTo(MySQLDatabaseName other) {
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
	public MySQLDatabaseName intern() {
		MySQLDatabaseName existing = interned.get(name);
		if(existing==null) {
			String internedName = name.intern();
			MySQLDatabaseName addMe = (name == internedName) ? this : new MySQLDatabaseName(internedName);
			existing = interned.putIfAbsent(internedName, addMe);
			if(existing==null) existing = addMe;
		}
		return existing;
	}

	@Override
	public com.aoindustries.aoserv.client.dto.MySQLDatabaseName getDto() {
		return new com.aoindustries.aoserv.client.dto.MySQLDatabaseName(name);
	}
}
