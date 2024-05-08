package com.example.imsafeapp.melissa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.imsafeapp.AboutUs;
import com.example.imsafeapp.Homepage;
import com.example.imsafeapp.Profile_Settings;
import com.example.imsafeapp.R;
import com.example.imsafeapp.community.user.CommunityActivity;
import com.example.imsafeapp.lisa.MainActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

public class ReportIncident extends AppCompatActivity {

    private GoogleMap myMap;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int FINE_PERMISSION_CODE = 1;
    private Location currentLocation;
    ImageView image, video, edit;
    Bitmap bitmap;
    Uri videoUri;
    int REQUEST_CODE_FOR_IMAGE = 1000;

    int REQUEST_CODE_VIDEO_CAPTURE = 2607;

    private static final int REQUEST_CODE = 100;

    Button submit;

    String[] items = {"Robbery taking place in neighbourhood", "Fire caught in Valley building", "Family held hostage in their home",
            "Man killed by speeding vehicle while crossing", "Kid died due to drowning in nearby lake"};

    String[] levels = {"Critical", "Major", "Minor"};

    AutoCompleteTextView autoCompleteTxt;

    AutoCompleteTextView autoCompleteTxt1;

    ArrayAdapter<String> adapterItems;

    ArrayAdapter<String> adapterItems1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_incident);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageView redirect = findViewById(R.id.profileIcon);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        // Replaces image with one associated with Google Account
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageView profileImageView = findViewById(R.id.profileIcon);

        if (account != null) {
            // Load the profile photo using Picasso library (or any other image loading library)
            Picasso.get().load(account.getPhotoUrl()).into(profileImageView);
        } else {
            // If the user is not signed in with Google, you can set a default image or hide the ImageView
            profileImageView.setImageResource(R.drawable.baseline_add_photo_alternate_24);
        }

        redirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReportIncident.this, Profile_Settings.class));
            }
        });

        //bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();

                // Use if-else statements instead of switch-case
                if (itemId == R.id.home) {
                    startActivity(new Intent(ReportIncident.this, Homepage.class));
                    return true;
                } else if (itemId == R.id.aboutus) {
                    Intent aboutIntent = new Intent(ReportIncident.this, AboutUs.class);
                    startActivity(aboutIntent);
                    return true;
                } else if (itemId == R.id.tips) {
                    Intent tipsIntent = new Intent(ReportIncident.this, MainActivity.class);
                    startActivity(tipsIntent);
                    // Handle Sub Option 1 click
                    return true;
                } else if (itemId == R.id.more) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://blog.ipleaders.in/emergency-india/"));
                    startActivity(browserIntent);
                    return true;
                } else if (itemId == R.id.community) {
                    Intent commIntent = new Intent(ReportIncident.this, CommunityActivity.class);
                    startActivity(commIntent);
                    return true;
                }
                return false;
            }
        });



        image = (ImageView) findViewById(R.id.image);
        video = (ImageView) findViewById(R.id.video);
        edit = (ImageView) findViewById(R.id.edit);
        submit = (Button) findViewById(R.id.submit);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

        autoCompleteTxt = findViewById(R.id.auto_complete_txt);

        adapterItems = new ArrayAdapter<String>(this, R.layout.list_item, items);

        autoCompleteTxt.setAdapter(adapterItems);

        autoCompleteTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();

                Toast.makeText(getApplicationContext(), "Item: " + item, Toast.LENGTH_SHORT).show();
            }
        });

        autoCompleteTxt1 = findViewById(R.id.urgency);

        adapterItems1 = new ArrayAdapter<String>(this, R.layout.urgent, levels);

        autoCompleteTxt1.setAdapter(adapterItems1);

        autoCompleteTxt1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int j, long l) {
                String item1 = adapterView.getItemAtPosition(j).toString();

                Toast.makeText(getApplicationContext(), "Item: " + item1, Toast.LENGTH_SHORT).show();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoCompleteTxt.setEnabled(true);
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Imgintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (Imgintent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(Imgintent, REQUEST_CODE_FOR_IMAGE);
                }
            }
        });

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Vidintent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if (Vidintent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(Vidintent, REQUEST_CODE_VIDEO_CAPTURE);
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(ReportIncident.this, android.Manifest.permission.SEND_SMS)
                        == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(ReportIncident.this, android.Manifest.permission.CALL_PHONE)
                        == PackageManager.PERMISSION_GRANTED) {
                    sendSMS();
                    makeCall();
                } else {
                    ActivityCompat.requestPermissions(ReportIncident.this, new String[]{android.Manifest.permission.SEND_SMS, android.Manifest.permission.CALL_PHONE},
                            REQUEST_CODE);
                }

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_FOR_IMAGE && resultCode == RESULT_OK) {
            //for image capture
            bitmap = (Bitmap) data.getExtras().get("data");
        }

        if (requestCode == REQUEST_CODE_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            //for video capture
            videoUri = data.getData();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(this, "Location permission is denied", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                sendSMS();
                makeCall();
            }
        }
    }

    private void getLastLocation() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();

        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null)
                {
                    currentLocation = location;
                }
            }
        });
    }

    private void sendSMS()
    {
        if(currentLocation != null)
        {
            String txt = autoCompleteTxt.getText().toString();
            String txt1 = autoCompleteTxt1.getText().toString();
            double lat = currentLocation.getLatitude();
            double lon = currentLocation.getLongitude();
            String message="http://maps.google.com/maps?saddr="+lat+","+lon;
            String number = "7745093563";

            StringBuffer smsBody = new StringBuffer();
            smsBody.append(Uri.parse(message));
            android.telephony.SmsManager.getDefault().sendTextMessage(number, null, "Incident: " + txt + "\n" + "Level of Urgency: " + txt1 + "\n" + smsBody.toString(), null,null);

            Toast.makeText(this, "success",Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(ReportIncident.this, MainActivity2.class);
            intent.putExtra("capturedImage", bitmap);
            if (videoUri != null) {
                intent.putExtra("capturedVideo", videoUri.toString());
            } else {
                // Handle the case where videoUri is null, if necessary
            }
            intent.putExtra("incident", txt);
            intent.putExtra("urgency", txt1);


            intent.putExtra("latitude", currentLocation.getLatitude());
            intent.putExtra("longitude", currentLocation.getLongitude());

            startActivity(intent);

            Toast.makeText(this, "Location sent via SMS", Toast.LENGTH_SHORT).show();

        }
        else
        {
            Toast.makeText(this, "Unable to retrieve current location", Toast.LENGTH_SHORT).show();
        }
    }

    private void makeCall()
    {
        String PHONE_NUMBER = "7745093563";

        Intent phoneintent = new Intent(Intent.ACTION_CALL);
        phoneintent.setData(Uri.parse("tel:" + PHONE_NUMBER));
        startActivity(phoneintent);
    }
}