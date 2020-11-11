package com.anjiplus.pda.util;

public class VinChangeUtil {

    /**
     * 24字节转17位VIN码
     * <p>
     * ASCII码，一个字节8位，范围是0-127，0-127表示不同字符，48-57 表示0-9，65-90表示A-Z，97-122表示a-z
     * 这里把两个16进制当作一个字节，得到其表示字符，然后再按照算法解密
     *
     * @param ascii
     * @return
     */
    public static String str24To17(String ascii) {
        if (ascii == null || "".equals(ascii)) {
            return null;
        }
        int m, n;
        int byteLen = ascii.length() / 2; // 每两个字符描述一个字节
        char[] sz12 = new char[byteLen];
        for (int i = 0; i < byteLen; i++) {
            m = i * 2 + 1;
            n = m + 1;
            int intVal = Integer.decode("0x" + ascii.substring(i * 2, m) + ascii.substring(m, n));
            sz12[i] = (char) intVal;
        }

        char[] sz17 = new char[17];
        sz17[16] = (char) (sz12[11] & 0x0f);
        sz17[15] = (char) ((sz12[11] & 0xf0) >> 4);
        sz17[14] = (char) (sz12[10] & 0x0f);
        sz17[13] = (char) ((sz12[10] & 0xf0) >> 4);
        sz17[12] = (char) (sz12[9] & 0x3f);
        sz17[11] = (char) (((sz12[9] & 0xc0) >> 6) | ((sz12[8] & 0x0f) << 2));
        sz17[10] = (char) (((sz12[8] & 0xf0) >> 4) | ((sz12[7] & 0x03) << 4));
        sz17[9] = (char) ((sz12[7] & 0xfc) >> 2);
        sz17[8] = (char) (sz12[6] & 0x3f);
        sz17[7] = (char) (((sz12[6] & 0xc0) >> 6) | ((sz12[5] & 0x0f) << 2));
        sz17[6] = (char) (((sz12[5] & 0xf0) >> 4) | ((sz12[4] & 0x03) << 4));
        sz17[5] = (char) ((sz12[4] & 0xfc) >> 2);
        sz17[4] = (char) (sz12[3] & 0x3f);
        sz17[3] = (char) (((sz12[3] & 0xc0) >> 6) | ((sz12[2] & 0x0f) << 2));
        sz17[2] = (char) (((sz12[2] & 0xf0) >> 4) | ((sz12[1] & 0x03) << 4));
        sz17[1] = (char) ((sz12[1] & 0xfc) >> 2);
        sz17[0] = (char) (sz12[0] & 0x3f);
        for (int i = 0; i < 17; i++) {
            sz17[i] = (char) (sz17[i] + 0x30);
        }
        return String.valueOf(sz17);
    }

    /**
     * 17位VIN码转24字节
     * 制卡可以用
     *
     * @param vin
     * @return
     */
    public static String str17To24(String vin) {
        if (vin == null || "".equals(vin)) {
            return null;
        }
        char[] sz17 = vin.toCharArray();
        char[] sz12 = new char[12];
        for (int i = 0; i < 17; i++) {
            sz17[i] = (char) (sz17[i] - 0x30);
        }
        sz12[11] = (char) ((sz17[16] & 0x0f) | (sz17[15] & 0x0f) << 4);
        sz12[10] = (char) ((sz17[14] & 0x0f) | (sz17[13] & 0x0f) << 4);

        sz12[9] = (char) ((sz17[11] << 6) | (sz17[12] & 0x3f));
        sz12[8] = (char) ((sz17[10] << 4) | (sz17[11] >> 2));
        sz12[7] = (char) ((sz17[9] << 2) | (sz17[10] >> 4));
        sz12[6] = (char) ((sz17[7] << 6) | (sz17[8] & 0x3f));
        sz12[5] = (char) ((sz17[6] << 4) | (sz17[7] >> 2));
        sz12[4] = (char) ((sz17[5] << 2) | (sz17[6] >> 4));
        sz12[3] = (char) ((sz17[3] << 6) | (sz17[4] & 0x3f));
        sz12[2] = (char) ((sz17[2] << 4) | (sz17[3] >> 2));
        sz12[1] = (char) ((sz17[1] << 2) | (sz17[2] >> 4));
        sz12[0] = (char) (sz17[0] & 0x3f);
        String strHex;
        StringBuilder sb = new StringBuilder();
        for (int n = 0; n < sz12.length; n++) {
            strHex = Integer.toHexString(sz12[n] & 0xFF);
            sb.append((strHex.length() == 1) ? "0" + strHex : strHex); // 每个字节由两个字符表示，位数不够，高位补0
        }

        return sb.toString().trim();
    }
}
