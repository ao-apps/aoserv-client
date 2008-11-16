package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.sql.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;

/**
 * A <code>MajordomoList</code> is one list within a <code>MajordomoServer</code>.
 *
 * @see  MajordomoServer
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class MajordomoList extends CachedObjectIntegerKey<MajordomoList> {

    static final int
        COLUMN_EMAIL_LIST=0,
        COLUMN_MAJORDOMO_SERVER=1
    ;
    static final String COLUMN_NAME_name = "name";
    static final String COLUMN_MAJORDOMO_SERVER_name = "majordomo_server";

    /**
     * The maximum length of an email list name.
     */
    public static final int MAX_NAME_LENGTH=64;

    int majordomo_server;
    String name;
    int listname_pipe_add;
    int listname_list_add;
    int owner_listname_add;
    int listname_owner_add;
    int listname_approval_add;
    int listname_request_pipe_add;

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_EMAIL_LIST: return Integer.valueOf(pkey);
            case COLUMN_MAJORDOMO_SERVER: return Integer.valueOf(majordomo_server);
            case 2: return name;
            case 3: return Integer.valueOf(listname_pipe_add);
            case 4: return Integer.valueOf(listname_list_add);
            case 5: return Integer.valueOf(owner_listname_add);
            case 6: return Integer.valueOf(listname_owner_add);
            case 7: return Integer.valueOf(listname_approval_add);
            case 8: return Integer.valueOf(listname_request_pipe_add);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public static String getDefaultInfoFile(String domain, String listName) {
        return
              "Information about the "+listName+" mailing list:\n"
            + "\n"
            + "HOW TO POST A MESSAGE TO THE LIST:\n"
            + "Just send an email message to "+listName+'@'+domain+". The message will\n"
            + "be distributed to all the members of the list.\n"
            + "\n"
            + "HOW TO UNSUBSCRIBE:\n"
            + "Send an email message to majordomo@"+domain+" with one line in the\n"
            + "body of the message:\n"
            + "\n"
            + "unsubscribe "+listName+"\n"
            + "\n"
            + "\n"
            + "FOR QUESTIONS:\n"
            + "If you ever need to get in contact with the owner of the list,\n"
            + "(if you have trouble unsubscribing, or have questions about the\n"
            + "list itself) send email to owner-"+listName+'@'+domain+".\n"
        ;
    }

    public String getDefaultInfoFile() {
        return getDefaultInfoFile(getMajordomoServer().getDomain().getDomain(), name);
    }
    
    public static String getDefaultIntroFile(String domain, String listName) {
        return
              "Welcome to the "+listName+" mailing list.\n"
            + "\n"
            + "Please save this message for future reference.\n"
            + "\n"
            + "HOW TO POST A MESSAGE TO THE LIST:\n"
            + "Just send an email message to "+listName+'@'+domain+". The message will\n"
            + "be distributed to all the members of the list.\n"
            + "\n"
            + "HOW TO UNSUBSCRIBE:\n"
            + "Send an email message to majordomo@"+domain+" with one line in the\n"
            + "body of the message:\n"
            + "\n"
            + "unsubscribe "+listName+"\n"
            + "\n"
            + "\n"
            + "FOR QUESTIONS:\n"
            + "If you ever need to get in contact with the owner of the list,\n"
            + "(if you have trouble unsubscribing, or have questions about the\n"
            + "list itself) send email to owner-"+listName+'@'+domain+".\n"
        ;
    }

    public String getDefaultIntroFile() {
        return getDefaultIntroFile(getMajordomoServer().getDomain().getDomain(), name);
    }

    public EmailList getEmailList() {
        EmailList obj=table.connector.emailLists.get(pkey);
        if(obj==null) throw new WrappedException(new SQLException("Unable to find EmailList: "+pkey));
        return obj;
    }

    /**
     * Gets the info file for the list.
     */
    public String getInfoFile() {
        return table.connector.requestStringQuery(AOServProtocol.CommandID.GET_MAJORDOMO_INFO_FILE, pkey);
    }

    /**
     * Gets the intro file for the list.
     */
    public String getIntroFile() {
        return table.connector.requestStringQuery(AOServProtocol.CommandID.GET_MAJORDOMO_INTRO_FILE, pkey);
    }

    public EmailPipeAddress getListPipeAddress() {
        EmailPipeAddress pipeAddress=table.connector.emailPipeAddresses.get(listname_pipe_add);
        if(pipeAddress==null) throw new WrappedException(new SQLException("Unable to find EmailPipeAddress: "+listname_pipe_add));
        return pipeAddress;
    }

    public EmailAddress getListApprovalAddress() {
        EmailAddress address=table.connector.emailAddresses.get(listname_approval_add);
        if(address==null) throw new WrappedException(new SQLException("Unable to find EmailAddress: "+listname_approval_add));
        return address;
    }

    public EmailListAddress getListListAddress() {
        EmailListAddress listAddress=table.connector.emailListAddresses.get(listname_list_add);
        if(listAddress==null) throw new WrappedException(new SQLException("Unable to find EmailListAddress: "+listname_list_add));
        return listAddress;
    }

    public EmailAddress getListOwnerAddress() {
        EmailAddress address=table.connector.emailAddresses.get(listname_owner_add);
        if(address==null) throw new WrappedException(new SQLException("Unable to find EmailAddress: "+listname_owner_add));
        return address;
    }

    public EmailPipeAddress getListRequestPipeAddress() {
        EmailPipeAddress pipeAddress=table.connector.emailPipeAddresses.get(listname_request_pipe_add);
        if(pipeAddress==null) throw new WrappedException(new SQLException("Unable to find EmailPipeAddress: "+listname_request_pipe_add));
        return pipeAddress;
    }

    public String getName() {
        return name;
    }

    public EmailAddress getOwnerListAddress() {
        EmailAddress address=table.connector.emailAddresses.get(owner_listname_add);
        if(address==null) throw new WrappedException(new SQLException("Unable to find EmailAddress: "+owner_listname_add));
        return address;
    }

    public MajordomoServer getMajordomoServer() {
        MajordomoServer obj=table.connector.majordomoServers.get(majordomo_server);
        if(obj==null) throw new WrappedException(new SQLException("Unable to find MajordomoServer: "+majordomo_server));
        return obj;
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.MAJORDOMO_LISTS;
    }

    public void init(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
        majordomo_server=result.getInt(2);
        name=result.getString(3);
        listname_pipe_add=result.getInt(4);
        listname_list_add=result.getInt(5);
        owner_listname_add=result.getInt(6);
        listname_owner_add=result.getInt(7);
        listname_approval_add=result.getInt(8);
        listname_request_pipe_add=result.getInt(9);
    }

    /**
     * Checks the validity of a list name.
     */
    public static boolean isValidListName(String name) {
        int len = name.length();
        if (len < 1 || len > MAX_NAME_LENGTH) return false;
        for (int c = 0; c < len; c++) {
            char ch = name.charAt(c);
            if (c == 0) {
                if ((ch < '0' || ch > '9') && (ch < 'a' || ch > 'z') && (ch < 'A' || ch > 'Z')) return false;
            } else {
                if ((ch < '0' || ch > '9') && (ch < 'a' || ch > 'z') && (ch < 'A' || ch > 'Z') && ch != '.' && ch != '-' && ch != '_') return false;
            }
        }
        return true;
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        majordomo_server=in.readCompressedInt();
        name=in.readUTF();
        listname_pipe_add=in.readCompressedInt();
        listname_list_add=in.readCompressedInt();
        owner_listname_add=in.readCompressedInt();
        listname_owner_add=in.readCompressedInt();
        listname_approval_add=in.readCompressedInt();
        listname_request_pipe_add=in.readCompressedInt();
    }

    public void setInfoFile(String file) {
        table.connector.requestUpdate(AOServProtocol.CommandID.SET_MAJORDOMO_INFO_FILE, pkey, file);
    }

    public void setIntroFile(String file) {
        table.connector.requestUpdate(AOServProtocol.CommandID.SET_MAJORDOMO_INTRO_FILE, pkey, file);
    }

    @Override
    String toStringImpl() {
        return name+'@'+getMajordomoServer().getDomain().domain;
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeCompressedInt(majordomo_server);
        out.writeUTF(name);
        out.writeCompressedInt(listname_pipe_add);
        out.writeCompressedInt(listname_list_add);
        out.writeCompressedInt(owner_listname_add);
        out.writeCompressedInt(listname_owner_add);
        out.writeCompressedInt(listname_approval_add);
        out.writeCompressedInt(listname_request_pipe_add);
    }
}