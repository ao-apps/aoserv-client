package com.aoindustries.aoserv.client.command;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServConnector;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>
 * A command that runs remotely (accesses the master).  It may be transferred
 * via serialization or a combination of the getParamMap and createRemoteCommand
 * methods.
 * </p>
 *
 * @author  AO Industries, Inc.
 */
abstract public class RemoteCommand<R> extends AOServCommand<R> implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Object[] emptyObjectArray = new Object[0];

    /**
     * <p>
     * Gets the unmodifable map of parameters contained by this command.  Any parameters
     * with <code>null</code> values are converted to empty strings.
     * </p>
     * <p>
     * Internally a <code>LinkedHashMap</code> is used so that iteration over the parameters
     * will provide them in the same order as the command constructor.
     * </p>
     * <p>
     * This default implementation uses reflection to call the javabeans
     * getters corresponding to the parameter names.
     * </p>
     * @see  #createRemoteCommand  to reconstruct the command object
     */
    public Map<String,String> getParamMap() {
        try {
            Constructor<? extends AOServCommand<?>> constructor = getCommandConstructor(getCommandName());
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            Annotation[][] parameterAnnotations = constructor.getParameterAnnotations();
            PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(getClass()).getPropertyDescriptors();
            int numParams = parameterTypes.length;
            assert numParams==parameterAnnotations.length;
            Map<String,String> paramMap = new LinkedHashMap<String,String>(numParams*4/3+1);
            for(int c=0; c<parameterTypes.length; c++) {
                // Find the @Param annotation
                Param paramAnnotation = null;
                for(Annotation anno : parameterAnnotations[c]) {
                    if(anno instanceof Param) {
                        paramAnnotation = (Param)anno;
                        break;
                    }
                }
                if(paramAnnotation==null) throw new AssertionError("paramAnnotation==null");
                String paramName = paramAnnotation.name();
                Class<?> parameterType = parameterTypes[c];
                // Find the JavaBeans getter
                Method getter = null;
                for(PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                    if(propertyDescriptor.getName().equals(paramName)) {
                        getter = propertyDescriptor.getReadMethod();
                        break;
                    }
                }
                assert getter.getReturnType()==parameterType;
                // Call the method
                Object paramValue = getter.invoke(this, emptyObjectArray);
                // Check nullable status
                if(paramValue==null && !paramAnnotation.nullable()) throw new AssertionError("null value from non-nullable parameter: "+paramName);
                // Convert to String
                String stringValue;
                if(parameterType==String.class) {
                    stringValue = (String)paramValue;
                } else {
                    throw new AssertionError("Unexpected parameter type: "+parameterType.getName());
                }
                // Convert null to empty String
                if(stringValue==null) stringValue = "";
                // Add the parameter to the map
                if(paramMap.put(paramName, stringValue)!=null) throw new AssertionError("Duplicate parameter name: "+paramName);
            }
            return Collections.unmodifiableMap(paramMap);
        } catch(IntrospectionException err) {
            throw new RuntimeException(err);
        } catch(IllegalAccessException err) {
            throw new RuntimeException(err);
        } catch(InvocationTargetException err) {
            throw new RuntimeException(err);
        }
    }

    /**
     * <p>
     * Constructs a remote command given its name and parameter map.
     * </p>
     * <p>
     * Commands are passed as a map of name, value pairs.
     * To help maintain compatibility between versions, any extra parameters
     * are ignored, while any missing parameters will default to null.  If
     * a parameter is non-nullable and missing, it will result in an error.
     * </p>
     * <p>
     * Just like AOSH, any parameters that are nullable and an empty string will
     * be converted to null.
     * </p>
     *
     * @see  #getParamMap()
     */
    public static RemoteCommand<?> createRemoteCommand(CommandName commandName, Map<String,String> paramMap) {
        throw new RuntimeException("TODO: Implement method");
    }

    /**
     * Checks if this command is read-only.  Read-only commands must have no side-affects.
     * Read-only connectors may only execute read-only commands.
     */
    abstract public boolean isReadOnlyCommand();

    /**
     * Determines if this command may be retried in the event of an error.  Defaults to <code>true</code>.
     */
    public boolean isRetryableCommand() {
        return true;
    }

    /**
     * Serializes the command to the server for execution.
     */
    @Override
    public R execute(AOServConnector<?,?> connector, boolean isInteractive) throws RemoteException {
        return connector.executeCommand(this, isInteractive).getResult();
    }
}