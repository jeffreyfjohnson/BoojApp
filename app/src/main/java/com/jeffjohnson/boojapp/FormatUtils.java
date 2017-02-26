package com.jeffjohnson.boojapp;

/**
 * Created by jeffreyjohnson on 2/25/17.
 */

public class FormatUtils {

    public static String formatPhone(String[] phoneNumber){
        if (phoneNumber.length > 2) {
            return "(" + phoneNumber[0] + ")-" + phoneNumber[1] + "-" + phoneNumber[2];
        }
        else if (phoneNumber.length > 1){
            return phoneNumber[0] + "-" + phoneNumber[1];
        }
        else {
            return "";
        }
    }

}
