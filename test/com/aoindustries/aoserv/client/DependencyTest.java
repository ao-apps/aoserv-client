/*
 * Copyright 2009-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.ValidationException;
import com.aoindustries.security.LoginException;
import com.aoindustries.util.graph.SymmetricAcyclicGraphChecker;
import com.aoindustries.util.graph.TopologicalSorter;
import java.io.IOException;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;
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

    private static final int NUM_TESTS = 5;

    private List<AOServConnector> conns;

    public DependencyTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws IOException, LoginException, ValidationException {
        conns = AOServConnectorTest.getTestConnectors();
    }

    @Override
    protected void tearDown() {
        conns = null;
    }

    public static Test suite() {
        return new TestSuite(DependencyTest.class);
    }

    /**
     * Test the getDependencies for cycles and makes sure that all edges are mirrored in the backward direction.
     *
     * Cycle algorithm adapted from:
     *     http://www.personal.kent.edu/~rmuhamma/Algorithms/MyAlgorithms/GraphAlgor/depthSearch.htm
     *     http://www.eecs.berkeley.edu/~kamil/teaching/sp03/041403.pdf
     */
    private void doTestGetDependencies(boolean isForward) throws RemoteException {
        for(final AOServConnector conn : conns) {
            System.out.println("    "+conn.getConnectAs()+":");
            //System.out.print("        ");
            //for(ServiceName serviceName : ServiceName.values) System.out.print(serviceName.name().charAt(0));
            //System.out.println();
            //System.out.print("        ");
            /*
            List<AOServObject<?>> allObjects = new ArrayList<AOServObject<?>>();
            for(ServiceName serviceName : ServiceName.values) {
                AOServService<?,?> service = conn.getServices().get(serviceName);
                Set<? extends AOServObject<?>> set = service.getSet();
                int numRows = set.size();
                if(numRows==0) System.out.print("E");
                else {
                    allObjects.addAll(set);
                    System.out.print('.');
                }
            }*/
            new SymmetricAcyclicGraphChecker<AOServObject<?>,RemoteException>(conn.getDependencyGraph(), isForward).checkGraph();
            //System.out.println();
        }
    }

    private void doTestGetDependencies() throws RemoteException {
        long startTime = System.currentTimeMillis();
        doTestGetDependencies(true);
        long endTime = System.currentTimeMillis() - startTime;
        System.out.println("    Finished in "+BigDecimal.valueOf(endTime, 3)+" sec");
    }

    public void testGetDependencies() throws RemoteException {
        System.out.println("Testing getDependencies:");
        for(int c=0; c<NUM_TESTS; c++) {
            doTestGetDependencies();
        }
    }

    private void doTestGetDependentObjects() throws RemoteException {
        long startTime = System.currentTimeMillis();
        doTestGetDependencies(false);
        long endTime = System.currentTimeMillis() - startTime;
        System.out.println("    Finished in "+BigDecimal.valueOf(endTime, 3)+" sec");
    }

    public void testGetDependentObjects() throws RemoteException {
        System.out.println("Testing getDependentObjects:");
        for(int c=0; c<NUM_TESTS; c++) {
            doTestGetDependentObjects();
        }
    }

    private void doTestTopologicalSort(boolean isForward) throws RemoteException {
        for(final AOServConnector conn : conns) {
            System.out.println("    "+conn.getConnectAs()+":");
            //System.out.print("        ");
            //for(ServiceName serviceName : ServiceName.values) System.out.print(serviceName.name().charAt(0));
            //System.out.println();
            //System.out.print("        ");
            /*
            List<AOServObject<?>> allObjects = new ArrayList<AOServObject<?>>();
            for(ServiceName serviceName : ServiceName.values) {
                AOServService<?,?> service = conn.getServices().get(serviceName);
                Set<? extends AOServObject<?>> set = service.getSet();
                int numRows = set.size();
                if(numRows==0) System.out.print("E");
                else {
                    allObjects.addAll(set);
                    System.out.print('.');
                }
            }*/
            List<? extends AOServObject<?>> topological = new TopologicalSorter<AOServObject<?>,RemoteException>(conn.getDependencyGraph(), isForward).sortGraph();
            //System.out.println();
        }
    }

    public void testTopologicalSortForward() throws RemoteException {
        System.out.println("testTopologicalSortForward:");
        for(int c=0; c<NUM_TESTS; c++) {
            long startTime = System.currentTimeMillis();
            doTestTopologicalSort(true);
            long endTime = System.currentTimeMillis() - startTime;
            System.out.println("    Finished in "+BigDecimal.valueOf(endTime, 3)+" sec");
        }
    }

    public void testTopologicalSortBackward() throws RemoteException {
        System.out.println("testTopologicalSortBackward:");
        for(int c=0; c<NUM_TESTS; c++) {
            long startTime = System.currentTimeMillis();
            doTestTopologicalSort(false);
            long endTime = System.currentTimeMillis() - startTime;
            System.out.println("    Finished in "+BigDecimal.valueOf(endTime, 3)+" sec");
        }
    }

    private void doTestDependencyGraphIterateVerticesTime() throws RemoteException {
        for(final AOServConnector conn : conns) {
            System.out.print("    "+conn.getConnectAs()+": ");
            Set<AOServObject<?>> vertices = conn.getDependencyGraph().getVertices();
            int count = 0;
            for(AOServObject<?> vertex : vertices) count++;
            System.out.println(count);
        }
    }

    public void testDependencyGraphIterateVerticesTime() throws RemoteException {
        System.out.println("testDependencyGraphIterateVerticesTime:");
        for(int c=0; c<NUM_TESTS; c++) {
            long startTime = System.currentTimeMillis();
            doTestDependencyGraphIterateVerticesTime();
            long endTime = System.currentTimeMillis() - startTime;
            System.out.println("    Finished in "+BigDecimal.valueOf(endTime, 3)+" sec");
        }
    }
}
