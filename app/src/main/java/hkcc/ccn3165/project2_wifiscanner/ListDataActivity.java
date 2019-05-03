package hkcc.ccn3165.project2_wifiscanner;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;

public class ListDataActivity extends AppCompatActivity {

    private ListView wifiListView;
    private wifiListadapter adapter;

    DatabaseHelper mDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_data);

        wifiListView = (ListView)findViewById(R.id.WIFIListView);

        mDatabaseHelper = new DatabaseHelper(this);

        populateListView();
    }

    private void populateListView() {
        Cursor data = mDatabaseHelper.getData();
        ArrayList<WIFIInformation> listData = new ArrayList<>();
        while(data.moveToNext()){
            WIFIInformation show = new WIFIInformation(data.getString(0), data.getString(1), data.getString(2), data.getString(3), data.getString(4), data.getString(5) );
            listData.add(show);
        }
        adapter = new wifiListadapter(this, R.layout.adapter_view_layout, listData);
        wifiListView.setAdapter(adapter);
    }
}
