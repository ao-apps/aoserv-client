package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.sql.*;
import com.aoindustries.util.*;
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

    String address;
    int domain;

    public int addEmailForwarding(String destination) {
        return table.connector.emailForwardings.addEmailForwarding(this, destination);
    }

    public String getAddress() {
	return address;
    }

    public BlackholeEmailAddress getBlackholeEmailAddress() {
	return table.connector.blackholeEmailAddresses.get(pkey);
    }

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case 1: return address;
            case COLUMN_DOMAIN: return Integer.valueOf(domain);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public EmailDomain getDomain() {
	EmailDomain domainObject = table.connector.emailDomains.get(domain);
	if (domainObject == null) throw new WrappedException(new SQLException("Unable to find EmailDomain: " + domain));
	return domainObject;
    }

    public List<EmailForwarding> getEmailForwardings() {
	return table.connector.emailForwardings.getEmailForwardings(this);
    }

    public EmailForwarding getEmailForwarding(String destination) {
	return table.connector.emailForwardings.getEmailForwarding(this, destination);
    }

    public List<EmailList> getEmailLists() {
	return table.connector.emailListAddresses.getEmailLists(this);
    }

    public List<EmailListAddress> getEmailListAddresses() {
	return table.connector.emailListAddresses.getEmailListAddresses(this);
    }

    public List<EmailListAddress> getEnabledEmailListAddresses() {
	return table.connector.emailListAddresses.getEnabledEmailListAddresses(this);
    }

    public EmailListAddress getEmailListAddress(EmailList list) {
        return table.connector.emailListAddresses.getEmailListAddress(this, list);
    }

    public List<EmailPipe> getEmailPipes() {
	return table.connector.emailPipeAddresses.getEmailPipes(this);
    }

    public List<EmailPipeAddress> getEmailPipeAddresses() {
	return table.connector.emailPipeAddresses.getEmailPipeAddresses(this);
    }

    public List<EmailPipeAddress> getEnabledEmailPipeAddresses() {
	return table.connector.emailPipeAddresses.getEnabledEmailPipeAddresses(this);
    }

    public EmailPipeAddress getEmailPipeAddress(EmailPipe pipe) {
        return table.connector.emailPipeAddresses.getEmailPipeAddress(this, pipe);
    }

    public List<LinuxAccount> getLinuxAccounts() {
	return table.connector.linuxAccAddresses.getLinuxAccounts(this);
    }

    public List<LinuxAccAddress> getLinuxAccAddresses() {
	return table.connector.linuxAccAddresses.getLinuxAccAddresses(this);
    }

    public LinuxAccAddress getLinuxAccAddress(LinuxAccount la) {
        return table.connector.linuxAccAddresses.getLinuxAccAddress(this, la);
    }

    protected int getTableIDImpl() {
	return SchemaTable.EMAIL_ADDRESSES;
    }

    void initImpl(ResultSet result) throws SQLException {
	pkey = result.getInt(1);
	address = result.getString(2);
	domain = result.getInt(3);
    }

    public boolean isUsed() {
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

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();

        // Everything using this address must be removable
        BlackholeEmailAddress bea=getBlackholeEmailAddress();
        if(bea!=null) reasons.addAll(bea.getCannotRemoveReasons());

        for(EmailForwarding ef : getEmailForwardings()) reasons.addAll(ef.getCannotRemoveReasons());

        for(EmailListAddress ela : table.connector.emailListAddresses.getEmailListAddresses(this)) reasons.addAll(ela.getCannotRemoveReasons());

        for(EmailPipeAddress epa : getEmailPipeAddresses()) reasons.addAll(epa.getCannotRemoveReasons());

        for(LinuxAccAddress laa : getLinuxAccAddresses()) reasons.addAll(laa.getCannotRemoveReasons());

        // Cannot be used as any part of a majordomo list
        for(MajordomoList ml : table.connector.majordomoLists.getRows()) {
            if(
                ml.owner_listname_add==pkey
                || ml.listname_owner_add==pkey
                || ml.listname_approval_add==pkey
            ) {
                EmailDomain ed=ml.getMajordomoServer().getDomain();
                reasons.add(new CannotRemoveReason<MajordomoList>("Used by Majordomo list "+ml.getName()+'@'+ed.getDomain()+" on "+ed.getAOServer().getServer().getHostname(), ml));
            }
        }
        
        // Cannot be used as any part of a majordomo server
        for(MajordomoServer ms : table.connector.majordomoServers.getRows()) {
            if(
                ms.owner_majordomo_add==pkey
                || ms.majordomo_owner_add==pkey
            ) {
                EmailDomain ed=ms.getDomain();
                reasons.add(new CannotRemoveReason("Used by Majordomo server "+ed.getDomain()+" on "+ed.getAOServer().getServer().getHostname()));
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
    public void remove() {
	table.connector.requestUpdateIL(
            AOServProtocol.REMOVE,
            SchemaTable.EMAIL_ADDRESSES,
            pkey
	);
    }

    String toStringImpl() {
        return address+'@'+getDomain().getDomain();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeCompressedInt(pkey);
	out.writeUTF(address);
	out.writeCompressedInt(domain);
    }
}