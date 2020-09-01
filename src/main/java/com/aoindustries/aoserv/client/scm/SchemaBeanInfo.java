/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2018, 2020  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.scm;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class SchemaBeanInfo extends SimpleBeanInfo {

	@SuppressWarnings("VolatileArrayField")
	private static volatile PropertyDescriptor[] properties;

	@Override
	public PropertyDescriptor[] getPropertyDescriptors () {
		try {
			PropertyDescriptor[] props = properties;
			if(props == null) {
				props = new PropertyDescriptor[] {
					new PropertyDescriptor("CvsRepository", Schema.class, "getCvsRepository", null),
				};
				properties = props;
			}
			return props; // Not copying array for performance
		} catch(IntrospectionException err) {
			throw new AssertionError(err);
		}
	}

	/**
	 * Include base class.
	 */
	@Override
	public BeanInfo[] getAdditionalBeanInfo() {
		try {
			return new BeanInfo[] {
				Introspector.getBeanInfo(Schema.class.getSuperclass())
			};
		} catch(IntrospectionException err) {
			throw new AssertionError(err);
		}
	}
}
