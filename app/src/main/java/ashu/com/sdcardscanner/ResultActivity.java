package ashu.com.sdcardscanner;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Ashwini on 4/29/16.
 */
public class ResultActivity  extends AppCompatActivity {
    private ListView filepropertyListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_activity);
        Bundle bundle = getIntent().getExtras();
        setTitle(bundle.getString(ScanningIntentService.EXTRA_KEY_TITLE));
        ArrayList<FileProperty> fileProperties = (ArrayList<FileProperty>)bundle.getSerializable(ScanningIntentService.EXTRA_KEY_FILE_PROPERTY_LIST);

        FileProperty[] adapter_data = new FileProperty[fileProperties.size()];
        int i;
        for(i =0 ; i< fileProperties.size() ;i++){
            adapter_data[i] = fileProperties.get(i);
        }
        FilePropertyAdapter adapter = new FilePropertyAdapter(this, R.layout.file_property_row, adapter_data);
        filepropertyListView = (ListView)findViewById(R.id.filePropertyListView);
        filepropertyListView.setAdapter(adapter);
    }

}
