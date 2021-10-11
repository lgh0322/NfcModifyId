package com.vaca.modifiId.utils;

import java.math.BigDecimal;

/**
 * Created by 朱德伟 on 2017/12/13.
 * 常用的字符串工具类
 */

public class StringUtil {
    public static String formatTo1(double f) {
        BigDecimal bg = new BigDecimal(f);
        return bg.setScale(1, BigDecimal.ROUND_HALF_UP).toString();
    }
    public static String formatTo1ROUNDDOWN(double f) {
        BigDecimal bg = new BigDecimal(f);
        return bg.setScale(1, BigDecimal.ROUND_DOWN).toString();
    }
}
