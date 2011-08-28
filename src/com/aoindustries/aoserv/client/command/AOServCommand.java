/*
 * Copyright 2009-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.command;

import com.aoindustries.aoserv.client.*;
import com.aoindustries.util.i18n.ApplicationResourcesAccessor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * Except normal tabular data queries, all other data access and data manipulation occurs as commands.
 * Commands may run entirely client-side, or be serialized to the server for processing.
 * Commands are checked client-side for performance and then checked server-side for security.  The client-side
 * checks may be efficiently used in interface design.
 * </p>
 * <p>
 * Each command may have any number of public constructors.  For AOSH automatic command-line parsing,
 * one of the public constructors should have its parameters marked with the Param annotation.  If
 * there are no parameters with the Param annotations, any empty constructor will be used.  Generally,
 * a command will have only one constructor that takes specific types and AOSH converts the command
 * line into the appropriate types.
 * </p>
 *
 * @author  AO Industries, Inc.
 */
abstract public class AOServCommand<R> {

    private static final Map<Class<? extends AOServCommand<?>>,CommandName> commandNames = new HashMap<Class<? extends AOServCommand<?>>,CommandName>();
    private static final Map<CommandName,Constructor<? extends AOServCommand<?>>> commandConstructors = new EnumMap<CommandName,Constructor<? extends AOServCommand<?>>>(CommandName.class);

    @SuppressWarnings("unchecked")
    private static void initMaps() {
        for(CommandName commandName : CommandName.values) {
            Class<? extends AOServCommand<?>> commandClass = commandName.getCommandClass();
            // Populate commandNames
            if(commandNames.put(commandClass, commandName)!=null) throw new AssertionError("Command class found more than once: "+commandName);
            // Populate commandConstructors
            Constructor<?>[] constructors = commandClass.getConstructors();
            Constructor<?> emptyConstructor = null;
            Constructor<?> commandConstructor = null;
            for(Constructor<?> constructor : constructors) {
                if(Modifier.isPublic(constructor.getModifiers())) {
                    Annotation[][] paramAnnotations = constructor.getParameterAnnotations();
                    if(paramAnnotations.length==0) {
                        emptyConstructor = constructor;
                    } else {
                        // Must have annotation on all parameters or none of the parameters
                        int annotationCount = 0;
                        for(Annotation[] annotations : paramAnnotations) {
                            boolean hasParam = false;
                            for(Annotation annotation : annotations) {
                                if(annotation instanceof Param /*annotation.annotationType()==Param.class*/) {
                                    if(hasParam) throw new AssertionError("Duplicate @Param annotations on constructor: commandName="+commandName+", commandClass="+commandClass.getName());
                                    hasParam = true;
                                }
                            }
                            if(hasParam) annotationCount++;
                        }
                        if(annotationCount!=0) {
                            if(annotationCount!=paramAnnotations.length) throw new AssertionError("Constructor parameter must either have no @Param annotations are one @Param annotation per parameter: commandName="+commandName+", commandClass="+commandClass.getName());
                            if(commandConstructor!=null) throw new AssertionError("Command contains more than one @Param constructor: commandName="+commandName+", commandClass="+commandClass.getName());
                            commandConstructor = constructor;
                        }
                    }
                }
            }
            if(commandConstructor==null) commandConstructor = emptyConstructor;
            if(commandConstructor==null) throw new AssertionError("No empty or @Param constructor found: commandName="+commandName+", commandClass="+commandClass.getName());
            commandConstructors.put(commandName, (Constructor<? extends AOServCommand<?>>)commandConstructor);
        }
    }
    static {
        initMaps();
    }

    /**
     * Gets the AOSH constructor for the provided command.
     */
    public static Constructor<? extends AOServCommand<?>> getCommandConstructor(CommandName commandName) {
        Constructor<? extends AOServCommand<?>> commandConstructor = commandConstructors.get(commandName);
        if(commandConstructor==null) throw new AssertionError("commandConstructor==null");
        return commandConstructor;
    }

    /**
     * Converts empty strings to <code>null</code>.
     */
    protected static String nullIfEmpty(String param) {
        return param==null || param.length()==0 ? null : param;
    }

    /**
     * Gets the command name for this command.
     */
    final public CommandName getCommandName() {
        @SuppressWarnings("unchecked")
        Class<? extends AOServCommand<R>> clazz = (Class<? extends AOServCommand<R>>)getClass();
        CommandName commandName = commandNames.get(clazz);
        if(commandName==null) throw new AssertionError("commandName not found for class: "+clazz);
        return commandName;
    }

    /**
     * Adds the error from the AOSH commands application resources bundle.
     */
    protected static Map<String,List<String>> addValidationError(Map<String,List<String>> errors, String param, String messageKey, Object... messageArgs) {
        return addValidationError(errors, param, ApplicationResources.accessor, messageKey, messageArgs);
    }

    /**
     * Adds the error from the provided resources bundle.
     */
    protected static Map<String,List<String>> addValidationError(Map<String,List<String>> errors, String param, ApplicationResourcesAccessor applicationResources, String messageKey, Object... messageArgs) {
        return addValidationErrorImpl(errors, param, applicationResources.getMessage(messageKey, messageArgs));
    }

    /**
     * Adds the provided error.
     */
    private static Map<String,List<String>> addValidationErrorImpl(Map<String,List<String>> errors, String param, String message) {
        if(errors.isEmpty()) errors = new HashMap<String,List<String>>();
        List<String> list = errors.get(param);
        if(list==null) errors.put(param, list = new ArrayList<String>());
        list.add(message);
        return errors;
    }

    /**
     * Adds password errors.
     */
    protected static Map<String,List<String>> addValidationError(Map<String,List<String>> errors, String param, List<PasswordChecker.Result> results) {
        if(PasswordChecker.hasResults(results)) {
            for(PasswordChecker.Result result : results) {
                errors = addValidationError(
                    errors,
                    param,
                    "AOServCommand.addValidateError.PasswordChecker.Result",
                    result.getCategory(),
                    result.getResult()
                );
            }
        }
        return errors;
    }

    /**
     * Merges validation errors.
     */
    protected static Map<String,List<String>> addValidationErrors(Map<String,List<String>> errors, Map<String,List<String>> additional) {
        for(Map.Entry<String,List<String>> entry : additional.entrySet()) {
            String param = entry.getKey();
            for(String message : entry.getValue()) errors = addValidationErrorImpl(errors, param, message);
        }
        return errors;
    }

    /**
     * Validates the command using the provided connector.  The validation will be
     * performed using the default business administrator for this connection (after
     * switch-user).
     */
    final public Map<String,List<String>> checkExecute(AOServConnector conn) throws RemoteException {
        return checkExecute(conn, conn, conn.getThisBusinessAdministrator());
    }

    /**
     * Checks whether this command is allowed to execute, returning a set of error
     * messages on a per-parameter basis.  Messages that are not associated with
     * a specific parameter will have a <code>null</code> key.  If there are no
     * problems, returns an empty map.
     *
     * Checks the following, in this order:
     * <ol>
     *   <li>Current user enabled</li>
     *   <li>Permissions</li>
     *   <li>Read-only status of connection and command</li>
     *   <li>Command-specific parameter checks - this often involves checking access to the relevant objects and validating data.</li>
     * </ol>
     * 
     * Security Note: This validation may be performed on a connector with higher privileges than
     * are normal for the provided connectedUser.  This is so server-side validation
     * may include checks that are not possible with the information that is directly
     * available to the user.  Use the connector associated with <code>commandUser</code> carefully.
     *
     * @param  userConn  the user's filtered connection - no special permissions exposed
     * @param  rootConn  the connector with maximum possible privileges, used to check as much as possible, not necessarily limited to the filtering imposed on userConn
     * @param  rootUser  the user that will execute the command, possible from a connector with elevated privileges
     */
    final public Map<String,List<String>> checkExecute(AOServConnector userConn, AOServConnector rootConn, BusinessAdministrator rootUser) throws RemoteException {
        Map<String,List<String>> errors = Collections.emptyMap();
        errors = addValidationErrors(
            errors,
            checkUserEnabled(rootUser)
        );
        if(errors.isEmpty()) {
            errors = addValidationErrors(
                errors,
                checkPermissions(rootUser)
            );
            if(errors.isEmpty()) {
                errors = addValidationErrors(
                    errors,
                    checkReadOnly(userConn)
                );
                if(errors.isEmpty()) {
                    errors = addValidationErrors(
                        errors,
                        checkCommand(userConn, rootConn, rootUser)
                    );
                }
            }
        }
        return errors;
    }

    /**
     * Checks whether the business administrator is enabled and allow to run this command.
     * Returns empty map when there are no problems.
     *
     * @param  rootUser  the user that will execute the command, possible from a connector with elevated privileges
     */
    final public Map<String,List<String>> checkUserEnabled(BusinessAdministrator rootUser) throws RemoteException {
        Map<String,List<String>> errors = Collections.emptyMap();
        if(rootUser.isDisabled()) {
            errors = addValidationError(errors, null, "AOServCommand.checkUserEnabled.disabled");
        }
        return errors;
    }

    /**
     * Checks if the business administrator has the permissions necessary to run this command.
     * Returns empty map when there are no problems.
     *
     * @param  rootUser  the user that will execute the command, possible from a connector with elevated privileges
     */
    final public Map<String,List<String>> checkPermissions(BusinessAdministrator rootUser) throws RemoteException {
        Map<String,List<String>> errors = Collections.emptyMap();
        Set<AOServPermission.Permission> permissions = getCommandName().getPermissions();
        if(!permissions.isEmpty()) {
            for(AOServPermission.Permission permission : permissions) {
                if(!rootUser.hasPermission(permission)) {
                    errors = addValidationError(errors, null, "AOServCommand.checkPermissions.missingPermission", permission);
                }
            }
        }
        return errors;
    }

    /**
     * Checks if an attempt is being made to execute a read-write command on a read-only connection.
     *
     * @param  userConn  the user's filtered connection - no special permissions exposed
     */
    final public Map<String,List<String>> checkReadOnly(AOServConnector userConn) throws RemoteException {
        Map<String,List<String>> errors = Collections.emptyMap();
        if(userConn.isReadOnly() && !isReadOnly()) {
            errors = addValidationError(errors, null, "AOServCommand.checkReadOnly.message");
        }
        return errors;
    }

    /**
     * Performs any command-specific checks, such as access checks and data validation.
     * Returns empty map when there are no problems.
     *
     * @param  userConn  the user's filtered connection - no special permissions exposed
     * @param  rootConn  the connector with maximum possible privileges, used to check as much as possible, not necessarily limited to the filtering imposed on userConn
     * @param  rootUser  the user that will execute the command, possible from a connector with elevated privileges
     */
    abstract protected Map<String,List<String>> checkCommand(AOServConnector userConn, AOServConnector rootConn, BusinessAdministrator rootUser) throws RemoteException;

    /**
     * Checks if this command is read-only.  A read-only command does not modify any state either
     * locally or on the server.  Some connectors are read-only and are only allowed to execute
     * read-only commands.
     */
    abstract public boolean isReadOnly();

    /**
     * Executes this command in non-interactive mode.
     */
    final public R execute(AOServConnector connector) throws RemoteException {
        return execute(connector, false);
    }

    /**
     * Executes the command and retrieves the result.
     * If the command return value is void, returns <code>null</code>.
     */
    abstract public R execute(AOServConnector connector, boolean isInteractive) throws RemoteException;
}
