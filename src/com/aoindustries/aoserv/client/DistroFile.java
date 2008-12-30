package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.md5.MD5;
import com.aoindustries.util.WrappedException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * For AO Industries use only.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class DistroFile extends FilesystemCachedObject<Integer,DistroFile> {

    static final int COLUMN_PKEY=0;
    public static final int COLUMN_OPERATING_SYSTEM_VERSION=1;
    public static final int COLUMN_PATH=2;
    static final String COLUMN_PATH_name = "path";
    static final String COLUMN_OPERATING_SYSTEM_VERSION_name= "operating_system_version";

    static final int
        MAX_PATH_LENGTH=178,
        MAX_TYPE_LENGTH=10,
        MAX_SYMLINK_TARGET_LENGTH=76,
        MAX_LINUX_ACCOUNT_LENGTH=12,
        MAX_LINUX_GROUP_LENGTH=10
    ;

    /**
     * The size may not be available for certain file types.
     */
    public static final long NULL_SIZE=-1;

    private int pkey;
    private int operating_system_version;
    private String path;
    private boolean optional;
    private String type;
    private long mode;
    private String linux_account;
    private String linux_group;
    private long size;
    private boolean has_file_md5;
    private long file_md5_hi;
    private long file_md5_lo;
    private String symlink_target;

    @Override
    boolean equalsImpl(Object O) {
	return
            O instanceof DistroFile
            && ((DistroFile)O).pkey==pkey
	;
    }

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_OPERATING_SYSTEM_VERSION: return Integer.valueOf(operating_system_version);
            case COLUMN_PATH: return path;
            case 3: return optional?Boolean.TRUE:Boolean.FALSE;
            case 4: return type;
            case 5: return Long.valueOf(mode);
            case 6: return linux_account;
            case 7: return linux_group;
            case 8: return size==NULL_SIZE?null:Long.valueOf(size);
            case 9: return has_file_md5?Long.valueOf(file_md5_hi):null;
            case 10: return has_file_md5?Long.valueOf(file_md5_lo):null;
            case 11: return symlink_target;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public int getPkey() {
        return pkey;
    }

    public OperatingSystemVersion getOperatingSystemVersion() {
        OperatingSystemVersion osv=table.connector.operatingSystemVersions.get(operating_system_version);
        if(osv==null) throw new WrappedException(new SQLException("Unable to find OperatingSystemVersion: "+operating_system_version));
        return osv;
    }

    public String getPath() {
        return path;
    }
    
    public boolean isOptional() {
        return optional;
    }
    
    public DistroFileType getType() {
        DistroFileType fileType=table.connector.distroFileTypes.get(type);
        if(fileType==null) throw new WrappedException(new SQLException("Unable to find DistroFileType: "+type));
        return fileType;
    }
    
    public long getMode() {
        return mode;
    }
    
    public LinuxAccount getLinuxAccount() {
        if(table==null) throw new NullPointerException("table is null");
        if(table.connector==null) throw new NullPointerException("table.connector is null");
        LinuxAccount linuxAccount=table.connector.linuxAccounts.get(linux_account);
        if(linuxAccount==null) throw new WrappedException(new SQLException("Unable to find LinuxAccount: "+linux_account));
        return linuxAccount;
    }

    public LinuxGroup getLinuxGroup() {
        LinuxGroup linuxGroup=table.connector.linuxGroups.get(linux_group);
        if(linuxGroup==null) throw new WrappedException(new SQLException("Unable to find LinuxGroup: "+linux_group));
        return linuxGroup;
    }

    public long getSize() {
        return size;
    }
    
    public boolean hasFileMD5() {
        return has_file_md5;
    }

    public long getFileMD5Hi() {
        return file_md5_hi;
    }
    
    public long getFileMD5Lo() {
        return file_md5_lo;
    }

    public String getSymlinkTarget() {
        return symlink_target;
    }

    public Integer getKey() {
        return pkey;
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.DISTRO_FILES;
    }

    @Override
    public int hashCodeImpl() {
        return pkey;
    }

    public void init(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
        operating_system_version=result.getInt(2);
        path=result.getString(3);
        optional=result.getBoolean(4);
        type=result.getString(5);
        mode=result.getLong(6);
        linux_account=result.getString(7);
        linux_group=result.getString(8);
        size=result.getLong(9);
        if(result.wasNull()) size=NULL_SIZE;
        file_md5_hi=result.getLong(10);
        has_file_md5=!result.wasNull();
        if(has_file_md5) file_md5_lo=result.getLong(11);
        else file_md5_hi=file_md5_lo=-1;
        symlink_target=result.getString(12);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        operating_system_version=in.readCompressedInt();
        path=in.readCompressedUTF();
        optional=in.readBoolean();
        type=in.readCompressedUTF().intern();
        mode=in.readLong();
        linux_account=in.readCompressedUTF().intern();
        linux_group=in.readCompressedUTF().intern();
        size=in.readLong();
        has_file_md5=in.readBoolean();
        file_md5_hi=has_file_md5?in.readLong():-1;
        file_md5_lo=has_file_md5?in.readLong():-1;
        symlink_target=in.readBoolean()?in.readCompressedUTF():null;
    }

    public void readRecord(DataInputStream in) throws IOException {
        pkey=in.readInt();
        operating_system_version=in.readInt();
        path=in.readUTF();
        optional=in.readBoolean();
        type=in.readUTF().intern();
        mode=in.readLong();
        linux_account=in.readUTF().intern();
        linux_group=in.readUTF().intern();
        size=in.readLong();
        has_file_md5=in.readBoolean();
        file_md5_hi=has_file_md5?in.readLong():-1;
        file_md5_lo=has_file_md5?in.readLong():-1;
        symlink_target=in.readBoolean()?in.readUTF():null;
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_108)>=0) {
            out.writeCompressedInt(pkey);
            out.writeCompressedInt(operating_system_version);
        }
        out.writeCompressedUTF(path, 0);
        out.writeBoolean(optional);
        out.writeCompressedUTF(type, 1);
        out.writeLong(mode);
        out.writeCompressedUTF(linux_account, 2);
        out.writeCompressedUTF(linux_group, 3);
        out.writeLong(size);
        out.writeBoolean(has_file_md5);
        if(has_file_md5) {
            if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_105)>=0) {
                out.writeLong(file_md5_hi);
                out.writeLong(file_md5_lo);
            } else out.writeUTF(MD5.getMD5String(file_md5_hi, file_md5_lo));
        }
        out.writeBoolean(symlink_target!=null);
        if(symlink_target!=null) out.writeCompressedUTF(symlink_target, 4);
    }

    public void writeRecord(DataOutputStream out) throws IOException {
        out.writeInt(pkey);
        out.writeInt(operating_system_version);
        if(path.length()>MAX_PATH_LENGTH) throw new IOException("path.length()>"+MAX_PATH_LENGTH+": "+path.length());
        out.writeUTF(path);
        out.writeBoolean(optional);
        if(type.length()>MAX_TYPE_LENGTH) throw new IOException("type.length()>"+MAX_TYPE_LENGTH+": "+type.length());
        out.writeUTF(type);
        out.writeLong(mode);
        if(linux_account.length()>MAX_LINUX_ACCOUNT_LENGTH) throw new IOException("linux_account.length()>"+MAX_LINUX_ACCOUNT_LENGTH+": "+linux_account.length());
        out.writeUTF(linux_account);
        if(linux_group.length()>MAX_LINUX_GROUP_LENGTH) throw new IOException("linux_group.length()>"+MAX_LINUX_GROUP_LENGTH+": "+linux_group.length());
        out.writeUTF(linux_group);
        out.writeLong(size);
        out.writeBoolean(has_file_md5);
        if(has_file_md5) {
            out.writeLong(file_md5_hi);
            out.writeLong(file_md5_lo);
        }
        out.writeBoolean(symlink_target!=null);
        if(symlink_target!=null) {
            if(symlink_target.length()>MAX_SYMLINK_TARGET_LENGTH) throw new IOException("symlink_target.length()>"+MAX_SYMLINK_TARGET_LENGTH+": "+symlink_target.length());
            out.writeUTF(symlink_target);
        }
    }
}
