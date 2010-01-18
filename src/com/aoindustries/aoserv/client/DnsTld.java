/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.DomainName;
import com.aoindustries.table.IndexType;
import java.util.Locale;

/**
 * A <code>DnsTld</code> is a name server top level domain.  A top level domain
 * is a domain that is one level above that which is controlled by AO Industries'
 * name servers.  Some common examples include <code>com</code>, <code>net</code>,
 * <code>org</code>, <code>co.uk</code>, <code>aq</code> (OK - not so common), and
 * <code>med.pro</code>.  The domains added to the name servers must be in the
 * format <code>subdomain</code>.<code>dns_tld</code>, where <code>subdomain</code>
 * is a word without dots (<code>.</code>), and <code>dns_tld</code> is one of
 * the top level domains in the database.  If a top level domain does not exist
 * that properly should, please contact AO Industries to have it added.
 *
 * @see  DnsZone
 *
 * @author  AO Industries, Inc.
 */
final public class DnsTld extends AOServObjectDomainNameKey<DnsTld> implements BeanFactory<com.aoindustries.aoserv.client.beans.DnsTld> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    public DnsTld(DnsTldService<?,?> service, DomainName domain) {
        super(service, domain);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="domain", index=IndexType.PRIMARY_KEY, description="the unique top-level domain")
    public DomainName getDomain() {
        return key;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.DnsTld getBean() {
        return new com.aoindustries.aoserv.client.beans.DnsTld(key.getBean());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    String getDescription(Locale userLocale) {
        return ApplicationResources.accessor.getMessage(userLocale, "DnsTld.description."+key);
    }
    // </editor-fold>
}