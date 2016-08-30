/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2010-2013, 2016  AO Industries, Inc.
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

import com.aoindustries.lang.LocalizedIllegalArgumentException;

/**
 * Thrown when internal object validation fails.
 *
 * @author  AO Industries, Inc.
 */
public class ValidationException extends Exception {

	private static final long serialVersionUID = -1153407618428602416L;

	final ValidationResult result;

	public ValidationException(ValidationResult result) {
		super(result.toString()); // Conversion done in server
		if(result.isValid()) throw new LocalizedIllegalArgumentException(ApplicationResources.accessor, "ValidationException.init.validResult");
		this.result = result;
	}

	public ValidationException(Throwable cause, ValidationResult result) {
		super(result.toString(), cause); // Conversion done in server
		if(result.isValid()) throw new LocalizedIllegalArgumentException(ApplicationResources.accessor, "ValidationException.init.validResult");
		this.result = result;
	}

	@Override
	public String getLocalizedMessage() {
		return result.toString(); // Conversion done in client
	}
}
