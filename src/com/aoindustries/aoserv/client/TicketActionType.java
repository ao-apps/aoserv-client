package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

/**
 * All of the types of ticket changes are represented by these
 * <code>TicketActionType</code>s.
 *
 * @see TicketAction
 * @see Ticket
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class TicketActionType extends GlobalObjectStringKey<TicketActionType> {

    static final int COLUMN_TYPE = 0;

    static final String COLUMN_TYPE_name = "type";

    private boolean visible_admin_only;

    /* TODO: public static final String
        ADMIN_HOLD="AH",
        ADMIN_KILL="AK",
        ADMIN_PRIORITY_CHANGE="AP",
        ASSIGNED="AS",
        BOUNCED="BO",
        CLIENT_HOLD="CH",
        CLIENT_KILLED="CK",
        CLIENT_PRIORITY_CHANGE="CP",
        COMPLETE_TICKET="CO",
        OPEN_TICKET="OP",
        REACTIVATE_TICKET="RE",
        TECHNOLOGY_CHANGE="TC",
        DEADLINE_CHANGE="DL",
        SET_BUSINESS="SB",
        SET_CONTACT_EMAILS="SE",
        SET_CONTACT_PHONES="SP",
        TYPE_CHANGE="TY",
        WORK_ENTRY="WK"
    ;*/

    @Override
    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_TYPE: return pkey;
            case 1: return visible_admin_only;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.TICKET_ACTION_TYPES;
    }

    public String getType() {
        return pkey;
    }

    public void init(ResultSet result) throws SQLException {
        pkey = result.getString(1);
        visible_admin_only = result.getBoolean(2);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey = in.readUTF().intern();
        visible_admin_only = in.readBoolean();
    }

    @Override
    String toStringImpl() {
        return toString(Locale.getDefault());
    }

    /**
     * Localized description.
     */
    public String toString(Locale userLocale) {
        return ApplicationResourcesAccessor.getMessage(userLocale, "TicketActionType."+pkey+".toString");
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeUTF(pkey);
        out.writeBoolean(visible_admin_only);
    }
}