/*
 * Copyright 2009-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.command;

import com.aoindustries.aoserv.client.*;
import java.util.Map;

/**
 * @author  AO Industries, Inc.
 */
public class AOServPermissionException extends SecurityException {

    private static final long serialVersionUID = -6731556196010571929L;

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
