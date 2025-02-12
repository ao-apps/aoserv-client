/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2017, 2018, 2019, 2020, 2021, 2022, 2025  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.pki;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.validation.ValidationException;
import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.email.CyrusImapdBind;
import com.aoindustries.aoserv.client.email.CyrusImapdServer;
import com.aoindustries.aoserv.client.email.SendmailServer;
import com.aoindustries.aoserv.client.linux.PosixPath;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.monitoring.AlertLevel;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.web.VirtualHost;
import java.io.IOException;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author  AO Industries, Inc.
 */
public final class Certificate extends CachedObjectIntegerKey<Certificate> {

  static final int COLUMN_PKEY = 0;
  static final int COLUMN_AO_SERVER = 1;
  static final int COLUMN_PACKAGE = 2;
  static final String COLUMN_AO_SERVER_name = "ao_server";
  static final String COLUMN_CERT_FILE_name = "cert_file";

  private int aoServer;
  private int packageNum;
  private PosixPath keyFile;
  private PosixPath csrFile;
  private PosixPath certFile;
  private PosixPath chainFile;
  private String certbotName;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public Certificate() {
    // Do nothing
  }

  @Override
  public String toStringImpl() throws SQLException, IOException {
    return getCommonName().toStringImpl();
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_PKEY:
        return pkey;
      case COLUMN_AO_SERVER:
        return aoServer;
      case COLUMN_PACKAGE:
        return packageNum;
      case 3:
        return keyFile;
      case 4:
        return csrFile;
      case 5:
        return certFile;
      case 6:
        return chainFile;
      case 7:
        return certbotName;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.SSL_CERTIFICATES;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    try {
      int pos = 1;
      pkey = result.getInt(pos++);
      aoServer = result.getInt(pos++);
      packageNum = result.getInt(pos++);
      keyFile = PosixPath.valueOf(result.getString(pos++));
      csrFile = PosixPath.valueOf(result.getString(pos++));
      certFile = PosixPath.valueOf(result.getString(pos++));
      chainFile = PosixPath.valueOf(result.getString(pos++));
      certbotName = result.getString(pos++);
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    try {
      pkey = in.readCompressedInt();
      aoServer = in.readCompressedInt();
      packageNum = in.readCompressedInt();
      keyFile = PosixPath.valueOf(in.readUTF());
      csrFile = PosixPath.valueOf(in.readNullUTF());
      certFile = PosixPath.valueOf(in.readUTF());
      chainFile = PosixPath.valueOf(in.readNullUTF());
      certbotName = in.readNullUTF();
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeCompressedInt(aoServer);
    out.writeCompressedInt(packageNum);
    out.writeUTF(keyFile.toString());
    out.writeNullUTF(Objects.toString(csrFile, null));
    out.writeUTF(certFile.toString());
    out.writeNullUTF(Objects.toString(chainFile, null));
    out.writeNullUTF(certbotName);
  }

  public Server getLinuxServer() throws SQLException, IOException {
    Server obj = table.getConnector().getLinux().getServer().get(pkey);
    if (obj == null) {
      throw new SQLException("Unable to find linux.Server: " + pkey);
    }
    return obj;
  }

  public Package getPackage() throws IOException, SQLException {
    Package obj = table.getConnector().getBilling().getPackage().get(packageNum);
    if (obj == null) {
      throw new SQLException("Unable to find Package: " + packageNum);
    }
    return obj;
  }

  /**
   * The private key file.
   */
  public PosixPath getKeyFile() {
    return keyFile;
  }

  /**
   * The optional CSR file.
   */
  public PosixPath getCsrFile() {
    return csrFile;
  }

  /**
   * The public key file.
   */
  public PosixPath getCertFile() {
    return certFile;
  }

  /**
   * The optional certificate chain file.
   */
  public PosixPath getChainFile() {
    return chainFile;
  }

  public String getCertbotName() {
    return certbotName;
  }

  public List<CertificateName> getNames() throws IOException, SQLException {
    return table.getConnector().getPki().getCertificateName().getNames(this);
  }

  public CertificateName getCommonName() throws SQLException, IOException {
    return table.getConnector().getPki().getCertificateName().getCommonName(this);
  }

  public List<CertificateName> getAltNames() throws IOException, SQLException {
    return table.getConnector().getPki().getCertificateName().getAltNames(this);
  }

  public List<CertificateOtherUse> getOtherUses() throws IOException, SQLException {
    return table.getConnector().getPki().getCertificateOtherUse().getOtherUses(this);
  }

  public List<CyrusImapdBind> getCyrusImapdBinds() throws IOException, SQLException {
    return table.getConnector().getEmail().getCyrusImapdBind().getCyrusImapdBinds(this);
  }

  public List<CyrusImapdServer> getCyrusImapdServers() throws IOException, SQLException {
    return table.getConnector().getEmail().getCyrusImapdServer().getCyrusImapdServers(this);
  }

  public List<VirtualHost> getHttpdSiteBinds() throws IOException, SQLException {
    return table.getConnector().getWeb().getVirtualHost().getHttpdSiteBinds(this);
  }

  public List<SendmailServer> getSendmailServersByServerCertificate() throws IOException, SQLException {
    return table.getConnector().getEmail().getSendmailServer().getSendmailServersByServerCertificate(this);
  }

  public List<SendmailServer> getSendmailServersByClientCertificate() throws IOException, SQLException {
    return table.getConnector().getEmail().getSendmailServer().getSendmailServersByClientCertificate(this);
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
   *
   * @param  allowCached  allow a cached response?  When {@code false} may be slow, but result will be up-to-date.
   *                      {@code true} is good for interactive use, while {@code false} is good for background
   *                      tasks such as certificate monitoring.
   */
  public List<Check> check(final boolean allowCached) throws IOException, SQLException {
    return table.getConnector().requestResult(
        true,
        AoservProtocol.CommandId.CHECK_SSL_CERTIFICATE,
        new AoservConnector.ResultRequest<List<Check>>() {
          private List<Check> result;

          @Override
          public void writeRequest(StreamableOutput out) throws IOException {
            out.writeCompressedInt(pkey);
            out.writeBoolean(allowCached);
          }

          @Override
          public void readResponse(StreamableInput in) throws IOException, SQLException {
            int code = in.readByte();
            if (code == AoservProtocol.NEXT) {
              int size = in.readCompressedInt();
              List<Check> results = new ArrayList<>(size);
              for (int c = 0; c < size; c++) {
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
              AoservProtocol.checkResult(code, in);
              throw new IOException("Unexpected response code: " + code);
            }
          }

          @Override
          public List<Check> afterRelease() {
            return Collections.unmodifiableList(result);
          }
        }
    );
  }
}
