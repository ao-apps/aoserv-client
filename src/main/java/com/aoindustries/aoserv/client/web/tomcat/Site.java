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
package com.aoindustries.aoserv.client.web.tomcat;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.validator.UnixPath;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * An <code>HttpdTomcatSite</code> indicates that an <code>HttpdSite</code>
 * uses the Jakarta Tomcat project as its servlet engine.  The servlet
 * engine may be configured in several ways, only what is common to
 * every type of Tomcat installation is stored in <code>HttpdTomcatSite</code>.
 *
 * @see  Site
 * @see  PrivateTomcatSite
 *
 * @author  AO Industries, Inc.
 */
final public class Site extends CachedObjectIntegerKey<Site> {

	static final int COLUMN_HTTPD_SITE=0;
	public static final String COLUMN_HTTPD_SITE_name = "httpd_site";

	private int version;
	private boolean blockWebinf;

	private boolean use_apache; // Only used for protocol compatibility on the server side

	/**
	 * The minimum amount of time in milliseconds between Java VM starts.
	 */
	public static final int MINIMUM_START_JVM_DELAY=30000;

	/**
	 * The minimum amount of time in milliseconds between Java VM start and stop.
	 */
	public static final int MINIMUM_STOP_JVM_DELAY=15000;

	public int addHttpdTomcatContext(
		String className,
		boolean cookies,
		boolean crossContext,
		UnixPath docBase,
		boolean override,
		String path,
		boolean privileged,
		boolean reloadable,
		boolean useNaming,
		String wrapperClass,
		int debug,
		UnixPath workDir,
		boolean serverXmlConfigured
	) throws IOException, SQLException {
		return table.getConnector().getHttpdTomcatContexts().addHttpdTomcatContext(
			this,
			className,
			cookies,
			crossContext,
			docBase,
			override,
			path,
			privileged,
			reloadable,
			useNaming,
			wrapperClass,
			debug,
			workDir,
			serverXmlConfigured
		);
	}

	public int addJkMount(
		String path,
		boolean mount
	) throws IOException, SQLException {
		return table.getConnector().getHttpdTomcatSiteJkMounts().addHttpdTomcatSiteJkMount(
			this,
			path,
			mount
		);
	}

	/**
	 * Determines if the API user is allowed to stop the Java virtual machine associated
	 * with this site.
	 */
	public boolean canStop() throws SQLException, IOException {
		if(getHttpdSite().isDisabled()) return false;
		SharedTomcatSite shr=getHttpdTomcatSharedSite();
		if(shr!=null) return shr.canStop();
		return true;
	}

	/**
	 * Determines if the API user is allowed to start the Java virtual machine associated
	 * with this site.
	 */
	public boolean canStart() throws SQLException, IOException {
		if(getHttpdSite().isDisabled()) return false;
		SharedTomcatSite shr=getHttpdTomcatSharedSite();
		if(shr!=null) return shr.canStart();
		return true;
	}

	@Override
	protected Object getColumnImpl(int i) {
		if(i == COLUMN_HTTPD_SITE) return pkey;
		if(i == 1) return version;
		if(i == 2) return blockWebinf;
		throw new IllegalArgumentException("Invalid index: "+i);
	}

	public com.aoindustries.aoserv.client.web.jboss.Site getHttpdJBossSite() throws SQLException, IOException {
		return table.getConnector().getHttpdJBossSites().get(pkey);
	}

	public com.aoindustries.aoserv.client.web.Site getHttpdSite() throws SQLException, IOException {
		com.aoindustries.aoserv.client.web.Site obj=table.getConnector().getHttpdSites().get(pkey);
		if(obj==null) throw new SQLException("Unable to find HttpdSite: "+pkey);
		return obj;
	}

	public Context getHttpdTomcatContext(String path) throws IOException, SQLException {
		return table.getConnector().getHttpdTomcatContexts().getHttpdTomcatContext(this, path);
	}

	public List<Context> getHttpdTomcatContexts() throws IOException, SQLException {
		return table.getConnector().getHttpdTomcatContexts().getHttpdTomcatContexts(this);
	}

	public SharedTomcatSite getHttpdTomcatSharedSite() throws IOException, SQLException {
		return table.getConnector().getHttpdTomcatSharedSites().get(pkey);
	}

	public PrivateTomcatSite getHttpdTomcatStdSite() throws IOException, SQLException {
		return table.getConnector().getHttpdTomcatStdSites().get(pkey);
	}

	public Version getHttpdTomcatVersion() throws SQLException, IOException {
		Version obj=table.getConnector().getHttpdTomcatVersions().get(version);
		if(obj==null) throw new SQLException("Unable to find HttpdTomcatVersion: "+version);
		if(
			obj.getTechnologyVersion(table.getConnector()).getOperatingSystemVersion(table.getConnector()).getPkey()
			!= getHttpdSite().getAoServer().getServer().getOperatingSystemVersion_id()
		) {
			throw new SQLException("resource/operating system version mismatch on HttpdTomcatSite: #"+pkey);
		}
		// Make sure version shared JVM if is a shared site
		SharedTomcatSite sharedSite = getHttpdTomcatSharedSite();
		if(sharedSite!=null) {
			if(
				obj.getPkey()
				!= sharedSite.getHttpdSharedTomcat().getHttpdTomcatVersion().getPkey()
			) {
				throw new SQLException("HttpdTomcatSite/HttpdSharedTomcat version mismatch on HttpdTomcatSite: #"+pkey);
			}
		}
		return obj;
	}

	public List<Worker> getHttpdWorkers() throws IOException, SQLException {
		return table.getConnector().getHttpdWorkers().getHttpdWorkers(this);
	}

	public List<JkMount> getJkMounts() throws IOException, SQLException {
		return table.getConnector().getHttpdTomcatSiteJkMounts().getHttpdTomcatSiteJkMounts(this);
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.HTTPD_TOMCAT_SITES;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey = result.getInt(1);
		version = result.getInt(2);
		blockWebinf = result.getBoolean(3);
		use_apache = result.getBoolean(4);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey = in.readCompressedInt();
		version = in.readCompressedInt();
		blockWebinf = in.readBoolean();
	}

	public String startJVM() throws IOException, SQLException {
		return table.getConnector().requestResult(false,
			AoservProtocol.CommandID.START_JVM,
			new AOServConnector.ResultRequest<String>() {
				String result;
				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(pkey);
				}

				@Override
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AoservProtocol.DONE) {
						result = in.readNullUTF();
						return;
					}
					AoservProtocol.checkResult(code, in);
					throw new IOException("Unexpected response code: "+code);
				}

				@Override
				public String afterRelease() {
					return result;
				}
			}
		);
	}

	public String stopJVM() throws IOException, SQLException {
		return table.getConnector().requestResult(false,
			AoservProtocol.CommandID.STOP_JVM,
			new AOServConnector.ResultRequest<String>() {
				String result;
				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(pkey);
				}

				@Override
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AoservProtocol.DONE) {
						result = in.readNullUTF();
						return;
					}
					AoservProtocol.checkResult(code, in);
					throw new IOException("Unexpected response code: "+code);
				}

				@Override
				public String afterRelease() {
					return result;
				}
			}
		);
	}

	@Override
	public String toStringImpl() throws SQLException, IOException {
		return getHttpdSite().toStringImpl();
	}

	/**
	 * Blocks access to <code>/META-INF</code>
	 * and <code>/WEB-INF</code> at the <a href="https://httpd.apache.org/">Apache</a> level.  When
	 * <a href="https://httpd.apache.org/">Apache</a> serves content directly, instead of passing all
	 * requests to <a href="http://tomcat.apache.org/">Tomcat</a>, this helps ensure proper protection
	 * of these paths.
	 */
	public boolean getBlockWebinf() {
		return blockWebinf;
	}

	public void setBlockWebinf(boolean blockWebinf) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.SET_HTTPD_TOMCAT_SITE_BLOCK_WEBINF, pkey, blockWebinf);
	}

	@Override
	public void write(CompressedDataOutputStream out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(version);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_6) >= 0) {
			out.writeBoolean(blockWebinf);
		} else  {
			out.writeBoolean(use_apache);
		}
	}
}
