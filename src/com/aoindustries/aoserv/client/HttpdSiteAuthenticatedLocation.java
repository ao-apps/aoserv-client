package com.aoindustries.aoserv.client;

/*
 * Copyright 2006-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * @see  HttpdSite
 *
 * @version  1.18
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdSiteAuthenticatedLocation extends CachedObjectIntegerKey<HttpdSiteAuthenticatedLocation> implements Removable {

    static final int
        COLUMN_PKEY=0,
        COLUMN_HTTPD_SITE=1
    ;
    static final String COLUMN_HTTPD_SITE_name = "httpd_site";

    private static String validateNonQuoteAscii(String s, String label) {
        // Is only comprised of space through ~ (ASCII), not including "
        for(int c=0;c<s.length();c++) {
            char ch=s.charAt(c);
            if(ch<' ' || ch>'~' || ch=='"') return "Invalid character in "+label+": "+ch;
        }
        return null;
    }
    public static String validatePath(String path) {
        if(path.length()==0) return "Location required";
        return validateNonQuoteAscii(path, "Location");
    }

    public static String validateAuthName(String authName) {
        return validateNonQuoteAscii(authName, "AuthName");
    }
    
    public static String validateAuthGroupFile(String authGroupFile) {
        // May be empty
        if(authGroupFile.length()==0) return null;
        // Must start with /
        if(authGroupFile.charAt(0)!='/') return "AuthGroupFile must start with /";
        // Doesn't have .. in it
        if(authGroupFile.indexOf("..")!=-1) return "AuthGroupFile may not contain ..";
        return validateNonQuoteAscii(authGroupFile, "AuthGroupFile");
    }

    public static String validateAuthUserFile(String authUserFile) {
        // May be empty
        if(authUserFile.length()==0) return null;
        // Must start with /
        if(authUserFile.charAt(0)!='/') return "AuthUserFile must start with /";
        // Doesn't have .. in it
        if(authUserFile.indexOf("..")!=-1) return "AuthUserFile may not contain ..";
        return validateNonQuoteAscii(authUserFile, "AuthUserFile");
    }

    public static String validateRequire(String require) {
        return validateNonQuoteAscii(require, "Require");
    }

    private int httpd_site;
    private String path;
    private boolean is_regular_expression;
    private String auth_name;
    private String auth_group_file;
    private String auth_user_file;
    private String require;

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        return Collections.emptyList();
    }

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_HTTPD_SITE: return Integer.valueOf(httpd_site);
            case 2: return path;
            case 3: return is_regular_expression;
            case 4: return auth_name;
            case 5: return auth_group_file;
            case 6: return auth_user_file;
            case 7: return require;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public HttpdSite getHttpdSite() throws SQLException, IOException {
	HttpdSite obj=table.connector.httpdSites.get(httpd_site);
	if(obj==null) throw new SQLException("Unable to find HttpdSite: "+httpd_site);
	return obj;
    }

    public String getPath() {
        return path;
    }
    
    public boolean getIsRegularExpression() {
        return is_regular_expression;
    }

    public String getAuthName() {
        return auth_name;
    }

    public String getAuthGroupFile() {
        return auth_group_file;
    }
    
    public String getAuthUserFile() {
        return auth_user_file;
    }
    
    public String getRequire() {
        return require;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.HTTPD_SITE_AUTHENTICATED_LOCATIONS;
    }

    public void init(ResultSet result) throws SQLException {
	pkey=result.getInt(1);
	httpd_site=result.getInt(2);
        path=result.getString(3);
        is_regular_expression=result.getBoolean(4);
        auth_name=result.getString(5);
        auth_group_file=result.getString(6);
        auth_user_file=result.getString(7);
        require=result.getString(8);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readCompressedInt();
	httpd_site=in.readCompressedInt();
        path=in.readCompressedUTF();
        is_regular_expression=in.readBoolean();
        auth_name=in.readCompressedUTF();
        auth_group_file=in.readCompressedUTF();
        auth_user_file=in.readCompressedUTF();
        require=in.readCompressedUTF().intern();
    }

    public void remove() throws IOException, SQLException {
        table.connector.requestUpdateIL(AOServProtocol.CommandID.REMOVE, SchemaTable.TableID.HTTPD_SITE_AUTHENTICATED_LOCATIONS, pkey);
    }

    public void setAttributes(
        String path,
        boolean isRegularExpression,
        String authName,
        String authGroupFile,
        String authUserFile,
        String require
    ) throws IOException, SQLException {
        table.connector.requestUpdateIL(
            AOServProtocol.CommandID.SET_HTTPD_SITE_AUTHENTICATED_LOCATION_ATTRIBUTES,
            pkey,
            path,
            isRegularExpression,
            authName,
            authGroupFile,
            authUserFile,
            require
        );
    }

    @Override
    String toStringImpl() throws SQLException, IOException {
        HttpdSite site=getHttpdSite();
        return site.toString()+':'+path;
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
	out.writeCompressedInt(pkey);
	out.writeCompressedInt(httpd_site);
        out.writeCompressedUTF(path, 0);
        out.writeBoolean(is_regular_expression);
        out.writeCompressedUTF(auth_name, 1);
        out.writeCompressedUTF(auth_group_file, 2);
        out.writeCompressedUTF(auth_user_file, 3);
        out.writeCompressedUTF(require, 4);
    }
}