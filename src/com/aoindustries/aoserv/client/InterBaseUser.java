package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.StringUtility;
import com.aoindustries.util.WrappedException;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * An <code>InterBaseUser</code> stores the details of an InterBase account
 * that are common to all servers.
 *
 * @see  InterBaseServerUser
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class InterBaseUser extends CachedObjectStringKey<InterBaseUser> implements PasswordProtected, Removable, Disablable {

    static final int COLUMN_USERNAME=0;

    /**
     * The maximum length of an InterBase username.
     */
    public static final int MAX_USERNAME_LENGTH=31;

    /**
     * The maximum length of an InterBase firstname, middlename, or lastname.
     */
    public static final int MAX_NAME_LENGTH=31;

    /**
     * The username of the InterBase super user.
     */
    public static final String SYSDBA="sysdba";

    /**
     * A password may be set to null, which means that the account will
     * be disabled.
     */
    public static final String NO_PASSWORD=null;

    public static final String NO_PASSWORD_DB_VALUE=null;

    private String
        first_name,
        middle_name,
        last_name
    ;
    int disable_log;

    public int addInterBaseServerUser(AOServer aoServer) {
	return table.connector.interBaseServerUsers.addInterBaseServerUser(pkey, aoServer);
    }

    public int arePasswordsSet() {
        return Username.groupPasswordsSet(getInterBaseServerUsers());
    }

    public boolean canDisable() {
        if(disable_log!=-1 || pkey.equals(SYSDBA)) return false;
        for(InterBaseServerUser isu : getInterBaseServerUsers()) if(!isu.canDisable()) return false;
        return true;
    }

    public boolean canEnable() {
        DisableLog dl=getDisableLog();
        if(dl==null) return false;
        else return dl.canEnable() && getUsername().disable_log==-1;
    }

    public PasswordChecker.Result[] checkPassword(Locale userLocale, String password) {
        return checkPassword(userLocale, pkey, password);
    }

    public static PasswordChecker.Result[] checkPassword(Locale userLocale, String username, String password) {
        return PasswordChecker.checkPassword(userLocale, username, password, true, false);
    }
/*
    public String checkPasswordDescribe(String password) {
        return checkPasswordDescribe(pkey, password);
    }

    public static String checkPasswordDescribe(String username, String password) {
        return PasswordChecker.checkPasswordDescribe(username, password, true, false);
    }
*/
    public void disable(DisableLog dl) {
        table.connector.requestUpdateIL(AOServProtocol.CommandID.DISABLE, SchemaTable.TableID.INTERBASE_USERS, dl.pkey, pkey);
    }
    
    public void enable() {
        table.connector.requestUpdateIL(AOServProtocol.CommandID.ENABLE, SchemaTable.TableID.INTERBASE_USERS, pkey);
    }

    public Object getColumn(int i) {
        if(i==COLUMN_USERNAME) return pkey;
        if(i==1) return first_name;
        if(i==2) return middle_name;
        if(i==3) return last_name;
        if(i==4) return disable_log==-1?null:Integer.valueOf(disable_log);
        throw new IllegalArgumentException("Invalid index: "+i);
    }

    public DisableLog getDisableLog() {
        if(disable_log==-1) return null;
        DisableLog obj=table.connector.disableLogs.get(disable_log);
        if(obj==null) throw new WrappedException(new SQLException("Unable to find DisableLog: "+disable_log));
        return obj;
    }

    public String getFirstName() {
        return first_name;
    }
    
    public String getMiddleName() {
        return middle_name;
    }
    
    public String getLastName() {
        return last_name;
    }
    
    public String getFullName() {
        if(first_name==null && middle_name==null && last_name==null) return null;
        StringBuilder SB=new StringBuilder();
        if(first_name!=null) SB.append(first_name);
        if(middle_name!=null) {
            if(SB.length()>0) SB.append(' ');
            SB.append(middle_name);
        }
        if(last_name!=null) {
            if(SB.length()>0) SB.append(' ');
            SB.append(last_name);
        }
        return SB.toString();
    }
    
    public InterBaseServerUser getInterBaseServerUser(AOServer aoServer) {
        return table.connector.interBaseServerUsers.getInterBaseServerUser(pkey, aoServer);
    }

    public List<InterBaseServerUser> getInterBaseServerUsers() {
        return table.connector.interBaseServerUsers.getInterBaseServerUsers(pkey);
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.INTERBASE_USERS;
    }

    public Username getUsername() {
        Username obj=table.connector.usernames.get(pkey);
        if(obj==null) throw new WrappedException(new SQLException("Unable to find Username: "+pkey));
        return obj;
    }

    void initImpl(ResultSet result) throws SQLException {
        pkey=result.getString(1);
        first_name=result.getString(2);
        middle_name=result.getString(3);
        last_name=result.getString(4);
        disable_log=result.getInt(5);
        if(result.wasNull()) disable_log=-1;
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readUTF().intern();
        first_name=in.readNullUTF();
        middle_name=in.readNullUTF();
        last_name=in.readNullUTF();
        disable_log=in.readCompressedInt();
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();

        for(InterBaseServerUser isu : getInterBaseServerUsers()) reasons.addAll(isu.getCannotRemoveReasons());

        return reasons;
    }

    public void remove() {
        table.connector.requestUpdateIL(
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.INTERBASE_USERS,
            pkey
        );
    }

    public void setPassword(String password) {
        for(InterBaseServerUser isu : getInterBaseServerUsers()) if(isu.canSetPassword()) isu.setPassword(password);
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        out.writeUTF(pkey);
        out.writeNullUTF(first_name);
        out.writeNullUTF(middle_name);
        out.writeNullUTF(last_name);
        out.writeCompressedInt(disable_log);
    }

    /**
     * Determines if a name can be used for first, middle, or last names.
     * A name is valid if its is charactes of [a-z],[A-Z],[0-9],_,. and is not
     * more than 31 characters long.
     */
    public static boolean isValidName(String name) {
        if(name==null) return true;
        if(name.length()>MAX_NAME_LENGTH) return false;
        for(int c=0;c<name.length();c++) {
            char ch=name.charAt(c);
            if(
                (ch<'a' || ch>'z')
                && (ch<'A' || ch>'Z')
                && (ch<'0' || ch>'9')
                && ch!='_'
                && ch!='.'
            ) return false;
        }
        return true;
    }

    /**
     * Determines if a name can be used as a username.  A name is valid if
     * it is between 1 and 128 characters in length and is a valid <code>Username</code>.
     *
     * @see  Username#isValidUsername
     */
    public static boolean isValidUsername(String name) {
        return name.length()<=MAX_USERNAME_LENGTH && Username.checkUsername(name, Locale.getDefault())==null;
    }

    public boolean canSetPassword() {
        return disable_log==-1 && !pkey.equals(SYSDBA);
    }
}