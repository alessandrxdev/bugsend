package com.arr.bugsend;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class BugSend {

    private final Activity activity;
    private String title;
    private String message;
    private String extra;
    private String email;
    private String asunto;
    private int drawable;

    public BugSend(Activity activity) {
        this.activity = activity;
    }

    public BugSend setTitle(String string) {
        this.title = string;
        return this;
    }

    public BugSend setMessage(String message) {
        this.message = message;
        return this;
    }

    public BugSend setEmail(String email) {
        this.email = email;
        return this;
    }

    public BugSend setSubject(String asunto) {
        this.asunto = asunto;
        return this;
    }

    public BugSend setIcon(int icon) {
        this.drawable = icon;
        return this;
    }

    public BugSend setExtraText(String info) {
        this.extra = info;
        return this;
    }

    public void show() {
        StringBuilder builder = new StringBuilder();
        try {
            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(activity.openFileInput("stack.trace")));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
            reader.close();
        } catch (Exception e) {
            activity.deleteFile("stack.trace");
            e.printStackTrace();
        }
        if (builder.length() > 0) {
            String report = getDiviceInfo() + builder;
            new MaterialAlertDialogBuilder(activity)
                    .setIcon(activity.getDrawable(drawable))
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(
                            android.R.string.ok,
                            (dialog, which) -> {
                                sendCrashReport(report);
                                activity.deleteFile("stack.trace");
                            })
                    .setNegativeButton(
                            android.R.string.cancel,
                            (dialog, which) -> {
                                activity.deleteFile("stack.trace");
                                dialog.dismiss();
                            })
                    .setCancelable(false)
                    .show();
        } else {
            activity.deleteFile("stack.trace");
        }
    }

    private void sendCrashReport(String report) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {email});
        intent.putExtra(Intent.EXTRA_SUBJECT, asunto);
        intent.putExtra(Intent.EXTRA_TEXT, report);
        intent.setType("text/plain");
        intent.setData(Uri.parse("mailto:"));
        activity.startActivity(Intent.createChooser(intent, "Send report"));
    }

    private String getDiviceInfo() {
        String TAB = "\n";
        StringBuilder builder = new StringBuilder();
        builder.append("MODEL: ").append(Build.MODEL);
        builder.append(TAB);
        builder.append("DEVICE: ").append(Build.MANUFACTURER);
        builder.append(TAB);
        builder.append("SDK: ").append(Build.VERSION.SDK_INT);
        builder.append(TAB);
        builder.append(extra);
        builder.append("\n\n");
        String info = builder.toString();

        return info;
    }
}
