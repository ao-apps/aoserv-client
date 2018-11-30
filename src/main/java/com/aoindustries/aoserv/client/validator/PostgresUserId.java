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

import com.aoindustries.aoserv.client.postgresql.Server;
import com.aoindustries.io.FastExternalizable;
import com.aoindustries.validation.InvalidResult;
import com.aoindustries.validation.ValidResult;
import com.aoindustries.validation.ValidationException;
import com.aoindustries.validation.ValidationResult;
import java.io.ObjectInputValidation;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Represents a PostgreSQL user ID.  PostgreSQL user ids must:
 * <ul>
 *   <li>Be non-null</li>
 *   <li>Be non-empty</li>
 *   <li>Be between 1 and 31 characters</li>
 *   <li>Must start with <code>[a-z]</code></li>
 *   <li>The rest of the characters may contain [a-z], [0-9], and underscore (_)</li>
 *   <li>Must not be a PostgreSQL reserved word</li>
 *   <li>Must be a valid <code>UserId</code> - this is implied by the above rules</li>
 * </ul>
 *
 * @author  AO Industries, Inc.
 */
final public class PostgresUserId extends UserId implements
	FastExternalizable,
	ObjectInputValidation
{

	/**
	 * The maximum length of a PostgreSQL username.
	 */
	public static final int MAX_LENGTH = 31;

	/**
	 * Validates a PostgreSQL user id.
	 */
	public static ValidationResult validate(String id) {
		if(id==null) return new InvalidResult(ApplicationResources.accessor, "PostgresUserId.validate.isNull");
		int len = id.length();
		if(len==0) return new InvalidResult(ApplicationResources.accessor, "PostgresUserId.validate.isEmpty");
		if(len > MAX_LENGTH) return new InvalidResult(ApplicationResources.accessor, "PostgresUserId.validate.tooLong", MAX_LENGTH, len);

		// The first character must be [a-z] or [0-9]
		char ch = id.charAt(0);
		if(
			(ch < 'a' || ch > 'z')
			&& (ch<'0' || ch>'9')
		) return new InvalidResult(ApplicationResources.accessor, "PostgresUserId.validate.startAtoZor0to9");

		// The rest may have additional characters
		for (int c = 1; c < len; c++) {
			ch = id.charAt(c);
			if (
				(ch<'a' || ch>'z')
				&& (ch<'0' || ch>'9')
				&& ch!='_'
			) return new InvalidResult(ApplicationResources.accessor, "PostgresUserId.validate.illegalCharacter");
		}
		if(
			id.equals("sameuser")
			|| id.equals("samegroup")
			|| id.equals("all")
			|| Server.ReservedWord.isReservedWord(id)
		) return new InvalidResult(ApplicationResources.accessor, "PostgresUserId.validate.reservedWord");
		assert UserId.validate(id).isValid() : "A PostgresUserId is always a valid UserId.";
		return ValidResult.getInstance();
	}

	private static final ConcurrentMap<String,PostgresUserId> interned = new ConcurrentHashMap<>();

	/**
	 * @param id  when {@code null}, returns {@code null}
	 */
	public static PostgresUserId valueOf(String id) throws ValidationException {
		if(id == null) return null;
		//PostgresUserId existing = interned.get(id);
		//return existing!=null ? existing : new PostgresUserId(id);
		return new PostgresUserId(id, true);
	}

	private PostgresUserId(String id, boolean validate) throws ValidationException {
		super(id, validate);
	}

	/**
	 * @param  id  Does not validate, should only be used with a known valid value.
	 */
	private PostgresUserId(String id) {
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
	public PostgresUserId intern() {
		PostgresUserId existing = interned.get(id);
		if(existing==null) {
			String internedId = id.intern();
			PostgresUserId addMe = (id == internedId) ? this : new PostgresUserId(internedId);
			existing = interned.putIfAbsent(internedId, addMe);
			if(existing==null) existing = addMe;
		}
		return existing;
	}

	@Override
	public com.aoindustries.aoserv.client.dto.PostgresUserId getDto() {
		return new com.aoindustries.aoserv.client.dto.PostgresUserId(id);
	}

	// <editor-fold defaultstate="collapsed" desc="FastExternalizable">
	private static final long serialVersionUID = 2L;

	public PostgresUserId() {
	}

	@Override
	public long getSerialVersionUID() {
		return serialVersionUID;
	}
	// </editor-fold>
}
