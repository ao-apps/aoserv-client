/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2011-2013, 2016  AO Industries, Inc.
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

import java.io.Serializable;

/**
 * For higher performance when validating objects fails, a validator result
 * is returned from the core validation routines instead of immediately
 * throwing ValidationException.  Methods that automatically perform validation,
 * including constructors, will throw ValidationException when needed.
 *
 * @author  AO Industries, Inc.
 */
public interface ValidationResult extends Serializable {

	/**
	 * Gets the validation result.
	 */
	boolean isValid();

	/**
	 * Gets a description of why invalid in the current thread's locale.
	 * Should be simply "Valid" (or translation) for valid.
	 */
	@Override
	String toString();
}
