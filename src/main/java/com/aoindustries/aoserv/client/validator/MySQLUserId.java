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

import com.aoindustries.aoserv.client.MySQLServer;
import com.aoindustries.io.FastExternalizable;
import com.aoindustries.validation.InvalidResult;
import com.aoindustries.validation.ValidResult;
import com.aoindustries.validation.ValidationException;
import com.aoindustries.validation.ValidationResult;
import java.io.ObjectInputValidation;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Represents a MySQL user ID.  MySQL user ids must:
 * <ul>
 *   <li>Be non-null</li>
 *   <li>Be non-empty</li>
 *   <li>Be between 1 and 16 characters</li>
 *   <li>Must start with <code>[a-z]</code></li>
 *   <li>The rest of the characters may contain [a-z], [0-9], and underscore (_)</li>
 *   <li>A special exemption is made for the <code>mysql.session</code> and <code>mysql.sys</code> reserved users added in MySQL 5.7.</li>
 *   <li>Must not be a MySQL reserved word</li>
 *   <li>Must be a valid <code>UserId</code> - this is implied by the above rules</li>
 * </ul>
 *
 * @author  AO Industries, Inc.
 */
final public class MySQLUserId extends UserId implements
	FastExternalizable,
	ObjectInputValidation
{

	/**
	 * The maximum length of a MySQL username.
	 */
	public static final int MAX_LENGTH = 31;

	/**
	 * Validates a MySQL user id.
	 */
	public static ValidationResult validate(String id) {
		if(id==null) return new InvalidResult(ApplicationResources.accessor, "MySQLUserId.validate.isNull");
		if(
			// Allow specific system users that otherwise do not match our allowed username pattern
			!"mysql.sys".equals(id)
			&& !"mysql.session".equals(id)
		) {
			int len = id.length();
			if(len==0) return new InvalidResult(ApplicationResources.accessor, "MySQLUserId.validate.isEmpty");
			if(len > MAX_LENGTH) return new InvalidResult(ApplicationResources.accessor, "MySQLUserId.validate.tooLong", MAX_LENGTH, len);

			// The first character must be [a-z] or [0-9]
			char ch = id.charAt(0);
			if(
				(ch < 'a' || ch > 'z')
				&& (ch<'0' || ch>'9')
			) return new InvalidResult(ApplicationResources.accessor, "MySQLUserId.validate.startAtoZor0to9");

			// The rest may have additional characters
			for (int c = 1; c < len; c++) {
				ch = id.charAt(c);
				if (
					(ch<'a' || ch>'z')
					&& (ch<'0' || ch>'9')
					&& ch!='_'
				) return new InvalidResult(ApplicationResources.accessor, "MySQLUserId.validate.illegalCharacter");
			}
			if(MySQLServer.ReservedWord.isReservedWord(id)) return new InvalidResult(ApplicationResources.accessor, "MySQLUserId.validate.reservedWord");
		}
		assert UserId.validate(id).isValid() : "A MySQLUserId is always a valid UserId.";
		return ValidResult.getInstance();
	}

	private static final ConcurrentMap<String,MySQLUserId> interned = new ConcurrentHashMap<>();

	/**
	 * @param id  when {@code null}, returns {@code null}
	 */
	public static MySQLUserId valueOf(String id) throws ValidationException {
		if(id == null) return null;
		//MySQLUserId existing = interned.get(id);
		//return existing!=null ? existing : new MySQLUserId(id);
		return new MySQLUserId(id);
	}

	private MySQLUserId(String id) throws ValidationException {
		super(id);
	}

	@Override
	void validate() throws ValidationException {
		ValidationResult result = validate(id);
		if(!result.isValid()) throw new ValidationException(result);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MySQLUserId intern() {
		try {
			MySQLUserId existing = interned.get(id);
			if(existing==null) {
				String internedId = id.intern();
				MySQLUserId addMe = id==internedId ? this : new MySQLUserId(internedId);
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
	public com.aoindustries.aoserv.client.dto.MySQLUserId getDto() {
		return new com.aoindustries.aoserv.client.dto.MySQLUserId(id);
	}

	// <editor-fold defaultstate="collapsed" desc="FastExternalizable">
	private static final long serialVersionUID = 2L;

	public MySQLUserId() {
	}

	@Override
	public long getSerialVersionUID() {
		return serialVersionUID;
	}
	// </editor-fold>
}
