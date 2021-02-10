package com.robypomper.comm.connection;

import com.robypomper.java.JavaDate;

import java.util.Date;

public class ConnectionStatsDefault implements ConnectionStats {

    // Internal vars

    // connection
    private Date lastConnection = null;
    private Date lastDisconnection = null;
    // heart beat
    private Date lastHeartBeat = null;
    private Date lastHeartBeatFailed = null;
    private long countHearBeat = 0;
    // rx and tx
    private Date lastDataRx = null;
    private long bytesRx = 0;
    private Date lastDataTx = null;
    private long bytesTx = 0;


    // Last Connection

    public Date getLastConnection() {
        return lastConnection;
    }

    public Date getLastDisconnection() {
        return lastDisconnection;
    }


    // Last HeartBeat

    public Date getLastHeartBeat() {
        return lastHeartBeat;
    }

    public Date getLastHeartBeatFailed() {
        return lastHeartBeatFailed;
    }

    public long getHeartBeatReceived() {
        return countHearBeat;
    }


    // Last Data

    public Date getLastDataRx() {
        return lastDataRx;
    }

    public long getBytesRx() {
        return bytesRx;
    }

    public Date getLastDataTx() {
        return lastDataTx;
    }

    public long getBytesTx() {
        return bytesTx;
    }


    // Setters (only for subclasses)

    public void updateOnConnected() {
        lastConnection = JavaDate.getNowDate();
    }

    public void updateOnDisconnected() {
        lastDisconnection = JavaDate.getNowDate();
    }

    public void updateOnHeartBeatSuccess() {
        lastHeartBeat = JavaDate.getNowDate();
        countHearBeat++;
    }

    public void updateOnHeartBeatFail() {
        lastHeartBeatFailed = JavaDate.getNowDate();
    }

    public void updateOnDataRx(byte[] data) {
        lastDataRx = JavaDate.getNowDate();
        bytesRx += data.length;
    }

    public void updateOnDataTx(byte[] data) {
        lastDataTx = JavaDate.getNowDate();
        bytesTx += data.length;
    }

}
