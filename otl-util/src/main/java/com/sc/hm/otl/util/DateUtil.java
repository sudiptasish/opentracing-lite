/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Sudiptasish Chanda
 */
public final class DateUtil {
    
    public static final String FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    
    public static String format(Date date) {
        DateFormat df = new SimpleDateFormat(FORMAT);
        return df.format(date);
    }
}
