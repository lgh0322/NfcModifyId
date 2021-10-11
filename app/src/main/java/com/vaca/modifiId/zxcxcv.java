package com.vaca.modifiId;

import com.vaca.modifiId.utils.ZHexUtil;
import com.vaca.modifiId.utils.ZTimeTool;

public class zxcxcv {
    public static String getSendHex(int sendPack) {
        String time = ZTimeTool.getCurrentDateTime("yy-MM-dd-HH-mm-ss");
        String[] arrTime = time.split("-");
        if (arrTime.length == 6) {
            int index0 = 90;
            int index1 = 10;
            int index2 = sendPack;//packet category
            int index3 = Integer.parseInt(arrTime[0]);//Year
            int index4 = Integer.parseInt(arrTime[1]);//Month
            int index5 = Integer.parseInt(arrTime[2]);//Day
            int index6 = Integer.parseInt(arrTime[3]);//Hour
            int index7 = Integer.parseInt(arrTime[4]);//Minute
            int index8 = Integer.parseInt(arrTime[5]);//Second
            int index9 = index0 + index1 + index2 + index3 + index4 + index5 + index6 + index7 + index8 + 2;
            if (index9 > 255) {
                index9 = index9 % 255;
            }
            byte[] data = new byte[]{(byte) index0, (byte) index1, (byte) index2, (byte) index3,
                    (byte) index4, (byte) index5, (byte) index6, (byte) index7, (byte) index8};

            String hexSend = ZHexUtil.encodeHexStr(data);
            String hexIndex9 = ZHexUtil.encodeHexStr(new byte[]{(byte) index9});
            int len = hexIndex9.length();
            String i9 = hexIndex9.substring(len - 2, len);
            return String.format("%s%s", hexSend, i9).toUpperCase();
        }
        return "";
    }
    public static byte[] fuck(){
        byte[] data_00 = ZHexUtil.hexStringToBytes(getSendHex(0));
        return data_00;
    }
}
