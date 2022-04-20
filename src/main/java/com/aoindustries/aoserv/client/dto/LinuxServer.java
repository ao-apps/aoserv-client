/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2009-2013, 2016, 2017, 2018, 2019, 2021, 2022  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.dto;

import com.aoapps.net.dto.DomainName;
import com.aoapps.net.dto.HostAddress;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * @author  AO Industries, Inc.
 */
public class LinuxServer extends AOServObject {

  private int server;
  private DomainName hostname;
  private Integer daemonBind;
  private int poolSize;
  private int distroHour;
  private Long lastDistroTime;
  private Integer failoverServer;
  private String daemonDeviceId;
  private Integer daemonConnectBind;
  private String timeZone;
  private Integer jilterBind;
  private boolean restrictOutboundEmail;
  private HostAddress daemonConnectAddress;
  private int failoverBatchSize;
  private Float monitoringLoadLow;
  private Float monitoringLoadMedium;
  private Float monitoringLoadHigh;
  private Float monitoringLoadCritical;
  private LinuxId uidMin;
  private LinuxId gidMin;
  private LinuxId uidMax;
  private LinuxId gidMax;
  private LinuxId lastUid;
  private LinuxId lastGid;
  private Long sftpUmask;

  public LinuxServer() {
    // Do nothing
  }

  public LinuxServer(
    int server,
    DomainName hostname,
    Integer daemonBind,
    int poolSize,
    int distroHour,
    Long lastDistroTime,
    Integer failoverServer,
    String daemonDeviceId,
    Integer daemonConnectBind,
    String timeZone,
    Integer jilterBind,
    boolean restrictOutboundEmail,
    HostAddress daemonConnectAddress,
    int failoverBatchSize,
    Float monitoringLoadLow,
    Float monitoringLoadMedium,
    Float monitoringLoadHigh,
    Float monitoringLoadCritical,
    LinuxId uidMin,
    LinuxId gidMin,
    LinuxId uidMax,
    LinuxId gidMax,
    LinuxId lastUid,
    LinuxId lastGid,
    Long sftpUmask
  ) {
    this.server = server;
    this.hostname = hostname;
    this.daemonBind = daemonBind;
    this.poolSize = poolSize;
    this.distroHour = distroHour;
    this.lastDistroTime = lastDistroTime;
    this.failoverServer = failoverServer;
    this.daemonDeviceId = daemonDeviceId;
    this.daemonConnectBind = daemonConnectBind;
    this.timeZone = timeZone;
    this.jilterBind = jilterBind;
    this.restrictOutboundEmail = restrictOutboundEmail;
    this.daemonConnectAddress = daemonConnectAddress;
    this.failoverBatchSize = failoverBatchSize;
    this.monitoringLoadLow = monitoringLoadLow;
    this.monitoringLoadMedium = monitoringLoadMedium;
    this.monitoringLoadHigh = monitoringLoadHigh;
    this.monitoringLoadCritical = monitoringLoadCritical;
    this.uidMin = uidMin;
    this.gidMin = gidMin;
    this.uidMax = uidMax;
    this.gidMax = gidMax;
    this.lastUid = lastUid;
    this.lastGid = lastGid;
    this.sftpUmask = sftpUmask;
  }

  public int getHost() {
    return server;
  }

  public void setHost(int server) {
    this.server = server;
  }

  public DomainName getHostname() {
    return hostname;
  }

  public void setHostname(DomainName hostname) {
    this.hostname = hostname;
  }

  public Integer getDaemonBind() {
    return daemonBind;
  }

  public void setDaemonBind(Integer daemonBind) {
    this.daemonBind = daemonBind;
  }

  public int getPoolSize() {
    return poolSize;
  }

  public void setPoolSize(int poolSize) {
    this.poolSize = poolSize;
  }

  public int getDistroHour() {
    return distroHour;
  }

  public void setDistroHour(int distroHour) {
    this.distroHour = distroHour;
  }

  public Calendar getLastDistroTime() {
    return DtoUtils.getCalendar(
      lastDistroTime,
      timeZone == null ? null : TimeZone.getTimeZone(timeZone)
    );
  }

  public void setLastDistroTime(Calendar lastDistroTime) {
    this.lastDistroTime = lastDistroTime == null ? null : lastDistroTime.getTimeInMillis();
  }

  public Integer getFailoverServer() {
    return failoverServer;
  }

  public void setFailoverServer(Integer failoverServer) {
    this.failoverServer = failoverServer;
  }

  public String getDaemonDeviceId() {
    return daemonDeviceId;
  }

  public void setDaemonDeviceId(String daemonDeviceId) {
    this.daemonDeviceId = daemonDeviceId;
  }

  public Integer getDaemonConnectBind() {
    return daemonConnectBind;
  }

  public void setDaemonConnectBind(Integer daemonConnectBind) {
    this.daemonConnectBind = daemonConnectBind;
  }

  public String getTimeZone() {
    return timeZone;
  }

  public void setTimeZone(String timeZone) {
    this.timeZone = timeZone;
  }

  public Integer getJilterBind() {
    return jilterBind;
  }

  public void setJilterBind(Integer jilterBind) {
    this.jilterBind = jilterBind;
  }

  public boolean isRestrictOutboundEmail() {
    return restrictOutboundEmail;
  }

  public void setRestrictOutboundEmail(boolean restrictOutboundEmail) {
    this.restrictOutboundEmail = restrictOutboundEmail;
  }

  public HostAddress getDaemonConnectAddress() {
    return daemonConnectAddress;
  }

  public void setDaemonConnectAddress(HostAddress daemonConnectAddress) {
    this.daemonConnectAddress = daemonConnectAddress;
  }

  public int getFailoverBatchSize() {
    return failoverBatchSize;
  }

  public void setFailoverBatchSize(int failoverBatchSize) {
    this.failoverBatchSize = failoverBatchSize;
  }

  public Float getMonitoringLoadLow() {
    return monitoringLoadLow;
  }

  public void setMonitoringLoadLow(Float monitoringLoadLow) {
    this.monitoringLoadLow = monitoringLoadLow;
  }

  public Float getMonitoringLoadMedium() {
    return monitoringLoadMedium;
  }

  public void setMonitoringLoadMedium(Float monitoringLoadMedium) {
    this.monitoringLoadMedium = monitoringLoadMedium;
  }

  public Float getMonitoringLoadHigh() {
    return monitoringLoadHigh;
  }

  public void setMonitoringLoadHigh(Float monitoringLoadHigh) {
    this.monitoringLoadHigh = monitoringLoadHigh;
  }

  public Float getMonitoringLoadCritical() {
    return monitoringLoadCritical;
  }

  public void setMonitoringLoadCritical(Float monitoringLoadCritical) {
    this.monitoringLoadCritical = monitoringLoadCritical;
  }

  public LinuxId getUidMin() {
    return uidMin;
  }

  public void setUidMin(LinuxId uidMin) {
    this.uidMin = uidMin;
  }

  public LinuxId getGidMin() {
    return gidMin;
  }

  public void setGidMin(LinuxId gidMin) {
    this.gidMin = gidMin;
  }

  public LinuxId getUidMax() {
    return uidMax;
  }

  public void setUidMax(LinuxId uidMax) {
    this.uidMax = uidMax;
  }

  public LinuxId getGidMax() {
    return gidMax;
  }

  public void setGidMax(LinuxId gidMax) {
    this.gidMax = gidMax;
  }

  public LinuxId getLastUid() {
    return lastUid;
  }

  public void setLastUid(LinuxId lastUid) {
    this.lastUid = lastUid;
  }

  public LinuxId getLastGid() {
    return lastGid;
  }

  public void setLastGid(LinuxId lastGid) {
    this.lastGid = lastGid;
  }

  public Long getSftpUmask() {
    return sftpUmask;
  }

  public void setSftpUmask(Long sftpUmask) {
    this.sftpUmask = sftpUmask;
  }
}
