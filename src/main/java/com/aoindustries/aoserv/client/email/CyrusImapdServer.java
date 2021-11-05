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
 * along with aoserv-client.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.aoindustries.aoserv.client.email;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.math.SafeMath;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.net.DomainName;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.net.AppProtocol;
import com.aoindustries.aoserv.client.net.Bind;
import com.aoindustries.aoserv.client.net.Host;
import com.aoindustries.aoserv.client.pki.Certificate;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/**
 * An <code>CyrusImapdServer</code> represents one running instance of Cyrus IMAPD.
 *
 * @see  CyrusImapdBind
 *
 * @author  AO Industries, Inc.
 */
public final class CyrusImapdServer extends CachedObjectIntegerKey<CyrusImapdServer> {

	static final int
		COLUMN_AO_SERVER = 0,
		COLUMN_SIEVE_NET_BIND = 1,
		COLUMN_CERTIFICATE = 3
	;
	static final String COLUMN_AO_SERVER_name = "ao_server";

	// Matches aoserv-master-db/aoindustries/email/CyrusImapdServer.TimeUnit-type.sql
	public enum TimeUnit {
		DAYS('d') {
			@Override
			float convertToDays(float duration) {
				return duration;
			}
		},
		HOURS('h') {
			@Override
			float convertToDays(float duration) {
				return duration / 24;
			}
		},
		MINUTES('m') {
			@Override
			float convertToDays(float duration) {
				return duration / (24 * 60);
			}
		},
		SECONDS('s') {
			@Override
			float convertToDays(float duration) {
				return duration / (24 * 60 * 60);
			}
		};

		private static final TimeUnit[] values = values();
		private static TimeUnit getFromSuffix(String suffix) {
			if(suffix == null) return null;
			if(suffix.length() != 1) throw new IllegalArgumentException("Suffix must be one character: " + suffix);
			char ch = suffix.charAt(0);
			for(TimeUnit value : values) {
				if(ch == value.suffix) return value;
			}
			throw new IllegalArgumentException("TimeUnit not found from suffix: " + ch);
		}

		private final char suffix;

		private TimeUnit(char suffix) {
			this.suffix = suffix;
		}

		public char getSuffix() {
			return suffix;
		}

		/**
		 * Converts to a number of days, rounded-up.
		 */
		public int getDays(float duration) {
			if(Float.isNaN(duration)) throw new IllegalArgumentException("duration is NaN");
			return SafeMath.castInt(Math.round(Math.ceil(convertToDays(duration))));
		}

		abstract float convertToDays(float duration);
	}

	/**
	 * Default value for cyrus_imapd_servers.allow_plaintext_auth
	 */
	public static final boolean DEFAULT_ALLOW_PLAINTEXT_AUTH = false;

	// Delayed delete seems unreliable so far
	//
	// It is difficult to delete folders and millions of lock files accumulating
	// under /var/lib/imap/lock/u/DELETED/user/*/**/*.lock
	//
	// Turned-off on all servers and removed defaults
	/**
	 * Default value for cyrus_imapd_servers.delete_duration
	 */
	public static final float DEFAULT_DELETE_DURATION = Float.NaN; // 3;

	/**
	 * Default value for cyrus_imapd_servers.delete_duration_unit
	 */
	public static final TimeUnit DEFAULT_DELETE_DURATION_UNIT = null; // TimeUnit.DAYS;

	/**
	 * Default value for cyrus_imapd_servers.expire_duration
	 */
	public static final float DEFAULT_EXPIRE_DURATION = 3;

	/**
	 * Default value for cyrus_imapd_servers.expire_duration_unit
	 */
	public static final TimeUnit DEFAULT_EXPIRE_DURATION_UNIT = TimeUnit.DAYS;

	/**
	 * Default value for cyrus_imapd_servers.expunge_duration
	 */
	public static final float DEFAULT_EXPUNGE_DURATION = 3;

	/**
	 * Default value for cyrus_imapd_servers.expunge_duration_unit
	 */
	public static final TimeUnit DEFAULT_EXPUNGE_DURATION_UNIT = TimeUnit.DAYS;

	private int sieveNetBind;
	private DomainName servername;
	private int certificate;
	private boolean allowPlaintextAuth;
	private float deleteDuration;
	private TimeUnit deleteDurationUnit;
	private float expireDuration;
	private TimeUnit expireDurationUnit;
	private float expungeDuration;
	private TimeUnit expungeDurationUnit;

	/**
	 * @deprecated  Only required for implementation, do not use directly.
	 *
	 * @see  #init(java.sql.ResultSet)
	 * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
	 */
	@Deprecated/* Java 9: (forRemoval = true) */
	public CyrusImapdServer() {
		// Do nothing
	}

	@Override
	public String toStringImpl() throws IOException, SQLException {
		return "Cyrus IMAPD @ " + (servername != null ? servername : getLinuxServer().getHostname());
	}

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_AO_SERVER: return pkey;
			case COLUMN_SIEVE_NET_BIND: return sieveNetBind==-1 ? null : sieveNetBind;
			case 2: return servername;
			case COLUMN_CERTIFICATE: return certificate;
			case 4: return allowPlaintextAuth;
			case 5: return deleteDuration;
			case 6: return deleteDurationUnit==null ? null : String.valueOf(deleteDurationUnit.getSuffix());
			case 7: return expireDuration;
			case 8: return expireDurationUnit==null ? null : String.valueOf(expireDurationUnit.getSuffix());
			case 9: return expungeDuration;
			case 10: return expungeDurationUnit==null ? null : String.valueOf(expungeDurationUnit.getSuffix());
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.CYRUS_IMAPD_SERVERS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			int pos = 1;
			pkey = result.getInt(pos++);
			sieveNetBind = result.getInt(pos++);
			if(result.wasNull()) sieveNetBind = -1;
			servername = DomainName.valueOf(result.getString(pos++));
			certificate = result.getInt(pos++);
			allowPlaintextAuth = result.getBoolean(pos++);
			deleteDuration = result.getFloat(pos++);
			if(result.wasNull()) deleteDuration = Float.NaN;
			deleteDurationUnit = TimeUnit.getFromSuffix(result.getString(pos++));
			expireDuration = result.getFloat(pos++);
			expireDurationUnit = TimeUnit.getFromSuffix(result.getString(pos++));
			expungeDuration = result.getFloat(pos++);
			if(result.wasNull()) expungeDuration = Float.NaN;
			expungeDurationUnit = TimeUnit.getFromSuffix(result.getString(pos++));
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
		try {
			pkey = in.readCompressedInt();
			sieveNetBind = in.readCompressedInt();
			servername = DomainName.valueOf(in.readNullUTF());
			certificate = in.readCompressedInt();
			allowPlaintextAuth = in.readBoolean();
			deleteDuration = in.readFloat();
			deleteDurationUnit = in.readNullEnum(TimeUnit.class);
			expireDuration = in.readFloat();
			expireDurationUnit = in.readNullEnum(TimeUnit.class);
			expungeDuration = in.readFloat();
			expungeDurationUnit = in.readNullEnum(TimeUnit.class);
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(sieveNetBind);
		out.writeNullUTF(Objects.toString(servername, null));
		out.writeCompressedInt(certificate);
		out.writeBoolean(allowPlaintextAuth);
		out.writeFloat(deleteDuration);
		out.writeNullEnum(deleteDurationUnit);
		out.writeFloat(expireDuration);
		out.writeNullEnum(expireDurationUnit);
		out.writeFloat(expungeDuration);
		out.writeNullEnum(expungeDurationUnit);
	}

	public Server getLinuxServer() throws SQLException, IOException {
		Server obj = table.getConnector().getLinux().getServer().get(pkey);
		if(obj == null) throw new SQLException("Unable to find linux.Server: " + pkey);
		return obj;
	}

	public Bind getSieveNetBind() throws IOException, SQLException {
		if(sieveNetBind == -1) return null;
		Bind nb = table.getConnector().getNet().getBind().get(sieveNetBind);
		// May be filtered
		if(nb == null) return null;
		String protocol = nb.getAppProtocol().getProtocol();
		if(!AppProtocol.SIEVE.equals(protocol)) throw new SQLException("Sieve NetBind is incorrect app_protocol for NetBind #" + nb.getPkey() + ": " + protocol);
		Host server = nb.getHost();
		if(!server.equals(getLinuxServer().getHost())) throw new SQLException("Sieve NetBind is not on this server for NetBind #" + nb.getPkey());
		return nb;
	}

	/**
	 * The fully qualified hostname for <code>servername</code>.
	 *
	 * When {@code null}, defaults to {@link Server#getHostname()}.
	 */
	public DomainName getServername() {
		return servername;
	}

	/**
	 * Gets the SSL certificate for this server.
	 *
	 * @return  the SSL certificate or {@code null} when filtered
	 */
	public Certificate getCertificate() throws SQLException, IOException {
		// May be filtered
		return table.getConnector().getPki().getCertificate().get(certificate);
	}

	/**
	 * Allows plaintext authentication (PLAIN/LOGIN) on non-TLS links.
	 */
	public boolean getAllowPlaintextAuth() {
		return allowPlaintextAuth;
	}

	/**
	 * Gets the duration after which delayed delete folders are removed.
	 * Enables <code>delete_mode: delayed</code>
	 *
	 * @return  the duration or {@link Float#NaN} when not set
	 *
	 * @see  #getDeleteDurationUnit()
	 */
	public float getDeleteDuration() {
		return deleteDuration;
	}

	/**
	 * Gets the time unit for {@link #getDeleteDuration()}.
	 * When not set, the duration represents days.
	 *
	 * @return  the unit or {@code null} when not set
	 */
	public TimeUnit getDeleteDurationUnit() {
		return deleteDurationUnit;
	}

	/**
	 * Prune the duplicate database of entries older than expire-duration.
	 *
	 * @return  the duration (never {@link Float#NaN})
	 *
	 * @see  #getExpireDurationUnit()
	 */
	public float getExpireDuration() {
		return expireDuration;
	}

	/**
	 * Gets the time unit for {@link #getExpireDuration()}.
	 * When not set, the duration represents days.
	 *
	 * @return  the unit or {@code null} when not set
	 */
	public TimeUnit getExpireDurationUnit() {
		return expireDurationUnit;
	}

	/**
	 * Gets the duration after which delayed expunge messages are removed.
	 * Enables <code>expunge_mode: delayed</code>
	 *
	 * @return  the duration or {@link Float#NaN} when not set
	 *
	 * @see  #getExpungeDurationUnit()
	 */
	public float getExpungeDuration() {
		return expungeDuration;
	}

	/**
	 * Gets the time unit for {@link #getExpungeDuration()}.
	 * When not set, the duration represents days.
	 *
	 * @return  the unit or {@code null} when not set
	 */
	public TimeUnit getExpungeDurationUnit() {
		return expungeDurationUnit;
	}

	public List<CyrusImapdBind> getCyrusImapdBinds() throws IOException, SQLException {
		return table.getConnector().getEmail().getCyrusImapdBind().getCyrusImapdBinds(this);
	}
}
