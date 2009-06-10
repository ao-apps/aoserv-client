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

    public static final String
        SET_BUSINESS="set_business",
        SET_CONTACT_EMAILS="set_contact_emails",
        SET_CONTACT_PHONE_NUMBERS="set_contact_phone_numbers",
        SET_CLIENT_PRIORITY="set_client_priority",
        SET_SUMMARY="set_summary",
        ADD_ANNOTATION="add_annotation",
        SET_STATUS="set_status"
    ;
    /* TODO:
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
        SET_CONTACT_EMAILS="SE",
        SET_CONTACT_PHONES="SP",
        TYPE_CHANGE="TY",
        WORK_ENTRY="WK"
    */

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

    /**
     * Generates a locale-specific summary.
     */
    String generateSummary(AOServConnector connector, Locale userLocale, String oldValue, String newValue) throws IOException, SQLException {
        // Substitute translated values for oldValue and newValue on certain types
        if(pkey.equals(SET_CLIENT_PRIORITY)) {
            /* TODO: if(oldValue!=null) {
                TicketPriority clientPriority = conn.getTicketPriorities().get(oldValue);
                if(clientPriority!=null) oldValue = clientPriority.toString(userLocale);
             * TODO: newValue, too
            }*/
        } else if(pkey.equals(SET_STATUS)) {
            if(oldValue!=null) {
                TicketStatus status = connector.getTicketStatuses().get(oldValue);
                if(status!=null) oldValue = status.toString(userLocale);
            }
            if(newValue!=null) {
                TicketStatus status = connector.getTicketStatuses().get(newValue);
                if(status!=null) newValue = status.toString(userLocale);
            }
        }
        if(oldValue==null) {
            if(newValue==null) return ApplicationResourcesAccessor.getMessage(userLocale, "TicketActionType."+pkey+".generatedSummary.null.null");
            return ApplicationResourcesAccessor.getMessage(userLocale, "TicketActionType."+pkey+".generatedSummary.null.notNull", newValue);
        } else {
            if(newValue==null) return ApplicationResourcesAccessor.getMessage(userLocale, "TicketActionType."+pkey+".generatedSummary.notNull.null", oldValue);
            return ApplicationResourcesAccessor.getMessage(userLocale, "TicketActionType."+pkey+".generatedSummary.notNull.notNull", oldValue, newValue);
        }
    }
}
