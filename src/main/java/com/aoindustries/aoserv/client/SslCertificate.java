/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2018  AO Industries, Inc.
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
import com.aoindustries.lang.ObjectUtils;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author  AO Industries, Inc.
 */
final public class SslCertificate extends CachedObjectIntegerKey<SslCertificate> {

	static final int
		COLUMN_PKEY = 0,
		COLUMN_AO_SERVER = 1,
		COLUMN_PACKAGE = 2
	;
	static final String COLUMN_AO_SERVER_name = "ao_server";
	static final String COLUMN_CERT_FILE_name = "cert_file";

	private int ao_server;
	private int packageNum;
	private UnixPath keyFile;
	private UnixPath csrFile;
	private UnixPath certFile;
	private UnixPath chainFile;
	private String certbotName;

	@Override
	String toStringImpl() throws SQLException, IOException {
		return getCommonName().toStringImpl();
	}

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_AO_SERVER: return ao_server;
			case COLUMN_PACKAGE: return packageNum;
			case 3: return keyFile;
			case 4: return csrFile;
			case 5: return certFile;
			case 6: return chainFile;
			case 7: return certbotName;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.SSL_CERTIFICATES;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			int pos = 1;
			pkey = result.getInt(pos++);
			ao_server = result.getInt(pos++);
			packageNum = result.getInt(pos++);
			keyFile = UnixPath.valueOf(result.getString(pos++));
			csrFile = UnixPath.valueOf(result.getString(pos++));
			certFile = UnixPath.valueOf(result.getString(pos++));
			chainFile = UnixPath.valueOf(result.getString(pos++));
			certbotName = result.getString(pos++);
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey = in.readCompressedInt();
			ao_server = in.readCompressedInt();
			packageNum = in.readCompressedInt();
			keyFile = UnixPath.valueOf(in.readUTF());
			csrFile = UnixPath.valueOf(in.readNullUTF());
			certFile = UnixPath.valueOf(in.readUTF());
			chainFile = UnixPath.valueOf(in.readNullUTF());
			certbotName = in.readNullUTF();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(ao_server);
		out.writeCompressedInt(packageNum);
		out.writeUTF(ObjectUtils.toString(keyFile));
		out.writeNullUTF(ObjectUtils.toString(csrFile));
		out.writeUTF(ObjectUtils.toString(certFile));
		out.writeNullUTF(ObjectUtils.toString(chainFile));
		out.writeNullUTF(certbotName);
	}

	public AOServer getAOServer() throws SQLException, IOException {
		AOServer obj = table.connector.getAoServers().get(pkey);
		if(obj == null) throw new SQLException("Unable to find AOServer: " + pkey);
		return obj;
	}

	public Package getPackage() throws IOException, SQLException {
		Package obj = table.connector.getPackages().get(packageNum);
		if(obj == null) throw new SQLException("Unable to find Package: " + packageNum);
		return obj;
	}

	public UnixPath getKeyFile() {
		return keyFile;
	}

	public UnixPath getCsrFile() {
		return csrFile;
	}

	public UnixPath getCertFile() {
		return certFile;
	}

	public UnixPath getChainFile() {
		return chainFile;
	}

	public String getCertbotName() {
		return certbotName;
	}

	public List<SslCertificateName> getNames() throws IOException, SQLException {
		return table.connector.getSslCertificateNames().getNames(this);
	}

	public SslCertificateName getCommonName() throws SQLException, IOException {
		return table.connector.getSslCertificateNames().getCommonName(this);
	}

	public List<SslCertificateName> getAltNames() throws IOException, SQLException {
		return table.connector.getSslCertificateNames().getAltNames(this);
	}

	public List<CyrusImapdBind> getCyrusImapdBinds() throws IOException, SQLException {
		return table.connector.getCyrusImapdBinds().getCyrusImapdBinds(this);
	}

	public List<CyrusImapdServer> getCyrusImapdServers() throws IOException, SQLException {
		return table.connector.getCyrusImapdServers().getCyrusImapdServers(this);
	}

	public List<HttpdSiteBind> getHttpdSiteBinds() throws IOException, SQLException {
		return table.connector.getHttpdSiteBinds().getHttpdSiteBinds(this);
	}

	public List<SendmailServer> getSendmailServersByServerCertificate() throws IOException, SQLException {
		return table.connector.getSendmailServers().getSendmailServersByServerCertificate(this);
	}

	public List<SendmailServer> getSendmailServersByClientCertificate() throws IOException, SQLException {
		return table.connector.getSendmailServers().getSendmailServersByClientCertificate(this);
	}

	public static class Check implements Serializable {

		private static final long serialVersionUID = 1L;

		private final String check;
		private final String value;
		private final AlertLevel alertLevel;
		private final String message;

		public Check(
			String check,
			String value,
			AlertLevel alertLevel,
			String message
		) {
			this.check = check;
			this.value = value;
			this.alertLevel = alertLevel;
			this.message = message;
		}

		/**
		 * Gets the human-readable description of the check performed.
		 */
		public String getCheck() {
			return check;
		}

		/**
		 * Gets any value representing the result of the check.
		 */
		public String getValue() {
			return value;
		}

		/**
		 * The alert level for monitoring purposes.
		 */
		public AlertLevel getAlertLevel() {
			return alertLevel;
		}

		/**
		 * Gets the optional human-readable result of the check.
		 */
		public String getMessage() {
			return message;
		}
	}

	/**
	 * Performs a status check on this certificate.
	 */
	public List<Check> check() throws IOException, SQLException {
		return table.connector.requestResult(true,
			AOServProtocol.CommandID.CHECK_SSL_CERTIFICATE,
			new AOServConnector.ResultRequest<List<Check>>() {
				private List<Check> result;

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(pkey);
				}

				@Override
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
					int code = in.readByte();
					if(code == AOServProtocol.NEXT) {
						int size = in.readCompressedInt();
						List<Check> results = new ArrayList<>(size);
						for(int c = 0; c < size; c++) {
							results.add(
								new Check(
									in.readUTF(),
									in.readUTF(),
									AlertLevel.valueOf(in.readUTF()),
									in.readNullUTF()
								)
							);
						}
						this.result = results;
					} else {
						AOServProtocol.checkResult(code, in);
						throw new IOException("Unexpected response code: " + code);
					}
				}

				@Override
				public List<Check> afterRelease() {
					return result;
				}
			}
		);
	}
}
