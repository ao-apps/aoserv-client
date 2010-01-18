/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.Set;

/**
 * The <code>DnsType</code> associated with a <code>DnsRecord</code> provides
 * details about which values should be used in the data field, and whether
 * a MX priority should exist.
 *
 * @see  DnsRecord
 *
 * @author  AO Industries, Inc.
 */
final public class DnsType extends AOServObjectStringKey<DnsType> implements BeanFactory<com.aoindustries.aoserv.client.beans.DnsType> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    /**
     * The possible <code>DnsType</code>s.
     */
    public static final String
        A="A",
        AAAA="AAAA",
        CNAME="CNAME",
        MX="MX",
        NS="NS",
        PTR="PTR",
        SPF="SPF",
        TXT="TXT"
    ;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    public DnsType(DnsTypeService<?,?> service, String type) {
        super(service, type);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="type", index=IndexType.PRIMARY_KEY, description="the type name")
    public String getType() {
        return key;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.DnsType getBean() {
        return new com.aoindustries.aoserv.client.beans.DnsType(key);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getDnsRecords()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl(Locale userLocale) throws RemoteException {
        return ApplicationResources.accessor.getMessage(userLocale, "DnsType."+key+".toString");
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public IndexedSet<DnsRecord> getDnsRecords() throws RemoteException {
        return getService().getConnector().getDnsRecords().filterIndexed(DnsRecord.COLUMN_TYPE, this);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    /* TODO
    public void checkData(String data) throws IllegalArgumentException {
        checkData(data, paramIpv4;);
    }

    public static void checkData(String data, boolean isParamIP) throws IllegalArgumentException {
	String origDest=date;
	if(data.length()==0) throw new IllegalArgumentException("Data may not by empty");

	if(isParamIP) {
            if(!IPAddress.isValidIPAddress(data)) throw new IllegalArgumentException("Invalid data IP address: "+data);
	} else {
            // May end with a single .
            if(data.charAt(data.length()-1)=='.') data=data.substring(0, data.length()-1);
            if(
                !DnsZoneService.isValidHostnamePart(data)
                && !EmailDomain.isValidFormat(data)
            ) throw new IllegalArgumentException("Invalid data hostname: "+origDest);
	}
    }
     */
    // </editor-fold>
}