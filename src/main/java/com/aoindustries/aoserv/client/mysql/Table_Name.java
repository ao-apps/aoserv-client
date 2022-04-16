/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2010-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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
 * along with aoserv-client.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.aoindustries.aoserv.client.mysql;

import com.aoapps.lang.dto.DtoFactory;
import com.aoapps.lang.i18n.Resources;
import com.aoapps.lang.validation.InvalidResult;
import com.aoapps.lang.validation.ValidResult;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.lang.validation.ValidationResult;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ResourceBundle;

/**
 * Represents a name that may be used for a MySQL table.  Table names must:
 * <ul>
 *   <li>Be non-null</li>
 *   <li>Be non-empty</li>
 *   <li>Be between 1 and 64 characters</li>
 *   <li>Must start with <code>[a-z,A-Z,_]</code></li>
 *   <li>The rest of the characters may contain [a-z], [A-Z], [0-9], underscore (_), hyphen (-), or dollar ($)</li>
 * </ul>
 *
 * @author  AO Industries, Inc.
 */
// TODO: PostgreSQL type will be "Table.Name" - move to inner class of Table to match, once there is a "Table" class.
public final class Table_Name implements
	Comparable<Table_Name>,
	Serializable,
	DtoFactory<com.aoindustries.aoserv.client.dto.MySQLTableName>
{

	private static final Resources RESOURCES =
		Resources.getResources(ResourceBundle::getBundle, Table_Name.class.getPackage(), null, "Table.Name.");

	private static final long serialVersionUID = -4427431696460618301L;

	/**
	 * The longest name allowed for a MySQL table name.
	 */
	public static final int MAX_LENGTH = 64;

	/**
	 * Validates a MySQL table name.
	 */
	// TODO: Add other characters allowed in Database.Name, such as space
	public static ValidationResult validate(String name) {
		if(name==null) return new InvalidResult(RESOURCES, "validate.isNull");
		int len = name.length();
		if(len==0) return new InvalidResult(RESOURCES, "validate.isEmpty");
		if(len > MAX_LENGTH) return new InvalidResult(RESOURCES, "validate.tooLong", MAX_LENGTH, len);

		// The first character must be [a-z], [A-Z], [0-9], or _
		char ch = name.charAt(0);
		if(
			   (ch < 'a' || ch > 'z')
			&& (ch < 'A' || ch > 'Z')
			&& (ch < '0' || ch > '9')
			&& ch != '_'
		) return new InvalidResult(RESOURCES, "validate.badFirstCharacter");

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
			) return new InvalidResult(RESOURCES, "validate.illegalCharacter");
		}
		return ValidResult.getInstance();
	}

	/**
	 * @param name  when {@code null}, returns {@code null}
	 */
	public static Table_Name valueOf(String name) throws ValidationException {
		if(name == null) return null;
		return new Table_Name(name);
	}

	private final String name;

	private Table_Name(String name) throws ValidationException {
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
		try {
			validate();
		} catch(ValidationException err) {
			InvalidObjectException newErr = new InvalidObjectException(err.getMessage());
			newErr.initCause(err);
			throw newErr;
		}
	}

	@Override
	public boolean equals(Object obj) {
		return
			(obj instanceof Table_Name)
			&& name.equals(((Table_Name)obj).name)
		;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public int compareTo(Table_Name other) {
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
