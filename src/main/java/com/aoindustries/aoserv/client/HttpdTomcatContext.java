/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2002-2009, 2016  AO Industries, Inc.
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
package com.aoindustries.aoserv.client;

import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.util.IntList;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents one context within a <code>HttpdTomcatSite</code>.
 *
 * @see  HttpdTomcatSite
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdTomcatContext extends CachedObjectIntegerKey<HttpdTomcatContext> implements Removable {

	static final int
		COLUMN_PKEY=0,
		COLUMN_TOMCAT_SITE=1
	;
	static final String COLUMN_TOMCAT_SITE_name = "tomcat_site";
	static final String COLUMN_PATH_name = "path";

	/**
	 * These are the default values for a new site.
	 */
	public static final String DEFAULT_CLASS_NAME=null;
	public static final boolean DEFAULT_COOKIES=true;
	public static final boolean DEFAULT_CROSS_CONTEXT=false;
	public static final boolean DEFAULT_OVERRIDE=false;
	public static final boolean DEFAULT_PRIVILEGED=false;
	public static final boolean DEFAULT_RELOADABLE=false;
	public static final boolean DEFAULT_USE_NAMING=true;
	public static final String DEFAULT_WRAPPER_CLASS=null;
	public static final int DEFAULT_DEBUG=0;
	public static final String DEFAULT_WORK_DIR=null;

	/**
	 * The ROOT webapp details
	 */
	public static final String ROOT_PATH="";
	public static final String ROOT_DOC_BASE="ROOT";

	int tomcat_site;
	private String class_name;
	private boolean cookies;
	private boolean cross_context;
	private String doc_base;
	private boolean override;
	String path;
	private boolean privileged;
	private boolean reloadable;
	private boolean use_naming;
	private String wrapper_class;
	private int debug;
	private String work_dir;

	public int addHttpdTomcatDataSource(
		String name,
		String driverClassName,
		String url,
		String username,
		String password,
		int maxActive,
		int maxIdle,
		int maxWait,
		String validationQuery
	) throws IOException, SQLException {
		return table.connector.getHttpdTomcatDataSources().addHttpdTomcatDataSource(
			this,
			name,
			driverClassName,
			url,
			username,
			password,
			maxActive,
			maxIdle,
			maxWait,
			validationQuery
		);
	}

	public int addHttpdTomcatParameter(String name, String value, boolean override, String description) throws IOException, SQLException {
		return table.connector.getHttpdTomcatParameters().addHttpdTomcatParameter(this, name, value, override, description);
	}

	@Override
	public List<CannotRemoveReason> getCannotRemoveReasons() {
		List<CannotRemoveReason> reasons=new ArrayList<>();
		if(path.length()==0) reasons.add(new CannotRemoveReason<>("Not allowed to remove the root context", this));
		return reasons;
	}

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_TOMCAT_SITE: return tomcat_site;
			case 2: return class_name;
			case 3: return cookies;
			case 4: return cross_context;
			case 5: return doc_base;
			case 6: return override;
			case 7: return path;
			case 8: return privileged;
			case 9: return reloadable;
			case 10: return use_naming;
			case 11: return wrapper_class;
			case 12: return debug;
			case 13: return work_dir;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public HttpdTomcatSite getHttpdTomcatSite() throws SQLException, IOException {
		HttpdTomcatSite obj=table.connector.getHttpdTomcatSites().get(tomcat_site);
		if(obj==null) throw new SQLException("Unable to find HttpdTomcatSite: "+tomcat_site);
		return obj;
	}

	public String getClassName() {
		return class_name;
	}

	public boolean useCookies() {
		return cookies;
	}

	public boolean allowCrossContext() {
		return cross_context;
	}

	public String getDocBase() {
		return doc_base;
	}

	public boolean allowOverride() {
		return override;
	}

	public String getPath() {
		return path;
	}

	public boolean isPrivileged() {
		return privileged;
	}

	public boolean isReloadable() {
		return reloadable;
	}

	public boolean useNaming() {
		return use_naming;
	}

	public String getWrapperClass() {
		return wrapper_class;
	}

	public int getDebugLevel() {
		return debug;
	}

	public String getWorkDir() {
		return work_dir;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.HTTPD_TOMCAT_CONTEXTS;
	}

	public List<HttpdTomcatDataSource> getHttpdTomcatDataSources() throws IOException, SQLException {
		return table.connector.getHttpdTomcatDataSources().getHttpdTomcatDataSources(this);
	}

	public HttpdTomcatDataSource getHttpdTomcatDataSource(String name) throws IOException, SQLException {
		return table.connector.getHttpdTomcatDataSources().getHttpdTomcatDataSource(this, name);
	}

	public List<HttpdTomcatParameter> getHttpdTomcatParameters() throws IOException, SQLException {
		return table.connector.getHttpdTomcatParameters().getHttpdTomcatParameters(this);
	}

	public HttpdTomcatParameter getHttpdTomcatParameter(String name) throws IOException, SQLException {
		return table.connector.getHttpdTomcatParameters().getHttpdTomcatParameter(this, name);
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey=result.getInt(1);
		tomcat_site=result.getInt(2);
		class_name=result.getString(3);
		cookies=result.getBoolean(4);
		cross_context=result.getBoolean(5);
		doc_base=result.getString(6);
		override=result.getBoolean(7);
		path=result.getString(8);
		privileged=result.getBoolean(9);
		reloadable=result.getBoolean(10);
		use_naming=result.getBoolean(11);
		wrapper_class=result.getString(12);
		debug=result.getInt(13);
		work_dir=result.getString(14);
	}

	public static boolean isValidDocBase(String docBase) {
		return
			docBase.length()>1
			&& docBase.charAt(0)=='/'
			&& !docBase.contains("//")
			&& !docBase.contains("..")
			&& docBase.indexOf('"')==-1
			&& docBase.indexOf('\\')==-1
			&& docBase.indexOf('\n')==-1
			&& docBase.indexOf('\r')==-1
		;
	}

	public static boolean isValidPath(String path) {
		return path.length()==0 || isValidDocBase(path);
	}

	public static boolean isValidWorkDir(String workDir) {
		return workDir==null || isValidDocBase(workDir);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readCompressedInt();
		tomcat_site=in.readCompressedInt();
		class_name=in.readNullUTF();
		cookies=in.readBoolean();
		cross_context=in.readBoolean();
		doc_base=in.readUTF();
		override=in.readBoolean();
		path=in.readUTF();
		privileged=in.readBoolean();
		reloadable=in.readBoolean();
		use_naming=in.readBoolean();
		wrapper_class=in.readNullUTF();
		debug=in.readCompressedInt();
		work_dir=in.readNullUTF();
	}

	public void setAttributes(
		final String className,
		final boolean cookies,
		final boolean crossContext,
		final String docBase,
		final boolean override,
		final String path,
		final boolean privileged,
		final boolean reloadable,
		final boolean useNaming,
		final String wrapperClass,
		final int debug,
		final String workDir
	) throws IOException, SQLException {
		table.connector.requestUpdate(
			true,
			new AOServConnector.UpdateRequest() {
				IntList invalidateList;

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(AOServProtocol.CommandID.SET_HTTPD_TOMCAT_CONTEXT_ATTRIBUTES.ordinal());
					out.writeCompressedInt(pkey);
					out.writeNullUTF(className);
					out.writeBoolean(cookies);
					out.writeBoolean(crossContext);
					out.writeUTF(docBase);
					out.writeBoolean(override);
					out.writeUTF(path);
					out.writeBoolean(privileged);
					out.writeBoolean(reloadable);
					out.writeBoolean(useNaming);
					out.writeNullUTF(wrapperClass);
					out.writeCompressedInt(debug);
					out.writeNullUTF(workDir);
				}

				@Override
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AOServProtocol.DONE) invalidateList=AOServConnector.readInvalidateList(in);
					else {
						AOServProtocol.checkResult(code, in);
						throw new IOException("Unexpected response code: "+code);
					}
				}

				@Override
				public void afterRelease() {
					table.connector.tablesUpdated(invalidateList);
				}
			}
		);
	}

	@Override
	public void remove() throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.REMOVE, SchemaTable.TableID.HTTPD_TOMCAT_CONTEXTS, pkey);
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(tomcat_site);
		out.writeNullUTF(class_name);
		out.writeBoolean(cookies);
		out.writeBoolean(cross_context);
		out.writeUTF(doc_base);
		out.writeBoolean(override);
		out.writeUTF(path);
		out.writeBoolean(privileged);
		out.writeBoolean(reloadable);
		out.writeBoolean(use_naming);
		out.writeNullUTF(wrapper_class);
		out.writeCompressedInt(debug);
		out.writeNullUTF(work_dir);
	}
}
