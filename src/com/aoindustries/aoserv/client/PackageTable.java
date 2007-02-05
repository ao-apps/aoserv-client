package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.WrappedException;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  Package
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class PackageTable extends CachedTableIntegerKey<Package> {

    PackageTable(AOServConnector connector) {
	super(connector, Package.class);
    }

    int addPackage(
	String name,
	Business business,
        PackageDefinition packageDefinition
    ) {
	return connector.requestIntQueryIL(
            AOServProtocol.ADD,
            SchemaTable.PACKAGES,
            name,
            business.pkey,
            packageDefinition.pkey
	);
    }

    public Package get(Object pkey) {
        if(pkey instanceof Integer) return get(((Integer)pkey).intValue());
        if(pkey instanceof String) return get((String)pkey);
        throw new IllegalArgumentException("pkey must be either an Integer or a String");
    }

    public Package get(int pkey) {
	return getUniqueRow(Package.COLUMN_PKEY, pkey);
    }

    public Package get(String packageName) {
	return getUniqueRow(Package.COLUMN_NAME, packageName);
    }

    public String generatePackageName(String template) {
	return connector.requestStringQuery(AOServProtocol.GENERATE_PACKAGE_NAME, template);
    }

    List<Package> getPackages(Business business) {
        return getIndexedRows(Package.COLUMN_ACCOUNTING, business.pkey);
    }

    List<Package> getPackages(PackageDefinition pd) {
        return getIndexedRows(Package.COLUMN_PACKAGE_DEFINITION, pd.pkey);
    }

    int getTableID() {
	return SchemaTable.PACKAGES;
    }

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.ADD_PACKAGE)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_PACKAGE, args, 3, err)) {
                try {
                    out.println(
                        connector.simpleAOClient.addPackage(
                            args[1],
                            args[2],
                            AOSH.parseInt(args[3], "package_definition")
                        )
                    );
                    out.flush();
                } catch(IllegalArgumentException iae) {
                    err.print("aosh: "+AOSHCommand.ADD_PACKAGE+": ");
                    err.println(iae.getMessage());
                    err.flush();
                }
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.CHECK_PACKAGE_NAME)) {
            if(AOSH.checkParamCount(AOSHCommand.CHECK_PACKAGE_NAME, args, 1, err)) {
                try {
                    SimpleAOClient.checkPackageName(args[1]);
                    out.println("true");
                } catch(IllegalArgumentException iae) {
                    out.print("aosh: "+AOSHCommand.CHECK_PACKAGE_NAME+": ");
                    out.println(iae.getMessage());
                }
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.DISABLE_PACKAGE)) {
            if(AOSH.checkParamCount(AOSHCommand.DISABLE_PACKAGE, args, 2, err)) {
                out.println(
                    connector.simpleAOClient.disablePackage(
                        args[1],
                        args[2]
                    )
                );
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.ENABLE_PACKAGE)) {
            if(AOSH.checkParamCount(AOSHCommand.ENABLE_PACKAGE, args, 1, err)) {
                connector.simpleAOClient.enablePackage(args[1]);
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.GENERATE_PACKAGE_NAME)) {
            if(AOSH.checkParamCount(AOSHCommand.GENERATE_PACKAGE_NAME, args, 1, err)) {
                out.println(connector.simpleAOClient.generatePackageName(args[1]));
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.IS_PACKAGE_NAME_AVAILABLE)) {
            if(AOSH.checkParamCount(AOSHCommand.IS_PACKAGE_NAME_AVAILABLE, args, 1, err)) {
                try {
                    out.println(connector.simpleAOClient.isPackageNameAvailable(args[1]));
                    out.flush();
                } catch(IllegalArgumentException iae) {
                    err.print("aosh: "+AOSHCommand.IS_PACKAGE_NAME_AVAILABLE+": ");
                    err.println(iae.getMessage());
                    err.flush();
                }
            }
            return true;
	} else return false;
    }

    public boolean isPackageNameAvailable(String packageName) {
	if(!Package.isValidPackageName(packageName)) throw new WrappedException(new SQLException("Invalid package name: "+packageName));
	return connector.requestBooleanQuery(AOServProtocol.IS_PACKAGE_NAME_AVAILABLE, packageName);
    }
}