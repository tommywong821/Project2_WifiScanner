package hkcc.ccn3165.project2_wifiscanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

class wifiListadapter extends ArrayAdapter<WIFIInformation> {

    private Context mContext;
    int mResource;

    public wifiListadapter(Context context, int resource, ArrayList<WIFIInformation> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String longitude = getItem(position).getLongitude();
        String latitude = getItem(position).getLatitude();
        String BSSID = getItem(position).getBSSID();
        String SSID = getItem(position).getSSID();
        String date = getItem(position).getDate();
        String time = getItem(position).getTime();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvLongitude = (TextView) convertView.findViewById(R.id.WIFILongitude);
        TextView tvlatitude = (TextView) convertView.findViewById(R.id.WIFILatitude);
        TextView tvBSSID = (TextView) convertView.findViewById(R.id.WIFIBSSID);
        TextView tvSSID = (TextView) convertView.findViewById(R.id.WIFISSID);
        TextView tvDate = (TextView) convertView.findViewById(R.id.WIFIDate);
        TextView tvTime = (TextView) convertView.findViewById(R.id.WIFITime);

        tvLongitude.setText(longitude);
        tvlatitude.setText(latitude);
        tvBSSID.setText(BSSID);
        tvSSID.setText(SSID);
        tvDate.setText(date);
        tvTime.setText(time);

        return convertView;
    }
}
