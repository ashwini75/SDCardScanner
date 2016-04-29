package ashu.com.sdcardscanner;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Ashwini on 4/29/16.
 */
public class FilePropertyAdapter extends ArrayAdapter<FileProperty> {
    Context context;
    int layoutResourceId;
    FileProperty filePropertyList[] = null;

    public FilePropertyAdapter(Context context, int layoutResourceId, FileProperty[] filePropertyList) {
        super(context, layoutResourceId, filePropertyList);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.filePropertyList = filePropertyList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        WeatherHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new WeatherHolder();
            holder.titleView = (TextView) row.findViewById(R.id.title_Textview);
            holder.dataView = (TextView) row.findViewById(R.id.data_TextView);

            row.setTag(holder);
        } else {
            holder = (WeatherHolder) row.getTag();
        }

        FileProperty fileProperty = filePropertyList[position];
        holder.titleView.setText(fileProperty.getTitle());
        holder.dataView.setText(fileProperty.getData());

        return row;
    }

    static class WeatherHolder {
        TextView titleView;
        TextView dataView;
    }
}
