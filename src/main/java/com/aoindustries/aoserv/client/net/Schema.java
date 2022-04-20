/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2018, 2020, 2021, 2022  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.net;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.AOServTable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author  AO Industries, Inc.
 */
public class Schema extends com.aoindustries.aoserv.client.Schema {

  private final com.aoindustries.aoserv.client.net.monitoring.Schema monitoring;
  public com.aoindustries.aoserv.client.net.monitoring.Schema getMonitoring() {return monitoring;}

  private final com.aoindustries.aoserv.client.net.reputation.Schema reputation;
  public com.aoindustries.aoserv.client.net.reputation.Schema getReputation() {return reputation;}

  private final List<? extends com.aoindustries.aoserv.client.Schema> schemas;
  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField") // Returning unmodifiable
  public List<? extends com.aoindustries.aoserv.client.Schema> getSchemas() {
    return schemas;
  }

  private final AppProtocolTable AppProtocol;
  public AppProtocolTable getAppProtocol() {return AppProtocol;}

  private final BindTable Bind;
  public BindTable getBind() {return Bind;}

  private final BindFirewallZoneTable BindFirewallZone;
  public BindFirewallZoneTable getBindFirewallZone() {return BindFirewallZone;}

  private final DeviceTable Device;
  public DeviceTable getDevice() {return Device;}

  private final DeviceIdTable DeviceId;
  public DeviceIdTable getDeviceId() {return DeviceId;}

  private final FirewallZoneTable FirewallZone;
  public FirewallZoneTable getFirewallZone() {return FirewallZone;}

  private final HostTable Host;
  public HostTable getHost() {return Host;}

  private final IpAddressTable IpAddress;
  public IpAddressTable getIpAddress() {return IpAddress;}

  private final TcpRedirectTable TcpRedirect;
  public TcpRedirectTable getTcpRedirect() {return TcpRedirect;}

  private final List<? extends AOServTable<?, ?>> tables;

  public Schema(AOServConnector connector) {
    super(connector);

    // TODO: Load schemas with ServiceLoader
    ArrayList<com.aoindustries.aoserv.client.Schema> newSchemas = new ArrayList<>();
    newSchemas.add(monitoring = new com.aoindustries.aoserv.client.net.monitoring.Schema(connector));
    newSchemas.add(reputation = new com.aoindustries.aoserv.client.net.reputation.Schema(connector));
    newSchemas.trimToSize();
    schemas = Collections.unmodifiableList(newSchemas);

    ArrayList<AOServTable<?, ?>> newTables = new ArrayList<>();
    newTables.add(AppProtocol = new AppProtocolTable(connector));
    newTables.add(Bind = new BindTable(connector));
    newTables.add(BindFirewallZone = new BindFirewallZoneTable(connector));
    newTables.add(Device = new DeviceTable(connector));
    newTables.add(DeviceId = new DeviceIdTable(connector));
    newTables.add(FirewallZone = new FirewallZoneTable(connector));
    newTables.add(Host = new HostTable(connector));
    newTables.add(IpAddress = new IpAddressTable(connector));
    newTables.add(TcpRedirect = new TcpRedirectTable(connector));
    newTables.trimToSize();
    tables = Collections.unmodifiableList(newTables);
  }

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField") // Returning unmodifiable
  public List<? extends AOServTable<?, ?>> getTables() {
    return tables;
  }

  @Override
  public String getName() {
    return "net";
  }
}
