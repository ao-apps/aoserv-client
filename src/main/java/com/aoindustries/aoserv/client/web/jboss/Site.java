/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aoserv-client.
 *
 * aoserv-client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aoserv-client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aoserv-client.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.aoserv.client.web.jboss;

import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.net.Bind;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
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

final public class Site extends CachedObjectIntegerKey<Site> {

	static final int COLUMN_TOMCAT_SITE=0;
	static final String COLUMN_TOMCAT_SITE_name = "tomcat_site";

	private int version;
	int jnpBind,
		webserverBind,
		rmiBind,
		hypersonicBind,
		jmxBind;

	@Override
	protected Object getColumnImpl(int i) {
		if(i==COLUMN_TOMCAT_SITE) return pkey;
		if(i==1) return version;	
		if(i==2) return jnpBind;
		if(i==3) return webserverBind;
		if(i==4) return rmiBind;
		if(i==5) return hypersonicBind;
		if(i==6) return jmxBind;
		throw new IllegalArgumentException("Invalid index: "+i);
	}

	public Version getHttpdJBossVersion() throws SQLException, IOException {
		Version obj=table.getConnector().getWeb_jboss().getVersion().get(version);
		if(obj==null) throw new SQLException("Unable to find HttpdJBossVersion: "+version);
		return obj;
	}

	public com.aoindustries.aoserv.client.web.tomcat.Site getHttpdTomcatSite() throws SQLException, IOException {
		com.aoindustries.aoserv.client.web.tomcat.Site obj=table.getConnector().getWeb_tomcat().getSite().get(pkey);
		if(obj==null) throw new SQLException("Unable to find HttpdTomcatSite: "+pkey);
		return obj;
	}

	public Bind getHypersonicBind() throws IOException, SQLException {
		Bind obj=table.getConnector().getNet().getBind().get(hypersonicBind);
		if(obj==null) throw new SQLException("Unable to find NetBind: "+hypersonicBind);
		return obj;
	}

	public Bind getJmxBind() throws IOException, SQLException {
		Bind obj=table.getConnector().getNet().getBind().get(jmxBind);
		if(obj==null) throw new SQLException("Unable to find NetBind: "+jmxBind);
		return obj;
	}

	public Bind getJnpBind() throws IOException, SQLException {
		Bind obj=table.getConnector().getNet().getBind().get(jnpBind);
		if(obj==null) throw new SQLException("Unable to find NetBind: "+jnpBind);
		return obj;
	}

	public Bind getRmiBind() throws SQLException, IOException {
		Bind obj=table.getConnector().getNet().getBind().get(rmiBind);
		if(obj==null) throw new SQLException("Unable to find NetBind: "+rmiBind);
		return obj;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.HTTPD_JBOSS_SITES;
	}

	public Bind getWebserverBind() throws IOException, SQLException {
		Bind obj=table.getConnector().getNet().getBind().get(webserverBind);
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
	public String toStringImpl() throws SQLException, IOException {
		return getHttpdTomcatSite().toStringImpl();
	}

	@Override
	public void write(CompressedDataOutputStream out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(version);
		out.writeCompressedInt(jnpBind);
		out.writeCompressedInt(webserverBind);
		out.writeCompressedInt(rmiBind);
		out.writeCompressedInt(hypersonicBind);
		out.writeCompressedInt(jmxBind);
	}
}
