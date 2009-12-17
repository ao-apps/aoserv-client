package com.aoindustries.aoserv.client;
/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.util.Sequence;
import com.aoindustries.util.UnsynchronizedSequence;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests the aoserv-client objects for correct bi-directional dependencies as well as no cycles in the
 * directed acyclic graph.
 *
 * @author  AO Industries, Inc.
 */
public class DependencyTest extends TestCase {
    
    private List<AOServConnector> conns;

    public DependencyTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        conns = AOServConnectorTest.getTestConnectors();
    }

    @Override
    protected void tearDown() throws Exception {
        conns = null;
    }

    public static Test suite() {
        return new TestSuite(DependencyTest.class);
    }

    abstract class ChildGetter {
        abstract List<? extends AOServObject> getChildren(AOServObject obj) throws IOException, SQLException;
    }

    class DependenciesGetter extends ChildGetter {
        List<? extends AOServObject> getChildren(AOServObject obj) throws IOException, SQLException {
            return obj.getDependencies();
        }
    }

    class DependentObjectsGetter extends ChildGetter {
        List<? extends AOServObject> getChildren(AOServObject obj) throws IOException, SQLException {
            return obj.getDependentObjects();
        }
    }

    private enum Color {WHITE, GRAY, BLACK};

    private void doTestGetDependenciesDfsVisit(Map<AOServObject,Color> colors, Map<AOServObject,AOServObject> predecessors, Sequence time, ChildGetter childGetter, ChildGetter backGetter, AOServObject v) throws IOException, SQLException {
        colors.put(v, Color.GRAY);
        for(AOServObject u : childGetter.getChildren(v)) {
            Color uMark = colors.get(u);
            if(Color.GRAY==uMark /*&& child.equals(predecessors.get(obj))*/) {
                StringBuilder SB = new StringBuilder();
                SB.append("Cycle exists:\n");
                SB.append("    ").append(u.getClass().getName()).append("(\"").append(u.toString()).append("\")\n");
                AOServObject pred = v;
                while(pred!=null) {
                    SB.append("    ").append(pred.getClass().getName()).append("(\"").append(pred.toString()).append("\")\n");
                    pred = pred.equals(u) ? null : predecessors.get(pred);
                }
                fail(SB.toString());
                throw new AssertionError("Should have already failed");
            }
            if(uMark==null) {
                predecessors.put(u, v);
                doTestGetDependenciesDfsVisit(colors, predecessors, time, childGetter, backGetter, u);
            }
        }
        predecessors.remove(v);
        colors.put(v, Color.BLACK);
    }

    /**
     * Test the getDependencies for cycles and makes sure that all edges are mirrored in the backward direction.
     *
     * Cycle algorithm adapted from:
     *     http://www.personal.kent.edu/~rmuhamma/Algorithms/MyAlgorithms/GraphAlgor/depthSearch.htm
     *     http://www.eecs.berkeley.edu/~kamil/teaching/sp03/041403.pdf
     */
    private void doTestGetDependencies(ChildGetter childGetter, ChildGetter backGetter) throws Exception {
        for(AOServConnector conn : conns) {
            String username = conn.getThisBusinessAdministrator().pkey;
            System.out.println("    "+username+":");
            System.out.print("        ");
            for(SchemaTable.TableID tableId : SchemaTable.TableID.values()) System.out.print(tableId.name().charAt(0));
            System.out.println();
            System.out.print("        ");
            Map<AOServObject,Color> colors = new HashMap<AOServObject,Color>();
            Map<AOServObject,AOServObject> predecessors = new HashMap<AOServObject,AOServObject>();
            Sequence time = new UnsynchronizedSequence();
            for(SchemaTable.TableID tableId : SchemaTable.TableID.values()) {
                AOServTable<?,? extends AOServObject> table=conn.getTable(tableId.ordinal());
                List<? extends AOServObject<?,? extends AOServObject>> rows=table.getRows();
                int numRows = rows.size();
                if(numRows==0) System.out.print("E");
                else {
                    for(int c=0;c<numRows;c++) {
                        AOServObject v = rows.get(c);
                        if(!colors.containsKey(v)) doTestGetDependenciesDfsVisit(colors, predecessors, time, childGetter, backGetter, v);
                    }
                    System.out.print('.');
                }
            }
            System.out.println();
        }
    }


    public void testGetDependencies() throws Exception {
        System.out.println("Testing getDependencies:");
        doTestGetDependencies(new DependenciesGetter(), new DependentObjectsGetter());
    }

    public void testGetDependentObjects() throws Exception {
        System.out.println("Testing getDependentObjects:");
        doTestGetDependencies(new DependentObjectsGetter(), new DependenciesGetter());
    }
}
