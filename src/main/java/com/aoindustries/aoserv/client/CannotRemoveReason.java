/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2004-2009, 2016, 2017, 2020, 2021  AO Industries, Inc.
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
package com.aoindustries.aoserv.client;

import java.util.Collections;
import java.util.List;

/**
 * Encapsulates a reason and optional dependent object.
 *
 * @see Removable
 *
 * @author  AO Industries, Inc.
 */
public final class CannotRemoveReason<T extends AOServObject<?, ? extends T>> {

	private final String reason;
	private final List<T> dependentObjects;

	public CannotRemoveReason(String reason) {
		this.reason=reason;
		this.dependentObjects=null;
	}

	public CannotRemoveReason(String reason, T dependentObject) {
		this.reason=reason;
		this.dependentObjects=dependentObject==null?null:Collections.singletonList(dependentObject);
	}

	/**
	 * @param  dependentObjects  No defensive copy is made
	 */
	public CannotRemoveReason(String reason, List<T> dependentObjects) {
		this.reason=reason;
		this.dependentObjects = Collections.unmodifiableList(dependentObjects);
	}

	public String getReason() {
		return reason;
	}

	@SuppressWarnings("ReturnOfCollectionOrArrayField") // Returning unmodifiable
	public List<T> getDependentObjects() {
		return dependentObjects;
	}
}
