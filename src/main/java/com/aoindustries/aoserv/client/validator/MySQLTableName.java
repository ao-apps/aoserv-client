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
import com.aoindustries.validation.InvalidResult;
import com.aoindustries.validation.ValidResult;
import com.aoindustries.validation.ValidationException;
import com.aoindustries.validation.ValidationResult;
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
 *   <li>The rest of the characters may contain [a-z], [A-Z], [0-9], underscore (_), hyphen (-), or dollar ($)</li>
 * </ul>
 *
 * @author  AO Industries, Inc.
 */
final public class MySQLTableName implements
	Comparable<MySQLTableName>,
	Serializable,
	ObjectInputValidation,
	DtoFactory<com.aoindustries.aoserv.client.dto.MySQLTableName>
{

	private static final long serialVersionUID = -4427431696460618301L;

	/**
	 * The longest name allowed for a MySQL table name.
	 */
	public static final int MAX_LENGTH = 64;

	/**
	 * Validates a MySQL table name.
	 */
	public static ValidationResult validate(String name) {
		if(name==null) return new InvalidResult(ApplicationResources.accessor, "MySQLTableName.validate.isNull");
		int len = name.length();
		if(len==0) return new InvalidResult(ApplicationResources.accessor, "MySQLTableName.validate.isEmpty");
		if(len > MAX_LENGTH) return new InvalidResult(ApplicationResources.accessor, "MySQLTableName.validate.tooLong", MAX_LENGTH, len);

		// The first character must be [a-z], [A-Z], [0-9], or _
		char ch = name.charAt(0);
		if(
			   (ch < 'a' || ch > 'z')
			&& (ch < 'A' || ch > 'Z')
			&& (ch < '0' || ch > '9')
			&& ch != '_'
		) return new InvalidResult(ApplicationResources.accessor, "MySQLTableName.validate.badFirstCharacter");

		// The rest may have additional characters
		for (int c = 1; c < len; c++) {
			ch = name.charAt(c);
			if (
				   (ch<'a' || ch>'z')
				&& (ch<'A' || ch>'Z')
				&& (ch<'0' || ch>'9')
				&& ch != '_'
				&& ch != '-'
				&& ch != '$'
			) return new InvalidResult(ApplicationResources.accessor, "MySQLTableName.validate.illegalCharacter");
		}
		return ValidResult.getInstance();
	}

	/**
	 * @param name  when {@code null}, returns {@code null}
	 */
	public static MySQLTableName valueOf(String name) throws ValidationException {
		if(name == null) return null;
		return new MySQLTableName(name);
	}

	final private String name;

	private MySQLTableName(String name) throws ValidationException {
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
