package com.arr.bugsend;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import com.arr.bugsend.R;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class BugSendNotification {

    private Activity mActivity;
    private String title;
    private String message;

    private String extra;
    private String email;
    private String asunto;

    private static final String CHANNEL_ID = "BugSend";
    private static final String CHANNEL_NAME = "BugSend";
    private static final String CHANNEL_DESCRIPTION = "Send bug to developer";

    public BugSendNotification(Activity activity) {
        this.mActivity = activity;
    }

    public BugSendNotification setEmail(String email) {
        this.email = email;
        return this;
    }

    public BugSendNotification setSubject(String asunto) {
        this.asunto = asunto;
        return this;
    }

    public BugSendNotification setExtraText(String info) {
        this.extra = info;
        return this;
    }

    public BugSendNotification setTitle(String title) {
        this.title = title;
        return this;
    }

    public BugSendNotification setMessage(String message) {
        this.message = message;
        return this;
    }

    public void show() {
        StringBuilder builder = new StringBuilder();
        try {
            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(mActivity.openFileInput("stack.trace")));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
            reader.close();
        } catch (Exception e) {
            mActivity.deleteFile("stack.trace");
            e.printStackTrace();
        }
        if (builder.length() > 0) {
            String report = getDiviceInfo() + builder;
            showNotification(report);
        } else {
            mActivity.deleteFile("stack.trace");
        }
    }

    private void showNotification(String error) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager =
                    mActivity.getSystemService(NotificationManager.class);
            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        // Crear la notificación utilizando NotificationCompat.Builder
        Intent bug = sendBug(error);
        PendingIntent pending =
                PendingIntent.getActivity(mActivity, 0, bug, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder notifBuilder =
                new NotificationCompat.Builder(mActivity, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_bug)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .addAction(
                                R.drawable.ic_send,
                                mActivity.getString(R.stringsend_report),
                                pending)
                        .setAutoCancel(true);

        // Mostrar la notificación
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mActivity);
        notificationManager.notify(0, notifBuilder.build());
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

    private Intent sendBug(String error) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {email});
        intent.putExtra(Intent.EXTRA_SUBJECT, asunto);
        intent.putExtra(Intent.EXTRA_TEXT, error);
        intent.setType("text/plain");
        intent.setData(Uri.parse("mailto:"));
        mActivity.deleteFile("stack.trace");
        return Intent.createChooser(intent, "Send report");
    }
}
