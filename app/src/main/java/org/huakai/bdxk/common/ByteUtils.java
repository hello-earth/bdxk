package org.huakai.bdxk.common;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/8/3.
 */

public class ByteUtils {
    /* Convert byte[] to hex string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
            * @param src byte[] data
    * @return hex string
    */
    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
    /**
     * Convert hex string to byte[]
     * @param hexString the hex string
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        byte[] d = new byte[length];
        for (int j = 0; j < length;j++) {
            int i = j*2;
            d[j] = (byte) Integer.parseInt(hexString.substring(i,i+2), 16);
        }
        return d;
    }

    public static int[] hexStringToInt(String hexString){
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        int[] d = new int[length];
        for (int j = 0; j < length;j++) {
            int i = j*2;
            d[j] = Integer.parseInt(hexString.substring(i,i+2), 16);
        }
        return d;
    }

    public static String byteToHexStr(byte[] b) {
        if (b == null || b.length <= 0) {
            return null;
        }
        String stmp = "";
        StringBuilder sb = new StringBuilder("");
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            sb.append((stmp.length() == 1) ? "0" + stmp : stmp);
        }
        return sb.toString().toUpperCase().trim();
    }

    public static int[] stringToInt(String hex){
        char[] hchar = hex.toCharArray();
        int[] d = new int[hchar.length];
        for(int i=0;i<hchar.length;i++){
            d[i]=Integer.valueOf(hchar[i]);
        }
        return d;
    }

    public static String getCmdHexStr(String sensorid, String src){
        SimpleDateFormat df = new SimpleDateFormat("yyMMddHHmmss");
        String cmd = "AA75"+src+"000E00"+sensorid+df.format(new Date());
        int[] hexStr = hexStringToInt(cmd);
        int org = 0;
        for(int hex : hexStr){
            org = org^hex;
        }
        return cmd+Integer.toHexString(org);
    }

    public static String getDecsCmdHexStr(String src, String desc){
        SimpleDateFormat df = new SimpleDateFormat("yyMMddHHmmss");
        int[] des = stringToInt(desc);
        String hexstr = "";
        for(int d : des)
            hexstr+=Integer.toHexString(d);
        String cmd = "AA75"+src+"001F000000000000000000"+df.format(new Date())+hexstr;
        int[] hexStr = hexStringToInt(cmd);
        int org = 0;
        for(int hex : hexStr){
            org = org^hex;
        }
        return cmd+Integer.toHexString(org);
    }

}
