/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2002-2009, 2016, 2017, 2018, 2019  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.CannotRemoveReason;
import com.aoindustries.aoserv.client.Removable;
import com.aoindustries.aoserv.client.linux.PosixPath;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.util.IntList;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents one context within a <code>HttpdTomcatSite</code>.
 *
 * @see  Site
 *
 * @author  AO Industries, Inc.
 */
final public class Context extends CachedObjectIntegerKey<Context> implements Removable {

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
	public static final PosixPath DEFAULT_WORK_DIR = null;
	public static final boolean DEFAULT_SERVER_XML_CONFIGURED = true;

	/**
	 * The ROOT webapp details
	 */
	public static final String ROOT_PATH="";
	public static final String ROOT_DOC_BASE="ROOT";

	int tomcat_site;
	private String class_name;
	private boolean cookies;
	private boolean cross_context;
	private PosixPath doc_base;
	private boolean override;
	String path;
	private boolean privileged;
	private boolean reloadable;
	private boolean use_naming;
	private String wrapper_class;
	private int debug;
	private PosixPath work_dir;
	private boolean server_xml_configured;

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
		return table.getConnector().getWeb_tomcat().getContextDataSource().addHttpdTomcatDataSource(
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
		return table.getConnector().getWeb_tomcat().getContextParameter().addHttpdTomcatParameter(this, name, value, override, description);
	}

	@Override
	public List<CannotRemoveReason<Context>> getCannotRemoveReasons() {
		List<CannotRemoveReason<Context>> reasons=new ArrayList<>();
		if(path.length()==0) reasons.add(new CannotRemoveReason<>("Not allowed to remove the root context", this));
		return reasons;
	}

	@Override
	protected Object getColumnImpl(int i) {
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
			case 14: return server_xml_configured;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public Site getHttpdTomcatSite() throws SQLException, IOException {
		Site obj=table.getConnector().getWeb_tomcat().getSite().get(tomcat_site);
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

	public PosixPath getDocBase() {
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

	public PosixPath getWorkDir() {
		return work_dir;
	}

	public boolean isServerXmlConfigured() {
		return server_xml_configured;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.HTTPD_TOMCAT_CONTEXTS;
	}

	public List<ContextDataSource> getHttpdTomcatDataSources() throws IOException, SQLException {
		return table.getConnector().getWeb_tomcat().getContextDataSource().getHttpdTomcatDataSources(this);
	}

	public ContextDataSource getHttpdTomcatDataSource(String name) throws IOException, SQLException {
		return table.getConnector().getWeb_tomcat().getContextDataSource().getHttpdTomcatDataSource(this, name);
	}

	public List<ContextParameter> getHttpdTomcatParameters() throws IOException, SQLException {
		return table.getConnector().getWeb_tomcat().getContextParameter().getHttpdTomcatParameters(this);
	}

	public ContextParameter getHttpdTomcatParameter(String name) throws IOException, SQLException {
		return table.getConnector().getWeb_tomcat().getContextParameter().getHttpdTomcatParameter(this, name);
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			pkey=result.getInt(1);
			tomcat_site=result.getInt(2);
			class_name=result.getString(3);
			cookies=result.getBoolean(4);
			cross_context=result.getBoolean(5);
			doc_base = PosixPath.valueOf(result.getString(6));
			override=result.getBoolean(7);
			path=result.getString(8);
			privileged=result.getBoolean(9);
			reloadable=result.getBoolean(10);
			use_naming=result.getBoolean(11);
			wrapper_class=result.getString(12);
			debug=result.getInt(13);
			work_dir = PosixPath.valueOf(result.getString(14));
			server_xml_configured = result.getBoolean(15);
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	public static boolean isValidDocBase(PosixPath docBase) {
		String docBaseStr = docBase.toString();
		return
			/* UnixPath checks these:
			docBase.length()>1
			&& docBase.charAt(0)=='/'
			&& !docBase.contains("//")
			&& !docBase.contains("..")
			*/
			docBaseStr.indexOf('"')==-1
			&& docBaseStr.indexOf('\\')==-1
			&& docBaseStr.indexOf('\n')==-1
			&& docBaseStr.indexOf('\r')==-1
		;
	}

	public static boolean isValidPath(String path) {
		try {
			return
				path.length() == 0
				|| (
					PosixPath.validate(path).isValid()
					&& isValidDocBase(PosixPath.valueOf(path))
				);
		} catch(ValidationException e) {
			throw new AssertionError("Already validated", e);
		}
	}

	public static boolean isValidWorkDir(PosixPath workDir) {
		return workDir==null || isValidDocBase(workDir);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey=in.readCompressedInt();
			tomcat_site=in.readCompressedInt();
			class_name=in.readNullUTF();
			cookies=in.readBoolean();
			cross_context=in.readBoolean();
			doc_base = PosixPath.valueOf(in.readUTF());
			override=in.readBoolean();
			path=in.readUTF();
			privileged=in.readBoolean();
			reloadable=in.readBoolean();
			use_naming=in.readBoolean();
			wrapper_class=in.readNullUTF();
			debug=in.readCompressedInt();
			work_dir = PosixPath.valueOf(in.readNullUTF());
			server_xml_configured = in.readBoolean();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	public void setAttributes(
		final String className,
		final boolean cookies,
		final boolean crossContext,
		final PosixPath docBase,
		final boolean override,
		final String path,
		final boolean privileged,
		final boolean reloadable,
		final boolean useNaming,
		final String wrapperClass,
		final int debug,
		final PosixPath workDir,
		final boolean serverXmlConfigured
	) throws IOException, SQLException {
		table.getConnector().requestUpdate(
			true,
			AoservProtocol.CommandID.SET_HTTPD_TOMCAT_CONTEXT_ATTRIBUTES,
			new AOServConnector.UpdateRequest() {
				IntList invalidateList;

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(pkey);
					out.writeNullUTF(className);
					out.writeBoolean(cookies);
					out.writeBoolean(crossContext);
					out.writeUTF(docBase.toString());
					out.writeBoolean(override);
					out.writeUTF(path);
					out.writeBoolean(privileged);
					out.writeBoolean(reloadable);
					out.writeBoolean(useNaming);
					out.writeNullUTF(wrapperClass);
					out.writeCompressedInt(debug);
					out.writeNullUTF(Objects.toString(workDir, null));
					out.writeBoolean(serverXmlConfigured);
				}

				@Override
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AoservProtocol.DONE) invalidateList=AOServConnector.readInvalidateList(in);
					else {
						AoservProtocol.checkResult(code, in);
						throw new IOException("Unexpected response code: "+code);
					}
				}

				@Override
				public void afterRelease() {
					table.getConnector().tablesUpdated(invalidateList);
				}
			}
		);
	}

	@Override
	public void remove() throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.REMOVE, Table.TableID.HTTPD_TOMCAT_CONTEXTS, pkey);
	}

	@Override
	public void write(CompressedDataOutputStream out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(tomcat_site);
		out.writeNullUTF(class_name);
		out.writeBoolean(cookies);
		out.writeBoolean(cross_context);
		out.writeUTF(doc_base.toString());
		out.writeBoolean(override);
		out.writeUTF(path);
		out.writeBoolean(privileged);
		out.writeBoolean(reloadable);
		out.writeBoolean(use_naming);
		out.writeNullUTF(wrapper_class);
		out.writeCompressedInt(debug);
		out.writeNullUTF(Objects.toString(work_dir, null));
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_3) >= 0) {
			out.writeBoolean(server_xml_configured);
		}
	}
}
