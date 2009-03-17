package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.StringUtility;
import java.io.*;
import java.sql.*;

/**
 * Each <code>Ticket</code> is of a specific <code>TicketType</code>.
 *
 * @see  Ticket
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class TicketType extends GlobalObjectStringKey<TicketType> {

    static final int COLUMN_TYPE=0;
    static final String COLUMN_TYPE_name = "type";

    private String description;
    boolean client_view;

    /**
     * The types of <code>Ticket</code>s.
     */
    public static final String
        NONE="",
        ACCOUNTING="Accounting",
        AOSERV="AOServ",
        CGI="CGI",
        CVS="CVS",
        CONTROL_PANEL="Control Panel",
        DNS="DNS/Host Names",
        JAVA_VIRTUAL_MACHINE="Java Virtual Machine",
        EMAIL="Email",
        INTERBASE="InterBase",
        LOGGING="Logging/Stats",
        NOTE_INSTALL_NOTE="NOTE: Install Note",
        MYSQL="MySQL",
        PERFORMANCE="Performance",
        PHP="PHP",
        POSTGRESQL="PostgreSQL",
        SHELL_ACCOUNT="Shell Account",
        TICKETS="Tickets",
        WEBSITES="Websites",
        XML="XML",
        TODO_EVENT="TODO: Event",
        TODO_HARDWARE="TODO: Hardware",
        TODO_MONITORING="TODO: Monitoring",
        TODO_PAYMENT="TODO: Payment",
        TODO_RELIABILITY="TODO: Reliability",
        TODO_SECURITY="TODO: Security",
        TODO_SUPPORT_TOOLS="TODO: Support Tools"
    ;

    Object getColumnImpl(int i) {
	if(i==COLUMN_TYPE) return pkey;
	if(i==1) return description;
	if(i==2) return client_view?Boolean.TRUE:Boolean.FALSE;
	throw new IllegalArgumentException("Invalid index: "+i);
    }

    public String getDescription() {
	return description;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.TICKET_TYPES;
    }

    public String getType() {
	return pkey;
    }

    public void init(ResultSet result) throws SQLException {
	pkey = result.getString(1);
	description = result.getString(2);
	client_view = result.getBoolean(3);
    }

    public boolean isClientViewable() {
	return client_view;
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readUTF().intern();
	description=in.readUTF();
	client_view=in.readBoolean();
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
	out.writeUTF(pkey);
	out.writeUTF(description);
	out.writeBoolean(client_view);
    }
}