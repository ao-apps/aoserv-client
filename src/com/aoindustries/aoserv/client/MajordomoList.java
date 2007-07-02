package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.profiler.*;
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
        Profiler.startProfile(Profiler.FAST, MajordomoList.class, "getColValueImpl(int)", null);
        try {
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
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public static String getDefaultInfoFile(String domain, String listName) {
        Profiler.startProfile(Profiler.FAST, MajordomoList.class, "getDefaultInfoFile(String,String)", null);
        try {
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
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public String getDefaultInfoFile() {
        Profiler.startProfile(Profiler.FAST, MajordomoList.class, "getDefaultInfoFile()", null);
        try {
            return getDefaultInfoFile(getMajordomoServer().getDomain().getDomain(), name);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
    
    public static String getDefaultIntroFile(String domain, String listName) {
        Profiler.startProfile(Profiler.FAST, MajordomoList.class, "getDefaultIntroFile(String,String)", null);
        try {
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
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public String getDefaultIntroFile() {
        Profiler.startProfile(Profiler.FAST, MajordomoList.class, "getDefaultIntroFile()", null);
        try {
            return getDefaultIntroFile(getMajordomoServer().getDomain().getDomain(), name);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public EmailList getEmailList() {
        Profiler.startProfile(Profiler.FAST, MajordomoList.class, "getEmailList()", null);
        try {
            EmailList obj=table.connector.emailLists.get(pkey);
            if(obj==null) throw new WrappedException(new SQLException("Unable to find EmailList: "+pkey));
            return obj;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Gets the info file for the list.
     */
    public String getInfoFile() {
        Profiler.startProfile(Profiler.UNKNOWN, MajordomoList.class, "getInfoFile()", null);
        try {
            return table.connector.requestStringQuery(AOServProtocol.GET_MAJORDOMO_INFO_FILE, pkey);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    /**
     * Gets the intro file for the list.
     */
    public String getIntroFile() {
        Profiler.startProfile(Profiler.UNKNOWN, MajordomoList.class, "getIntroFile()", null);
        try {
            return table.connector.requestStringQuery(AOServProtocol.GET_MAJORDOMO_INTRO_FILE, pkey);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public EmailPipeAddress getListPipeAddress() {
        Profiler.startProfile(Profiler.FAST, MajordomoList.class, "getListPipeAddress()", null);
        try {
            EmailPipeAddress pipeAddress=table.connector.emailPipeAddresses.get(listname_pipe_add);
            if(pipeAddress==null) throw new WrappedException(new SQLException("Unable to find EmailPipeAddress: "+listname_pipe_add));
            return pipeAddress;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public EmailAddress getListApprovalAddress() {
        Profiler.startProfile(Profiler.FAST, MajordomoList.class, "getListApprovalAddress()", null);
        try {
            EmailAddress address=table.connector.emailAddresses.get(listname_approval_add);
            if(address==null) throw new WrappedException(new SQLException("Unable to find EmailAddress: "+listname_approval_add));
            return address;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public EmailListAddress getListListAddress() {
        Profiler.startProfile(Profiler.FAST, MajordomoList.class, "getListListAddress()", null);
        try {
            EmailListAddress listAddress=table.connector.emailListAddresses.get(listname_list_add);
            if(listAddress==null) throw new WrappedException(new SQLException("Unable to find EmailListAddress: "+listname_list_add));
            return listAddress;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public EmailAddress getListOwnerAddress() {
        Profiler.startProfile(Profiler.FAST, MajordomoList.class, "getListOwnerAddress()", null);
        try {
            EmailAddress address=table.connector.emailAddresses.get(listname_owner_add);
            if(address==null) throw new WrappedException(new SQLException("Unable to find EmailAddress: "+listname_owner_add));
            return address;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public EmailPipeAddress getListRequestPipeAddress() {
        Profiler.startProfile(Profiler.FAST, MajordomoList.class, "getListRequestPipeAddress()", null);
        try {
            EmailPipeAddress pipeAddress=table.connector.emailPipeAddresses.get(listname_request_pipe_add);
            if(pipeAddress==null) throw new WrappedException(new SQLException("Unable to find EmailPipeAddress: "+listname_request_pipe_add));
            return pipeAddress;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public String getName() {
        return name;
    }

    public EmailAddress getOwnerListAddress() {
        Profiler.startProfile(Profiler.FAST, MajordomoList.class, "getOwnerListAddress()", null);
        try {
            EmailAddress address=table.connector.emailAddresses.get(owner_listname_add);
            if(address==null) throw new WrappedException(new SQLException("Unable to find EmailAddress: "+owner_listname_add));
            return address;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public MajordomoServer getMajordomoServer() {
        Profiler.startProfile(Profiler.FAST, MajordomoList.class, "getMajordomoServer()", null);
        try {
            MajordomoServer obj=table.connector.majordomoServers.get(majordomo_server);
            if(obj==null) throw new WrappedException(new SQLException("Unable to find MajordomoServer: "+majordomo_server));
            return obj;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.MAJORDOMO_LISTS;
    }

    void initImpl(ResultSet result) throws SQLException {
        Profiler.startProfile(Profiler.FAST, MajordomoList.class, "initImpl(ResultSet)", null);
        try {
            pkey=result.getInt(1);
            majordomo_server=result.getInt(2);
            name=result.getString(3);
            listname_pipe_add=result.getInt(4);
            listname_list_add=result.getInt(5);
            owner_listname_add=result.getInt(6);
            listname_owner_add=result.getInt(7);
            listname_approval_add=result.getInt(8);
            listname_request_pipe_add=result.getInt(9);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Checks the validity of a list name.
     */
    public static boolean isValidListName(String name) {
        Profiler.startProfile(Profiler.FAST, MajordomoList.class, "isValidListName(String)", null);
        try {
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
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public void read(CompressedDataInputStream in) throws IOException {
        Profiler.startProfile(Profiler.IO, MajordomoList.class, "read(CompressedDataInputStream)", null);
        try {
            pkey=in.readCompressedInt();
            majordomo_server=in.readCompressedInt();
            name=in.readUTF();
            listname_pipe_add=in.readCompressedInt();
            listname_list_add=in.readCompressedInt();
            owner_listname_add=in.readCompressedInt();
            listname_owner_add=in.readCompressedInt();
            listname_approval_add=in.readCompressedInt();
            listname_request_pipe_add=in.readCompressedInt();
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public void setInfoFile(String file) {
        Profiler.startProfile(Profiler.UNKNOWN, MajordomoList.class, "setInfoFile(String)", null);
        try {
            table.connector.requestUpdate(AOServProtocol.SET_MAJORDOMO_INFO_FILE, pkey, file);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public void setIntroFile(String file) {
        Profiler.startProfile(Profiler.UNKNOWN, MajordomoList.class, "setIntroFile(String)", null);
        try {
            table.connector.requestUpdate(AOServProtocol.SET_MAJORDOMO_INTRO_FILE, pkey, file);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    String toStringImpl() {
        return name+'@'+getMajordomoServer().getDomain().domain;
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        Profiler.startProfile(Profiler.IO, MajordomoList.class, "write(CompressedDataOutputStream,String)", null);
        try {
            out.writeCompressedInt(pkey);
            out.writeCompressedInt(majordomo_server);
            out.writeUTF(name);
            out.writeCompressedInt(listname_pipe_add);
            out.writeCompressedInt(listname_list_add);
            out.writeCompressedInt(owner_listname_add);
            out.writeCompressedInt(listname_owner_add);
            out.writeCompressedInt(listname_approval_add);
            out.writeCompressedInt(listname_request_pipe_add);
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }
}