/*
 * Copyright 2009-2013, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

import java.util.Calendar;

/**
 * @author  AO Industries, Inc.
 */
public class AOServer extends AOServObject {

	private int server;
	private DomainName hostname;
	private Integer daemonBind;
	private HashedPassword daemonKey;
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

	public AOServer() {
	}

	public AOServer(
		int server,
		DomainName hostname,
		Integer daemonBind,
		HashedPassword daemonKey,
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
		Float monitoringLoadCritical
	) {
		this.server = server;
		this.hostname = hostname;
		this.daemonBind = daemonBind;
		this.daemonKey = daemonKey;
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
	}

	public int getServer() {
		return server;
	}

	public void setServer(int server) {
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

	public HashedPassword getDaemonKey() {
		return daemonKey;
	}

	public void setDaemonKey(HashedPassword daemonKey) {
		this.daemonKey = daemonKey;
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
		return DtoUtils.getCalendar(lastDistroTime);
	}

	public void setLastDistroTime(Calendar lastDistroTime) {
		this.lastDistroTime = lastDistroTime==null ? null : lastDistroTime.getTimeInMillis();
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
}
