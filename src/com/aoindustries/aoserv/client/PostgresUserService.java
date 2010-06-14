/*
 * Copyright 2001-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * @see  PostgresUser
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.postgres_users)
public interface PostgresUserService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServService<C,F,Integer,PostgresUser> {

    /* TODO
    void addPostgresUser(String username) throws IOException, SQLException {
        connector.requestUpdateIL(
            true,
            AOServProtocol.CommandID.ADD,
            SchemaTable.TableID.POSTGRES_USERS,
            username
        );
    }

    List<PostgresUser> getPostgresUsers(Business business) throws SQLException, IOException {
        String accounting = business.pkey;

        List<PostgresUser> cached=getRows();
        int size=cached.size();
        List<PostgresUser> matches=new ArrayList<PostgresUser>(size);
        for(int c=0;c<size;c++) {
            PostgresUser psu=cached.get(c);
            if(psu.getUsername().accounting.equals(accounting)) matches.add(psu);
        }
        return matches;
    }

    @Override
    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IOException, IllegalArgumentException, SQLException {
        String command=args[0];
        if(command.equalsIgnoreCase(AOSHCommand.ADD_POSTGRES_USER)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_POSTGRES_USER, args, 1, err)) {
                connector.getSimpleAOClient().addPostgresUser(
                    args[1]
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.ARE_POSTGRES_USER_PASSWORDS_SET)) {
            if(AOSH.checkParamCount(AOSHCommand.ARE_POSTGRES_USER_PASSWORDS_SET, args, 1, err)) {
                int result=connector.getSimpleAOClient().arePostgresUserPasswordsSet(args[1]);
                if(result==PasswordProtected.NONE) out.println("none");
                else if(result==PasswordProtected.SOME) out.println("some");
                else if(result==PasswordProtected.ALL) out.println("all");
                else throw new RuntimeException("Unexpected value for result: "+result);
                out.flush();
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.CHECK_POSTGRES_PASSWORD)) {
            if(AOSH.checkParamCount(AOSHCommand.CHECK_POSTGRES_PASSWORD, args, 2, err)) {
                PasswordChecker.Result[] results = SimpleAOClient.checkPostgresPassword(args[1], args[2]);
                if(PasswordChecker.hasResults(results)) {
                    PasswordChecker.printResults(results, out);
                    out.flush();
                }
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.CHECK_POSTGRES_USERNAME)) {
            if(AOSH.checkParamCount(AOSHCommand.CHECK_POSTGRES_USERNAME, args, 1, err)) {
                SimpleAOClient.checkPostgresUsername(args[1]);
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.DISABLE_POSTGRES_USER)) {
            if(AOSH.checkParamCount(AOSHCommand.DISABLE_POSTGRES_USER, args, 2, err)) {
                out.println(
                    connector.getSimpleAOClient().disablePostgresUser(
                        args[1],
                        args[2]
                    )
                );
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.ENABLE_POSTGRES_USER)) {
            if(AOSH.checkParamCount(AOSHCommand.ENABLE_POSTGRES_USER, args, 1, err)) {
                connector.getSimpleAOClient().enablePostgresUser(args[1]);
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_POSTGRES_USER)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_POSTGRES_USER, args, 1, err)) {
                connector.getSimpleAOClient().removePostgresUser(
                    args[1]
                );
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.SET_POSTGRES_USER_PASSWORD)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_POSTGRES_USER_PASSWORD, args, 2, err)) {
                connector.getSimpleAOClient().setPostgresUserPassword(
                    args[1],
                    args[2]
                );
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.WAIT_FOR_POSTGRES_USER_REBUILD)) {
            if(AOSH.checkParamCount(AOSHCommand.WAIT_FOR_POSTGRES_USER_REBUILD, args, 1, err)) {
                connector.getSimpleAOClient().waitForPostgresUserRebuild(args[1]);
            }
            return true;
        }
        return false;
    }

    void waitForRebuild(AOServer aoServer) throws IOException, SQLException {
        connector.requestUpdate(
            true,
            AOServProtocol.CommandID.WAIT_FOR_REBUILD,
            SchemaTable.TableID.POSTGRES_USERS,
            aoServer.pkey
        );
    }

    int addPostgresServerUser(String username, PostgresServer postgresServer) throws IOException, SQLException {
    	int pkey=connector.requestIntQueryIL(
            true,
            AOServProtocol.CommandID.ADD,
            SchemaTable.TableID.POSTGRES_SERVER_USERS,
            username,
            postgresServer.pkey
    	);
    	return pkey;
    }

    PostgresServerUser getPostgresServerUser(String username, PostgresServer postgresServer) throws IOException, SQLException {
        return getPostgresServerUser(username, postgresServer.pkey);
    }

    PostgresServerUser getPostgresServerUser(String username, int postgresServer) throws IOException, SQLException {
	List<PostgresServerUser> table=getRows();
	int size=table.size();
	for(int c=0;c<size;c++) {
            PostgresServerUser psu=table.get(c);
            if(
                psu.username.equals(username)
                && psu.postgres_server==postgresServer
            ) return psu;
	}
	return null;
    }

    @Override
    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.ADD_POSTGRES_SERVER_USER)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_POSTGRES_SERVER_USER, args, 3, err)) {
                out.println(
                    connector.getSimpleAOClient().addPostgresServerUser(
                        args[1],
                        args[2],
                        args[3]
                    )
                );
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.DISABLE_POSTGRES_SERVER_USER)) {
            if(AOSH.checkParamCount(AOSHCommand.DISABLE_POSTGRES_SERVER_USER, args, 4, err)) {
                out.println(
                    connector.getSimpleAOClient().disablePostgresServerUser(
                        args[1],
                        args[2],
                        args[3],
                        args[4]
                    )
                );
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.ENABLE_POSTGRES_SERVER_USER)) {
            if(AOSH.checkParamCount(AOSHCommand.ENABLE_POSTGRES_SERVER_USER, args, 3, err)) {
                connector.getSimpleAOClient().enablePostgresServerUser(args[1], args[2], args[3]);
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.IS_POSTGRES_SERVER_USER_PASSWORD_SET)) {
            if(AOSH.checkParamCount(AOSHCommand.IS_POSTGRES_SERVER_USER_PASSWORD_SET, args, 2, err)) {
                out.println(
                    connector.getSimpleAOClient().isPostgresServerUserPasswordSet(
                        args[1],
                        args[2],
                        args[3]
                    )
                );
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_POSTGRES_SERVER_USER)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_POSTGRES_SERVER_USER, args, 3, err)) {
                connector.getSimpleAOClient().removePostgresServerUser(
                    args[1],
                    args[2],
                    args[3]
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.SET_POSTGRES_SERVER_USER_PASSWORD)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_POSTGRES_SERVER_USER_PASSWORD, args, 4, err)) {
                connector.getSimpleAOClient().setPostgresServerUserPassword(
                    args[1],
                    args[2],
                    args[3],
                    args[4]
                );
            }
            return true;
	}
	return false;
    }
     */
}