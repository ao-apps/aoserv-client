/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2006-2009, 2016, 2017, 2018, 2019, 2021  AO Industries, Inc.
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
 * along with aoserv-client.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.aoindustries.aoserv.client.web;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.validation.ValidationException;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.CannotRemoveReason;
import com.aoindustries.aoserv.client.Removable;
import com.aoindustries.aoserv.client.linux.PosixPath;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * TODO: Make the authentication aspect optional since this now has an optional handler.
 *
 * @see  Site
 *
 * @author  AO Industries, Inc.
 */
public final class Location extends CachedObjectIntegerKey<Location> implements Removable {

	static final int
		COLUMN_PKEY=0,
		COLUMN_HTTPD_SITE=1
	;
	static final String COLUMN_HTTPD_SITE_name = "httpd_site";

	/**
	 * The set of expected handlers.  This is not an enum because others may be
	 * added at any time while older clients are still running.
	 */
	public static final class Handler {

		/**
		 * Enables <a href="https://httpd.apache.org/docs/2.4/mod/mod_status.html#enable">Apache Status Support</a>.
		 */
		public static final String SERVER_STATUS = "server-status";

		/**
		 * Represents the current value.  Used when setting attributes and not wanting to update
		 * the handler.
		 */
		public static final String CURRENT = "*";

		private Handler() {}
	}

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

	public static String validateAuthGroupFile(PosixPath authGroupFile) {
		// May be empty
		if(authGroupFile == null) return null;
		return validateNonQuoteAscii(authGroupFile.toString(), "AuthGroupFile");
	}

	public static String validateAuthUserFile(PosixPath authUserFile) {
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
	private PosixPath auth_group_file;
	private PosixPath auth_user_file;
	private String require;
	private String handler;

	@Override
	public List<CannotRemoveReason<?>> getCannotRemoveReasons() {
		return Collections.emptyList();
	}

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_HTTPD_SITE: return httpd_site;
			case 2: return path;
			case 3: return is_regular_expression;
			case 4: return auth_name;
			case 5: return auth_group_file;
			case 6: return auth_user_file;
			case 7: return require;
			case 8: return handler;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	public Site getHttpdSite() throws SQLException, IOException {
		Site obj=table.getConnector().getWeb().getSite().get(httpd_site);
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

	public PosixPath getAuthGroupFile() {
		return auth_group_file;
	}

	public PosixPath getAuthUserFile() {
		return auth_user_file;
	}

	public String getRequire() {
		return require;
	}

	/**
	 * Gets the optional handler for <code>SetHandler</code>.
	 * May be one of the values in {@link Handler} or any other value added
	 * in the future.
	 */
	public String getHandler() {
		return handler;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.HTTPD_SITE_AUTHENTICATED_LOCATIONS;
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
				auth_group_file = s.isEmpty() ? null : PosixPath.valueOf(s);
			}
			{
				String s = result.getString(7);
				auth_user_file = s.isEmpty() ? null : PosixPath.valueOf(s);
			}
			require=result.getString(8);
			handler = result.getString(9);
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
		try {
			pkey=in.readCompressedInt();
			httpd_site=in.readCompressedInt();
			path=in.readCompressedUTF();
			is_regular_expression=in.readBoolean();
			auth_name=in.readCompressedUTF();
			{
				String s = in.readCompressedUTF();
				auth_group_file = s.isEmpty() ? null : PosixPath.valueOf(s);
			}
			{
				String s = in.readCompressedUTF();
				auth_user_file = s.isEmpty() ? null : PosixPath.valueOf(s);
			}
			require=in.readCompressedUTF().intern();
			handler = in.readBoolean() ? in.readCompressedUTF().intern() : null;
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void remove() throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.REMOVE, Table.TableID.HTTPD_SITE_AUTHENTICATED_LOCATIONS, pkey);
	}

	public void setAttributes(
		String path,
		boolean isRegularExpression,
		String authName,
		PosixPath authGroupFile,
		PosixPath authUserFile,
		String require,
		String handler
	) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(
			true,
			AoservProtocol.CommandID.SET_HTTPD_SITE_AUTHENTICATED_LOCATION_ATTRIBUTES,
			pkey,
			path,
			isRegularExpression,
			authName,
			authGroupFile==null ? "" : authGroupFile.toString(),
			authUserFile==null ? "" : authUserFile.toString(),
			require,
			handler==null ? "" : handler
		);
	}

	@Override
	public String toStringImpl() throws SQLException, IOException {
		Site site=getHttpdSite();
		return site.toStringImpl()+':'+path;
	}

	@Override
	public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(httpd_site);
		out.writeCompressedUTF(path, 0);
		out.writeBoolean(is_regular_expression);
		out.writeCompressedUTF(auth_name, 1);
		out.writeCompressedUTF(auth_group_file==null ? "" : auth_group_file.toString(), 2);
		out.writeCompressedUTF(auth_user_file==null ? "" : auth_user_file.toString(), 3);
		out.writeCompressedUTF(require, 4);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_13) >= 0) {
			out.writeBoolean(handler != null);
			if(handler != null) out.writeCompressedUTF(handler, 5);
		}
	}
}
