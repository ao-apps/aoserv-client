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

/**
 * A valid result singleton.
 *
 * @author  AO Industries, Inc.
 */
final class ValidResult implements ValidationResult {

	private static final long serialVersionUID = -5742207860354792003L;

	private static final ValidResult singleton = new ValidResult();

	static ValidResult getInstance() {
		return singleton;
	}

	private ValidResult() {
	}

	private Object readResolve() {
		return singleton;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public String toString() {
		return ApplicationResources.accessor.getMessage("ValidResult.toString");
	}
}
