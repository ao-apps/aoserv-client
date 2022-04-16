/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2010-2013, 2016, 2017, 2018, 2020, 2021, 2022  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.linux;

import com.aoapps.lang.dto.DtoFactory;
import com.aoapps.lang.i18n.Resources;
import com.aoapps.lang.util.ComparatorUtils;
import com.aoapps.lang.util.Internable;
import com.aoapps.lang.validation.InvalidResult;
import com.aoapps.lang.validation.ValidResult;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.lang.validation.ValidationResult;
import com.aoapps.net.Path;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ResourceBundle;
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
public final class PosixPath implements
	Comparable<PosixPath>,
	Serializable,
	DtoFactory<com.aoindustries.aoserv.client.dto.PosixPath>,
	Internable<PosixPath>
{

	private static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, PosixPath.class);

	private static final long serialVersionUID = -4832121065303689152L;

	public static ValidationResult validate(String path) {
		// Be non-null
		if(path==null) return new InvalidResult(RESOURCES, "validate.isNull");
		// Be non-empty
		if(path.length()==0) return new InvalidResult(RESOURCES, "validate.empty");
		// Start with a /
		char ch;
		if((ch = path.charAt(0))!='/') return new InvalidResult(RESOURCES, "validate.startWithNonSlash", ch);
		// Not contain any null characters
		int i;
		if((i = path.indexOf('\0'))!=-1) return new InvalidResult(RESOURCES, "validate.containsNullCharacter", i);
		// Not contain any /../ or /./ path elements
		if((i = path.indexOf("/../"))!=-1) return new InvalidResult(RESOURCES, "validate.containsDotDot", i);
		if((i = path.indexOf("/./"))!=-1) return new InvalidResult(RESOURCES, "validate.containsDot", i);
		// Not end with / unless "/"
		if(path.length()>1 && path.endsWith("/")) return new InvalidResult(RESOURCES, "validate.endsSlash");
		// Not end with /.. or /.
		if(path.endsWith("/.")) return new InvalidResult(RESOURCES, "validate.endsSlashDot");
		if(path.endsWith("/..")) return new InvalidResult(RESOURCES, "validate.endsSlashDotDot");
		// Not contain any // in the path
		if((i = path.indexOf("//"))!=-1) return new InvalidResult(RESOURCES, "validate.containsDoubleSlash", i);
		return ValidResult.getInstance();
	}

	private static final ConcurrentMap<String, PosixPath> interned = new ConcurrentHashMap<>();

	/**
	 * @param path  when {@code null}, returns {@code null}
	 */
	public static PosixPath valueOf(String path) throws ValidationException {
		if(path == null) return null;
		//PosixPath existing = interned.get(path);
		//return existing!=null ? existing : new PosixPath(path);
		return new PosixPath(path, true);
	}

	private final String path;

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
			(obj instanceof PosixPath)
			&& path.equals(((PosixPath)obj).path)
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
			@SuppressWarnings("StringEquality")
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
