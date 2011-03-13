/*
 * Copyright 2010-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class EmailInbox extends AOServObject {

    private int linuxAccount;
    private Integer autoresponderFrom;
    private String autoresponderSubject;
    private UnixPath autoresponderPath;
    private boolean isAutoresponderEnabled;
    private boolean useInbox;
    private Short trashEmailRetention;
    private Short junkEmailRetention;
    private String saIntegrationMode;
    private float saRequiredScore;
    private Integer saDiscardScore;

    public EmailInbox() {
    }

    public EmailInbox(
        int linuxAccount,
        Integer autoresponderFrom,
        String autoresponderSubject,
        UnixPath autoresponderPath,
        boolean isAutoresponderEnabled,
        boolean useInbox,
        Short trashEmailRetention,
        Short junkEmailRetention,
        String saIntegrationMode,
        float saRequiredScore,
        Integer saDiscardScore
    ) {
        this.linuxAccount = linuxAccount;
        this.autoresponderFrom = autoresponderFrom;
        this.autoresponderSubject = autoresponderSubject;
        this.autoresponderPath = autoresponderPath;
        this.isAutoresponderEnabled = isAutoresponderEnabled;
        this.useInbox = useInbox;
        this.trashEmailRetention = trashEmailRetention;
        this.junkEmailRetention = junkEmailRetention;
        this.saIntegrationMode = saIntegrationMode;
        this.saRequiredScore = saRequiredScore;
        this.saDiscardScore = saDiscardScore;
    }

    public int getLinuxAccount() {
        return linuxAccount;
    }

    public void setLinuxAccount(int linuxAccount) {
        this.linuxAccount = linuxAccount;
    }

    public Integer getAutoresponderFrom() {
        return autoresponderFrom;
    }

    public void setAutoresponderFrom(Integer autoresponderFrom) {
        this.autoresponderFrom = autoresponderFrom;
    }

    public String getAutoresponderSubject() {
        return autoresponderSubject;
    }

    public void setAutoresponderSubject(String autoresponderSubject) {
        this.autoresponderSubject = autoresponderSubject;
    }

    public UnixPath getAutoresponderPath() {
        return autoresponderPath;
    }

    public void setAutoresponderPath(UnixPath autoresponderPath) {
        this.autoresponderPath = autoresponderPath;
    }

    public boolean isIsAutoresponderEnabled() {
        return isAutoresponderEnabled;
    }

    public void setIsAutoresponderEnabled(boolean isAutoresponderEnabled) {
        this.isAutoresponderEnabled = isAutoresponderEnabled;
    }

    public boolean isUseInbox() {
        return useInbox;
    }

    public void setUseInbox(boolean useInbox) {
        this.useInbox = useInbox;
    }

    public Short getTrashEmailRetention() {
        return trashEmailRetention;
    }

    public void setTrashEmailRetention(Short trashEmailRetention) {
        this.trashEmailRetention = trashEmailRetention;
    }

    public Short getJunkEmailRetention() {
        return junkEmailRetention;
    }

    public void setJunkEmailRetention(Short junkEmailRetention) {
        this.junkEmailRetention = junkEmailRetention;
    }

    public String getSaIntegrationMode() {
        return saIntegrationMode;
    }

    public void setSaIntegrationMode(String saIntegrationMode) {
        this.saIntegrationMode = saIntegrationMode;
    }

    public float getSaRequiredScore() {
        return saRequiredScore;
    }

    public void setSaRequiredScore(float saRequiredScore) {
        this.saRequiredScore = saRequiredScore;
    }

    public Integer getSaDiscardScore() {
        return saDiscardScore;
    }

    public void setSaDiscardScore(Integer saDiscardScore) {
        this.saDiscardScore = saDiscardScore;
    }
}
