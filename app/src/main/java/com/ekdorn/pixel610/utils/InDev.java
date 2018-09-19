package com.ekdorn.pixel610.utils;

import com.ekdorn.pixel610.pixeldungeon.PXL610;

public class InDev {
    private static final String developer_key = "ponosius300";

    public static boolean isDeveloper() {
        return (PXL610.user_name().equals(developer_key));
    }
}
