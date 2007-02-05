package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;

/**
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class SRKernel extends ServerReportSection<SRKernel> {

    static final int COLUMN_SERVER_REPORT=0;
    
    private int files_allocated_min;
    private float files_allocated_avg;
    private int files_allocated_max;
    private int files_used_min;
    private float files_used_avg;
    private int files_used_max;
    private int files_max_min;
    private float files_max_avg;
    private int files_max_max;
    private int nr_inodes_min;
    private float nr_inodes_avg;
    private int nr_inodes_max;
    private int nr_free_inodes_min;
    private float nr_free_inodes_avg;
    private int nr_free_inodes_max;
    private int inode_preshrink_min;
    private float inode_preshrink_avg;
    private int inode_preshrink_max;
    private int msgmax_min;
    private float msgmax_avg;
    private int msgmax_max;
    private int msgmnb_min;
    private float msgmnb_avg;
    private int msgmnb_max;
    private int msgmni_min;
    private float msgmni_avg;
    private int msgmni_max;
    private int nr_msg_min;
    private float nr_msg_avg;
    private int nr_msg_max;
    private int entropy_avail_min;
    private float entropy_avail_avg;
    private int entropy_avail_max;
    private int rtsig_max_min;
    private float rtsig_max_avg;
    private int rtsig_max_max;
    private int rtsig_nr_min;
    private float rtsig_nr_avg;
    private int rtsig_nr_max;
    private int semmsl_min;
    private float semmsl_avg;
    private int semmsl_max;
    private int semmns_min;
    private float semmns_avg;
    private int semmns_max;
    private int semopm_min;
    private float semopm_avg;
    private int semopm_max;
    private int semmni_min;
    private float semmni_avg;
    private int semmni_max;
    private int nr_sem_min;
    private float nr_sem_avg;
    private int nr_sem_max;
    private int shmall_min;
    private float shmall_avg;
    private int shmall_max;
    private int shmmax_min;
    private float shmmax_avg;
    private int shmmax_max;
    private int shmmni_min;
    private float shmmni_avg;
    private int shmmni_max;
    private int nr_shm_min;
    private float nr_shm_avg;
    private int nr_shm_max;
    private int shm_used_min;
    private float shm_used_avg;
    private int shm_used_max;
    private int threads_max_min;
    private float threads_max_avg;
    private int threads_max_max;
    
    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_SERVER_REPORT: return Integer.valueOf(server_report);
            case 1: return Integer.valueOf(files_allocated_min);
            case 2: return new Float(files_allocated_avg);
            case 3: return Integer.valueOf(files_allocated_max);
            case 4: return Integer.valueOf(files_used_min);
            case 5: return new Float(files_used_avg);
            case 6: return Integer.valueOf(files_used_max);
            case 7: return Integer.valueOf(files_max_min);
            case 8: return new Float(files_max_avg);
            case 9: return Integer.valueOf(files_max_max);
            case 10: return Integer.valueOf(nr_inodes_min);
            case 11: return new Float(nr_inodes_avg);
            case 12: return Integer.valueOf(nr_inodes_max);
            case 13: return Integer.valueOf(nr_free_inodes_min);
            case 14: return new Float(nr_free_inodes_avg);
            case 15: return Integer.valueOf(nr_free_inodes_max);
            case 16: return Integer.valueOf(inode_preshrink_min);
            case 17: return new Float(inode_preshrink_avg);
            case 18: return Integer.valueOf(inode_preshrink_max);
            case 19: return Integer.valueOf(msgmax_min);
            case 20: return new Float(msgmax_avg);
            case 21: return Integer.valueOf(msgmax_max);
            case 22: return Integer.valueOf(msgmnb_min);
            case 23: return new Float(msgmnb_avg);
            case 24: return Integer.valueOf(msgmnb_max);
            case 25: return Integer.valueOf(msgmni_min);
            case 26: return new Float(msgmni_avg);
            case 27: return Integer.valueOf(msgmni_max);
            case 28: return Integer.valueOf(nr_msg_min);
            case 29: return new Float(nr_msg_avg);
            case 30: return Integer.valueOf(nr_msg_max);
            case 31: return Integer.valueOf(entropy_avail_min);
            case 32: return new Float(entropy_avail_avg);
            case 33: return Integer.valueOf(entropy_avail_max);
            case 34: return Integer.valueOf(rtsig_max_min);
            case 35: return new Float(rtsig_max_avg);
            case 36: return Integer.valueOf(rtsig_max_max);
            case 37: return Integer.valueOf(rtsig_nr_min);
            case 38: return new Float(rtsig_nr_avg);
            case 39: return Integer.valueOf(rtsig_nr_max);
            case 40: return Integer.valueOf(semmsl_min);
            case 41: return new Float(semmsl_avg);
            case 42: return Integer.valueOf(semmsl_max);
            case 43: return Integer.valueOf(semmns_min);
            case 44: return new Float(semmns_avg);
            case 45: return Integer.valueOf(semmns_max);
            case 46: return Integer.valueOf(semopm_min);
            case 47: return new Float(semopm_avg);
            case 48: return Integer.valueOf(semopm_max);
            case 49: return Integer.valueOf(semmni_min);
            case 50: return new Float(semmni_avg);
            case 51: return Integer.valueOf(semmni_max);
            case 52: return Integer.valueOf(nr_sem_min);
            case 53: return new Float(nr_sem_avg);
            case 54: return Integer.valueOf(nr_sem_max);
            case 55: return Integer.valueOf(shmall_min);
            case 56: return new Float(shmall_avg);
            case 57: return Integer.valueOf(shmall_max);
            case 58: return Integer.valueOf(shmmax_min);
            case 59: return new Float(shmmax_avg);
            case 60: return Integer.valueOf(shmmax_max);
            case 61: return Integer.valueOf(shmmni_min);
            case 62: return new Float(shmmni_avg);
            case 63: return Integer.valueOf(shmmni_max);
            case 64: return Integer.valueOf(nr_shm_min);
            case 65: return new Float(nr_shm_avg);
            case 66: return Integer.valueOf(nr_shm_max);
            case 67: return Integer.valueOf(shm_used_min);
            case 68: return new Float(shm_used_avg);
            case 69: return Integer.valueOf(shm_used_max);
            case 70: return Integer.valueOf(threads_max_min);
            case 71: return new Float(threads_max_avg);
            case 72: return Integer.valueOf(threads_max_max);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }
    
    public int getFilesAllocatedMin() {
        return files_allocated_min;
    }
    
    public float getFilesAllocatedAvg() {
        return files_allocated_avg;
    }
    
    public int getFilesAllocatedMax() {
        return files_allocated_max;
    }

    public int getFilesUsedMin() {
        return files_used_min;
    }
    
    public float getFilesUsedAvg() {
        return files_used_avg;
    }
    
    public int getFilesUsedMax() {
        return files_used_max;
    }

    public int getFilesMaxMin() {
        return files_max_min;
    }
    
    public float getFilesMaxAvg() {
        return files_max_avg;
    }
    
    public int getFilesMaxMax() {
        return files_max_max;
    }

    public int getNrInodesMin() {
        return nr_inodes_min;
    }
    
    public float getNrInodesAvg() {
        return nr_inodes_avg;
    }
    
    public int getNrInodesMax() {
        return nr_inodes_max;
    }

    public int getNrFreeInodesMin() {
        return nr_free_inodes_min;
    }
    
    public float getNrFreeInodesAvg() {
        return nr_free_inodes_avg;
    }
    
    public int getNrFreeInodesMax() {
        return nr_free_inodes_max;
    }

    public int getInodePreshrinkMin() {
        return inode_preshrink_min;
    }
    
    public float getInodePreshrinkAvg() {
        return inode_preshrink_avg;
    }
    
    public int getInodePreshrinkMax() {
        return inode_preshrink_max;
    }

    public int getMsgMaxMin() {
        return msgmax_min;
    }
    
    public float getMsgMaxAvg() {
        return msgmax_avg;
    }
    
    public int getMsgMaxMax() {
        return msgmax_max;
    }

    public int getMsgMnbMin() {
        return msgmnb_min;
    }
    
    public float getMsgMnbAvg() {
        return msgmnb_avg;
    }
    
    public int getMsgMnbMax() {
        return msgmnb_max;
    }

    public int getMsgMniMin() {
        return msgmni_min;
    }
    
    public float getMsgMniAvg() {
        return msgmni_avg;
    }
    
    public int getMsgMniMax() {
        return msgmni_max;
    }

    public int getNrMsgMin() {
        return nr_msg_min;
    }
    
    public float getNrMsgAvg() {
        return nr_msg_avg;
    }
    
    public int getNrMsgMax() {
        return nr_msg_max;
    }

    public int getEntropyAvailMin() {
        return entropy_avail_min;
    }
    
    public float getEntropyAvailAvg() {
        return entropy_avail_avg;
    }
    
    public int getEntropyAvailMax() {
        return entropy_avail_max;
    }

    public int getRtsigMaxMin() {
        return rtsig_max_min;
    }
    
    public float getRtsigMaxAvg() {
        return rtsig_max_avg;
    }
    
    public int getRtsigMaxMax() {
        return rtsig_max_max;
    }

    public int getRtsigNrMin() {
        return rtsig_nr_min;
    }
    
    public float getRtsigNrAvg() {
        return rtsig_nr_avg;
    }
    
    public int getRtsigNrMax() {
        return rtsig_nr_max;
    }

    public int getSemMslMin() {
        return semmsl_min;
    }
    
    public float getSemMslAvg() {
        return semmsl_avg;
    }
    
    public int getSemMslMax() {
        return semmsl_max;
    }

    public int getSemMnsMin() {
        return semmns_min;
    }
    
    public float getSemMnsAvg() {
        return semmns_avg;
    }
    
    public int getSemMnsMax() {
        return semmns_max;
    }

    public int getSemOpmMin() {
        return semopm_min;
    }
    
    public float getSemOpmAvg() {
        return semopm_avg;
    }
    
    public int getSemOpmMax() {
        return semopm_max;
    }

    public int getSemMniMin() {
        return semmni_min;
    }
    
    public float getSemMniAvg() {
        return semmni_avg;
    }
    
    public int getSemMniMax() {
        return semmni_max;
    }

    public int getNrSemMin() {
        return nr_sem_min;
    }
    
    public float getNrSemAvg() {
        return nr_sem_avg;
    }
    
    public int getNrSemMax() {
        return nr_sem_max;
    }

    public int getShmAllMin() {
        return shmall_min;
    }
    
    public float getShmAllAvg() {
        return shmall_avg;
    }
    
    public int getShmAllMax() {
        return shmall_max;
    }

    public int getShmMaxMin() {
        return shmmax_min;
    }
    
    public float getShmMaxAvg() {
        return shmmax_avg;
    }
    
    public int getShmMaxMax() {
        return shmmax_max;
    }

    public int getShmMniMin() {
        return shmmni_min;
    }
    
    public float getShmMniAvg() {
        return shmmni_avg;
    }
    
    public int getShmMniMax() {
        return shmmni_max;
    }

    public int getNrShmMin() {
        return nr_shm_min;
    }
    
    public float getNrShmAvg() {
        return nr_shm_avg;
    }
    
    public int getNrShmMax() {
        return nr_shm_max;
    }

    public int getShmUsedMin() {
        return shm_used_min;
    }
    
    public float getShmUsedAvg() {
        return shm_used_avg;
    }
    
    public int getShmUsedMax() {
        return shm_used_max;
    }

    public int getThreadsMaxMin() {
        return threads_max_min;
    }
    
    public float getThreadsMaxAvg() {
        return threads_max_avg;
    }
    
    public int getThreadsMaxMax() {
        return threads_max_max;
    }

    public Integer getKey() {
	return server_report;
    }

    protected int getTableIDImpl() {
	return SchemaTable.SR_KERNEL;
    }

    int hashCodeImpl() {
	return server_report;
    }

    void initImpl(ResultSet result) throws SQLException {
        server_report=result.getInt(1);
        files_allocated_min=result.getInt(2);
        files_allocated_avg=result.getFloat(3);
        files_allocated_max=result.getInt(4);
        files_used_min=result.getInt(5);
        files_used_avg=result.getFloat(6);
        files_used_max=result.getInt(7);
        files_max_min=result.getInt(8);
        files_max_avg=result.getFloat(9);
        files_max_max=result.getInt(10);
        nr_inodes_min=result.getInt(11);
        nr_inodes_avg=result.getFloat(12);
        nr_inodes_max=result.getInt(13);
        nr_free_inodes_min=result.getInt(14);
        nr_free_inodes_avg=result.getFloat(15);
        nr_free_inodes_max=result.getInt(16);
        inode_preshrink_min=result.getInt(17);
        inode_preshrink_avg=result.getFloat(18);
        inode_preshrink_max=result.getInt(19);
        msgmax_min=result.getInt(20);
        msgmax_avg=result.getFloat(21);
        msgmax_max=result.getInt(22);
        msgmnb_min=result.getInt(23);
        msgmnb_avg=result.getFloat(24);
        msgmnb_max=result.getInt(25);
        msgmni_min=result.getInt(26);
        msgmni_avg=result.getFloat(27);
        msgmni_max=result.getInt(28);
        nr_msg_min=result.getInt(29);
        nr_msg_avg=result.getFloat(30);
        nr_msg_max=result.getInt(31);
        entropy_avail_min=result.getInt(32);
        entropy_avail_avg=result.getFloat(33);
        entropy_avail_max=result.getInt(34);
        rtsig_max_min=result.getInt(35);
        rtsig_max_avg=result.getFloat(36);
        rtsig_max_max=result.getInt(37);
        rtsig_nr_min=result.getInt(38);
        rtsig_nr_avg=result.getFloat(39);
        rtsig_nr_max=result.getInt(40);
        semmsl_min=result.getInt(41);
        semmsl_avg=result.getFloat(42);
        semmsl_max=result.getInt(43);
        semmns_min=result.getInt(44);
        semmns_avg=result.getFloat(45);
        semmns_max=result.getInt(46);
        semopm_min=result.getInt(47);
        semopm_avg=result.getFloat(48);
        semopm_max=result.getInt(49);
        semmni_min=result.getInt(50);
        semmni_avg=result.getFloat(51);
        semmni_max=result.getInt(52);
        nr_sem_min=result.getInt(53);
        nr_sem_avg=result.getFloat(54);
        nr_sem_max=result.getInt(55);
        shmall_min=result.getInt(56);
        shmall_avg=result.getFloat(57);
        shmall_max=result.getInt(58);
        shmmax_min=result.getInt(59);
        shmmax_avg=result.getFloat(60);
        shmmax_max=result.getInt(61);
        shmmni_min=result.getInt(62);
        shmmni_avg=result.getFloat(63);
        shmmni_max=result.getInt(64);
        nr_shm_min=result.getInt(65);
        nr_shm_avg=result.getFloat(66);
        nr_shm_max=result.getInt(67);
        shm_used_min=result.getInt(68);
        shm_used_avg=result.getFloat(69);
        shm_used_max=result.getInt(70);
        threads_max_min=result.getInt(71);
        threads_max_avg=result.getFloat(72);
        threads_max_max=result.getInt(73);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        server_report=in.readCompressedInt();
        files_allocated_min=in.readCompressedInt();
        files_allocated_avg=in.readFloat();
        files_allocated_max=in.readCompressedInt();
        files_used_min=in.readCompressedInt();
        files_used_avg=in.readFloat();
        files_used_max=in.readCompressedInt();
        files_max_min=in.readCompressedInt();
        files_max_avg=in.readFloat();
        files_max_max=in.readCompressedInt();
        nr_inodes_min=in.readCompressedInt();
        nr_inodes_avg=in.readFloat();
        nr_inodes_max=in.readCompressedInt();
        nr_free_inodes_min=in.readCompressedInt();
        nr_free_inodes_avg=in.readFloat();
        nr_free_inodes_max=in.readCompressedInt();
        inode_preshrink_min=in.readCompressedInt();
        inode_preshrink_avg=in.readFloat();
        inode_preshrink_max=in.readCompressedInt();
        msgmax_min=in.readCompressedInt();
        msgmax_avg=in.readFloat();
        msgmax_max=in.readCompressedInt();
        msgmnb_min=in.readCompressedInt();
        msgmnb_avg=in.readFloat();
        msgmnb_max=in.readCompressedInt();
        msgmni_min=in.readCompressedInt();
        msgmni_avg=in.readFloat();
        msgmni_max=in.readCompressedInt();
        nr_msg_min=in.readCompressedInt();
        nr_msg_avg=in.readFloat();
        nr_msg_max=in.readCompressedInt();
        entropy_avail_min=in.readCompressedInt();
        entropy_avail_avg=in.readFloat();
        entropy_avail_max=in.readCompressedInt();
        rtsig_max_min=in.readCompressedInt();
        rtsig_max_avg=in.readFloat();
        rtsig_max_max=in.readCompressedInt();
        rtsig_nr_min=in.readCompressedInt();
        rtsig_nr_avg=in.readFloat();
        rtsig_nr_max=in.readCompressedInt();
        semmsl_min=in.readCompressedInt();
        semmsl_avg=in.readFloat();
        semmsl_max=in.readCompressedInt();
        semmns_min=in.readCompressedInt();
        semmns_avg=in.readFloat();
        semmns_max=in.readCompressedInt();
        semopm_min=in.readCompressedInt();
        semopm_avg=in.readFloat();
        semopm_max=in.readCompressedInt();
        semmni_min=in.readCompressedInt();
        semmni_avg=in.readFloat();
        semmni_max=in.readCompressedInt();
        nr_sem_min=in.readCompressedInt();
        nr_sem_avg=in.readFloat();
        nr_sem_max=in.readCompressedInt();
        shmall_min=in.readCompressedInt();
        shmall_avg=in.readFloat();
        shmall_max=in.readCompressedInt();
        shmmax_min=in.readCompressedInt();
        shmmax_avg=in.readFloat();
        shmmax_max=in.readCompressedInt();
        shmmni_min=in.readCompressedInt();
        shmmni_avg=in.readFloat();
        shmmni_max=in.readCompressedInt();
        nr_shm_min=in.readCompressedInt();
        nr_shm_avg=in.readFloat();
        nr_shm_max=in.readCompressedInt();
        shm_used_min=in.readCompressedInt();
        shm_used_avg=in.readFloat();
        shm_used_max=in.readCompressedInt();
        threads_max_min=in.readCompressedInt();
        threads_max_avg=in.readFloat();
        threads_max_max=in.readCompressedInt();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeCompressedInt(server_report);
        out.writeCompressedInt(files_allocated_min);
        out.writeFloat(files_allocated_avg);
        out.writeCompressedInt(files_allocated_max);
        out.writeCompressedInt(files_used_min);
        out.writeFloat(files_used_avg);
        out.writeCompressedInt(files_used_max);
        out.writeCompressedInt(files_max_min);
        out.writeFloat(files_max_avg);
        out.writeCompressedInt(files_max_max);
        out.writeCompressedInt(nr_inodes_min);
        out.writeFloat(nr_inodes_avg);
        out.writeCompressedInt(nr_inodes_max);
        out.writeCompressedInt(nr_free_inodes_min);
        out.writeFloat(nr_free_inodes_avg);
        out.writeCompressedInt(nr_free_inodes_max);
        out.writeCompressedInt(inode_preshrink_min);
        out.writeFloat(inode_preshrink_avg);
        out.writeCompressedInt(inode_preshrink_max);
        out.writeCompressedInt(msgmax_min);
        out.writeFloat(msgmax_avg);
        out.writeCompressedInt(msgmax_max);
        out.writeCompressedInt(msgmnb_min);
        out.writeFloat(msgmnb_avg);
        out.writeCompressedInt(msgmnb_max);
        out.writeCompressedInt(msgmni_min);
        out.writeFloat(msgmni_avg);
        out.writeCompressedInt(msgmni_max);
        out.writeCompressedInt(nr_msg_min);
        out.writeFloat(nr_msg_avg);
        out.writeCompressedInt(nr_msg_max);
        out.writeCompressedInt(entropy_avail_min);
        out.writeFloat(entropy_avail_avg);
        out.writeCompressedInt(entropy_avail_max);
        out.writeCompressedInt(rtsig_max_min);
        out.writeFloat(rtsig_max_avg);
        out.writeCompressedInt(rtsig_max_max);
        out.writeCompressedInt(rtsig_nr_min);
        out.writeFloat(rtsig_nr_avg);
        out.writeCompressedInt(rtsig_nr_max);
        out.writeCompressedInt(semmsl_min);
        out.writeFloat(semmsl_avg);
        out.writeCompressedInt(semmsl_max);
        out.writeCompressedInt(semmns_min);
        out.writeFloat(semmns_avg);
        out.writeCompressedInt(semmns_max);
        out.writeCompressedInt(semopm_min);
        out.writeFloat(semopm_avg);
        out.writeCompressedInt(semopm_max);
        out.writeCompressedInt(semmni_min);
        out.writeFloat(semmni_avg);
        out.writeCompressedInt(semmni_max);
        out.writeCompressedInt(nr_sem_min);
        out.writeFloat(nr_sem_avg);
        out.writeCompressedInt(nr_sem_max);
        out.writeCompressedInt(shmall_min);
        out.writeFloat(shmall_avg);
        out.writeCompressedInt(shmall_max);
        out.writeCompressedInt(shmmax_min);
        out.writeFloat(shmmax_avg);
        out.writeCompressedInt(shmmax_max);
        out.writeCompressedInt(shmmni_min);
        out.writeFloat(shmmni_avg);
        out.writeCompressedInt(shmmni_max);
        out.writeCompressedInt(nr_shm_min);
        out.writeFloat(nr_shm_avg);
        out.writeCompressedInt(nr_shm_max);
        out.writeCompressedInt(shm_used_min);
        out.writeFloat(shm_used_avg);
        out.writeCompressedInt(shm_used_max);
        out.writeCompressedInt(threads_max_min);
        out.writeFloat(threads_max_avg);
        out.writeCompressedInt(threads_max_max);
    }
}