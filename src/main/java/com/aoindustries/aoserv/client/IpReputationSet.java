/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2012-2013, 2016, 2017  AO Industries, Inc.
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

import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.util.IntList;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;

/**
 * An <code>IpReputationSet</code> stores network and host IP reputation fed
 * from external sources.
 *
 * @author  AO Industries, Inc.
 */
final public class IpReputationSet extends CachedObjectIntegerKey<IpReputationSet> {

	static final int
		COLUMN_PKEY=0,
		COLUMN_ACCOUNTING=1,
		COLUMN_IDENTIFIER=2
	;
	static final String COLUMN_IDENTIFIER_name= "identifier";

	private AccountingCode accounting;
	private String identifier;
	private boolean allowSubaccountUse;
	private int maxHosts;
	private short maxUncertainReputation;
	private short maxDefiniteReputation;
	private short networkPrefix;
	private short maxNetworkReputation;
	private int hostDecayInterval;
	private long lastHostDecay;
	private int networkDecayInterval;
	private long lastNetworkDecay;
	private long lastReputationAdded;

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.IP_REPUTATION_SETS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			int pos = 1;
			pkey = result.getInt(pos++);
			accounting = AccountingCode.valueOf(result.getString(pos++));
			identifier = result.getString(pos++);
			allowSubaccountUse = result.getBoolean(pos++);
			maxHosts = result.getInt(pos++);
			maxUncertainReputation = result.getShort(pos++);
			maxDefiniteReputation = result.getShort(pos++);
			networkPrefix = result.getShort(pos++);
			maxNetworkReputation = result.getShort(pos++);
			hostDecayInterval = result.getInt(pos++);
			lastHostDecay = result.getTimestamp(pos++).getTime();
			networkDecayInterval = result.getInt(pos++);
			lastNetworkDecay = result.getTimestamp(pos++).getTime();
			lastReputationAdded = result.getTimestamp(pos++).getTime();
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedUTF(accounting.toString(), 0);
		out.writeUTF(identifier);
		out.writeBoolean(allowSubaccountUse);
		out.writeCompressedInt(maxHosts);
		out.writeShort(maxUncertainReputation);
		out.writeShort(maxDefiniteReputation);
		out.writeShort(networkPrefix);
		out.writeShort(maxNetworkReputation);
		out.writeCompressedInt(hostDecayInterval);
		out.writeLong(lastHostDecay);
		out.writeCompressedInt(networkDecayInterval);
		out.writeLong(lastNetworkDecay);
		if(version.compareTo(AOServProtocol.Version.VERSION_1_67)>=0) out.writeLong(lastReputationAdded);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey = in.readCompressedInt();
			accounting = AccountingCode.valueOf(in.readCompressedUTF()).intern();
			identifier = in.readUTF();
			allowSubaccountUse = in.readBoolean();
			maxHosts = in.readCompressedInt();
			maxUncertainReputation = in.readShort();
			maxDefiniteReputation = in.readShort();
			networkPrefix = in.readShort();
			maxNetworkReputation = in.readShort();
			hostDecayInterval = in.readCompressedInt();
			lastHostDecay = in.readLong();
			networkDecayInterval = in.readCompressedInt();
			lastNetworkDecay = in.readLong();
			lastReputationAdded = in.readLong();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_ACCOUNTING: return accounting;
			case COLUMN_IDENTIFIER: return identifier;
			case 3: return allowSubaccountUse;
			case 4: return maxHosts;
			case 5: return maxUncertainReputation;
			case 6: return maxDefiniteReputation;
			case 7: return networkPrefix;
			case 8: return maxNetworkReputation;
			case 9: return hostDecayInterval;
			case 10: return getLastHostDecay();
			case 11: return networkDecayInterval;
			case 12: return getLastNetworkDecay();
			case 13: return getLastReputationAdded();
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	/**
	 * May be filtered.
	 */
	public Business getBusiness() throws SQLException, IOException {
		return table.connector.getBusinesses().get(accounting);
	}

	/**
	 * Gets the system-wide unique identifier for this reputation set.
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * Allows sub accounts to use this set.  They cannot see the set hosts and networks.
	 */
	public boolean getAllowSubaccountUse() {
		return allowSubaccountUse;
	}

	/**
	 * Gets the maximum number of individual hosts that will be tracked.
	 */
	public int getMaxHosts() {
		return maxHosts;
	}

	/**
	 * Gets the maximum uncertain reputation score for a host.
	 */
	public short getMaxUncertainReputation() {
		return maxUncertainReputation;
	}

	/**
	 * Gets the maximum definite reputation score for a host.
	 */
	public short getMaxDefiniteReputation() {
		return maxDefiniteReputation;
	}

	/**
	 * Gets the network prefix size, such as 24 for a /24 (class C) network.
	 */
	public short getNetworkPrefix() {
		return networkPrefix;
	}

	/**
	 * Gets the maximum reputation score for a network.
	 */
	public short getMaxNetworkReputation() {
		return maxNetworkReputation;
	}

	/**
	 * Gets the number of seconds between each host reputation decay.
	 */
	public int getHostDecayInterval() {
		return hostDecayInterval;
	}

	/**
	 * Gets the last time the hosts were decayed.
	 */
	public Timestamp getLastHostDecay() {
		return new Timestamp(lastHostDecay);
	}

	/**
	 * Gets the number of seconds between each network reputation decay.
	 */
	public int getNetworkDecayInterval() {
		return networkDecayInterval;
	}

	/**
	 * Gets the last time the networks were decayed.
	 */
	public Timestamp getLastNetworkDecay() {
		return new Timestamp(lastNetworkDecay);
	}

	/**
	 * Gets the last time reputation was added.
	 */
	public Timestamp getLastReputationAdded() {
		return new Timestamp(lastReputationAdded);
	}

	public List<IpReputationSetHost> getHosts() throws IOException, SQLException {
		return table.connector.getIpReputationSetHosts().getHosts(this);
	}

	public List<IpReputationSetNetwork> getNetworks() throws IOException, SQLException {
		return table.connector.getIpReputationSetNetworks().getNetworks(this);
	}

	// <editor-fold desc="Commands">
	// Note: toChar must never be 'N', since that would conflict with 'N' used for "Network".
	public enum ConfidenceType {
		UNCERTAIN {
			@Override
			public char toChar() {
				return 'U';
			}
		},
		DEFINITE {
			@Override
			public char toChar() {
				return 'D';
			}
		};

		public abstract char toChar();

		public static ConfidenceType fromChar(char ch) {
			switch(ch) {
				case 'U': return UNCERTAIN;
				case 'D': return DEFINITE;
				default : throw new IllegalArgumentException("Unexpected ConfidenceType character: " + ch);
			}
		}
	}

	public enum ReputationType {
		GOOD {
			@Override
			public char toChar() {
				return 'G';
			}
		},
		BAD {
			@Override
			public char toChar() {
				return 'B';
			}
		};

		public abstract char toChar();

		public static ReputationType fromChar(char ch) {
			switch(ch) {
				case 'G': return GOOD;
				case 'B': return BAD;
				default : throw new IllegalArgumentException("Unexpected ReputationType character: " + ch);
			}
		}
	}

	public final static class AddReputation {
		final int host;
		final ConfidenceType confidence;
		final ReputationType reputationType;
		final short score;

		public AddReputation(
			int host,
			ConfidenceType confidence,
			ReputationType reputationType,
			short score
		) {
			this.host = host;
			this.confidence = confidence;
			this.reputationType = reputationType;
			this.score = score;
		}

		public int getHost() {
			return host;
		}

		public ConfidenceType getConfidence() {
			return confidence;
		}

		public ReputationType getReputationType() {
			return reputationType;
		}

		public short getScore() {
			return score;
		}
	}

	public void addReputation(int host, ConfidenceType confidence, ReputationType reputationType, short score) throws IOException, SQLException {
		addReputation(
			Collections.singletonList(
				new AddReputation(host, confidence, reputationType, score)
			)
		);
	}

	public void addReputation(final Collection<AddReputation> addReputations) throws IOException, SQLException {
		final int size = addReputations.size();
		if(size>0) {
			table.connector.requestUpdate(
				true,
				AOServProtocol.CommandID.ADD_IP_REPUTATION,
				new AOServConnector.UpdateRequest() {
					IntList invalidateList;

					@Override
					public void writeRequest(CompressedDataOutputStream out) throws IOException {
						out.writeCompressedInt(pkey);
						out.writeCompressedInt(size);
						int count = 0;
						for(AddReputation addRep : addReputations) {
							out.writeInt(addRep.host);
							out.writeChar(addRep.confidence.toChar());
							out.writeChar(addRep.reputationType.toChar());
							out.writeShort(addRep.score);
							count++;
						}
						if(count!=size) throw new ConcurrentModificationException("size!=count: " + size + "!=" + count);
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
	}
	// </editor-fold>
}
