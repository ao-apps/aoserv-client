/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2017, 2018, 2019, 2020, 2021  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.net;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.dto.DtoFactory;
import com.aoapps.lang.i18n.Resources;
import com.aoapps.lang.util.InternUtils;
import com.aoapps.lang.util.Internable;
import com.aoapps.lang.validation.InvalidResult;
import com.aoapps.lang.validation.ValidResult;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.lang.validation.ValidationResult;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Defines a firewalld zone that exists on a {@link Host}.
 *
 * @author  AO Industries, Inc.
 */
public final class FirewallZone extends CachedObjectIntegerKey<FirewallZone> {

	private static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, FirewallZone.class);

	/**
	 * Represents a name that may be used for a {@link FirewallZone}.  Zones names must:
	 * <ul>
	 *   <li>Be non-null</li>
	 *   <li>Be non-empty</li>
	 *   <li>Be between 1 and 17 characters</li>
	 *   <li>Contain the characters [a-z], [A-Z], [0-9], underscore (_), hyphen (-), and slash (/)</li>
	 *   <li>Not begin with a slash (/)</li>
	 *   <li>Not end with a slash (/)</li>
	 *   <li>Not contain more than one slash (/)</li>
	 * </ul>
	 * <p>
	 *   We're unable to find well-defined rules for valid zone names.  The rules above are based on the source code
	 *   for <a href="https://firewalld.org/">firewalld</a> included with CentOS 7.
	 * </p>
	 * <ol>
	 *   <li>See <code>/usr/lib/python2.7/site-packages/firewall/core/io/zone.py</code>, <code>check_name</code>.</li>
	 *   <li>See <code>/usr/lib/python2.7/site-packages/firewall/core/io/io_object.py</code>, <code>check_name</code>.</li>
	 *   <li>See <code>/usr/lib/python2.7/site-packages/firewall/functions.py</code>, <code>max_zone_name_len</code>.</li>
	 * </ol>
	 * <p>
	 * Additionally, we tried creating a new zone with some UTF-8 characters, specifically Japanese,
	 * and <code>firewalld-cmd</code> just stalled, not even responding to <code>Ctrl-C</code>.  We are implementing with a
	 * strict ASCII-compatible definition of "alphanumeric".
	 * </p>
	 *
	 * @author  AO Industries, Inc.
	 */
	final static public class Name implements
		Comparable<Name>,
		Serializable,
		DtoFactory<com.aoindustries.aoserv.client.dto.FirewallZoneName>,
		Internable<Name>
	{

		private static final long serialVersionUID = 1L;

		/**
		 * The longest name allowed for a {@link FirewallZone}.
		 */
		public static final int MAX_LENGTH = 17;

		/**
		 * Validates a {@link FirewallZone} name.
		 */
		public static ValidationResult validate(String name) {
			if(name == null) return new InvalidResult(RESOURCES, "Name.validate.isNull");
			int len = name.length();
			if(len == 0) return new InvalidResult(RESOURCES, "Name.validate.isEmpty");
			if(len > MAX_LENGTH) return new InvalidResult(RESOURCES, "Name.validate.tooLong", MAX_LENGTH, len);

			// Contain the characters [a-z], [A-Z], [0-9], underscore (_), hyphen (-), and slash (/)
			for (int c = 0; c < len; c++) {
				char ch = name.charAt(c);
				if (
					(ch<'a' || ch>'z')
					&& (ch < 'A' || ch > 'Z')
					&& (ch < '0' || ch > '9')
					&& ch != '_'
					&& ch != '-'
					&& ch != '/'
				) return new InvalidResult(RESOURCES, "Name.validate.illegalCharacter");
			}
			// Not begin with a slash (/)
			if(name.charAt(0) == '/') return new InvalidResult(RESOURCES, "Name.validate.startsWithSlash");
			// Not end with a slash (/)
			if(name.charAt(len - 1) == '/') return new InvalidResult(RESOURCES, "Name.validate.endsWithSlash");
			// Not contain more than one slash (/)
			int slashPos = name.indexOf('/');
			if(slashPos != -1 && name.indexOf('/', slashPos + 1) != -1) return new InvalidResult(RESOURCES, "Name.validate.moreThanOneSlash");
			return ValidResult.getInstance();
		}

		private static final ConcurrentMap<String, Name> interned = new ConcurrentHashMap<>();

		/**
		 * @param name  when {@code null}, returns {@code null}
		 */
		public static Name valueOf(String name) throws ValidationException {
			if(name == null) return null;
			//Name existing = interned.get(name);
			//return existing!=null ? existing : new Name(name);
			return new Name(name, true);
		}

		final private String name;

		private Name(String name, boolean validate) throws ValidationException {
			this.name = name;
			if(validate) validate();
		}

		/**
		 * @param  name  Does not validate, should only be used with a known valid value.
		 */
		private Name(String name) {
			ValidationResult result;
			assert (result = validate(name)).isValid() : result.toString();
			this.name = name;
		}

		private void validate() throws ValidationException {
			ValidationResult result = validate(name);
			if(!result.isValid()) throw new ValidationException(result);
		}

		/**
		 * Perform same validation as constructor on readObject.
		 */
		private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
			ois.defaultReadObject();
			try {
				validate();
			} catch(ValidationException err) {
				InvalidObjectException newErr = new InvalidObjectException(err.getMessage());
				newErr.initCause(err);
				throw newErr;
			}
		}

		@Override
		public boolean equals(Object O) {
			return
				O != null
				&& O instanceof Name
				&& name.equals(((Name)O).name)
			;
		}

		@Override
		public int hashCode() {
			return name.hashCode();
		}

		@Override
		public int compareTo(Name other) {
			return (this == other) ? 0 : name.compareTo(other.name);
		}

		@Override
		public String toString() {
			return name;
		}

		/**
		 * Interns this name much in the same fashion as <code>String.intern()</code>.
		 *
		 * @see  String#intern()
		 */
		@Override
		public Name intern() {
			Name existing = interned.get(name);
			if(existing == null) {
				String internedName = name.intern();
				@SuppressWarnings("StringEquality")
				Name addMe = (name == internedName) ? this : new Name(internedName);
				existing = interned.putIfAbsent(internedName, addMe);
				if(existing == null) existing = addMe;
			}
			return existing;
		}

		@Override
		public com.aoindustries.aoserv.client.dto.FirewallZoneName getDto() {
			return new com.aoindustries.aoserv.client.dto.FirewallZoneName(name);
		}
	}

	/**
	 * Some Firewalld Zone names used within code.
	 */
	public static final Name
		DMZ,
		EXTERNAL,
		HOME,
		INTERNAL,
		PUBLIC,
		WORK
	;
	static {
		try {
			DMZ = Name.valueOf("dmz");
			EXTERNAL = Name.valueOf("external");
			HOME = Name.valueOf("home");
			INTERNAL = Name.valueOf("internal");
			PUBLIC = Name.valueOf("public");
			WORK = Name.valueOf("work");
		} catch(ValidationException e) {
			throw new AssertionError("These hard-coded values are valid", e);
		}
	}

	static final int
		COLUMN_PKEY = 0,
		COLUMN_SERVER = 1
	;
	static final String COLUMN_SERVER_name = "server";
	static final String COLUMN_NAME_name = "name";

	private int server;
	private Name name;
	private String _short;
	private String description;
	private boolean fail2ban;

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_SERVER: return server;
			case 2: return name;
			case 3: return _short;
			case 4: return description;
			case 5: return fail2ban;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	public Host getHost() throws SQLException, IOException {
		Host se = table.getConnector().getNet().getHost().get(server);
		if(se == null) throw new SQLException("Unable to find Host: " + server);
		return se;
	}

	public Name getName() {
		return name;
	}

	public String getShort() {
		return _short;
	}

	public String getDescription() {
		return description;
	}

	public boolean getFail2ban() {
		return fail2ban;
	}

	public List<BindFirewallZone> getNetBindFirewalldZones() throws IOException, SQLException {
		return table.getConnector().getNet().getBindFirewallZone().getNetBindFirewalldZones(this);
	}

	public List<Bind> getNetBinds() throws IOException, SQLException {
		List<BindFirewallZone> nbfzs = getNetBindFirewalldZones();
		List<Bind> nbs = new ArrayList<>(nbfzs.size());
		for(BindFirewallZone nbfz : nbfzs) {
			nbs.add(nbfz.getNetBind());
		}
		return nbs;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.FIREWALLD_ZONES;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			pkey        = result.getInt(1);
			server      = result.getInt(2);
			name        = Name.valueOf(result.getString(3));
			_short      = result.getString(4);
			description = result.getString(5);
			fail2ban    = result.getBoolean(6);
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
		try {
			pkey = in.readCompressedInt();
			server = in.readCompressedInt();
			name = Name.valueOf(in.readUTF()).intern();
			_short = InternUtils.intern(in.readNullUTF());
			description = InternUtils.intern(in.readNullUTF());
			fail2ban = in.readBoolean();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public String toStringImpl() {
		return server + ":" + name;
	}

	@Override
	public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(server);
		out.writeUTF(name.toString());
		out.writeNullUTF(_short);
		out.writeNullUTF(description);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_9) >= 0) {
			out.writeBoolean(fail2ban);
		}
	}
}
