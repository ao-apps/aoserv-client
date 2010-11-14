/*
 * Copyright 2000-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.command.AOServCommand;
import com.aoindustries.aoserv.client.command.CheckUsernamePasswordCommand;
import com.aoindustries.aoserv.client.command.SetUsernamePasswordCommand;
import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionSet;
import java.rmi.RemoteException;
import java.util.List;

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
final public class Username
extends AOServObjectUserIdKey
implements Comparable<Username>, DtoFactory<com.aoindustries.aoserv.client.dto.Username>, PasswordProtected /* TODO: Removable, Disablable*/ {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private AccountingCode accounting;
    final private Integer disableLog;

    public Username(AOServConnector<?,?> connector, UserId username, AccountingCode accounting, Integer disableLog) {
        super(connector, username);
        this.accounting = accounting;
        this.disableLog = disableLog;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        accounting = intern(accounting);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(Username other) {
        return getKey().compareTo(other.getKey());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    public static final String COLUMN_USERNAME = "username";
    @SchemaColumn(order=0, name=COLUMN_USERNAME, index=IndexType.PRIMARY_KEY, description="the unique username")
    public UserId getUsername() {
        return getKey();
    }

    static final String COLUMN_ACCOUNTING = "accounting";
    @SchemaColumn(order=1, name=COLUMN_ACCOUNTING, index=IndexType.INDEXED, description="the business that this user is part of")
    public Business getBusiness() throws RemoteException {
    	return getConnector().getBusinesses().get(accounting);
    }

    static final String COLUMN_DISABLE_LOG = "disable_log";
    @SchemaColumn(order=2, name=COLUMN_DISABLE_LOG, index=IndexType.INDEXED, description="indicates that the username is disabled")
    public DisableLog getDisableLog() throws RemoteException {
        if(disableLog==null) return null;
        return getConnector().getDisableLogs().get(disableLog);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    @Override
    public com.aoindustries.aoserv.client.dto.Username getDto() {
        return new com.aoindustries.aoserv.client.dto.Username(getDto(getKey()), getDto(accounting), disableLog);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject> addDependencies(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = super.addDependencies(unionSet);
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getBusiness());
        // Caused loop in dependency DAG: AOServObjectUtils.addDependencySet(unionSet, getDisableLog());
        return unionSet;
    }

    @Override
    protected UnionSet<AOServObject> addDependentObjects(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = super.addDependentObjects(unionSet);
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getBusinessAdministrator());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getLinuxAccounts());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getMysqlUsers());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getPostgresUsers());
        return unionSet;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public BusinessAdministrator getBusinessAdministrator() throws RemoteException {
    	return getConnector().getBusinessAdministrators().filterUnique(BusinessAdministrator.COLUMN_USERNAME, this);
    }

    public IndexedSet<LinuxAccount> getLinuxAccounts() throws RemoteException {
        return getConnector().getLinuxAccounts().filterIndexed(LinuxAccount.COLUMN_USERNAME, this);
    }

    public IndexedSet<MySQLUser> getMysqlUsers() throws RemoteException {
        return getConnector().getMysqlUsers().filterIndexed(MySQLUser.COLUMN_USERNAME, this);
    }

    public IndexedSet<PostgresUser> getPostgresUsers() throws RemoteException {
        return getConnector().getPostgresUsers().filterIndexed(PostgresUser.COLUMN_USERNAME, this);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Password Protected">
    @Override
    public AOServCommand<List<PasswordChecker.Result>> getCheckPasswordCommand(String password) {
        return new CheckUsernamePasswordCommand(this, password);
    }

    @Override
    public AOServCommand<Void> getSetPasswordCommand(String plaintext) {
        return new SetUsernamePasswordCommand(this, plaintext);
    }
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
	    getConnector().getBusinessAdministrators().addBusinessAdministrator(
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
	    getConnector().getLinuxAccounts().addLinuxAccount(
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
    	return getConnector().getMysqlUsers().addMySQLUser(pkey, mysqlServer, host);
    }

    public void addPostgresUser() throws IOException, SQLException {
        getConnector().getPostgresUsers().addPostgresUser(pkey);
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
        if(disableLog!=null) return false;
        LinuxAccount la=getLinuxAccount();
        if(la!=null && la.disableLog==null) return false;
        for(MySQLUser mu : getMySQLUsers()) if(mu.disableLog==null) return false;
        PostgresUser pu=getPostgresUser();
        if(pu!=null && pu.disableLog==null) return false;
        return true;
    }
    
    public boolean canEnable() throws SQLException, IOException {
        DisableLog dl=getDisableLog();
        if(dl==null) return false;
        else return dl.canEnable() && getBusiness().disableLog==null;
    }

    public void disable(DisableLog dl) throws IOException, SQLException {
        getConnector().requestUpdateIL(true, AOServProtocol.CommandID.DISABLE, SchemaTable.TableID.USERNAMES, dl.pkey, pkey);
    }
    
    public void enable() throws IOException, SQLException {
        getConnector().requestUpdateIL(true, AOServProtocol.CommandID.ENABLE, SchemaTable.TableID.USERNAMES, pkey);
    }

    public boolean isDisabled() {
        return disableLog!=null;
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

    public boolean isUsed() throws IOException, SQLException {
    	return
            getLinuxAccount()!=null
            || getBusinessAdministrator()!=null
            || !getMySQLUsers().isEmpty()
            || getPostgresUser()!=null
    	;
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() throws SQLException, IOException {
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
    	getConnector().requestUpdateIL(
            true,
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.USERNAMES,
            pkey
    	);
    }
     */
    // </editor-fold>
}