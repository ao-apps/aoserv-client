/*
 * Copyright 2004-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
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
public final class CannotRemoveReason<T extends AOServObject> {

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

	public CannotRemoveReason(String reason, List<T> dependentObjects) {
		this.reason=reason;
		this.dependentObjects=dependentObjects;
	}

	public String getReason() {
		return reason;
	}

	public List<T> getDependentObjects() {
		return dependentObjects;
	}
}
