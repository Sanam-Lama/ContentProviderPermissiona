package com.example.contentproviderpermissions;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.security.Permission;

public class MainActivity extends AppCompatActivity {

    TextView text_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text_view = (TextView)findViewById(R.id.textView1);

        // Providing MarshMallow permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            // when the permission is granted, clicked "Allow" then it goes to onRequestPermissionREsult's if (requestcode == ..)
        }
        else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_CONTACTS}, 1);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // when "allow" is clicked, it reaches here and starts processing
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // permission granted, do your work

            Log.e("REQUEST", "REQUESTPERMISSION WAS GRANTED");
            fetchContacts();
        }

        // if the user hits "DENY" for the first time
        else {
            // permission denied
          //  Log.e("ELSE", "PERMISSION DENIED");
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                Toast.makeText(this, "Rationale displayed", Toast.LENGTH_SHORT).show();
            }

            // this is when the user checks "Do not ask again box"
            else {
                Toast.makeText(this, "Never ask again selected",Toast.LENGTH_SHORT).show();
            }
        }
        return;

    }

    // method that fetches the contacts from contacts app using Content Provider
    public void fetchContacts() {
        String phoneNumber = null;
        String email = null;

        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

        Uri EmailCONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        String Email_CONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
        String DATA = ContactsContract.CommonDataKinds.Email.DATA;

        StringBuffer output = new StringBuffer();
        //ContentResolver contentResolver = getContentResolver();

        Log.e("CURSOR", "ABOVE THE CURSOR LINE");

        Cursor cursor = getContentResolver().query(CONTENT_URI, null, null, null, null);
        // Loop for every contact in the phone
        if(cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));

                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));
                if(hasPhoneNumber > 0) {
                    output.append("\n First name:" + name);

                    // Query and loop for every phone number of the contact

                    // what does this Phone_CONTACT_ID + " = ?" mean ?????

                    Cursor phoneCursor = getContentResolver().query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[] {contact_id}, null);
                    while (phoneCursor.moveToNext()) {
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                        output.append("\n Phone number: " + phoneNumber);
                    }
                    phoneCursor.close();

                    // Query and loop for every email of the contact
                    Cursor emailCursor = getContentResolver().query(EmailCONTENT_URI, null, Email_CONTACT_ID + " = ?", new String[] {contact_id}, null);
                    while (emailCursor.moveToNext()) {
                        email = emailCursor.getString(emailCursor.getColumnIndex(DATA));
                        output.append("\n Email: " + email);
                    }
                    emailCursor.close();
                }
                output.append("\n");
            }
            text_view.setText(output);
        }
    }
}
