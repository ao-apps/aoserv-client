/*
 * Copyright 2005-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * @see  PackageDefinition
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.package_definitions)
public interface PackageDefinitionService extends AOServService<Integer,PackageDefinition> {

    /* TODO
    int addPackageDefinition(
        final Business business,
        final PackageCategory category,
        final String name,
        final String version,
        final String display,
        final String description,
        final int setupFee,
        final TransactionType setupFeeTransactionType,
        final int monthlyRate,
        final TransactionType monthlyRateTransactionType
    ) throws IOException, SQLException {
        return connector.requestResult(
            true,
            new AOServConnector.ResultRequest<Integer>() {
                int pkey;
                IntList invalidateList;

                public void writeRequest(CompressedDataOutputStream out) throws IOException {
                    out.writeCompressedInt(AOServProtocol.CommandID.ADD.ordinal());
                    out.writeCompressedInt(SchemaTable.TableID.PACKAGE_DEFINITIONS.ordinal());
                    out.writeUTF(business.pkey);
                    out.writeUTF(category.pkey);
                    out.writeUTF(name);
                    out.writeUTF(version);
                    out.writeUTF(display);
                    out.writeUTF(description);
                    out.writeCompressedInt(setupFee);
                    out.writeBoolean(setupFeeTransactionType!=null);
                    if(setupFeeTransactionType!=null) out.writeUTF(setupFeeTransactionType.pkey);
                    out.writeCompressedInt(monthlyRate);
                    out.writeUTF(monthlyRateTransactionType.pkey);
                }

                public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
                    int code=in.readByte();
                    if(code==AOServProtocol.DONE) {
                        pkey=in.readCompressedInt();
                        invalidateList=AOServConnector.readInvalidateList(in);
                    } else {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unknown response code: "+code);
                    }
                }

                public Integer afterRelease() {
                    connector.tablesUpdated(invalidateList);
                    return pkey;
                }
            }
        );
    }
    */

    /* TODO
    PackageDefinition getPackageDefinition(Business business, PackageCategory category, String name, String version) throws IOException, SQLException {
        String accounting=business.pkey;
        String categoryName=category.pkey;
        List<PackageDefinition> pds=getRows();
        int size=pds.size();
        for(int c=0;c<size;c++) {
            PackageDefinition pd=pds.get(c);
            if(
                pd.accounting==accounting // OK - interned
                && pd.category==categoryName // OK - interned
                && pd.name.equals(name)
                && pd.version.equals(version)
            ) return pd;
        }
        return null;
    }

    List<PackageDefinition> getPackageDefinitions(Business business, PackageCategory category) throws IOException, SQLException {
        String accounting=business.pkey;
        String categoryName=category.pkey;

        List<PackageDefinition> cached = getRows();
        List<PackageDefinition> matches = new ArrayList<PackageDefinition>(cached.size());
        int size=cached.size();
        for(int c=0;c<size;c++) {
            PackageDefinition pd=cached.get(c);
            if(
                pd.accounting==accounting // OK - interned
                && pd.category==categoryName // OK - interned
            ) matches.add(pd);
        }
        return matches;
    }
     */
}
