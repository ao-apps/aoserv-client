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
 * Each <code>Username</code> is unique across all systems and must
 * be allocated to a <code>Package</code> before use in any of the
 * account types.
 *
 * @see  BusinessAdministrator
 * @see  InterBaseUser
 * @see  LinuxAccount
 * @see  MySQLUser
 * @see  PostgresUser
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class Username extends CachedObjectStringKey<Username> implements PasswordProtected, Removable, Disablable {
	
    static final int
        COLUMN_USERNAME=0,
        COLUMN_PACKAGE=1
    ;

    public static final int MAX_LENGTH=255;

    String packageName;
    int disable_log;

    public void addBusinessAdministrator(
	String name,
	String title,
	long birthday,
	boolean isPrivate,
	String workPhone,
	String homePhone,
	String cellPhone,
	String fax,
	String email,
	String address1,
	String address2,
	String city,
	String state,
	String country,
	String zip
    ) {
	table.connector.businessAdministrators.addBusinessAdministrator(
            this,
            name,
            title,
            birthday,
            isPrivate,
            workPhone,
            homePhone,
            cellPhone,
            fax,
            email,
            address1,
            address2,
            city,
            state,
            country,
            zip
	);
    }

    public void addInterBaseUser(String firstName, String middleName, String lastName) {
        table.connector.interBaseUsers.addInterBaseUser(this, firstName, middleName, lastName);
    }

    public void addLinuxAccount(
        LinuxGroup primaryGroup,
	String name,
	String office_location,
	String office_phone,
	String home_phone,
	LinuxAccountType typeObject,
	Shell shellObject
    ) {
	addLinuxAccount(primaryGroup.getName(), name, office_location, office_phone, home_phone, typeObject.pkey, shellObject.pkey);
    }

    public void addLinuxAccount(
        String primaryGroup,
	String name,
	String office_location,
	String office_phone,
	String home_phone,
	String type,
	String shell
    ) {
	table.connector.linuxAccounts.addLinuxAccount(
            this,
            primaryGroup,
            name,
            office_location,
            office_phone,
            home_phone,
            type,
            shell
	);
    }

    public void addMySQLUser() {
        table.connector.mysqlUsers.addMySQLUser(pkey);
    }

    public void addPostgresUser() {
        table.connector.postgresUsers.addPostgresUser(pkey);
    }

    public int arePasswordsSet() {
        // Build the array of objects
        List<PasswordProtected> pps=new ArrayList<PasswordProtected>();
	BusinessAdministrator ba=getBusinessAdministrator();
	if(ba!=null) pps.add(ba);
	InterBaseUser iu=getInterBaseUser();
	if(iu!=null) pps.add(iu);
        LinuxAccount la=getLinuxAccount();
	if(la!=null) pps.add(la);
	MySQLUser mu=getMySQLUser();
	if(mu!=null) pps.add(mu);
	PostgresUser pu=getPostgresUser();
	if(pu!=null) pps.add(pu);
        return Username.groupPasswordsSet(pps);
    }

    public boolean canDisable() {
        if(disable_log!=-1) return false;
        InterBaseUser iu=getInterBaseUser();
        if(iu!=null && iu.disable_log==-1) return false;
        LinuxAccount la=getLinuxAccount();
        if(la!=null && la.disable_log==-1) return false;
        MySQLUser mu=getMySQLUser();
        if(mu!=null && mu.disable_log==-1) return false;
        PostgresUser pu=getPostgresUser();
        if(pu!=null && pu.disable_log==-1) return false;
        return true;
    }
    
    public boolean canEnable() {
        DisableLog dl=getDisableLog();
        if(dl==null) return false;
        else return dl.canEnable() && getPackage().disable_log==-1;
    }

    /**
     * Checks the strength of a password as used by this <code>Username</code>.
     */
    public PasswordChecker.Result[] checkPassword(Locale userLocale, String password) {
	BusinessAdministrator ba=getBusinessAdministrator();
	if(ba!=null) {
            PasswordChecker.Result[] results=ba.checkPassword(userLocale, password);
            if(PasswordChecker.hasResults(userLocale, results)) return results;
	}

	InterBaseUser iu=getInterBaseUser();
	if(iu!=null) {
            PasswordChecker.Result[] results=iu.checkPassword(userLocale, password);
            if(PasswordChecker.hasResults(userLocale, results)) return results;
	}

        LinuxAccount la=getLinuxAccount();
	if(la!=null) {
            PasswordChecker.Result[] results=la.checkPassword(userLocale, password);
            if(PasswordChecker.hasResults(userLocale, results)) return results;
	}

	MySQLUser mu=getMySQLUser();
	if(mu!=null) {
            PasswordChecker.Result[] results=mu.checkPassword(userLocale, password);
            if(PasswordChecker.hasResults(userLocale, results)) return results;
	}

	PostgresUser pu=getPostgresUser();
	if(pu!=null) {
            PasswordChecker.Result[] results=pu.checkPassword(userLocale, password);
            if(PasswordChecker.hasResults(userLocale, results)) return results;
	}

        return PasswordChecker.getAllGoodResults(userLocale);
    }

    /*
    public String checkPasswordDescribe(String password) {
	BusinessAdministrator ba=getBusinessAdministrator();
	if(ba!=null) {
            String results=ba.checkPasswordDescribe(password);
            if(results!=null) return results;
	}

	InterBaseUser iu=getInterBaseUser();
	if(iu!=null) {
            String results=iu.checkPasswordDescribe(password);
            if(results!=null) return results;
	}

        LinuxAccount la=getLinuxAccount();
	if(la!=null) {
            String results=la.checkPasswordDescribe(password);
            if(results!=null) return results;
	}

	MySQLUser mu=getMySQLUser();
	if(mu!=null) {
            String results=mu.checkPasswordDescribe(password);
            if(results!=null) return results;
	}

	PostgresUser pu=getPostgresUser();
	if(pu!=null) {
		String results=pu.checkPasswordDescribe(password);
		if(results!=null) return results;
	}

	return null;
    }
*/
    public void disable(DisableLog dl) {
        table.connector.requestUpdateIL(AOServProtocol.CommandID.DISABLE, SchemaTable.TableID.USERNAMES, dl.pkey, pkey);
    }
    
    public void enable() {
        table.connector.requestUpdateIL(AOServProtocol.CommandID.ENABLE, SchemaTable.TableID.USERNAMES, pkey);
    }

    public BusinessAdministrator getBusinessAdministrator() {
	return table.connector.businessAdministrators.get(pkey);
    }

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_USERNAME: return pkey;
            case COLUMN_PACKAGE: return packageName;
            case 2: return disable_log==-1?null:Integer.valueOf(disable_log);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public DisableLog getDisableLog() {
        if(disable_log==-1) return null;
        DisableLog obj=table.connector.disableLogs.get(disable_log);
        if(obj==null) throw new WrappedException(new SQLException("Unable to find DisableLog: "+disable_log));
        return obj;
    }

    public InterBaseUser getInterBaseUser() {
	return table.connector.interBaseUsers.get(pkey);
    }

    public LinuxAccount getLinuxAccount() {
	return table.connector.linuxAccounts.get(pkey);
    }

    public MySQLUser getMySQLUser() {
	return table.connector.mysqlUsers.get(pkey);
    }

    public Package getPackage() {
	Package packageObject=table.connector.packages.get(packageName);
	if (packageObject == null) throw new WrappedException(new SQLException("Unable to find Package: " + packageName));
	return packageObject;
    }

    public PostgresUser getPostgresUser() {
	return table.connector.postgresUsers.get(pkey);

    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.USERNAMES;
    }

    public String getUsername() {
	return pkey;
    }

    static int groupPasswordsSet(List<? extends PasswordProtected> pps) {
        int totalAll=0;
	for(int c=0;c<pps.size();c++) {
            int result=pps.get(c).arePasswordsSet();
            if(result==PasswordProtected.SOME) return PasswordProtected.SOME;
            if(result==PasswordProtected.ALL) totalAll++;
	}
        return totalAll==pps.size()?PasswordProtected.ALL:totalAll==0?PasswordProtected.NONE:PasswordProtected.SOME;
    }

    void initImpl(ResultSet result) throws SQLException {
	pkey = result.getString(1);
	packageName = result.getString(2);
        disable_log=result.getInt(3);
        if(result.wasNull()) disable_log=-1;
    }

    public boolean isUsed() {
	return
            getInterBaseUser()!=null
            || getLinuxAccount()!=null
            || getBusinessAdministrator()!=null
            || getMySQLUser()!=null
            || getPostgresUser()!=null
	;
    }

    /**
     * Determines if a name can be used as a username.  A name is valid if
     * it is between 1 and 255 characters in length and uses only ASCII 0x21
     * through 0x7f, excluding the following characters:
     * <code>space , : ( ) [ ] ' " | & ; A-Z \ /</code>
     *
     * @return  <code>null</code> if the username is valid or a locale-specific reason why it is not valid
     */
    public static String checkUsername(String username, Locale locale) {
	int len = username.length();
        if(len==0) return ApplicationResourcesAccessor.getMessage(locale, "Username.checkUsername.noUsername");
	if(len > MAX_LENGTH) return ApplicationResourcesAccessor.getMessage(locale, "Username.checkUsername.tooLong");

        // The first character must be [a-z]
	char ch = username.charAt(0);
	if (ch < 'a' || ch > 'z') return ApplicationResourcesAccessor.getMessage(locale, "Username.checkUsername.startAToZ");

        // The rest may have additional characters
	for (int c = 1; c < len; c++) {
            ch = username.charAt(c);
            if(ch==' ') return ApplicationResourcesAccessor.getMessage(locale, "Username.checkUsername.noSpace");
            if(ch<=0x21 || ch>0x7f) return ApplicationResourcesAccessor.getMessage(locale, "Username.checkUsername.specialCharacter");
            if(ch>='A' && ch<='Z') return ApplicationResourcesAccessor.getMessage(locale, "Username.checkUsername.noCapital");
            if(ch==',') return ApplicationResourcesAccessor.getMessage(locale, "Username.checkUsername.comma");
            if(ch==':') return ApplicationResourcesAccessor.getMessage(locale, "Username.checkUsername.colon");
            if(ch=='(') return ApplicationResourcesAccessor.getMessage(locale, "Username.checkUsername.leftParen");
            if(ch==')') return ApplicationResourcesAccessor.getMessage(locale, "Username.checkUsername.rightParen");
            if(ch=='[') return ApplicationResourcesAccessor.getMessage(locale, "Username.checkUsername.leftSquare");
            if(ch==']') return ApplicationResourcesAccessor.getMessage(locale, "Username.checkUsername.rightSquare");
            if(ch=='\'') return ApplicationResourcesAccessor.getMessage(locale, "Username.checkUsername.apostrophe");
            if(ch=='"') return ApplicationResourcesAccessor.getMessage(locale, "Username.checkUsername.quote");
            if(ch=='|') return ApplicationResourcesAccessor.getMessage(locale, "Username.checkUsername.verticalBar");
            if(ch=='&') return ApplicationResourcesAccessor.getMessage(locale, "Username.checkUsername.ampersand");
            if(ch==';') return ApplicationResourcesAccessor.getMessage(locale, "Username.checkUsername.semicolon");
            if(ch=='\\') return ApplicationResourcesAccessor.getMessage(locale, "Username.checkUsername.backslash");
            if(ch=='/') return ApplicationResourcesAccessor.getMessage(locale, "Username.checkUsername.slash");
	}
	return null;
    }

    /**
     * Determines if a name can be used as a username.  A name is valid if
     * it is between 1 and 255 characters in length and uses only ASCII 0x21
     * through 0x7f, excluding the following characters:
     * <code>space , : ( ) [ ] ' " | & ; A-Z \ /</code>
     *
     * @deprecated  Please use <code>checkUsername(String)</code> instead to provide user with specific problems.
     */
    public static boolean isValidUsername(String username) {
        return checkUsername(username, Locale.getDefault())==null;
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readUTF().intern();
	packageName=in.readUTF().intern();
        disable_log=in.readCompressedInt();
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();

        InterBaseUser iu=getInterBaseUser();
        if(iu!=null) reasons.add(new CannotRemoveReason<InterBaseUser>("Used by InterBase user: "+iu.getUsername().getUsername(), iu));
        LinuxAccount la=getLinuxAccount();
        if(la!=null) reasons.add(new CannotRemoveReason<LinuxAccount>("Used by Linux account: "+la.getUsername().getUsername(), la));
        BusinessAdministrator ba=getBusinessAdministrator();
        if(ba!=null) reasons.add(new CannotRemoveReason<BusinessAdministrator>("Used by Business Administrator: "+ba.getUsername().getUsername(), ba));
        MySQLUser mu=getMySQLUser();
        if(mu!=null) reasons.add(new CannotRemoveReason<MySQLUser>("Used by MySQL user: "+mu.getUsername().getUsername(), mu));
        PostgresUser pu=getPostgresUser();
        if(pu!=null) reasons.add(new CannotRemoveReason<PostgresUser>("Used by PostgreSQL user: "+pu.getUsername().getUsername(), pu));

        return reasons;
    }

    public void remove() {
	table.connector.requestUpdateIL(
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.USERNAMES,
            pkey
	);
    }

    public void setPassword(String password) {
	BusinessAdministrator ba=getBusinessAdministrator();
	if(ba!=null) ba.setPassword(password);

	InterBaseUser iu=getInterBaseUser();
	if(iu!=null) iu.setPassword(password);

        LinuxAccount la=getLinuxAccount();
	if(la!=null) la.setPassword(password);

	MySQLUser mu=getMySQLUser();
	if(mu!=null) mu.setPassword(password);

	PostgresUser pu=getPostgresUser();
	if(pu!=null) pu.setPassword(password);
    }

    public boolean canSetPassword() {
        if(disable_log!=-1) return false;

        BusinessAdministrator ba=getBusinessAdministrator();
	if(ba!=null && !ba.canSetPassword()) return false;

	InterBaseUser iu=getInterBaseUser();
	if(iu!=null && !iu.canSetPassword()) return false;

        LinuxAccount la=getLinuxAccount();
	if(la!=null && !la.canSetPassword()) return false;

	MySQLUser mu=getMySQLUser();
	if(mu!=null && !mu.canSetPassword()) return false;

	PostgresUser pu=getPostgresUser();
	if(pu!=null && !pu.canSetPassword()) return false;
        
        return ba!=null || iu!=null || la!=null || mu!=null || pu!=null;
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeUTF(pkey);
	out.writeUTF(packageName);
        out.writeCompressedInt(disable_log);
    }
}