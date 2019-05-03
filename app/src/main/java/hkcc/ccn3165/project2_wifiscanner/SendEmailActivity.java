package hkcc.ccn3165.project2_wifiscanner;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class SendEmailActivity extends AppCompatActivity {
    private EditText mEditTextTo;
    private EditText mEditTextSubject;
    private EditText mEditTextMessage;
    private ListView mTextViewWIFIInfo;
    private Button mSend;
    private wifiListadapter adapter;

    private String showWIFI="";

    DatabaseHelper mDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_email);

        mDatabaseHelper = new DatabaseHelper(this);

        mEditTextTo = (EditText)findViewById(R.id.etTo);
        mEditTextSubject = (EditText)findViewById(R.id.etSubject);
        mEditTextMessage = (EditText)findViewById(R.id.etMessage);
        mTextViewWIFIInfo = (ListView) findViewById(R.id.tvWIFIInfo);
        showWifiInfo();

        mSend = (Button)findViewById(R.id.btnSend);
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMail();
            }
        });
    }

    private void showWifiInfo() {
        Cursor data = mDatabaseHelper.getData();
        String buffer;
        int count = 1;
        ArrayList<WIFIInformation> listData = new ArrayList<>();
        while(data.moveToNext()){
            buffer = String.format("%d. %s, %s, %s, %s, %s, %s", count, data.getString(0), data.getString(1), data.getString(2), data.getString(3), data.getString(4), data.getString(5));
            showWIFI = showWIFI + buffer + "\n";
            WIFIInformation show = new WIFIInformation(data.getString(0), data.getString(1), data.getString(2), data.getString(3), data.getString(4), data.getString(5) );
            listData.add(show);
            count++;
        }
        adapter = new wifiListadapter(this, R.layout.adapter_view_layout, listData);
        mTextViewWIFIInfo.setAdapter(adapter);
    }

    private void sendMail() {
        String recipientList = mEditTextTo.getText().toString();
        String[] recipients = recipientList.split(",");

        String subject = mEditTextSubject.getText().toString();
        String message =  mEditTextMessage.getText().toString() +"\n" + "\nScanned WIFI: \n"  + showWIFI;

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, recipients);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);

        emailIntent.setType("message/rfc822");
        startActivity(Intent.createChooser(emailIntent, "Choose an email client"));
        finish();

    }
}
