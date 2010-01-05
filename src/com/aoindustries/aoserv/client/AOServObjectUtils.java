package com.aoindustries.aoserv.client;

/*
 * Copyright 2009-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.table.IndexType;
import java.lang.reflect.Method;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * An <code>AOServObject</code> is the lowest level object
 * for all data in the system.  Each <code>AOServObject</code>
 * belongs to an <code>AOServService</code>, each service
 * belongs to an <code>AOServConnector</code>, and each
 * connector belongs to an <code>AOServConnectorFactory</code>.
 *
 * @author  AO Industries, Inc.
 *
 * @see  AOServService
 */
public class AOServObjectUtils {

    private AOServObjectUtils() {
    }

    private static final Collator collator = Collator.getInstance(Locale.ENGLISH);
    public static int compareIgnoreCaseConsistentWithEquals(String S1, String S2) {
        if(S1==S2) return 0;
        int diff = collator.compare(S1, S2);
        if(diff!=0) return diff;
        return S1.compareTo(S2);
    }

    public static int compare(int i1, int i2) {
        return i1<i2 ? -1 : i1==i2 ? 0 : 1;
    }

    /**
     * Sorts nulls before non-nulls.
     */
    public static int compare(Integer i1, Integer i2) {
        // nulls before non-nulls
        if(i1==null) return i2==null ? 0 : -1;
        return i2==null ? 1 : i1<i2 ? -1 : i1==i2 ? 0 : 1;
    }

    public static int compare(short s1, short s2) {
        return s1<s2 ? -1 : s1==s2 ? 0 : 1;
    }

    public static int compareHostnames(String host1, String host2) {
        if(host1==host2) return 0;
        while(host1.length()>0 && host2.length()>0) {
            int pos=host1.lastIndexOf('.');
            String section1;
            if(pos==-1) {
                section1=host1;
                host1="";
            } else {
                section1=host1.substring(pos+1);
                host1=host1.substring(0, pos);
            }

            pos=host2.lastIndexOf('.');
            String section2;
            if(pos==-1) {
                section2=host2;
                host2="";
            } else {
                section2=host2.substring(pos+1);
                host2=host2.substring(0, pos);
            }

            int diff=compareIgnoreCaseConsistentWithEquals(section1, section2);
            if(diff!=0) return diff;
        }
        return compareIgnoreCaseConsistentWithEquals(host1, host2);
    }

    /**
     * Returns an unmodifiable set of the provided objects, not including any null values.
     */
    public static Set<? extends AOServObject> createDependencySet() {
        return Collections.emptySet();
    }

    /**
     * Returns an unmodifiable set of the provided objects, not including any null values.
     */
    public static Set<? extends AOServObject> createDependencySet(AOServObject obj) {
        if(obj==null) return Collections.emptySet();
        return Collections.singleton(obj);
    }

    /**
     * Returns an unmodifiable set of the provided objects, not including any null values.
     * It is assumed that the set passed-in is unmodifiable and it will be returned directly if
     * it contains no null values.
     */
    public static Set<? extends AOServObject> createDependencySet(Set<? extends AOServObject> objs) {
        boolean hasNull = false;
        for(AOServObject obj : objs) {
            if(obj==null) {
                hasNull = true;
                break;
            }
        }
        if(!hasNull) return objs;
        Set<AOServObject> set = new HashSet<AOServObject>(objs.size()*4/3+1);
        for(AOServObject obj : objs) {
            if(obj!=null) set.add(obj);
        }
        return AOServServiceUtils.unmodifiableSet(set);
    }

    /**
     * Returns an unmodifiable set of the provided objects, not including any null values.
     */
    @SuppressWarnings("unchecked")
    public static Set<? extends AOServObject> createDependencySet(Set... objss) {
        int totalSize = 0;
        for(Set<? extends AOServObject> objs : objss) totalSize+=objs.size();
        Set<AOServObject> set = new HashSet<AOServObject>(totalSize*4/3+1);
        for(Set<? extends AOServObject> objs : objss) {
            for(AOServObject obj : objs) {
                if(obj!=null) set.add(obj);
            }
        }
        if(set.size()==0) return Collections.emptySet();
        return AOServServiceUtils.unmodifiableSet(set);
    }

    /**
     * Returns an unmodifiable set of the provided objects, not including any null values.
     */
    public static Set<? extends AOServObject> createDependencySet(AOServObject... objs) {
        Set<AOServObject> set = new HashSet<AOServObject>(objs.length*4/3+1);
        for(AOServObject obj : objs) {
            if(obj!=null) set.add(obj);
        }
        if(set.size()==0) return Collections.emptySet();
        return AOServServiceUtils.unmodifiableSet(set);
    }

    private static final ConcurrentMap<Class<? extends AOServObject>,List<MethodColumn>> columns = new ConcurrentHashMap<Class<? extends AOServObject>, List<MethodColumn>>(ServiceName.values.size()*4/3+1, 0.75F, 1);

    /**
     * Gets the columns for the provided class, in column index order.
     * All SchemaColumn methods must not take parameters.
     * Ensures that no column name is duplicated.
     * Ensures that there is no gap in column index numbers.
     * Ensures that at most one primary key exists.
     * Ensures that no unique index exists without a primary key defined.
     */
    public static List<MethodColumn> getMethodColumns(Class<? extends AOServObject> clazz) {
        List<MethodColumn> methodColumns = columns.get(clazz);
        if(methodColumns==null) {
//            Class<?> getKeyReturnType;
//            try {
//                getKeyReturnType = clazz.getMethod("getKey").getReturnType();
//            } catch(NoSuchMethodException err) {
//                throw new AssertionError("getKey() method not found: "+clazz.getName());
//            }

            ArrayList<MethodColumn> newColumns = new ArrayList<MethodColumn>();
            Set<String> columnNames = new HashSet<String>();
            Set<String> primaryKeys = new HashSet<String>();
            Set<String> uniques = new HashSet<String>();
            for(Method method : clazz.getMethods()) {
                SchemaColumn schemaColumn = method.getAnnotation(SchemaColumn.class);
                if(schemaColumn!=null) {
                    String cname = schemaColumn.name();
                    // All SchemaColumn methods must not take parameters.
                    if(method.getParameterTypes().length>0) throw new AssertionError("Column method should not have any parameters: "+clazz.getName()+"."+method.getName());
                    if(!columnNames.add(cname)) throw new AssertionError("Column name found twice: "+clazz.getName()+"->"+cname);
                    int order = schemaColumn.order();
                    if(order<0) throw new AssertionError("Column order<0: "+clazz.getName()+"->"+order);
                    int newSize = order+1;
                    newColumns.ensureCapacity(newSize);
                    while(newColumns.size()<newSize) newColumns.add(null);
                    IndexType indexType = schemaColumn.index();
                    if(indexType==IndexType.PRIMARY_KEY) {
                        primaryKeys.add(cname);
//                        // Ensures the primary key column return values matches the return type of the <code>getKey</code> method.
//                        Class<?> methodReturnType = method.getReturnType();
//                        if(getKeyReturnType!=methodReturnType) {
//                            throw new AssertionError(
//                                "Mismatched key return types: "
//                                +clazz.getName()+"."+method.getName()+"()->"+methodReturnType.getName()
//                                +" and "+clazz.getName()+".getKey()->"+getKeyReturnType.getName()
//                            );
//                        }
                    } else if(indexType==IndexType.UNIQUE) {
                        uniques.add(cname);
                    }
                    if(newColumns.set(order, new MethodColumn(cname, indexType, method, schemaColumn))!=null) throw new AssertionError("Column index found twice: "+clazz.getName()+"->"+order);
                }
            }
            int size = newColumns.size();
            // Make sure each column index is used in succession
            if(size!=columnNames.size()) {
                // Find missing column(s)
                StringBuilder message = new StringBuilder("The following column indexes do not have a corresponding column method: "+clazz.getName()+"->");
                boolean didOne = false;
                for(int c=0; c<size; c++) {
                    if(newColumns.get(c)==null) {
                        if(didOne) message.append(", ");
                        else didOne = true;
                        message.append(c);
                    }
                }
                throw new AssertionError(message.toString());
            }
            // Ensures that at most one primary key exists.
            if(primaryKeys.size()>1) {
                StringBuilder message = new StringBuilder("More than one primary key found: ");
                message.append(clazz.getName()).append("->");
                boolean didOne = false;
                for(String cname : primaryKeys) {
                    if(didOne) message.append(", ");
                    else didOne = true;
                    message.append(cname);
                }
                throw new AssertionError(message.toString());
            }
            // Ensures that no unique index exists without a primary key defined.
            if(!uniques.isEmpty() && primaryKeys.isEmpty()) {
                StringBuilder message = new StringBuilder(uniques.size()==1 ? "Unique column exists without primary key: ":"Unique columns exist without primary key: ");
                message.append(clazz.getName()).append("->");
                boolean didOne = false;
                for(String cname : uniques) {
                    if(didOne) message.append(", ");
                    else didOne = true;
                    message.append(cname);
                }
                throw new AssertionError(message.toString());
            }
            // Make unmodifiable
            List<MethodColumn> unmod;
            if(size==0) throw new AssertionError("No columns found");
            if(size==1) unmod = Collections.singletonList(newColumns.get(0));
            else {
                newColumns.trimToSize();
                unmod = Collections.unmodifiableList(newColumns);
            }
            // Put in cache
            List<MethodColumn> existingColumns = columns.putIfAbsent(clazz, unmod);
            methodColumns = existingColumns==null ? unmod : existingColumns;
        }
        return methodColumns;
    }

    private static final ConcurrentMap<Class<? extends AOServObject>,Map<String,MethodColumn>> columnMaps = new ConcurrentHashMap<Class<? extends AOServObject>, Map<String,MethodColumn>>(ServiceName.values.size()*4/3+1, 0.75F, 1);

    /**
     * Provides map from getMethodColumns.
     */
    public static Map<String,MethodColumn> getMethodColumnMap(Class<? extends AOServObject> clazz) {
        Map<String,MethodColumn> map = columnMaps.get(clazz);
        if(map==null) {
            List<MethodColumn> list = getMethodColumns(clazz);
            Map<String,MethodColumn> newMap = new HashMap<String,MethodColumn>(list.size()*4/3+1);
            for(MethodColumn mc : list) newMap.put(mc.getColumnName(), mc);
            newMap = Collections.unmodifiableMap(newMap);
            // Put in cache
            Map<String,MethodColumn> existingColumnMap = columnMaps.putIfAbsent(clazz, newMap);
            map = existingColumnMap==null ? newMap : existingColumnMap;
        }
        return map;
    }
}
