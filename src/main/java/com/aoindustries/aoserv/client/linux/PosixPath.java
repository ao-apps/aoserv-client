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
package com.aoindustries.aoserv.client.linux;

import static com.aoindustries.aoserv.client.linux.ApplicationResources.accessor;
import com.aoindustries.dto.DtoFactory;
import com.aoindustries.net.Path;
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
 * Represents a full path in POSIX style.  Paths must:
 * <ul>
 *   <li>Be non-null</li>
 *   <li>Be non-empty</li>
 *   <li>Start with a <code>/</code></li>
 *   <li>Not contain any null characters</li>
 *   <li>Not contain any /../ or /./ path elements</li>
 *   <li>Not end with / unless "/"</li>
 *   <li>Not end with /.. or /.</li>
 *   <li>Not contain any // in the path</li>
 * </ul>
 * <p>
 * TODO: This matches {@link Path} with the exception of disallowing trailing slash except for "/".
 *       Remove this redundancy?  Subclass {@link Path}?
 * </p>
 *
 * @author  AO Industries, Inc.
 */
// TODO: Move to an ao-posix-types package, similar to ao-net-types
final public class PosixPath implements
	Comparable<PosixPath>,
	Serializable,
	ObjectInputValidation,
	DtoFactory<com.aoindustries.aoserv.client.dto.PosixPath>,
	Internable<PosixPath>
{

	private static final long serialVersionUID = -4832121065303689152L;

	public static ValidationResult validate(String path) {
		// Be non-null
		if(path==null) return new InvalidResult(accessor, "PosixPath.validate.isNull");
		// Be non-empty
		if(path.length()==0) return new InvalidResult(accessor, "PosixPath.validate.empty");
		// Start with a /
		if(path.charAt(0)!='/') return new InvalidResult(accessor, "PosixPath.validate.startWithNonSlash", path.charAt(0));
		// Not contain any null characters
		if(path.indexOf('\0')!=-1) return new InvalidResult(accessor, "PosixPath.validate.containsNullCharacter", path.indexOf('\0'));
		// Not contain any /../ or /./ path elements
		if(path.indexOf("/../")!=-1) return new InvalidResult(accessor, "PosixPath.validate.containsDotDot", path.indexOf("/../"));
		if(path.indexOf("/./")!=-1) return new InvalidResult(accessor, "PosixPath.validate.containsDot", path.indexOf("/./"));
		// Not end with / unless "/"
		if(path.length()>1 && path.endsWith("/")) return new InvalidResult(accessor, "PosixPath.validate.endsSlash");
		// Not end with /.. or /.
		if(path.endsWith("/.")) return new InvalidResult(accessor, "PosixPath.validate.endsSlashDot");
		if(path.endsWith("/..")) return new InvalidResult(accessor, "PosixPath.validate.endsSlashDotDot");
		// Not contain any // in the path
		if(path.indexOf("//")!=-1) return new InvalidResult(accessor, "PosixPath.validate.containsDoubleSlash", path.indexOf("//"));
		return ValidResult.getInstance();
	}

	private static final ConcurrentMap<String,PosixPath> interned = new ConcurrentHashMap<>();

	/**
	 * @param path  when {@code null}, returns {@code null}
	 */
	public static PosixPath valueOf(String path) throws ValidationException {
		if(path == null) return null;
		//PosixPath existing = interned.get(path);
		//return existing!=null ? existing : new PosixPath(path);
		return new PosixPath(path, true);
	}

	final private String path;

	private PosixPath(String path, boolean validate) throws ValidationException {
		this.path = path;
		if(validate) validate();
	}

	/**
	 * @param  path  Does not validate, should only be used with a known valid value.
	 */
	private PosixPath(String path) {
		ValidationResult result;
		assert (result = validate(path)).isValid() : result.toString();
		this.path = path;
	}

	private void validate() throws ValidationException {
		ValidationResult result = validate(path);
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
			&& O instanceof PosixPath
			&& path.equals(((PosixPath)O).path)
		;
	}

	@Override
	public int hashCode() {
		return path.hashCode();
	}

	@Override
	public int compareTo(PosixPath other) {
		return this==other ? 0 : ComparatorUtils.compareIgnoreCaseConsistentWithEquals(path, other.path);
	}

	@Override
	public String toString() {
		return path;
	}

	/**
	 * Interns this path much in the same fashion as <code>String.intern()</code>.
	 *
	 * @see  String#intern()
	 */
	@Override
	public PosixPath intern() {
		PosixPath existing = interned.get(path);
		if(existing==null) {
			String internedPath = path.intern();
			PosixPath addMe = (path == internedPath) ? this : new PosixPath(internedPath);
			existing = interned.putIfAbsent(internedPath, addMe);
			if(existing==null) existing = addMe;
		}
		return existing;
	}

	@Override
	public com.aoindustries.aoserv.client.dto.PosixPath getDto() {
		return new com.aoindustries.aoserv.client.dto.PosixPath(path);
	}

	// TODO: subPath, prefix, suffix matching Path except watch for trailing slash
}