package pers.liyi.bullet.utils.box;

import android.util.Log;


public class LogUtils {
    /**
     * LEVEL_V = 1 : Verbose
     */
    public static final int LEVEL_V = 1;
    /**
     * LEVEL_V = 2 : Debug
     */
    public static final int LEVEL_D = 2;
    /**
     * LEVEL_V = 3 : Info
     */
    public static final int LEVEL_I = 3;
    /**
     * LEVEL_V = 4 : Warning
     */
    public static final int LEVEL_W = 4;
    /**
     * LEVEL_V = 5 : Error
     */
    public static final int LEVEL_E = 5;
    /**
     * LEVEL_ALL = 6 : All logs
     */
    public static final int LEVEL_ALL = 6;

    public static int logLevel = LEVEL_ALL;

    public static boolean enable = true;

    public static void v(String tag, String text) {
        if (enable && (logLevel == LEVEL_V || logLevel == LEVEL_ALL)) {
            String msg = buildMessage("[V]", text);
            Log.v(tag, msg);
        }
    }

    public static void d(String tag, String text) {
        if (enable && (logLevel == LEVEL_D || logLevel == LEVEL_ALL)) {
            String msg = buildMessage("[D]", text);
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String text) {
        if (enable && (logLevel == LEVEL_I || logLevel == LEVEL_ALL)) {
            String msg = buildMessage("[I]", text);
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String text) {
        if (enable && (logLevel == LEVEL_W || logLevel == LEVEL_ALL)) {
            String msg = buildMessage("[W]", text);
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String text) {
        if (enable && (logLevel == LEVEL_E || logLevel == LEVEL_ALL)) {
            String msg = buildMessage("[E]", text);
            Log.e(tag, msg);
        }
    }

    /**
     * Building Message
     *
     * @param level
     * @param text  The message you would like logged.
     * @return Message String
     */
    protected static String buildMessage(String level, String text) {
        StackTraceElement caller = new Throwable().fillInStackTrace().getStackTrace()[2];
        return new StringBuilder()
                .append(level).append("\t")
                .append("=================================================================")
                .append("|| ThreadID >>> " + Thread.currentThread().getId()).append(",\n")
                .append("|| --------------------------------------------------------------")
                .append("|| FileName >>> ").append(caller.getFileName()).append(",\n")// package name + class name
                .append("|| --------------------------------------------------------------")
                .append("|| ClassName >>> ").append(caller.getClassName()).append(",\n")
                .append("|| --------------------------------------------------------------")
                .append("|| MethodName >>> ").append(caller.getMethodName()).append("(): ")
                .append("|| --------------------------------------------------------------")
                .append("|| Line >>> ").append(caller.getLineNumber()).append("\n")
                .append("|| ==============================================================")
                .append("|| ").append(text)
                .append("=================================================================")
                .toString();
    }
}
