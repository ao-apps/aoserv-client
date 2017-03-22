/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2006-2009, 2016, 2017  AO Industries, Inc.
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

import com.aoindustries.aoserv.client.validator.UnixPath;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * @see  HttpdSite
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdSiteAuthenticatedLocation extends CachedObjectIntegerKey<HttpdSiteAuthenticatedLocation> implements Removable {

	static final int
		COLUMN_PKEY=0,
		COLUMN_HTTPD_SITE=1
	;
	static final String COLUMN_HTTPD_SITE_name = "httpd_site";

	private static String validateNonQuoteAscii(String s, String label) {
		// Is only comprised of space through ~ (ASCII), not including "
		for(int c=0;c<s.length();c++) {
			char ch=s.charAt(c);
			if(ch<' ' || ch>'~' || ch=='"') return "Invalid character in "+label+": "+ch;
		}
		return null;
	}

	public static String validatePath(String path) {
		if(path.length()==0) return "Location required";
		return validateNonQuoteAscii(path, "Location");
	}

	public static String validateAuthName(String authName) {
		return validateNonQuoteAscii(authName, "AuthName");
	}

	public static String validateAuthGroupFile(UnixPath authGroupFile) {
		// May be empty
		if(authGroupFile == null) return null;
		return validateNonQuoteAscii(authGroupFile.toString(), "AuthGroupFile");
	}

	public static String validateAuthUserFile(UnixPath authUserFile) {
		// May be empty
		if(authUserFile == null) return null;
		return validateNonQuoteAscii(authUserFile.toString(), "AuthUserFile");
	}

	public static String validateRequire(String require) {
		return validateNonQuoteAscii(require, "Require");
	}

	private int httpd_site;
	private String path;
	private boolean is_regular_expression;
	private String auth_name;
	private UnixPath auth_group_file;
	private UnixPath auth_user_file;
	private String require;

	@Override
	public List<CannotRemoveReason<?>> getCannotRemoveReasons() {
		return Collections.emptyList();
	}

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_HTTPD_SITE: return httpd_site;
			case 2: return path;
			case 3: return is_regular_expression;
			case 4: return auth_name;
			case 5: return auth_group_file;
			case 6: return auth_user_file;
			case 7: return require;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public HttpdSite getHttpdSite() throws SQLException, IOException {
		HttpdSite obj=table.connector.getHttpdSites().get(httpd_site);
		if(obj==null) throw new SQLException("Unable to find HttpdSite: "+httpd_site);
		return obj;
	}

	public String getPath() {
		return path;
	}

	public boolean getIsRegularExpression() {
		return is_regular_expression;
	}

	public String getAuthName() {
		return auth_name;
	}

	public UnixPath getAuthGroupFile() {
		return auth_group_file;
	}

	public UnixPath getAuthUserFile() {
		return auth_user_file;
	}

	public String getRequire() {
		return require;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.HTTPD_SITE_AUTHENTICATED_LOCATIONS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			pkey=result.getInt(1);
			httpd_site=result.getInt(2);
			path=result.getString(3);
			is_regular_expression=result.getBoolean(4);
			auth_name=result.getString(5);
			{
				String s = result.getString(6);
				auth_group_file = s.isEmpty() ? null : UnixPath.valueOf(s);
			}
			{
				String s = result.getString(7);
				auth_user_file = s.isEmpty() ? null : UnixPath.valueOf(s);
			}
			require=result.getString(8);
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey=in.readCompressedInt();
			httpd_site=in.readCompressedInt();
			path=in.readCompressedUTF();
			is_regular_expression=in.readBoolean();
			auth_name=in.readCompressedUTF();
			{
				String s = in.readCompressedUTF();
				auth_group_file = s.isEmpty() ? null : UnixPath.valueOf(s);
			}
			{
				String s = in.readCompressedUTF();
				auth_user_file = s.isEmpty() ? null : UnixPath.valueOf(s);
			}
			require=in.readCompressedUTF().intern();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void remove() throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.REMOVE, SchemaTable.TableID.HTTPD_SITE_AUTHENTICATED_LOCATIONS, pkey);
	}

	public void setAttributes(
		String path,
		boolean isRegularExpression,
		String authName,
		UnixPath authGroupFile,
		UnixPath authUserFile,
		String require
	) throws IOException, SQLException {
		table.connector.requestUpdateIL(
			true,
			AOServProtocol.CommandID.SET_HTTPD_SITE_AUTHENTICATED_LOCATION_ATTRIBUTES,
			pkey,
			path,
			isRegularExpression,
			authName,
			authGroupFile==null ? "" : authGroupFile.toString(),
			authUserFile==null ? "" : authUserFile.toString(),
			require
		);
	}

	@Override
	String toStringImpl() throws SQLException, IOException {
		HttpdSite site=getHttpdSite();
		return site.toStringImpl()+':'+path;
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(httpd_site);
		out.writeCompressedUTF(path, 0);
		out.writeBoolean(is_regular_expression);
		out.writeCompressedUTF(auth_name, 1);
		out.writeCompressedUTF(auth_group_file==null ? "" : auth_group_file.toString(), 2);
		out.writeCompressedUTF(auth_user_file==null ? "" : auth_user_file.toString(), 3);
		out.writeCompressedUTF(require, 4);
	}
}
