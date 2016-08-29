/*
 * Copyright 2001-2013, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * An <code>HttpdJBossSite</code> indicates that an <code>HttpdSite</code>
 * uses JBoss as its servlet engine.  The servlet engine may be 
 * configured in several ways, only what is common to every type of 
 * JBoss installation is stored in <code>HttpdJBossSite</code>.
 *
 * @see  HttpdSite
 *
 * @author  AO Industries, Inc.
 */

final public class HttpdJBossSite extends CachedObjectIntegerKey<HttpdJBossSite> {

	static final int COLUMN_TOMCAT_SITE=0;
	static final String COLUMN_TOMCAT_SITE_name = "tomcat_site";

	private int version;
	int jnpBind,
		webserverBind,
		rmiBind,
		hypersonicBind,
		jmxBind;

	@Override
	Object getColumnImpl(int i) {
		if(i==COLUMN_TOMCAT_SITE) return pkey;
		if(i==1) return version;	
		if(i==2) return jnpBind;
		if(i==3) return webserverBind;
		if(i==4) return rmiBind;
		if(i==5) return hypersonicBind;
		if(i==6) return jmxBind;
		throw new IllegalArgumentException("Invalid index: "+i);
	}

	public HttpdJBossVersion getHttpdJBossVersion() throws SQLException, IOException {
		HttpdJBossVersion obj=table.connector.getHttpdJBossVersions().get(version);
		if(obj==null) throw new SQLException("Unable to find HttpdJBossVersion: "+version);
		return obj;
	}

	public HttpdTomcatSite getHttpdTomcatSite() throws SQLException, IOException {
		HttpdTomcatSite obj=table.connector.getHttpdTomcatSites().get(pkey);
		if(obj==null) throw new SQLException("Unable to find HttpdTomcatSite: "+pkey);
		return obj;
	}

	public NetBind getHypersonicBind() throws IOException, SQLException {
		NetBind obj=table.connector.getNetBinds().get(hypersonicBind);
		if(obj==null) throw new SQLException("Unable to find NetBind: "+hypersonicBind);
		return obj;
	}

	public NetBind getJmxBind() throws IOException, SQLException {
		NetBind obj=table.connector.getNetBinds().get(jmxBind);
		if(obj==null) throw new SQLException("Unable to find NetBind: "+jmxBind);
		return obj;
	}

	public NetBind getJnpBind() throws IOException, SQLException {
		NetBind obj=table.connector.getNetBinds().get(jnpBind);
		if(obj==null) throw new SQLException("Unable to find NetBind: "+jnpBind);
		return obj;
	}

	public NetBind getRmiBind() throws SQLException, IOException {
		NetBind obj=table.connector.getNetBinds().get(rmiBind);
		if(obj==null) throw new SQLException("Unable to find NetBind: "+rmiBind);
		return obj;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.HTTPD_JBOSS_SITES;
	}

	public NetBind getWebserverBind() throws IOException, SQLException {
		NetBind obj=table.connector.getNetBinds().get(webserverBind);
		if(obj==null) throw new SQLException("Unable to find NetBind: "+webserverBind);
		return obj;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey=result.getInt(1);
		version=result.getInt(2);
		jnpBind=result.getInt(3);
		webserverBind=result.getInt(4);
		rmiBind=result.getInt(5);
		hypersonicBind=result.getInt(6);
		jmxBind=result.getInt(7);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readCompressedInt();
		version=in.readCompressedInt();
		jnpBind=in.readCompressedInt();
		webserverBind=in.readCompressedInt();
		rmiBind=in.readCompressedInt();
		hypersonicBind=in.readCompressedInt();
		jmxBind=in.readCompressedInt();
	}

	@Override
	String toStringImpl() throws SQLException, IOException {
		return getHttpdTomcatSite().toStringImpl();
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(version);
		out.writeCompressedInt(jnpBind);
		out.writeCompressedInt(webserverBind);
		out.writeCompressedInt(rmiBind);
		out.writeCompressedInt(hypersonicBind);
		out.writeCompressedInt(jmxBind);
	}
}
