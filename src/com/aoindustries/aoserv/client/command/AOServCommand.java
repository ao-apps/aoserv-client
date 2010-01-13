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
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
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
 * Each command may have any number of public constructors.  One of the public constructors should
 * have its parameters marked with the Param annotation.  If there are no parameters with the Param
 * annotations, an empty constructor will be used.  Generally, a command will have up to two
 * constructors, one that is AOSH-compatible with the Param annotation, and the other that
 * is more type-specific.
 * </p>
 *
 * @author  AO Industries, Inc.
 */
abstract public class AOServCommand<R> implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Map<Class<? extends AOServCommand>,CommandName> commandNames = new HashMap<Class<? extends AOServCommand>,CommandName>();
    private static final Map<CommandName,Constructor<? extends AOServCommand<?>>> commandConstructors = new HashMap<CommandName,Constructor<? extends AOServCommand<?>>>();

    @SuppressWarnings("unchecked")
    private static void initMaps() {
        for(CommandName commandName : CommandName.values) {
            Class<? extends AOServCommand> commandClass = commandName.getCommandClass();
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
     * value is void, returns <code>null</code>.  This will first ensure that the current
     * user has the appropriate permissions and will throw AOServPermissionException if doesn't
     * have the correct permissions.  This will first call <code>validate</code>
     * and throw an exception if the validation fails.
     */
    final public R execute(AOServConnector<?,?> connector, boolean isInteractive) throws AOServPermissionException, ValidationException, RemoteException {
        // TODO: Make sure current user is enabled

        // TODO: Check permissions

        // Validate
        Map<String,List<String>> errors = validate(connector.getLocale(), connector.getThisBusinessAdministrator());
        if(!errors.isEmpty()) throw new ValidationException(this, errors);

        return doExecute(connector, isInteractive);
    }

    /**
     * Called after security checks and validation succeeds.
     */
    abstract protected R doExecute(AOServConnector<?,?> connector, boolean isInteractive) throws RemoteException;

    /**
     * Determines if this command may be retried in the event of an error.  Defaults to <code>true</code>.
     */
    public boolean isRetryable() {
        return true;
    }
}
