package com.aoindustries.aoserv.client;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  EmailSmtpSmartHost
 *
 * @author  AO Industries, Inc.
 */
public final class EmailSmtpSmartHostTable extends CachedTableIntegerKey<EmailSmtpSmartHost> {

    EmailSmtpSmartHostTable(AOServConnector connector) {
        super(connector, EmailSmtpSmartHost.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(EmailSmtpSmartHost.COLUMN_NET_BIND_name+'.'+NetBind.COLUMN_SERVER_name+'.'+Server.COLUMN_PACKAGE_name+'.'+Package.COLUMN_NAME_name, ASCENDING),
        new OrderBy(EmailSmtpSmartHost.COLUMN_NET_BIND_name+'.'+NetBind.COLUMN_SERVER_name+'.'+Server.COLUMN_NAME_name, ASCENDING),
        new OrderBy(EmailSmtpSmartHost.COLUMN_NET_BIND_name+'.'+NetBind.COLUMN_IP_ADDRESS_name+'.'+IPAddress.COLUMN_IP_ADDRESS_name, ASCENDING),
        new OrderBy(EmailSmtpSmartHost.COLUMN_NET_BIND_name+'.'+NetBind.COLUMN_IP_ADDRESS_name+'.'+IPAddress.COLUMN_NET_DEVICE_name+'.'+NetDevice.COLUMN_DEVICE_ID_name, ASCENDING),
        new OrderBy(EmailSmtpSmartHost.COLUMN_NET_BIND_name+'.'+NetBind.COLUMN_PORT_name, ASCENDING),
        new OrderBy(EmailSmtpSmartHost.COLUMN_NET_BIND_name+'.'+NetBind.COLUMN_NET_PROTOCOL_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public EmailSmtpSmartHost get(int pkey) throws IOException, SQLException {
    	return getUniqueRow(EmailSmtpSmartHost.COLUMN_NET_BIND, pkey);
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.EMAIL_SMTP_SMART_HOSTS;
    }
}