/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2006-2013, 2016, 2017, 2018, 2019, 2020, 2021  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aoserv-client.
 *
 * aoserv-client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aoserv-client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aoserv-client.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.account.User;
import com.aoindustries.aoserv.client.schema.Column;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.schema.Type;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests all of the types returned by AOServTable.getColumn(int) to make sure they match the types in the schema_columns table.
 *
 * TODO: This test does not run without a master setup.
 *
 * @author  AO Industries, Inc.
 */
@SuppressWarnings("UseOfSystemOutOrSystemErr")
public class ObjectTypesTODO extends TestCase {

	private List<AOServConnector> conns;

	public ObjectTypesTODO(String testName) {
		super(testName);
	}

	@Override
	protected void setUp() throws Exception {
		conns = AOServConnectorTODO.getTestConnectors();
	}

	@Override
	protected void tearDown() throws Exception {
		conns = null;
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(ObjectTypesTODO.class);

		return suite;
	}

	/**
	 * Test the type of all the objects in each AOServTable.
	 */
	public void testTableObjectTypes() throws Exception {
		System.out.println("Testing all object types returned by getColumn(int index)");
		System.out.println(". = Tested");
		System.out.println("E = Empty Table, Tests Not Performed");
		System.out.println("N = All Null, Tests Not Performed");
		System.out.println("U = Unsupported Operation");
		int numTables = Table.TableID.values().length;
		for(AOServConnector conn : conns) {
			User.Name connUsername = conn.getCurrentAdministrator().getKey();
			System.out.println("    "+connUsername);
			for(int c=0;c<numTables;c++) {
				// Excluded for testing speed
				if(
					c==Table.TableID.DISTRO_FILES.ordinal()
					|| c==Table.TableID.WhoisHistory.ordinal() // TODO: Just exclude output/error columns?
				) continue;
				AOServTable<?, ?> table=conn.getTable(c);
				String tableName=table.getTableName();
				System.out.print("        "+tableName+": ");
				List<? extends AOServObject<?, ?>> rows=table.getRows();
				if(rows.isEmpty()) System.out.println('E');
				else {
					List<Column> columns=table.getTableSchema().getSchemaColumns(conn);
					for(Column column : columns) {
						String columnName = column.getName();
						Type type = column.getType(conn);
						int typeNum=type.getId();
						char tested='N';
						for(AOServObject<?, ?> row : rows) {
							// Cast to proper type if not null
							Object value=row.getColumn(column.getIndex());
							if(value!=null) {
								Class<?> expectedType=null;
								try {
									switch(typeNum) {
										case Type.ACCOUNTING: {expectedType=String.class; String accounting=(String)value; break;}
										case Type.BOOLEAN: {expectedType=Boolean.class; Boolean b=(Boolean)value; break;}
										//case SchemaType.BYTE: {expectedType=Byte.class; Byte b=(Byte)value; break;}
										//case SchemaType.CITY: {expectedType=String.class; String city=(String)value; break;}
										//case SchemaType.COUNTRY: {expectedType=String.class; String country=(String)value; break;}
										case Type.DATE: {expectedType=Date.class; Date date=(Date)value; break;}
										case Type.DECIMAL_2: {expectedType=Integer.class; Integer decimal2=(Integer)value; break;}
										case Type.DECIMAL_3: {expectedType=Integer.class; Integer decimal3=(Integer)value; break;}
										case Type.DOUBLE: {expectedType=Double.class; Double d=(Double)value; break;}
										case Type.EMAIL: {expectedType=String.class; String email=(String)value; break;}
										case Type.FKEY: {expectedType=Integer.class; Integer fkey=(Integer)value; break;}
										case Type.FLOAT: {expectedType=Float.class; Float f=(Float)value; break;}
										case Type.HOSTNAME: {expectedType=String.class; String hostname=(String)value; break;}
										case Type.INT: {expectedType=Integer.class; Integer i=(Integer)value; break;}
										case Type.INTERVAL: {expectedType=Long.class; Long interval=(Long)value; break;}
										case Type.IP_ADDRESS: {expectedType=String.class; String ip=(String)value; break;}
										case Type.LONG: {expectedType=Long.class; Long l=(Long)value; break;}
										//case SchemaType.OCTAL_INT: {expectedType=Integer.class; Integer i=(Integer)value; break;}
										case Type.OCTAL_LONG: {expectedType=Long.class; Long l=(Long)value; break;}
										//case SchemaType.PACKAGE: {expectedType=String.class; String pack=(String)value; break;}
										case Type.PATH: {expectedType=String.class; String path=(String)value; break;}
										case Type.PHONE: {expectedType=String.class; String phone=(String)value; break;}
										case Type.PKEY: {expectedType=Integer.class; Integer pkey=(Integer)value; break;}
										case Type.SHORT: {expectedType=Short.class; Short s=(Short)value; break;}
										//case SchemaType.STATE: {expectedType=String.class; String state=(String)value; break;}
										case Type.STRING: {expectedType=String.class; String s=(String)value; break;}
										case Type.TIME: {expectedType=Date.class; Date time=(Date)value; break;}
										case Type.URL: {expectedType=String.class; String url=(String)value; break;}
										case Type.USERNAME: {expectedType=String.class; String username=(String)value; break;}
										//case SchemaType.ZIP: {expectedType=String.class; String zip=(String)value; break;}
										case Type.ZONE: {expectedType=String.class; String zone=(String)value; break;}
										case Type.BIG_DECIMAL: {expectedType=BigDecimal.class; BigDecimal bigDecimal=(BigDecimal)value; break;}
										default: {
											fail("Unexpected SchemaType id: "+typeNum);
											throw new AssertionError();
										}
									}
									assert expectedType != null;
									String string=type.getString(value, -1);
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
}
