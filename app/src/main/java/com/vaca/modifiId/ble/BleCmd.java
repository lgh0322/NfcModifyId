package com.vaca.modifiId.ble;



import java.util.Date;

import static com.vaca.modifiId.utils.CRCUtils.calCRC8;

public class BleCmd {
    public final static byte CMD_SET_TIME = (byte) 0xEC;
    public final static int CMD_SET_TIME2 = 0xEC;


    private static int seqNo = 0;


    private static void addNo() {
        seqNo++;
        if (seqNo >= 255) {
            seqNo = 0;
        }
    }


    public static byte[] reset() {
        int len = 0;
        byte[] cmd = new byte[6 + len];
        cmd[0] = (byte) 0xCC;
        cmd[1] = (byte) CMD_SET_TIME;
        cmd[2] = (byte) ~CMD_SET_TIME;
        cmd[3] = (byte) seqNo;
        cmd[4] = (byte) 0;
        cmd[5] = calCRC8(cmd);
        addNo();
        return cmd;
    }


}
