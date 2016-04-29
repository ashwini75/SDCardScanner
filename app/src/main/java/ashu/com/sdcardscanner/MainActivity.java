package ashu.com.sdcardscanner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Button startScanButton, stopScanButton, viewLargeFilesButton, viewFrequentFilesButton;
    private LinearLayout resultLinearLayout;
    private ProgressBar progressBar;
    private ScanningResultReceiver myScanningResultReceiver;
    private Intent scanningIntentService;
    private TextView avgFileSize;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        startScanButton = (Button) findViewById(R.id.start_scan_button);
        stopScanButton = (Button) findViewById(R.id.stop_scan_button);
        viewLargeFilesButton = (Button) findViewById(R.id.large_file_button);
        viewFrequentFilesButton = (Button) findViewById(R.id.frequent_file_button);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        resultLinearLayout = (LinearLayout) findViewById(R.id.result_layout);

        progressBar.setVisibility(View.GONE);
        startScanButton.setOnClickListener(startButtonClickListener);
        stopScanButton.setOnClickListener(stopButtonClickListener);

        avgFileSize = (TextView) findViewById(R.id.avg_file_size_view);

        myScanningResultReceiver = new ScanningResultReceiver();
        //register BroadcastReceiver
        IntentFilter intentFilter = new IntentFilter(ScanningIntentService.ACTION_INTENT_SERVICE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(myScanningResultReceiver, intentFilter);

        fab = (FloatingActionButton) findViewById(R.id.share_result);
        fab.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //un-register BroadcastReceiver
        unregisterReceiver(myScanningResultReceiver);
    }

    private View.OnClickListener startButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            progressBar.setVisibility(View.VISIBLE);
            fab.setVisibility(View.GONE);
            scanningIntentService = new Intent(MainActivity.this, ScanningIntentService.class);
            resultLinearLayout.setVisibility(View.GONE);
            startService(scanningIntentService);
        }
    };

    private View.OnClickListener stopButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            progressBar.setVisibility(View.GONE);
            if(scanningIntentService != null){
                stopService(scanningIntentService);
                scanningIntentService = null;
            }
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class ScanningResultReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, Intent intent) {
            progressBar.setVisibility(View.GONE);
            final Bundle outBundle = intent.getBundleExtra(ScanningIntentService.EXTRA_KEY_OUT);
            resultLinearLayout.setVisibility(View.VISIBLE);
            Long avgFilesSize = outBundle.getLong(ScanningIntentService.EXTRA_AVG_SIZE);
            avgFileSize.setText( context.getString(R.string.avg_file_size) + String.valueOf(avgFilesSize) + " Kb");

            viewLargeFilesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString(ScanningIntentService.EXTRA_KEY_TITLE, context.getString(R.string.view_large_files_title));
                    bundle.putSerializable(ScanningIntentService.EXTRA_KEY_FILE_PROPERTY_LIST, outBundle.getSerializable(ScanningIntentService.EXTRA_LARGE_FILE_LIST));
                    Intent intent = new Intent(context, ResultActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });

            viewFrequentFilesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString(ScanningIntentService.EXTRA_KEY_TITLE, context.getString(R.string.view_frequent_files_title));
                    bundle.putSerializable(ScanningIntentService.EXTRA_KEY_FILE_PROPERTY_LIST, outBundle.getSerializable(ScanningIntentService.EXTRA_FREQUENT_FILE_LIST));
                    Intent intent = new Intent(context, ResultActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });

            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent shareIntent =  new Intent(android.content.Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"add what a subject you want");
                    String shareMessage= outBundle.toString();
                    shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,shareMessage);
                    startActivity(Intent.createChooser(shareIntent,"Sharing via"));
                }
            });
        }
    }
}
