package com.aoindustries.aoserv.client.command;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.ServiceName;
import java.util.Locale;

/**
 * Each command has a unique name.  These IDs may change over time, but
 * are constant for one release.
 *
 * @author  AO Industries, Inc.
 */
public enum CommandName {
    desc(DescribeCommand.class, null),
    // TODO: select(SelectCommand.class, null),
    // TODO: show(ShowCommand.class, null);
    ;

    private final Class<? extends AOServCommand> commandClass;
    private final ServiceName serviceName;

    private CommandName(Class<? extends AOServCommand> commandClass, ServiceName serviceName) {
        this.commandClass = commandClass;
        this.serviceName = serviceName;
    }

    public Class<? extends AOServCommand> getCommandClass() {
        return commandClass;
    }

    /**
     * Gets the service name this command is best associated with, or <code>null</code>
     * if there is no association.
     */
    public ServiceName getServiceName() {
        return serviceName;
    }

    /**
     * Gets a short description of the service.
     */
    public String getShortDesc(Locale userLocale) {
        return ApplicationResources.accessor.getMessage(userLocale, "CommandName."+name()+".shortDesc");
    }
}
