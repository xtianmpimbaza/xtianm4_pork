package com.example.liew.idelivery;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ContactUs extends AppCompatActivity {

    ImageView image_call,image_mail,image_facebook;
    public static String FACEBOOK_URL = "https://www.facebook.com/";
    public static String FACEBOOK_PAGE_ID = "PorkDelivery";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_contact_us);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        toolbar.setTitle("Contact Us");
        setSupportActionBar(toolbar);

        image_call = (ImageView)findViewById(R.id.image_call);
        image_mail = (ImageView)findViewById(R.id.image_mail);
        image_facebook = (ImageView)findViewById(R.id.image_facebook);

        image_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:+256700582075"));
                if (ActivityCompat.checkSelfPermission(ContactUs.this,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                }
                startActivity(intent);

            }
        });

        image_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"skiguddeivan@gmail.com"});
                emailIntent.putExtra(Intent.EXTRA_CC, new String[]{""});

                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Pork Delivery Enquiry");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "");

                emailIntent.setType("message/rfc822");
                startActivity(Intent.createChooser(emailIntent,"Choose email..."));
            }
        });

        image_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
                String facebookUrl = getFacebookPageURL(getBaseContext());
                facebookIntent.setData(Uri.parse(facebookUrl));
                startActivity(facebookIntent);
            }
        });
    }

    public String getFacebookPageURL(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                return "fb://page/" + FACEBOOK_PAGE_ID;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return FACEBOOK_URL; //normal web url
        }
    }
}
