package com.arr.bugsend.utils;
import android.app.Application;
import android.content.Context;
import androidx.annotation.NonNull;
import java.io.FileOutputStream;
import java.io.IOException;

public class HandlerUtil implements Thread.UncaughtExceptionHandler {

    private final Application app;
    private final Thread.UncaughtExceptionHandler defaultExceptionHandler;

    public HandlerUtil(Application app) {
        this.app = app;
        this.defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        saveCrashReport(throwable);
        if (defaultExceptionHandler != null) {
            defaultExceptionHandler.uncaughtException(thread, throwable);
        }
    }

    private void saveCrashReport(Throwable throwable) {
        StackTraceElement[] stackTrace = throwable.getStackTrace();
        StringBuilder strReport = new StringBuilder();
        strReport.append(throwable).append("\n\n").append("••••• Stack Trace •••••\n");
        for (StackTraceElement stackTraceElement : stackTrace) {
            strReport.append(" ").append(stackTraceElement).append("\n");
        }

        strReport.append("••••••\n\n");
        try (FileOutputStream fos = app.openFileOutput("stack.trace", Context.MODE_PRIVATE)) {
            fos.write(strReport.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
