package com.aoindustries.aoserv.client.command;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServPermission;
import java.util.Map;

/**
 * @author  AO Industries, Inc.
 */
public class AOServPermissionException extends SecurityException {

    private static final long serialVersionUID = 1L;

    final private AOServCommand<?> command;
    final private Map<AOServPermission.Permission,Boolean> permissions;

    public AOServPermissionException(AOServCommand<?> command, Map<AOServPermission.Permission,Boolean> permissions) {
        this.command = command;
        this.permissions = permissions;
    }

    /**
     * Gets the command that failed.
     */
    public AOServCommand<?> getCommand() {
        return command;
    }

    /**
     * Gets the permissions that were required and whether each one was meet by the calling user.
     */
    public Map<AOServPermission.Permission,Boolean> getPermissions() {
        return permissions;
    }
}
