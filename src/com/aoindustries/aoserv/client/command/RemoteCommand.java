/*
 * Copyright 2010-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.command;

import com.aoindustries.aoserv.client.*;
import java.io.Serializable;
import java.rmi.RemoteException;

/**
 * A command that runs remotely (accesses the master).  It may be transferred
 * via serialization or a combination of the getParamMap and createRemoteCommand
 * methods.
 *
 * @author  AO Industries, Inc.
 */
abstract public class RemoteCommand<R> extends AOServCommand<R> implements Serializable {

    // TODO: private static final long serialVersionUID = 1L;

    //private static final Object[] emptyObjectArray = new Object[0];

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
    /*
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
    */

    /**
     * Determines if this command may be retried in the event of an error.  Defaults to <code>true</code>.
     */
    public boolean isIdempotent() {
        return true;
    }

    /**
     * Serializes the command to the server for execution.
     */
    @Override
    public R execute(AOServConnector connector, boolean isInteractive) throws RemoteException {
        return connector.executeCommand(this, isInteractive).getResult();
    }
}
