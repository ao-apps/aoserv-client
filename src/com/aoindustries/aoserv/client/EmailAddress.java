package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.sql.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * An <code>EmailAddress</code> represents a unique email
 * address hosted on an AOServ server.  This email address
 * may only go to one destination,
 *
 *
 * @see  BlackholeEmailAddress
 * @see  EmailForwarding
 * @see  EmailListAddress
 * @see  EmailPipeAddress
 * @see  LinuxAccAddress
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class EmailAddress extends CachedObjectIntegerKey<EmailAddress> implements Removable {

    static final int
        COLUMN_PKEY=0,
        COLUMN_DOMAIN=2
    ;
    static final String COLUMN_DOMAIN_name = "domain";
    static final String COLUMN_ADDRESS_name = "address";

    String address;
    int domain;

    public int addEmailForwarding(String destination) throws IOException, SQLException {
        return table.connector.getEmailForwardings().addEmailForwarding(this, destination);
    }

    public String getAddress() {
	return address;
    }

    public BlackholeEmailAddress getBlackholeEmailAddress() throws IOException, SQLException {
	return table.connector.getBlackholeEmailAddresses().get(pkey);
    }

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case 1: return address;
            case COLUMN_DOMAIN: return Integer.valueOf(domain);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public EmailDomain getDomain() throws SQLException, IOException {
	EmailDomain domainObject = table.connector.getEmailDomains().get(domain);
	if (domainObject == null) throw new SQLException("Unable to find EmailDomain: " + domain);
	return domainObject;
    }

    public List<EmailForwarding> getEmailForwardings() throws IOException, SQLException {
	return table.connector.getEmailForwardings().getEmailForwardings(this);
    }

    public List<EmailForwarding> getEnabledEmailForwardings() throws SQLException, IOException {
	return table.connector.getEmailForwardings().getEnabledEmailForwardings(this);
    }

    public EmailForwarding getEmailForwarding(String destination) throws IOException, SQLException {
	return table.connector.getEmailForwardings().getEmailForwarding(this, destination);
    }

    public List<EmailList> getEmailLists() throws IOException, SQLException {
	return table.connector.getEmailListAddresses().getEmailLists(this);
    }

    public List<EmailListAddress> getEmailListAddresses() throws IOException, SQLException {
	return table.connector.getEmailListAddresses().getEmailListAddresses(this);
    }

    public List<EmailListAddress> getEnabledEmailListAddresses() throws IOException, SQLException {
	return table.connector.getEmailListAddresses().getEnabledEmailListAddresses(this);
    }

    public EmailListAddress getEmailListAddress(EmailList list) throws IOException, SQLException {
        return table.connector.getEmailListAddresses().getEmailListAddress(this, list);
    }

    public List<EmailPipe> getEmailPipes() throws IOException, SQLException {
	return table.connector.getEmailPipeAddresses().getEmailPipes(this);
    }

    public List<EmailPipeAddress> getEmailPipeAddresses() throws IOException, SQLException {
	return table.connector.getEmailPipeAddresses().getEmailPipeAddresses(this);
    }

    public List<EmailPipeAddress> getEnabledEmailPipeAddresses() throws IOException, SQLException {
	return table.connector.getEmailPipeAddresses().getEnabledEmailPipeAddresses(this);
    }

    public EmailPipeAddress getEmailPipeAddress(EmailPipe pipe) throws IOException, SQLException {
        return table.connector.getEmailPipeAddresses().getEmailPipeAddress(this, pipe);
    }

    public List<LinuxServerAccount> getLinuxServerAccounts() throws IOException, SQLException {
	return table.connector.getLinuxAccAddresses().getLinuxServerAccounts(this);
    }

    public List<LinuxAccAddress> getLinuxAccAddresses() throws IOException, SQLException {
	return table.connector.getLinuxAccAddresses().getLinuxAccAddresses(this);
    }

    public LinuxAccAddress getLinuxAccAddress(LinuxServerAccount lsa) throws IOException, SQLException {
        return table.connector.getLinuxAccAddresses().getLinuxAccAddress(this, lsa);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.EMAIL_ADDRESSES;
    }

    public void init(ResultSet result) throws SQLException {
	pkey = result.getInt(1);
	address = result.getString(2);
	domain = result.getInt(3);
    }

    public boolean isUsed() throws IOException, SQLException {
        // Anything using this address must be removable
        return
            getBlackholeEmailAddress()!=null
            || !getEmailForwardings().isEmpty()
            || !getEmailListAddresses().isEmpty()
            || !getEmailPipeAddresses().isEmpty()
            || !getLinuxAccAddresses().isEmpty()
        ;
    }

    /**
     * Determines if an email address is in a valid <code>name@domain.com</code>
     * format.
     */
    public static boolean isValidEmailAddress(String name) {
	int pos=name.indexOf('@');
	if(pos==-1) return false;
	String address=name.substring(0, pos);
	if(!isValidFormat(address)) return false;
	return EmailDomain.isValidFormat(name.substring(pos+1));
    }

    public static boolean isValidFormat(String name) {
	int len = name.length();
        // May not be empty (previously used for catch-all addresses)
        if(len<=0) return false;
	for (int c = 0; c < len; c++) {
            char ch = name.charAt(c);
            if (
                (ch < 'a' || ch > 'z')
                && (ch < '0' || ch > '9')
                && ch != '.'
                && ch != '-'
                && ch != '&'
                && ch != '+'
                && ch != '_'
            ) return false;
	}
	return true;
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readCompressedInt();
	address=in.readUTF();
	domain=in.readCompressedInt();
    }

    public List<CannotRemoveReason> getCannotRemoveReasons(Locale userLocale) throws SQLException, IOException {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();

        // Everything using this address must be removable
        BlackholeEmailAddress bea=getBlackholeEmailAddress();
        if(bea!=null) reasons.addAll(bea.getCannotRemoveReasons(userLocale));

        for(EmailForwarding ef : getEmailForwardings()) reasons.addAll(ef.getCannotRemoveReasons(userLocale));

        for(EmailListAddress ela : table.connector.getEmailListAddresses().getEmailListAddresses(this)) reasons.addAll(ela.getCannotRemoveReasons(userLocale));

        for(EmailPipeAddress epa : getEmailPipeAddresses()) reasons.addAll(epa.getCannotRemoveReasons(userLocale));

        for(LinuxAccAddress laa : getLinuxAccAddresses()) reasons.addAll(laa.getCannotRemoveReasons(userLocale));

        // Cannot be used as any part of a majordomo list
        for(MajordomoList ml : table.connector.getMajordomoLists().getRows()) {
            if(
                ml.owner_listname_add==pkey
                || ml.listname_owner_add==pkey
                || ml.listname_approval_add==pkey
            ) {
                EmailDomain ed=ml.getMajordomoServer().getDomain();
                reasons.add(new CannotRemoveReason<MajordomoList>("Used by Majordomo list "+ml.getName()+'@'+ed.getDomain()+" on "+ed.getAOServer().getHostname(), ml));
            }
        }
        
        // Cannot be used as any part of a majordomo server
        for(MajordomoServer ms : table.connector.getMajordomoServers().getRows()) {
            if(
                ms.owner_majordomo_add==pkey
                || ms.majordomo_owner_add==pkey
            ) {
                EmailDomain ed=ms.getDomain();
                reasons.add(new CannotRemoveReason("Used by Majordomo server "+ed.getDomain()+" on "+ed.getAOServer().getHostname()));
            }
        }
        
        return reasons;
    }

    /**
     * Removes this <code>EmailAddress</code> any any references to it.  For example, if
     * this address is sent to a <code>LinuxAccount</code>, the address is disassociated
     * before it is removed.  For integrity, this entire operation is handled in a single
     * database transaction.
     */
    public void remove() throws IOException, SQLException {
    	table.connector.requestUpdateIL(
            true,
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.EMAIL_ADDRESSES,
            pkey
        );
    }

    @Override
    String toStringImpl(Locale userLocale) throws SQLException, IOException {
        return address+'@'+getDomain().getDomain();
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
	out.writeCompressedInt(pkey);
	out.writeUTF(address);
	out.writeCompressedInt(domain);
    }
}