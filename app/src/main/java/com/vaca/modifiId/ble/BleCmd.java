package com.vaca.modifiId.ble;


import static com.vaca.modifiId.utils.CRCUtils.calCRC8;

public class BleCmd {
    public final static byte CMD_GET_ALL_CARD = (byte) 0xA1;
    public final static byte CMD_GET_POWER = (byte) 0xA2;
    public final static byte CMD_SET_LED = (byte) 0xA3;
    public final static byte CMD_GET_MACHINE_ID = (byte) 0xA4;


    private static int seqNo = 0;

    private static void addNo() {
        seqNo++;
        if (seqNo >= 255) {
            seqNo = 0;
        }
    }


    public static byte[] getAllCard() {
        int len = 0;
        byte[] cmd = new byte[6 + len];
        cmd[0] = (byte) 0xCC;
        cmd[1] = (byte) CMD_GET_ALL_CARD;
        cmd[2] = (byte) ~CMD_GET_ALL_CARD;
        cmd[3] = (byte) seqNo;
        cmd[4] = (byte) 0;
        cmd[5] = calCRC8(cmd);
        addNo();
        return cmd;
    }


    public static byte[] getPower() {
        int len = 0;
        byte[] cmd = new byte[6 + len];
        cmd[0] = (byte) 0xCC;
        cmd[1] = (byte) CMD_GET_POWER;
        cmd[2] = (byte) ~CMD_GET_POWER;
        cmd[3] = (byte) seqNo;
        cmd[4] = (byte) 0;
        cmd[5] = calCRC8(cmd);
        addNo();
        return cmd;
    }


    public static byte[] setIndicator(int greanLed) {
        int len = 1;
        byte[] cmd = new byte[6 + len];
        cmd[0] = (byte) 0xCC;
        cmd[1] = (byte) CMD_SET_LED;
        cmd[2] = (byte) ~CMD_SET_LED;
        cmd[3] = (byte) seqNo;
        cmd[4] = (byte) 0;
        cmd[5] = (byte) greanLed;
        cmd[6] = calCRC8(cmd);
        addNo();
        return cmd;
    }


    public static byte[] getMachineId() {
        int len = 0;
        byte[] cmd = new byte[6 + len];
        cmd[0] = (byte) 0xCC;
        cmd[1] = (byte) CMD_GET_MACHINE_ID;
        cmd[2] = (byte) ~CMD_GET_MACHINE_ID;
        cmd[3] = (byte) seqNo;
        cmd[4] = (byte) 0;
        cmd[5] = calCRC8(cmd);
        addNo();
        return cmd;
    }


}
