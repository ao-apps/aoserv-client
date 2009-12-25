package com.aoindustries.aoserv.client.command;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.AOServPermission;
import com.aoindustries.aoserv.client.BusinessAdministrator;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * Except normal tabular data queries, all other data access and data manipulation occurs as commands.
 * Commands may run entirely client-side, or be serialized to the server for processing.
 * Commands are checked client-side for performance and then checked server-side for safety.  The client-side
 * checks may be used in interface design.
 * </p>
 * <p>
 * Each command may have one or two constructors.  When two constructors are provided,
 * they should each have the same number of parameters.  Only one of them should have its
 * parameters matched with the <code>Param</code> annotation.  The other constructor
 * provides the expected types.  In essence, one constructor represents the type-specific
 * parameters while the other represents the standard Java and web-services-compatible
 * types.
 * </p>
 *
 * @author  AO Industries, Inc.
 */
abstract public class AOServCommand<R> implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Map<Class<? extends AOServCommand>,CommandName> commandNames = new HashMap<Class<? extends AOServCommand>,CommandName>();
    static {
        for(CommandName commandName : CommandName.values) {
            if(commandNames.put(commandName.getCommandClass(), commandName)!=null) throw new AssertionError("Command class found more than once: "+commandName);
        }
    }

    /**
     * Gets the command name for this command.
     */
    final public CommandName getCommandName() {
        Class<? extends AOServCommand> clazz = getClass();
        CommandName commandName = commandNames.get(clazz);
        if(commandName==null) throw new AssertionError("commandName not found for class: "+clazz);
        return commandName;
    }

    /**
     * Gets the unmodifiable set of permissions that are required to be allowed
     * to execute this command.  An empty set indicates no specific permissions are
     * required.
     */
    abstract public Set<AOServPermission.Permission> getPermissions() throws RemoteException;

    /**
     * Validates this command, returning a set of error messages on a per-parameter
     * basis.  Messages that are not associated with a specific parameters will have
     * a <code>null</code> key.  If there is no validation problems, returns an
     * empty map.
     */
    abstract public Map<String,List<String>> validate(Locale locale, BusinessAdministrator connectedUser) throws RemoteException;

    /**
     * Executes the command and retrieves the result.  If the command return
     * value is void, returns <code>Void.TYPE</code>.  This will first ensure that the current
     * user has the appropriate permissions and will throw AOServPermissionException if doesn't
     * have the correct permissions.  This will first call <code>validate</code>
     * and throw an exception if the validation fails.
     */
    final public R execute(AOServConnector<?,?> connector) throws AOServPermissionException, ValidationException, RemoteException {
        // TODO: Check permissions

        // Validate
        Map<String,List<String>> errors = validate(connector.getLocale(), connector.getThisBusinessAdministrator());
        if(!errors.isEmpty()) throw new ValidationException(this, errors);

        return doExecute(connector);
    }

    /**
     * Called after security checks and validation succeeds.
     */
    abstract protected R doExecute(AOServConnector<?,?> connector) throws RemoteException;
}
