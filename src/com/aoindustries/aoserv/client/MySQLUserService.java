/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * @see  MySQLUser
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.mysql_users)
public interface MySQLUserService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServService<C,F,Integer,MySQLUser> {

    /* TODO
    int addMySQLUser(final String username, final MySQLServer mysqlServer, final String host) throws IOException, SQLException {
        return connector.requestResult(
            true,
            new AOServConnector.ResultRequest<Integer>() {
                int pkey;
                IntList invalidateList;

                public void writeRequest(CompressedDataOutputStream out) throws IOException {
                    out.writeCompressedInt(AOServProtocol.CommandID.ADD.ordinal());
                    out.writeCompressedInt(SchemaTable.TableID.MYSQL_USERS.ordinal());
                    out.writeUTF(username);
                    out.writeCompressedInt(mysqlServer.pkey);
                    out.writeBoolean(host!=null); if(host!=null) out.writeUTF(host);
                }

                public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
                    int code=in.readByte();
                    if(code==AOServProtocol.DONE) {
                        pkey=in.readCompressedInt();
                        invalidateList=AOServConnector.readInvalidateList(in);
                    } else {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unexpected response code: "+code);
                    }
                }

                public Integer afterRelease() {
                    connector.tablesUpdated(invalidateList);
                    return pkey;
                }
            }
        );
    }

    MySQLUser getMySQLUser(String username, MySQLServer ms) throws IOException, SQLException {
        int msPKey=ms.pkey;
        // Use index first
        List<MySQLUser> table=getIndexedRows(MySQLUser.COLUMN_USERNAME, username);
        int size=table.size();
    	for(int c=0;c<size;c++) {
            MySQLUser mu=table.get(c);
            if(mu.mysql_server==msPKey) return mu;
    	}
    	return null;
    }

    List<MySQLUser> getMySQLUsers(Business business) throws IOException, SQLException {
        String accounting = business.pkey;
        List<MySQLUser> cached=getRows();
        int size=cached.size();
        List<MySQLUser> matches=new ArrayList<MySQLUser>(size);
        for(int c=0;c<size;c++) {
            MySQLUser mu=cached.get(c);
            if(mu.getUsername().accounting.equals(accounting)) matches.add(mu);
        }
        return matches;
    }

    List<MySQLUser> getMySQLUsers(MySQLServer ms) throws IOException, SQLException {
        return getIndexedRows(MySQLUser.COLUMN_MYSQL_SERVER, ms.pkey);
    }

    @Override
    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
        String command=args[0];
    	if(command.equalsIgnoreCase(AOSHCommand.ADD_MYSQL_USER)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_MYSQL_USER, args, 4, err)) {
                int pkey=connector.getSimpleAOClient().addMySQLUser(
                    args[1],
                    args[2],
                    args[3],
                    args[4]
                );
                out.println(pkey);
                out.flush();
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.CHECK_MYSQL_PASSWORD)) {
            if(AOSH.checkParamCount(AOSHCommand.CHECK_MYSQL_PASSWORD, args, 2, err)) {
                PasswordChecker.Result[] results=SimpleAOClient.checkMySQLPassword(args[1], args[2]);
                if(PasswordChecker.hasResults(Locale.getDefault(), results)) {
                    PasswordChecker.printResults(results, out);
                    out.flush();
                }
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.CHECK_MYSQL_USERNAME)) {
            if(AOSH.checkParamCount(AOSHCommand.CHECK_MYSQL_USERNAME, args, 1, err)) {
                SimpleAOClient.checkMySQLUsername(args[1]);
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.DISABLE_MYSQL_USER)) {
            if(AOSH.checkParamCount(AOSHCommand.DISABLE_MYSQL_USER, args, 4, err)) {
                out.println(
                    connector.getSimpleAOClient().disableMySQLUser(
                        args[1],
                        args[2],
                        args[3],
                        args[4]
                    )
                );
                out.flush();
            }
            return true;
    	} else if(command.equalsIgnoreCase(AOSHCommand.ENABLE_MYSQL_USER)) {
            if(AOSH.checkParamCount(AOSHCommand.ENABLE_MYSQL_USER, args, 3, err)) {
                connector.getSimpleAOClient().enableMySQLUser(
                    args[1],
                    args[2],
                    args[3]
                );
            }
            return true;
    	} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_MYSQL_USER)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_MYSQL_USER, args, 3, err)) {
                connector.getSimpleAOClient().removeMySQLUser(
                    args[1],
                    args[2],
                    args[3]
                );
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.IS_MYSQL_USER_PASSWORD_SET)) {
                if(AOSH.checkParamCount(AOSHCommand.IS_MYSQL_USER_PASSWORD_SET, args, 3, err)) {
                    out.println(
                        connector.getSimpleAOClient().isMySQLUserPasswordSet(
                            args[1],
                            args[2],
                            args[3]
                        )
                    );
                    out.flush();
                }
                return true;
    	} else if(command.equalsIgnoreCase(AOSHCommand.SET_MYSQL_USER_PASSWORD)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_MYSQL_USER_PASSWORD, args, 4, err)) {
                connector.getSimpleAOClient().setMySQLUserPassword(
                    args[1],
                    args[2],
                    args[3],
                    args[4]
                );
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.WAIT_FOR_MYSQL_USER_REBUILD)) {
            if(AOSH.checkParamCount(AOSHCommand.WAIT_FOR_MYSQL_USER_REBUILD, args, 1, err)) {
                connector.getSimpleAOClient().waitForMySQLUserRebuild(args[1]);
            }
            return true;
        }
        return false;
    }

    void waitForRebuild(AOServer aoServer) throws IOException, SQLException {
        connector.requestUpdate(
            true,
            AOServProtocol.CommandID.WAIT_FOR_REBUILD,
            SchemaTable.TableID.MYSQL_USERS,
            aoServer.pkey
        );
    }
    */
}