/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2013, 2016, 2017, 2018, 2019, 2020  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.linux;

import com.aoindustries.dto.DtoFactory;
import com.aoindustries.i18n.Resources;
import com.aoindustries.validation.InvalidResult;
import com.aoindustries.validation.ValidResult;
import com.aoindustries.validation.ValidationException;
import com.aoindustries.validation.ValidationResult;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * Several resources on a {@link Server} require a server-wide
 * unique identifier.  All of the possible identifiers are represented
 * by {@link LinuxId}.
 *
 * @author  AO Industries, Inc.
 */
final public class LinuxId implements
	Comparable<LinuxId>,
	Serializable,
	ObjectInputValidation,
	DtoFactory<com.aoindustries.aoserv.client.dto.LinuxId>
{

	private static final Resources RESOURCES = Resources.getResources(LinuxId.class.getPackage());

	private static final long serialVersionUID = -6222776271442175855L;

	public static ValidationResult validate(int id) {
		if(id<0) return new InvalidResult(RESOURCES, "LinuxId.validate.lessThanZero", id);
		if(id>65535) return new InvalidResult(RESOURCES, "LinuxId.validate.greaterThan64k", id);
		return ValidResult.getInstance();
	}

	private static final AtomicReferenceArray<LinuxId> cache = new AtomicReferenceArray<>(65536);

	public static LinuxId valueOf(int id) throws ValidationException {
		ValidationResult result = validate(id);
		if(!result.isValid()) throw new ValidationException(result);
		LinuxId linuxId = cache.get(id);
		if(linuxId==null) {
			linuxId = new LinuxId(id);
			if(!cache.compareAndSet(id, null, linuxId)) linuxId = cache.get(id);
		}
		return linuxId;
	}

	final private int id;

	/**
	 * @param  id  Does not validate, should only be used with a known valid value.
	 */
	private LinuxId(int id) {
		ValidationResult result;
		assert (result = validate(id)).isValid() : result.toString();
		this.id=id;
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

	private Object readResolve() throws InvalidObjectException {
		try {
			return valueOf(id);
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
			&& O instanceof LinuxId
			&& ((LinuxId)O).id==id
		;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public int compareTo(LinuxId other) {
		return this==other ? 0 : Integer.compare(id, other.id);
	}

	@Override
	public String toString() {
		return Integer.toString(id);
	}

	public int getId() {
		return id;
	}

	@Override
	public com.aoindustries.aoserv.client.dto.LinuxId getDto() {
		return new com.aoindustries.aoserv.client.dto.LinuxId(id);
	}
}
