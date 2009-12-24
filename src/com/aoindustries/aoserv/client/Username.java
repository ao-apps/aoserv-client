package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.rmi.RemoteException;
import java.util.Set;

/**
 * Each <code>Username</code> is unique across all systems and must
 * be allocated to a <code>Business</code> before use in any of the
 * account types.
 *
 * @see  BusinessAdministrator
 * @see  LinuxAccount
 * @see  MySQLUser
 * @see  PostgresUser
 *
 * @author  AO Industries, Inc.
 */
final public class Username extends AOServObjectStringKey<Username> /* TODO: implements PasswordProtected, Removable, Disablable*/ {
	
    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    public static final int MAX_LENGTH=255;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final String accounting;
    final int disable_log;

    public Username(UsernameService<?,?> table, String username, String accounting, int disable_log) {
        super(table, username);
        this.accounting = accounting.intern();
        this.disable_log = disable_log;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="username", unique=true, description="the unique username")
    public String getUsername() {
        return key;
    }

    /**
     * May be filtered.
     */
    @SchemaColumn(order=1, name="accounting", description="the business that this user is part of")
    public Business getBusiness() throws RemoteException {
    	return getService().getConnector().getBusinesses().get(accounting);
    }

    @SchemaColumn(order=2, name="disable_log", description="indicates that the username is disabled")
    public DisableLog getDisableLog() throws RemoteException {
        if(disable_log==-1) return null;
        DisableLog obj=getService().getConnector().getDisableLogs().get(disable_log);
        if(obj==null) throw new RemoteException("Unable to find DisableLog: "+disable_log);
        return obj;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return createDependencySet(
            getBusiness(),
            getDisableLog()
        );
    }

    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return createDependencySet(
            createDependencySet(
                getBusinessAdministrator()
                // TODO: getLinuxAccount(),
                // TODO: getPostgresUser()
            )
            // TODO: getMySQLUsers()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public BusinessAdministrator getBusinessAdministrator() throws RemoteException {
    	return getService().getConnector().getBusinessAdministrators().get(key);
    }

    /* TODO
    public LinuxAccount getLinuxAccount() throws IOException, SQLException {
        return table.connector.getLinuxAccounts().get(pkey);
    }

    public List<MySQLUser> getMySQLUsers() throws IOException, SQLException {
        return table.connector.getMysqlUsers().getIndexedRows(MySQLUser.COLUMN_USERNAME, pkey);
    }

    public PostgresUser getPostgresUser() throws IOException, SQLException {
        return table.connector.getPostgresUsers().get(pkey);
    }
     */
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    /*
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
        String zip,
        boolean enableEmailSupport
    ) throws IOException, SQLException {
	    table.connector.getBusinessAdministrators().addBusinessAdministrator(
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
            zip,
            enableEmailSupport
    	);
    }

    public void addLinuxAccount(
        LinuxGroup primaryGroup,
        String name,
        String office_location,
        String office_phone,
        String home_phone,
        LinuxAccountType typeObject,
        Shell shellObject
    ) throws IOException, SQLException {
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
    ) throws IOException, SQLException {
	    table.connector.getLinuxAccounts().addLinuxAccount(
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

    public int addMySQLUser(MySQLServer mysqlServer, String host) throws IOException, SQLException {
    	return table.connector.getMysqlUsers().addMySQLUser(pkey, mysqlServer, host);
    }

    public void addPostgresUser() throws IOException, SQLException {
        table.connector.getPostgresUsers().addPostgresUser(pkey);
    }

    public int arePasswordsSet() throws IOException, SQLException {
        // Build the array of objects
        List<PasswordProtected> pps=new ArrayList<PasswordProtected>();
        BusinessAdministrator ba=getBusinessAdministrator();
        if(ba!=null) pps.add(ba);
        LinuxAccount la=getLinuxAccount();
        if(la!=null) pps.add(la);
        pps.addAll(getMySQLUsers());
        PostgresUser pu=getPostgresUser();
        if(pu!=null) pps.add(pu);
        return Username.groupPasswordsSet(pps);
    }

    public boolean canDisable() throws IOException, SQLException {
        if(disable_log!=-1) return false;
        LinuxAccount la=getLinuxAccount();
        if(la!=null && la.disable_log==-1) return false;
        for(MySQLUser mu : getMySQLUsers()) if(mu.disable_log==-1) return false;
        PostgresUser pu=getPostgresUser();
        if(pu!=null && pu.disable_log==-1) return false;
        return true;
    }
    
    public boolean canEnable() throws SQLException, IOException {
        DisableLog dl=getDisableLog();
        if(dl==null) return false;
        else return dl.canEnable() && getBusiness().disable_log==-1;
    }
    */
    /**
     * Checks the strength of a password as used by this <code>Username</code>.
     */
    /*
    public PasswordChecker.Result[] checkPassword(Locale userLocale, String password) throws IOException, SQLException {
        BusinessAdministrator ba=getBusinessAdministrator();
        if(ba!=null) {
            PasswordChecker.Result[] results=ba.checkPassword(userLocale, password);
            if(PasswordChecker.hasResults(userLocale, results)) return results;
    	}

        LinuxAccount la=getLinuxAccount();
    	if(la!=null) {
            PasswordChecker.Result[] results=la.checkPassword(userLocale, password);
            if(PasswordChecker.hasResults(userLocale, results)) return results;
    	}

        for(MySQLUser mu : getMySQLUsers()) {
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

    public void disable(DisableLog dl) throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.DISABLE, SchemaTable.TableID.USERNAMES, dl.pkey, pkey);
    }
    
    public void enable() throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.ENABLE, SchemaTable.TableID.USERNAMES, pkey);
    }

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_USERNAME: return pkey;
            case COLUMN_ACCOUNTING: return accounting;
            case COLUMN_DISABLE_LOG: return disable_log==-1?null:Integer.valueOf(disable_log);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public boolean isDisabled() {
        return disable_log!=-1;
    }

    static int groupPasswordsSet(List<? extends PasswordProtected> pps) throws IOException, SQLException {
        int totalAll=0;
    	for(int c=0;c<pps.size();c++) {
            int result=pps.get(c).arePasswordsSet();
            if(result==PasswordProtected.SOME) return PasswordProtected.SOME;
            if(result==PasswordProtected.ALL) totalAll++;
        }
        return totalAll==pps.size()?PasswordProtected.ALL:totalAll==0?PasswordProtected.NONE:PasswordProtected.SOME;
    }

    public void init(ResultSet result) throws SQLException {
        pkey = result.getString(1);
        accounting = result.getString(2);
        disable_log=result.getInt(3);
        if(result.wasNull()) disable_log=-1;
    }

    public boolean isUsed() throws IOException, SQLException {
    	return
            getLinuxAccount()!=null
            || getBusinessAdministrator()!=null
            || !getMySQLUsers().isEmpty()
            || getPostgresUser()!=null
    	;
    }
    */
    /**
     * Determines if a name can be used as a username.  A name is valid if
     * it is between 1 and 255 characters in length and uses only ASCII 0x21
     * through 0x7f, excluding the following characters:
     * <code>space , : ( ) [ ] ' " | & ; A-Z \ /</code>
     *
     * @return  <code>null</code> if the username is valid or a locale-specific reason why it is not valid
     */
    /* TODO
    public static String checkUsername(String username, Locale locale) {
	int len = username.length();
        if(len==0) return ApplicationResources.accessor.getMessage(locale, "Username.checkUsername.noUsername");
	if(len > MAX_LENGTH) return ApplicationResources.accessor.getMessage(locale, "Username.checkUsername.tooLong");

        // The first character must be [a-z]
	char ch = username.charAt(0);
	if (ch < 'a' || ch > 'z') return ApplicationResources.accessor.getMessage(locale, "Username.checkUsername.startAToZ");

        // The rest may have additional characters
	for (int c = 1; c < len; c++) {
            ch = username.charAt(c);
            if(ch==' ') return ApplicationResources.accessor.getMessage(locale, "Username.checkUsername.noSpace");
            if(ch<=0x21 || ch>0x7f) return ApplicationResources.accessor.getMessage(locale, "Username.checkUsername.specialCharacter");
            if(ch>='A' && ch<='Z') return ApplicationResources.accessor.getMessage(locale, "Username.checkUsername.noCapital");
            if(ch==',') return ApplicationResources.accessor.getMessage(locale, "Username.checkUsername.comma");
            if(ch==':') return ApplicationResources.accessor.getMessage(locale, "Username.checkUsername.colon");
            if(ch=='(') return ApplicationResources.accessor.getMessage(locale, "Username.checkUsername.leftParen");
            if(ch==')') return ApplicationResources.accessor.getMessage(locale, "Username.checkUsername.rightParen");
            if(ch=='[') return ApplicationResources.accessor.getMessage(locale, "Username.checkUsername.leftSquare");
            if(ch==']') return ApplicationResources.accessor.getMessage(locale, "Username.checkUsername.rightSquare");
            if(ch=='\'') return ApplicationResources.accessor.getMessage(locale, "Username.checkUsername.apostrophe");
            if(ch=='"') return ApplicationResources.accessor.getMessage(locale, "Username.checkUsername.quote");
            if(ch=='|') return ApplicationResources.accessor.getMessage(locale, "Username.checkUsername.verticalBar");
            if(ch=='&') return ApplicationResources.accessor.getMessage(locale, "Username.checkUsername.ampersand");
            if(ch==';') return ApplicationResources.accessor.getMessage(locale, "Username.checkUsername.semicolon");
            if(ch=='\\') return ApplicationResources.accessor.getMessage(locale, "Username.checkUsername.backslash");
            if(ch=='/') return ApplicationResources.accessor.getMessage(locale, "Username.checkUsername.slash");
	}
        
        // More strict at sign control is required for user@domain structure in Cyrus virtdomains.
        int atPos = username.indexOf('@');
        if(atPos!=-1) {
            if(atPos==0) return ApplicationResources.accessor.getMessage(locale, "Username.checkUsername.startWithAt");
            if(atPos==(len-1)) return ApplicationResources.accessor.getMessage(locale, "Username.checkUsername.endWithAt");
            int atPos2 = username.indexOf('@', atPos+1);
            if(atPos2!=-1) return ApplicationResources.accessor.getMessage(locale, "Username.checkUsername.onlyOneAt");
            if(username.startsWith("cyrus@")) return ApplicationResources.accessor.getMessage(locale, "Username.checkUsername.startWithCyrusAt");
            if(username.endsWith("@default")) return ApplicationResources.accessor.getMessage(locale, "Username.checkUsername.endWithAtDefault");
        }

        return null;
    }
    */
    /**
     * Determines if a name can be used as a username.  A name is valid if
     * it is between 1 and 255 characters in length and uses only ASCII 0x21
     * through 0x7f, excluding the following characters:
     * <code>space , : ( ) [ ] ' " | & ; A-Z \ /</code>
     *
     * @deprecated  Please use <code>checkUsername(String)</code> instead to provide user with specific problems.
     */
    /* TODO
    public static boolean isValidUsername(String username) {
        return checkUsername(username, Locale.getDefault())==null;
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readUTF().intern();
        accounting=in.readUTF().intern();
        disable_log=in.readCompressedInt();
    }

    public List<CannotRemoveReason> getCannotRemoveReasons(Locale userLocale) throws SQLException, IOException {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();

        LinuxAccount la=getLinuxAccount();
        if(la!=null) reasons.add(new CannotRemoveReason<LinuxAccount>("Used by Linux account: "+la.getUsername().getUsername(), la));
        BusinessAdministrator ba=getBusinessAdministrator();
        if(ba!=null) reasons.add(new CannotRemoveReason<BusinessAdministrator>("Used by Business Administrator: "+ba.getUsername().getUsername(), ba));
        List<MySQLUser> mus=getMySQLUsers();
        if(!mus.isEmpty()) reasons.add(new CannotRemoveReason<MySQLUser>("Used by MySQL users: "+pkey, mus));
        PostgresUser pu=getPostgresUser();
        if(pu!=null) reasons.add(new CannotRemoveReason<PostgresUser>("Used by PostgreSQL user: "+pu.getUsername().getUsername(), pu));

        return reasons;
    }

    public void remove() throws IOException, SQLException {
    	table.connector.requestUpdateIL(
            true,
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.USERNAMES,
            pkey
    	);
    }

    public void setPassword(String password) throws SQLException, IOException {
        BusinessAdministrator ba=getBusinessAdministrator();
        if(ba!=null) ba.setPassword(password);

        LinuxAccount la=getLinuxAccount();
    	if(la!=null) la.setPassword(password);

        for(MySQLUser mu : getMySQLUsers()) mu.setPassword(password);

        PostgresUser pu=getPostgresUser();
        if(pu!=null) pu.setPassword(password);
    }

    public boolean canSetPassword() throws IOException, SQLException {
        if(disable_log!=-1) return false;

        BusinessAdministrator ba=getBusinessAdministrator();
    	if(ba!=null && !ba.canSetPassword()) return false;

        LinuxAccount la=getLinuxAccount();
    	if(la!=null && !la.canSetPassword()) return false;

        List<MySQLUser> mus = getMySQLUsers();
    	for(MySQLUser mu : mus) if(!mu.canSetPassword()) return false;

        PostgresUser pu=getPostgresUser();
        if(pu!=null && !pu.canSetPassword()) return false;
        
        return ba!=null || la!=null || !mus.isEmpty() || pu!=null;
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeUTF(pkey);
        out.writeUTF(accounting);
        out.writeCompressedInt(disable_log);
    }
     */
    // </editor-fold>
}