package com.zyt;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by Administrator on 2015/8/1.
 */
public class Util {
    public static void safeClose(Closeable... closeable) {
        for (Closeable c : closeable) {
            if (c != null) {
                try {
                    c.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static void safeClose(AutoCloseable... autoCloseable) {
        for (AutoCloseable ac : autoCloseable) {
            if (ac != null) {
                try {
                    ac.close();
                } catch (Exception e) {
                }
            }
        }
    }

    private Util() {
    }
}
