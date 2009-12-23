package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * @see  Business
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.businesses)
public interface BusinessService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServServiceStringKey<C,F,Business> {
    // <editor-fold defaultstate="collapsed" desc="TODO">
    /*
    private String rootAccounting;

    void addBusiness(
        final String accounting,
        final String contractNumber,
        final Server defaultServer,
        final String parent,
        final boolean canAddBackupServers,
    	final boolean canAddBusinesses,
        final boolean canSeePrices,
        final boolean billParent,
        final PackageDefinition packageDefinition
    ) throws IOException, SQLException {
        connector.requestUpdate(
            true,
            new AOServConnector.UpdateRequest() {
                IntList invalidateList;

                public void writeRequest(CompressedDataOutputStream out) throws IOException {
                    out.writeCompressedInt(AOServProtocol.CommandID.ADD.ordinal());
                    out.writeCompressedInt(SchemaTable.TableID.BUSINESSES.ordinal());
                    out.writeUTF(accounting);
                    out.writeBoolean(contractNumber!=null);
                    if(contractNumber!=null) out.writeUTF(contractNumber);
                    out.writeCompressedInt(defaultServer.pkey);
                    out.writeUTF(parent);
                    out.writeBoolean(canAddBackupServers);
                    out.writeBoolean(canAddBusinesses);
                    out.writeBoolean(canSeePrices);
                    out.writeBoolean(billParent);
                    out.writeCompressedInt(packageDefinition.pkey);
                }

                public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
                    int code=in.readByte();
                    if(code==AOServProtocol.DONE) invalidateList=AOServConnector.readInvalidateList(in);
                    else {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unexpected response code: "+code);
                    }
                }

                public void afterRelease() {
                    connector.tablesUpdated(invalidateList);
                }
            }
        );
    }

    @Override
    public void clearCache() {
        super.clearCache();
        synchronized(this) {
            rootAccounting=null;
        }
    }

    public String generateAccountingCode(String template) throws IOException, SQLException {
    	return connector.requestStringQuery(true, AOServProtocol.CommandID.GENERATE_ACCOUNTING_CODE, template);
    }

    List<Business> getChildBusinesses(Business business) throws IOException, SQLException {
        String accounting=business.pkey;

        List<Business> cached=getRows();
        List<Business> matches=new ArrayList<Business>();
        int size=cached.size();
        for(int c=0;c<size;c++) {
            Business bu=cached.get(c);
            if(accounting.equals(bu.parent)) matches.add(bu);
        }
        return matches;
    }

    synchronized public String getRootAccounting() throws IOException, SQLException {
        if(rootAccounting==null) rootAccounting=connector.requestStringQuery(true, AOServProtocol.CommandID.GET_ROOT_BUSINESS);
        return rootAccounting;
    }

    public Business getRootBusiness() throws IOException, SQLException {
        String accounting=getRootAccounting();
        Business bu=get(accounting);
        if(bu==null) throw new SQLException("Unable to find Business: "+accounting);
        return bu;
    }
    */
    /**
     * Gets the list of all businesses that either have a null parent (the
     * actual root of the business tree) or where the parent is inaccessible.
     */
    /*
    public List<Business> getTopLevelBusinesses() throws IOException, SQLException {
        List<Business> cached=getRows();
        List<Business> matches=new ArrayList<Business>();
        int size=cached.size();
        for(int c=0;c<size;c++) {
            Business bu=cached.get(c);
            if(bu.parent==null || getUniqueRow(Business.COLUMN_ACCOUNTING, bu.parent)==null) matches.add(bu);
        }
        return matches;
    }

    @Override
    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, SQLException, IOException {
        String command=args[0];
        if(command.equalsIgnoreCase(AOSHCommand.ADD_BUSINESS)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_BUSINESS, args, 9, err)) {
                try {
                    connector.getSimpleAOClient().addBusiness(
                        args[1],
                        args[2],
                        args[3],
                        args[4],
                        AOSH.parseBoolean(args[5], "can_add_backup_servers"),
                        AOSH.parseBoolean(args[6], "can_add_businesses"),
                        AOSH.parseBoolean(args[7], "can_see_prices"),
                        AOSH.parseBoolean(args[8], "bill_parent"),
                        AOSH.parseInt(args[9], "package_definition")
                    );
                } catch(IllegalArgumentException iae) {
                    err.print("aosh: "+AOSHCommand.ADD_BUSINESS+": ");
                    err.println(iae.getMessage());
                    err.flush();
                }
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.CANCEL_BUSINESS)) {
            if(AOSH.checkParamCount(AOSHCommand.CANCEL_BUSINESS, args, 2, err)) {
                connector.getSimpleAOClient().cancelBusiness(args[1], args[2]);
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.CHECK_ACCOUNTING)) {
            if(AOSH.checkParamCount(AOSHCommand.CHECK_ACCOUNTING, args, 1, err)) {
                try {
                    SimpleAOClient.checkAccounting(args[1]);
                    out.println("true");
                } catch(IllegalArgumentException iae) {
                    out.print("aosh: "+AOSHCommand.CHECK_ACCOUNTING+": ");
                    out.println(iae.getMessage());
                }
                out.flush();
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.DISABLE_BUSINESS)) {
            if(AOSH.checkParamCount(AOSHCommand.DISABLE_BUSINESS, args, 2, err)) {
                out.println(
                    connector.getSimpleAOClient().disableBusiness(
                        args[1],
                        args[2]
                    )
                );
                out.flush();
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.ENABLE_BUSINESS)) {
            if(AOSH.checkParamCount(AOSHCommand.ENABLE_BUSINESS, args, 1, err)) {
                connector.getSimpleAOClient().enableBusiness(args[1]);
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.GENERATE_ACCOUNTING)) {
            if(AOSH.checkParamCount(AOSHCommand.GENERATE_ACCOUNTING, args, 1, err)) {
                out.println(connector.getSimpleAOClient().generateAccountingCode(args[1]));
                out.flush();
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.GET_ROOT_BUSINESS)) {
            if(AOSH.checkParamCount(AOSHCommand.GET_ROOT_BUSINESS, args, 0, err)) {
                out.println(connector.getSimpleAOClient().getRootBusiness());
                out.flush();
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.IS_ACCOUNTING_AVAILABLE)) {
            if(AOSH.checkParamCount(AOSHCommand.IS_ACCOUNTING_AVAILABLE, args, 1, err)) {
                try {
                    out.println(connector.getSimpleAOClient().isAccountingAvailable(args[1]));
                    out.flush();
                } catch(IllegalArgumentException iae) {
                    err.print("aosh: "+AOSHCommand.IS_ACCOUNTING_AVAILABLE+": ");
                    err.println(iae.getMessage());
                    err.flush();
                }
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.MOVE_BUSINESS)) {
            if(AOSH.checkParamCount(AOSHCommand.MOVE_BUSINESS, args, 3, err)) {
                connector.getSimpleAOClient().moveBusiness(
                    args[1],
                    args[2],
                    args[3],
                    isInteractive?out:null
                );
                out.flush();
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.SET_BUSINESS_ACCOUNTING)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_BUSINESS_ACCOUNTING, args, 2, err)) {
                connector.getSimpleAOClient().setBusinessAccounting(args[1], args[2]);
            }
            return true;
        } else return false;
    }

    public boolean isAccountingAvailable(String accounting) throws SQLException, IOException {
        if(!Business.isValidAccounting(accounting)) throw new SQLException("Invalid accounting code: "+accounting);
        return connector.requestBooleanQuery(true, AOServProtocol.CommandID.IS_ACCOUNTING_AVAILABLE, accounting);
    }

    List<Business> getBusinesses(PackageDefinition pd) throws IOException, SQLException {
        return getIndexedRows(Business.COLUMN_PACKAGE_DEFINITION, pd.pkey);
    }

    // <editor-fold defaultstate="collapsed" desc="Tree compatibility">
    private final Tree<Business> tree = new Tree<Business>() {
        public List<Node<Business>> getRootNodes() throws IOException, SQLException {
            List<Business> topLevelBusinesses = getTopLevelBusinesses();
            int size = topLevelBusinesses.size();
            if(size==0) {
                return Collections.emptyList();
            } else if(size==1) {
                Node<Business> singleNode = new BusinessTreeNode(topLevelBusinesses.get(0));
                return Collections.singletonList(singleNode);
            } else {
                List<Node<Business>> rootNodes = new ArrayList<Node<Business>>(size);
                for(Business topLevelBusiness : topLevelBusinesses) rootNodes.add(new BusinessTreeNode(topLevelBusiness));
                return Collections.unmodifiableList(rootNodes);
            }
        }
    };

    static class BusinessTreeNode implements Node<Business> {

        private final Business business;

        BusinessTreeNode(Business business) {
            this.business = business;
        }

        public List<Node<Business>> getChildren() throws IOException, SQLException {
            // Look for any existing children
            List<Business> children = business.getChildBusinesses();
            int size = children.size();
            if(size==0) {
                if(business.canAddBusinesses()) {
                    // Can have children but empty
                    return Collections.emptyList();
                } else {
                    // Not allowed to have children
                    return null;
                }
            } else if(size==1) {
                Node<Business> singleNode = new BusinessTreeNode(children.get(0));
                return Collections.singletonList(singleNode);
            } else {
                List<Node<Business>> childNodes = new ArrayList<Node<Business>>(size);
                for(Business child : children) childNodes.add(new BusinessTreeNode(child));
                return Collections.unmodifiableList(childNodes);
            }
        }

        public Business getValue() {
            return business;
        }
    }
    */
    /**
     * Gets a Tree view of all the accessible businesses.
     * All access to the tree read-through to the underlying storage
     * and are thus subject to change at any time.  If you need a consistent
     * snapshot of the tree, consider using TreeCopy.
     *
     * @see  TreeCopy
     */
    /* TODO
    public Tree<Business> getTree() {
        return tree;
    }
    // </editor-fold>
     */
    // </editor-fold>
}
