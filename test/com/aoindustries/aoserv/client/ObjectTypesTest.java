package com.aoindustries.aoserv.client;
/*
 * Copyright 2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */

import com.aoindustries.util.StandardErrorHandler;
import java.sql.Date;
import java.util.List;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests all of the types returned by AOServTable.getColumn(int) to make sure they match the types in the schema_columns table.
 *
 * @author  AO Industries, Inc.
 */
public class ObjectTypesTest extends TestCase {
    
    private AOServConnector conn;

    public ObjectTypesTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        conn=AOServConnector.getConnector(new StandardErrorHandler());
    }

    protected void tearDown() throws Exception {
        conn=null;
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(ObjectTypesTest.class);
        
        return suite;
    }

    /**
     * Test the type of all the objects in each AOServTable.
     */
    public void testTableObjectTypes() {
        System.out.println("Testing all object types returned by getColumn(int index)");
        System.out.println(". = Tested");
        System.out.println("E = Empty Table, Tests Not Performed");
        System.out.println("N = All Null, Tests Not Performed");
        System.out.println("U = Unsupported Operation");
        for(int c=0;c<SchemaTable.NUM_TABLES;c++) {
            AOServTable table=conn.getTable(c);
            String tableName=table.getTableName();
            System.out.print("    "+tableName+": ");
            List<AOServObject> rows=table.getRows();
            if(rows.isEmpty()) System.out.println('E');
            else {
                List<SchemaColumn> columns=table.getTableSchema().getSchemaColumns(conn);
                for(SchemaColumn column : columns) {
                    String columnName=column.getColumnName();
                    SchemaType type=column.getSchemaType(conn);
                    int typeNum=type.getNum();
                    char tested='N';
                    for(AOServObject row : rows) {
                        // Cast to proper type if not null
                        Object value=row.getColumn(column.getIndex());
                        if(value!=null) {
                            Class expectedType=null;
                            try {
                                switch(typeNum) {
                                    case SchemaType.ACCOUNTING: {expectedType=String.class; String accounting=(String)value; break;}
                                    case SchemaType.BOOLEAN: {expectedType=Boolean.class; Boolean b=(Boolean)value; break;}
                                    case SchemaType.BYTE: {expectedType=Byte.class; Byte b=(Byte)value; break;}
                                    case SchemaType.CITY: {expectedType=String.class; String city=(String)value; break;}
                                    case SchemaType.COUNTRY: {expectedType=String.class; String country=(String)value; break;}
                                    case SchemaType.DATE: {expectedType=Date.class; Date date=(Date)value; break;}
                                    case SchemaType.DECIMAL_2: {expectedType=Integer.class; Integer decimal2=(Integer)value; break;}
                                    case SchemaType.DECIMAL_3: {expectedType=Integer.class; Integer decimal3=(Integer)value; break;}
                                    case SchemaType.DOUBLE: {expectedType=Double.class; Double d=(Double)value; break;}
                                    case SchemaType.EMAIL: {expectedType=String.class; String email=(String)value; break;}
                                    case SchemaType.FKEY: {expectedType=Integer.class; Integer fkey=(Integer)value; break;}
                                    case SchemaType.FLOAT: {expectedType=Float.class; Float f=(Float)value; break;}
                                    case SchemaType.HOSTNAME: {expectedType=String.class; String hostname=(String)value; break;}
                                    case SchemaType.INT: {expectedType=Integer.class; Integer i=(Integer)value; break;}
                                    case SchemaType.INTERVAL: {expectedType=Long.class; Long interval=(Long)value; break;}
                                    case SchemaType.IP_ADDRESS: {expectedType=String.class; String ip=(String)value; break;}
                                    case SchemaType.LONG: {expectedType=Long.class; Long l=(Long)value; break;}
                                    case SchemaType.OCTAL_INT: {expectedType=Integer.class; Integer i=(Integer)value; break;}
                                    case SchemaType.OCTAL_LONG: {expectedType=Long.class; Long l=(Long)value; break;}
                                    case SchemaType.PACKAGE: {expectedType=String.class; String pack=(String)value; break;}
                                    case SchemaType.PATH: {expectedType=String.class; String path=(String)value; break;}
                                    case SchemaType.PHONE: {expectedType=String.class; String phone=(String)value; break;}
                                    case SchemaType.PKEY: {expectedType=Integer.class; Integer pkey=(Integer)value; break;}
                                    case SchemaType.SHORT: {expectedType=Short.class; Short s=(Short)value; break;}
                                    case SchemaType.STATE: {expectedType=String.class; String state=(String)value; break;}
                                    case SchemaType.STRING: {expectedType=String.class; String s=(String)value; break;}
                                    case SchemaType.TIME: {expectedType=Date.class; Date time=(Date)value; break;}
                                    case SchemaType.URL: {expectedType=String.class; String url=(String)value; break;}
                                    case SchemaType.USERNAME: {expectedType=String.class; String username=(String)value; break;}
                                    case SchemaType.ZIP: {expectedType=String.class; String zip=(String)value; break;}
                                    case SchemaType.ZONE: {expectedType=String.class; String zone=(String)value; break;}
                                    default: fail("Unexpected SchemaType id: "+typeNum);
                                }
                                String string=type.getString(value);
                                Object parsedValue=type.parseString(string);
                                if(value instanceof Date) {
                                    // milliseconds may be dropped
                                    long valueSeconds=((Date)value).getTime();
                                    long parsedSeconds=((Date)value).getTime();
                                    assertEquals(valueSeconds, parsedSeconds);
                                } else {
                                    assertEquals(value, parsedValue);
                                }
                                tested='.';
                            } catch(ClassCastException err) {
                                fail(tableName+"."+columnName+"=\""+value+"\": Unable to cast from type "+value.getClass().getName()+" to "+(expectedType==null ? null : expectedType.getName()));
                            } catch(UnsupportedOperationException err) {
                                tested='U';
                                break;
                            }
                        }
                    }
                    System.out.print(tested);
                }
                System.out.println();
            }
        }
    }
}
