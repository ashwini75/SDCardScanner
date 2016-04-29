package ashu.com.sdcardscanner;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by Ashwini on 4/29/16.
 */
public class ScanningIntentService extends IntentService {
    boolean stopped;

    public static final String ACTION_INTENT_SERVICE = "ashu.com.sdcardscanner.RESPONSE";
    public static final String EXTRA_KEY_TITLE = "ashu.com.sdcardscanner.TITLE";
    public static final String EXTRA_KEY_OUT = "ashu.com.sdcardscanner.EXTRA_OUT";
    public static final String EXTRA_AVG_SIZE = "ashu.com.sdcardscanner.EXTRA_AVG_SIZE";
    public static final String EXTRA_FREQUENT_FILE_LIST = "ashu.com.sdcardscanner.FREQUENT_FILE_LIST";
    public static final String EXTRA_LARGE_FILE_LIST = "ashu.com.sdcardscanner.EXTRA_LARGE_FILE_LIST";
    public static final String EXTRA_KEY_FILE_PROPERTY_LIST = "EXTRA_UPDATE";

    public ScanningIntentService() {
        super("ashu.com.sdcardscanner.ScanningIntentService");
        stopped = false;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.setAction(Intent.ACTION_MAIN);
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent pendingIntent_doNothing = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                resultIntent,                    //empty Intent do nothing
                0);
        String notificationText = "Scan Service Running";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification myNotification = new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle("Scanning External Storage")
                .setContentText(notificationText)
                .setTicker("Scan Service Notification")
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent_doNothing)
                .build();

        notificationManager.notify(1, myNotification);


        List<File> files = FileUtil.getFiles(new File(Environment.getExternalStorageDirectory().getAbsolutePath()), new ArrayList<File>());
        ArrayList<FileProperty> largeFileList = getLargestFileListProperty(FileUtil.getBiggestFiles(files));
        ArrayList<FileProperty> frequentFileList = getFrequentFileListProperty(FileUtil.calculateHighestFrequencyFileExtensions(files));
        Long avgSizeInKB = FileUtil.calculateAverageFileSize(files) / 1024;

        if (!stopped) {
            Bundle outBundle = new Bundle();
            outBundle.putLong(EXTRA_AVG_SIZE, avgSizeInKB);
            outBundle.putSerializable(EXTRA_FREQUENT_FILE_LIST, frequentFileList);
            outBundle.putSerializable(EXTRA_LARGE_FILE_LIST, largeFileList);
            Intent intentResponse = new Intent();
            intentResponse.setAction(ACTION_INTENT_SERVICE);
            intentResponse.addCategory(Intent.CATEGORY_DEFAULT);
            intentResponse.putExtra(EXTRA_KEY_OUT, outBundle);
            sendBroadcast(intentResponse);
        }
    }

    private ArrayList<FileProperty> getFrequentFileListProperty(Map<String, Integer> fileMap) {
        ArrayList<FileProperty> frequentFileList = new ArrayList<>();
        Iterator it = fileMap.entrySet().iterator();
        int count = 0;
        while (it.hasNext() & count < 5) {
            Map.Entry pair = (Map.Entry) it.next();
            frequentFileList.add(new FileProperty(pair.getKey().toString(), pair.getValue().toString()));
            count++;
        }
        return frequentFileList;

    }

    private ArrayList<FileProperty> getLargestFileListProperty(List<File> files) {
        ArrayList<FileProperty> largestFileList = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            largestFileList.add(new FileProperty(files.get(i).getName(), String.valueOf(files.get(i).length() / 1024) + " Kb"));
        }
        return largestFileList;
    }

    @Override
    public void onDestroy() {
        stopped = true;
        super.onDestroy();
    }

}
