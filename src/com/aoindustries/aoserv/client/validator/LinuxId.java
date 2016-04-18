/*
 * Copyright 2000-2013, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.validator;

import com.aoindustries.aoserv.client.AOServObject;
import com.aoindustries.aoserv.client.DtoFactory;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * Several resources on a <code>Server</code> require a server-wide
 * unique identifier.  All of the possible identifiers are represented
 * by <code>LinuxId</code>s.
 *
 * @author  AO Industries, Inc.
 */
final public class LinuxId implements
	Comparable<LinuxId>,
	Serializable,
	ObjectInputValidation,
	DtoFactory<com.aoindustries.aoserv.client.dto.LinuxId>
{

	private static final long serialVersionUID = -6222776271442175855L;

	public static ValidationResult validate(int id) {
		if(id<0) return new InvalidResult(ApplicationResources.accessor, "LinuxId.validate.lessThanZero", id);
		if(id>65535) return new InvalidResult(ApplicationResources.accessor, "LinuxId.validate.greaterThan64k", id);
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

	private LinuxId(int id) throws ValidationException {
		this.id=id;
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
		return this==other ? 0 : AOServObject.compare(id, other.id);
	}

	@Override
	public String toString() {
		return Integer.toString(id);
	}

	public int getId() {
		return id;
	}

	public boolean isSystem() {
		return id < 1000 || id==65534 || id==65535;
	}

	@Override
	public com.aoindustries.aoserv.client.dto.LinuxId getDto() {
		return new com.aoindustries.aoserv.client.dto.LinuxId(id);
	}
}
