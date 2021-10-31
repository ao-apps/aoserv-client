/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2018, 2020, 2021  AO Industries, Inc.
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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class SchemaBeanInfo extends SimpleBeanInfo {

	private static final PropertyDescriptor[] properties;
	static {
		try {
			properties = new PropertyDescriptor[] {
				new PropertyDescriptor("DaemonAcl",   Schema.class, "getDaemonAcl",   null),
				new PropertyDescriptor("Group",       Schema.class, "getGroup",       null),
				new PropertyDescriptor("GroupServer", Schema.class, "getGroupServer", null),
				new PropertyDescriptor("GroupType",   Schema.class, "getGroupType",   null),
				new PropertyDescriptor("GroupUser",   Schema.class, "getGroupUser",   null),
				new PropertyDescriptor("Server",      Schema.class, "getServer",      null),
				new PropertyDescriptor("Shell",       Schema.class, "getShell",       null),
				new PropertyDescriptor("TimeZone",    Schema.class, "getTimeZone",    null),
				new PropertyDescriptor("User",        Schema.class, "getUser",        null),
				new PropertyDescriptor("UserServer",  Schema.class, "getUserServer",  null),
				new PropertyDescriptor("UserType",    Schema.class, "getUserType",    null),
			};
		} catch(IntrospectionException err) {
			throw new ExceptionInInitializerError(err);
		}
	}

	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField") // Not copying array for performance
	public PropertyDescriptor[] getPropertyDescriptors () {
		return properties;
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
