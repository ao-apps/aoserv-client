/*
 * Copyright 2001-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.DomainName;
import com.aoindustries.aoserv.client.validator.InetAddress;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionSet;
import java.rmi.RemoteException;

/**
 * A <code>DnsRecord</code> is one line of a <code>DnsZone</code>
 * (name server zone file).
 *
 * @see  DnsZone
 *
 * @author  AO Industries, Inc.
 */
final public class DnsRecord extends AOServObjectIntegerKey<DnsRecord> implements BeanFactory<com.aoindustries.aoserv.client.beans.DnsRecord> /*, TODO: Removable */ {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private int zone;
    private String domain;
    private String type;
    final private Integer mxPriority;
    private InetAddress dataIpAddress;
    private DomainName dataDomainName;
    private String dataText;
    final private Integer dhcpAddress;
    final private Integer ttl;

    public DnsRecord(
        DnsRecordService<?,?> service,
        int resource,
        int zone,
        String domain,
        String type,
        Integer mxPriority,
        InetAddress dataIpAddress,
        DomainName dataDomainName,
        String dataText,
        Integer dhcpAddress,
        Integer ttl
    ) {
        super(service, resource);
        this.zone = zone;
        this.domain = domain;
        this.type = type;
        this.mxPriority = mxPriority;
        this.dataIpAddress = dataIpAddress;
        this.dataDomainName = dataDomainName;
        this.dataText = dataText;
        this.dhcpAddress = dhcpAddress;
        this.ttl = ttl;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        domain = intern(domain);
        type = intern(type);
        dataIpAddress = intern(dataIpAddress);
        dataDomainName = intern(dataDomainName);
        dataText = intern(dataText);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(DnsRecord other) throws RemoteException {
        int diff = zone==other.zone ? 0 : getZone().compareToImpl(other.getZone());
        if(diff!=0) return diff;
        diff = domain.compareTo(other.getDomain());
        if(diff!=0) return diff;
        diff = type==other.type ? 0 : getType().compareToImpl(other.getType()); // OK - interned
        if(diff!=0) return diff;
        diff = AOServObjectUtils.compare(mxPriority, other.mxPriority);
        if(diff!=0) return diff;
        diff = AOServObjectUtils.compare(dataIpAddress, other.dataIpAddress);
        if(diff!=0) return diff;
        diff = AOServObjectUtils.compare(dataDomainName, other.dataDomainName);
        if(diff!=0) return diff;
        return AOServObjectUtils.compare(dataText, other.dataText);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="resource", index=IndexType.PRIMARY_KEY, description="the resource id")
    public Resource getResource() throws RemoteException {
        return getService().getConnector().getResources().get(key);
    }

    static final String COLUMN_ZONE = "zone";
    @SchemaColumn(order=1, name=COLUMN_ZONE, index=IndexType.INDEXED, description="the zone as found in dns_zones")
    public DnsZone getZone() throws RemoteException {
        return getService().getConnector().getDnsZones().get(zone);
    }

    @SchemaColumn(order=2, name="domain", description="the first column in the zone files")
    public String getDomain() {
        return domain;
    }

    static final String COLUMN_TYPE = "type";
    @SchemaColumn(order=3, name=COLUMN_TYPE, index=IndexType.INDEXED, description="the type as found in dns_types")
    public DnsType getType() throws RemoteException {
        return getService().getConnector().getDnsTypes().get(type);
    }

    @SchemaColumn(order=4, name="mx_priority", description="the priority for the MX records")
    public Integer getMxPriority() {
        return mxPriority;
    }

    @SchemaColumn(order=5, name="data_ip_address", description="the destination IP address for A and AAAA records")
    public InetAddress getDataIpAddress() {
        return dataIpAddress;
    }

    @SchemaColumn(order=6, name="data_domain_name", description="the fully-qualitied domain name for CNAME, MX, NS, and PTR records")
    public DomainName getDataDomainName() {
        return dataDomainName;
    }

    @SchemaColumn(order=7, name="data_text", description="the text data for SPF and TXT records")
    public String getDataText() {
        return dataText;
    }

    @SchemaColumn(order=8, name="dhcp_address", description="the pkey of the IP address that will update this DNS record")
    public IPAddress getDhcpAddress() throws RemoteException {
        if(dhcpAddress==null) return null;
        return getService().getConnector().getIpAddresses().get(dhcpAddress);
    }

    @SchemaColumn(order=9, name="ttl", description="the record-specific TTL, if not provided will use the TTL of the zone")
    public Integer getTtl() {
        return ttl;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    @Override
    public com.aoindustries.aoserv.client.beans.DnsRecord getBean() {
        return new com.aoindustries.aoserv.client.beans.DnsRecord(key, zone, domain, type, mxPriority, getBean(dataIpAddress), getBean(dataDomainName), dataText, dhcpAddress, ttl);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject> addDependencies(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getResource());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getZone());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getType());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getDhcpAddress());
        return unionSet;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() throws RemoteException {
        StringBuilder SB=new StringBuilder();
        SB
            .append(zone)
            .append(": ")
            .append(domain)
            .append(" IN ")
            .append(type)
        ;
        if(mxPriority!=null) SB.append(' ').append(mxPriority);
        // Each record type
        if(dataIpAddress!=null) SB.append(' ').append(dataIpAddress);
        else if(dataDomainName!=null) SB.append(' ').append(getRelativeDataDomainName());
        else if(dataText!=null) {
            SB.append(' ');
            if(!dataText.startsWith("\"")) SB.append('"');
            SB.append(dataText);
            if(!dataText.endsWith("\"")) SB.append('"');
        } else throw new AssertionError();
        if(ttl!=null) SB.append(' ').append(ttl);
        return SB.toString();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Zone File Utilities">
    /**
     * Gets the domain, but in fully-qualified, absolute path (with trailing period).
     */
    public String getFullyQualifiedDomain() throws RemoteException {
        if(domain=="@") return getZone().getZone().toString()+'.'; // OK - interned
        if(domain.endsWith(".")) return domain;
        return domain+'.'+getZone().getZone().toString()+'.';
    }

    /**
     * Gets the data domain in relative form if in same domain or fully-qualified if in different domain.
     */
    public String getRelativeDataDomainName() throws RemoteException {
        String dotZone = "."+getZone().getZone().toString();
        String dataDomain = dataDomainName.toString();
        if(dataDomain.startsWith(dotZone)) return dataDomain.substring(0, dataDomain.length()-dotZone.length()); // Relative
        else return dataDomain+'.'; // Absolute
    }

    /**
     * Checks if this record conflicts with the provided record, meaning they may not both exist
     * in a zone file at the same time.  The current conflicts checked are:
     * <ol>
     *   <li>CNAME must exist by itself, and only one CNAME maximum, per domain</li>
     * </ol>
     *
     * @return <code>true</code> if there is a conflict, <code>false</code> if the records may coexist.
     */
    public boolean hasConflict(DnsRecord other) throws RemoteException {
        String domain1 = getFullyQualifiedDomain();
        String domain2 = other.getFullyQualifiedDomain();
        
        // Look for CNAME conflict
        if(domain1.equals(domain2)) {
            // If either (or both) are CNAME, there is a conflict
            if(
                type==DnsType.CNAME // OK - interned
                || other.type==DnsType.CNAME // OK - interned
            ) {
                return true;
            }
        }
        return false;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    /* TODO
    public List<CannotRemoveReason> getCannotRemoveReasons() {
        return Collections.emptyList();
    }

    public void remove() throws IOException, SQLException {
    	getService().getConnector().requestUpdateIL(true, AOServProtocol.CommandID.REMOVE, SchemaTable.TableID.DNS_RECORDS, pkey);
    }
     */
    // </editor-fold>
}