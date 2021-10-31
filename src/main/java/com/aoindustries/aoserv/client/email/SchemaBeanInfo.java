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
package com.aoindustries.aoserv.client.email;

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
				new PropertyDescriptor("Address",             Schema.class, "getAddress",             null),
				new PropertyDescriptor("AttachmentBlock",     Schema.class, "getAttachmentBlock",     null),
				new PropertyDescriptor("AttachmentType",      Schema.class, "getAttachmentType",      null),
				new PropertyDescriptor("BlackholeAddress",    Schema.class, "getBlackholeAddress",    null),
				new PropertyDescriptor("CyrusImapdBind",      Schema.class, "getCyrusImapdBind",      null),
				new PropertyDescriptor("CyrusImapdServer",    Schema.class, "getCyrusImapdServer",    null),
				new PropertyDescriptor("Domain",              Schema.class, "getDomain",              null),
				new PropertyDescriptor("Forwarding",          Schema.class, "getForwarding",          null),
				new PropertyDescriptor("InboxAddress",        Schema.class, "getInboxAddress",        null),
				new PropertyDescriptor("List",                Schema.class, "getList",                null),
				new PropertyDescriptor("ListAddress",         Schema.class, "getListAddress",         null),
				new PropertyDescriptor("MajordomoList",       Schema.class, "getMajordomoList",       null),
				new PropertyDescriptor("MajordomoServer",     Schema.class, "getMajordomoServer",     null),
				new PropertyDescriptor("MajordomoVersion",    Schema.class, "getMajordomoVersion",    null),
				new PropertyDescriptor("Pipe",                Schema.class, "getPipe",                null),
				new PropertyDescriptor("PipeAddress",         Schema.class, "getPipeAddress",         null),
				new PropertyDescriptor("SendmailBind",        Schema.class, "getSendmailBind",        null),
				new PropertyDescriptor("SendmailServer",      Schema.class, "getSendmailServer",      null),
				new PropertyDescriptor("SmtpRelay",           Schema.class, "getSmtpRelay",           null),
				new PropertyDescriptor("SmtpRelayType",       Schema.class, "getSmtpRelayType",       null),
				new PropertyDescriptor("SmtpSmartHost",       Schema.class, "getSmtpSmartHost",       null),
				new PropertyDescriptor("SmtpSmartHostDomain", Schema.class, "getSmtpSmartHostDomain", null),
				new PropertyDescriptor("SpamAssassinMode",    Schema.class, "getSpamAssassinMode",    null),
				new PropertyDescriptor("SpamMessage",         Schema.class, "getSpamMessage",         null),
				new PropertyDescriptor("SystemAlias",         Schema.class, "getSystemAlias",         null),
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
