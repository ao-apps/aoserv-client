/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class VirtualServer {

    private int server;
    private int primaryRam;
    private int primaryRamTarget;
    private Integer secondaryRam;
    private Integer secondaryRamTarget;
    private String minimumProcessorType;
    private String minimumProcessorArchitecture;
    private Integer minimumProcessorSpeed;
    private Integer minimumProcessorSpeedTarget;
    private short processorCores;
    private short processorCoresTarget;
    private short processorWeight;
    private short processorWeightTarget;
    private boolean primaryPhysicalServerLocked;
    private boolean secondaryPhysicalServerLocked;
    private boolean requiresHvm;
    private String vncPassword;

    public VirtualServer() {
    }

    public VirtualServer(
        int server,
        int primaryRam,
        int primaryRamTarget,
        Integer secondaryRam,
        Integer secondaryRamTarget,
        String minimumProcessorType,
        String minimumProcessorArchitecture,
        Integer minimumProcessorSpeed,
        Integer minimumProcessorSpeedTarget,
        short processorCores,
        short processorCoresTarget,
        short processorWeight,
        short processorWeightTarget,
        boolean primaryPhysicalServerLocked,
        boolean secondaryPhysicalServerLocked,
        boolean requiresHvm,
        String vncPassword
    ) {
        this.server = server;
        this.primaryRam = primaryRam;
        this.primaryRamTarget = primaryRamTarget;
        this.secondaryRam = secondaryRam;
        this.secondaryRamTarget = secondaryRamTarget;
        this.minimumProcessorType = minimumProcessorType;
        this.minimumProcessorArchitecture = minimumProcessorArchitecture;
        this.minimumProcessorSpeed = minimumProcessorSpeed;
        this.minimumProcessorSpeedTarget = minimumProcessorSpeedTarget;
        this.processorCores = processorCores;
        this.processorCoresTarget = processorCoresTarget;
        this.processorWeight = processorWeight;
        this.processorWeightTarget = processorWeightTarget;
        this.primaryPhysicalServerLocked = primaryPhysicalServerLocked;
        this.secondaryPhysicalServerLocked = secondaryPhysicalServerLocked;
        this.requiresHvm = requiresHvm;
        this.vncPassword = vncPassword;
    }

    public int getServer() {
        return server;
    }

    public void setServer(int server) {
        this.server = server;
    }

    public int getPrimaryRam() {
        return primaryRam;
    }

    public void setPrimaryRam(int primaryRam) {
        this.primaryRam = primaryRam;
    }

    public int getPrimaryRamTarget() {
        return primaryRamTarget;
    }

    public void setPrimaryRamTarget(int primaryRamTarget) {
        this.primaryRamTarget = primaryRamTarget;
    }

    public Integer getSecondaryRam() {
        return secondaryRam;
    }

    public void setSecondaryRam(Integer secondaryRam) {
        this.secondaryRam = secondaryRam;
    }

    public Integer getSecondaryRamTarget() {
        return secondaryRamTarget;
    }

    public void setSecondaryRamTarget(Integer secondaryRamTarget) {
        this.secondaryRamTarget = secondaryRamTarget;
    }

    public String getMinimumProcessorType() {
        return minimumProcessorType;
    }

    public void setMinimumProcessorType(String minimumProcessorType) {
        this.minimumProcessorType = minimumProcessorType;
    }

    public String getMinimumProcessorArchitecture() {
        return minimumProcessorArchitecture;
    }

    public void setMinimumProcessorArchitecture(String minimumProcessorArchitecture) {
        this.minimumProcessorArchitecture = minimumProcessorArchitecture;
    }

    public Integer getMinimumProcessorSpeed() {
        return minimumProcessorSpeed;
    }

    public void setMinimumProcessorSpeed(Integer minimumProcessorSpeed) {
        this.minimumProcessorSpeed = minimumProcessorSpeed;
    }

    public Integer getMinimumProcessorSpeedTarget() {
        return minimumProcessorSpeedTarget;
    }

    public void setMinimumProcessorSpeedTarget(Integer minimumProcessorSpeedTarget) {
        this.minimumProcessorSpeedTarget = minimumProcessorSpeedTarget;
    }

    public short getProcessorCores() {
        return processorCores;
    }

    public void setProcessorCores(short processorCores) {
        this.processorCores = processorCores;
    }

    public short getProcessorCoresTarget() {
        return processorCoresTarget;
    }

    public void setProcessorCoresTarget(short processorCoresTarget) {
        this.processorCoresTarget = processorCoresTarget;
    }

    public short getProcessorWeight() {
        return processorWeight;
    }

    public void setProcessorWeight(short processorWeight) {
        this.processorWeight = processorWeight;
    }

    public short getProcessorWeightTarget() {
        return processorWeightTarget;
    }

    public void setProcessorWeightTarget(short processorWeightTarget) {
        this.processorWeightTarget = processorWeightTarget;
    }

    public boolean isPrimaryPhysicalServerLocked() {
        return primaryPhysicalServerLocked;
    }

    public void setPrimaryPhysicalServerLocked(boolean primaryPhysicalServerLocked) {
        this.primaryPhysicalServerLocked = primaryPhysicalServerLocked;
    }

    public boolean isSecondaryPhysicalServerLocked() {
        return secondaryPhysicalServerLocked;
    }

    public void setSecondaryPhysicalServerLocked(boolean secondaryPhysicalServerLocked) {
        this.secondaryPhysicalServerLocked = secondaryPhysicalServerLocked;
    }

    public boolean isRequiresHvm() {
        return requiresHvm;
    }

    public void setRequiresHvm(boolean requiresHvm) {
        this.requiresHvm = requiresHvm;
    }

    public String getVncPassword() {
        return vncPassword;
    }

    public void setVncPassword(String vncPassword) {
        this.vncPassword = vncPassword;
    }

}
