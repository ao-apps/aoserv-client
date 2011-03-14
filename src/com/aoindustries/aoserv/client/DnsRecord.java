/*
 * Copyright 2001-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.WrappedException;
import java.rmi.RemoteException;

/**
 * A <code>DnsRecord</code> is one line of a <code>DnsZone</code>
 * (name server zone file).
 *
 * @see  DnsZone
 *
 * @author  AO Industries, Inc.
 */
final public class DnsRecord extends Resource implements Comparable<DnsRecord>, DtoFactory<com.aoindustries.aoserv.client.dto.DnsRecord> /*, TODO: Removable */ {

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private static final long serialVersionUID = 3037419132925775543L;

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
        AOServConnector connector,
        int pkey,
        String resourceType,
        AccountingCode accounting,
        long created,
        UserId createdBy,
        Integer disableLog,
        long lastEnabled,
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
        super(connector, pkey, resourceType, accounting, created, createdBy, disableLog, lastEnabled);
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
    public int compareTo(DnsRecord other) {
        try {
            int diff = zone==other.zone ? 0 : getZone().compareTo(other.getZone());
            if(diff!=0) return diff;
            diff = domain.compareTo(other.getDomain());
            if(diff!=0) return diff;
            diff = type==other.type ? 0 : getType().compareTo(other.getType()); // OK - interned
            if(diff!=0) return diff;
            diff = compare(mxPriority, other.mxPriority);
            if(diff!=0) return diff;
            diff = compare(dataIpAddress, other.dataIpAddress);
            if(diff!=0) return diff;
            diff = compare(dataDomainName, other.dataDomainName);
            if(diff!=0) return diff;
            return compare(dataText, other.dataText);
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    public static final MethodColumn COLUMN_ZONE = getMethodColumn(DnsRecord.class, "zone");
    @DependencySingleton
    @SchemaColumn(order=RESOURCE_LAST_COLUMN+1, index=IndexType.INDEXED, description="the zone as found in dns_zones")
    public DnsZone getZone() throws RemoteException {
        return getConnector().getDnsZones().get(zone);
    }

    @SchemaColumn(order=RESOURCE_LAST_COLUMN+2, description="the first column in the zone files")
    public String getDomain() {
        return domain;
    }

    public static final MethodColumn COLUMN_TYPE = getMethodColumn(DnsRecord.class, "type");
    @DependencySingleton
    @SchemaColumn(order=RESOURCE_LAST_COLUMN+3, index=IndexType.INDEXED, description="the type as found in dns_types")
    public DnsType getType() throws RemoteException {
        return getConnector().getDnsTypes().get(type);
    }

    @SchemaColumn(order=RESOURCE_LAST_COLUMN+4, description="the priority for the MX records")
    public Integer getMxPriority() {
        return mxPriority;
    }

    @SchemaColumn(order=RESOURCE_LAST_COLUMN+5, description="the destination IP address for A and AAAA records")
    public InetAddress getDataIpAddress() {
        return dataIpAddress;
    }

    @SchemaColumn(order=RESOURCE_LAST_COLUMN+6, description="the fully-qualitied domain name for CNAME, MX, NS, and PTR records")
    public DomainName getDataDomainName() {
        return dataDomainName;
    }

    @SchemaColumn(order=RESOURCE_LAST_COLUMN+7, description="the text data for SPF and TXT records")
    public String getDataText() {
        return dataText;
    }

    public static final MethodColumn COLUMN_DHCP_ADDRESS = getMethodColumn(DnsRecord.class, "dhcpAddress");
    @DependencySingleton
    @SchemaColumn(order=RESOURCE_LAST_COLUMN+8, index=IndexType.INDEXED, description="the pkey of the IP address that will update this DNS record")
    public IPAddress getDhcpAddress() throws RemoteException {
        if(dhcpAddress==null) return null;
        return getConnector().getIpAddresses().get(dhcpAddress);
    }

    @SchemaColumn(order=RESOURCE_LAST_COLUMN+9, description="the record-specific TTL, if not provided will use the TTL of the zone")
    public Integer getTtl() {
        return ttl;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public DnsRecord(AOServConnector connector, com.aoindustries.aoserv.client.dto.DnsRecord dto) throws ValidationException {
        this(
            connector,
            dto.getPkey(),
            dto.getResourceType(),
            getAccountingCode(dto.getAccounting()),
            getTimeMillis(dto.getCreated()),
            getUserId(dto.getCreatedBy()),
            dto.getDisableLog(),
            getTimeMillis(dto.getLastEnabled()),
            dto.getZone(),
            dto.getDomain(),
            dto.getType(),
            dto.getMxPriority(),
            getInetAddress(dto.getDataIpAddress()),
            getDomainName(dto.getDataDomainName()),
            dto.getDataText(),
            dto.getDhcpAddress(),
            dto.getTtl()
        );
    }

    @Override
    public com.aoindustries.aoserv.client.dto.DnsRecord getDto() {
        return new com.aoindustries.aoserv.client.dto.DnsRecord(
            key,
            getResourceTypeName(),
            getDto(getAccounting()),
            created,
            getDto(getCreatedByUsername()),
            disableLog,
            lastEnabled,
            zone,
            domain,
            type,
            mxPriority,
            getDto(dataIpAddress),
            getDto(dataDomainName),
            dataText,
            dhcpAddress,
            ttl
        );
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
    	getConnector().requestUpdateIL(true, AOServProtocol.CommandID.REMOVE, SchemaTable.TableID.DNS_RECORDS, pkey);
    }
     */
    // </editor-fold>
}