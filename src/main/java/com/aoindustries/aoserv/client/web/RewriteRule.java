/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2017, 2018, 2019, 2021  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.web;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.util.ApacheEscape;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

/**
 * Each {@link VirtualHost} may have <a href="https://httpd.apache.org/docs/2.4/mod/mod_rewrite.html#rewriterule">RewriteRule</a> configurations attached to it.
 *
 * @see  VirtualHost
 *
 * @author  AO Industries, Inc.
 */
public final class RewriteRule extends CachedObjectIntegerKey<RewriteRule> {

	static final int
		COLUMN_id = 0,
		COLUMN_virtualHost = 1
	;
	static final String COLUMN_virtualHost_name = "virtualHost";
	static final String COLUMN_sortOrder_name = "sortOrder";

	private int virtualHost;
	private short sortOrder;
	private String pattern;
	private String substitution;
	private String flags;
	private String comment;

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_id: return pkey;
			case COLUMN_virtualHost: return virtualHost;
			case 2: return sortOrder;
			case 3: return pattern;
			case 4: return substitution;
			case 5: return flags;
			case 6: return comment;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	public int getId() {
		return pkey;
	}

	public int getVirtualHost_id() {
		return virtualHost;
	}

	public VirtualHost getVirtualHost() throws SQLException, IOException {
		VirtualHost obj = table.getConnector().getWeb().getVirtualHost().get(virtualHost);
		if(obj == null) throw new SQLException("Unable to find VirtualHost: " + virtualHost);
		return obj;
	}

	public short getSortOrder() {
		return sortOrder;
	}

	public String getPattern() {
		return pattern;
	}

	public String getSubstitution() {
		return substitution;
	}

	/**
	 * Gets the <a href="https://httpd.apache.org/docs/2.4/rewrite/flags.html">RewriteRule Flags</a>.
	 *
	 * @return  The flags or {@code null} when none.
	 *
	 * @see  #hasFlag(java.lang.String)
	 * @see  #hasFlag(java.lang.String...)
	 */
	public String getFlags() {
		return flags;
	}

	/**
	 * Case-insensitive check if contains the given flag.
	 *
	 * @see  #getFlags()
	 * @see  #hasFlag(java.lang.String...)
	 */
	public boolean hasFlag(String flag) {
		if(flags == null) return false;
		String flagsUC = flags.toUpperCase(Locale.ROOT);
		String flagUC = flag.toUpperCase(Locale.ROOT);
		return
			flagsUC.equals(flagUC)                   // flag
			|| flagsUC.startsWith(flagUC + ',')      // flag,...
			|| flagsUC.startsWith(flagUC + '=')      // flag=...
			|| flagsUC.endsWith(',' + flagUC)        // ...,flag
			|| flagsUC.contains(',' + flagUC + ',')  // ...,flag,...
			|| flagsUC.contains(',' + flagUC + '='); // ...,flag=...
	}

	/**
	 * Case-insensitive check if contains any of the given flags.
	 *
	 * @see  #getFlags()
	 * @see  #hasFlag(java.lang.String)
	 */
	public boolean hasFlag(String ... flags) {
		if(flags == null) return false;
		for(String flag : flags) {
			if(hasFlag(flag)) return true;
		}
		return false;
	}

	/**
	 * Gets an optional comment describing the rule.
	 */
	public String getComment() {
		return comment;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.RewriteRule;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		int pos = 1;
		pkey         = result.getInt(pos++);
		virtualHost  = result.getInt(pos++);
		sortOrder    = result.getShort(pos++);
		pattern      = result.getString(pos++);
		substitution = result.getString(pos++);
		flags        = result.getString(pos++);
		comment      = result.getString(pos++);
	}

	@Override
	public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
		pkey         = in.readCompressedInt();
		virtualHost  = in.readCompressedInt();
		sortOrder    = in.readShort();
		pattern      = in.readUTF();
		substitution = in.readUTF();
		flags        = in.readNullUTF();
		comment      = in.readNullUTF();
	}

	/**
	 * Gets the Apache directive for this RewriteRule.
	 */
	public String getApacheDirective(String dollarVariable) {
		StringBuilder sb = new StringBuilder();
		sb
			.append("RewriteRule ")
			.append(ApacheEscape.escape(dollarVariable, pattern))
			.append(' ')
			.append(ApacheEscape.escape(dollarVariable, substitution));
		if(flags != null) {
			sb.append(' ').append(ApacheEscape.escape(dollarVariable, '[' + flags + ']'));
		}
		return sb.toString();
	}

	/**
	 * @see #getApacheDirective(java.lang.String)
	 * @see ApacheEscape#DEFAULT_DOLLAR_VARIABLE
	 */
	@Override
	public String toStringImpl() {
		return getApacheDirective(ApacheEscape.DEFAULT_DOLLAR_VARIABLE);
	}

	@Override
	public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(virtualHost);
		out.writeShort(sortOrder);
		out.writeUTF(pattern);
		out.writeUTF(substitution);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_21) >= 0) {
			out.writeNullUTF(flags);
		}
		out.writeNullUTF(comment);
		if(
			protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_4) >= 0
			&& protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_20) <= 0
		) {
			// noEscape
			out.writeBoolean(hasFlag("NE", "noescape"));
		}
	}
}
