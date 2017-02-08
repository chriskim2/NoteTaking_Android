package com.example.donghyunkim.andr_final_project;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.io.File;
import java.net.URI;
import java.util.Calendar;

public class DetailActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {

    private GoogleMap mMap;
    private GoogleApiClient mLocationClient;
    private double lat = 43.642661;
    private double lng = -79.386242;

    DatabaseHelper myDB = new DatabaseHelper(this);

    int currentId = -1;
    ImageView iv = null;
    EditText et = null;
    Button btnPicture = null;
    Button btnDelete = null;
    Button btnMap = null;
    String strUri = "";
    String method = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        getSupportActionBar().setTitle("MEMO");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        iv = (ImageView)findViewById(R.id.iv);
        et = (EditText)findViewById(R.id.editText);
        btnPicture = (Button)findViewById(R.id.btnPicture);
        btnDelete = (Button)findViewById(R.id.btnDelete);
        btnMap = (Button)findViewById(R.id.btnMap);

        Intent intent = getIntent();
        method = intent.getStringExtra("method");

        if (method.equals("add")) {
            btnDelete.setVisibility(View.INVISIBLE);
            btnMap.setEnabled(false);
        }
        else if (method.equals("edit")) {
            editData(intent);
        }
    }

    @Override
    protected void onDestroy() {
        myDB.close();
        recycleBitmap(this.iv);
        super.onDestroy();
    }

    private static void recycleBitmap(ImageView iv) {
        Drawable d = iv.getDrawable();
        if (d != null) {
            if (d instanceof BitmapDrawable) {
                Bitmap b = ((BitmapDrawable) d).getBitmap();
                b = null;
            }

            d.setCallback(null);
        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Back button clicked!", Toast.LENGTH_SHORT).show();
        super.onBackPressed();
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            if (method.equals("edit")) {
                updateData();
            }
            else if (method.equals("add")) {
                insertData();
            }
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateData() {
        boolean isUpdated = myDB.update(currentId, strUri, et.getText().toString(), lat, lng);

        if (isUpdated == true) {
            Toast.makeText(DetailActivity.this, "Data updated", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(DetailActivity.this, "Data not updated", Toast.LENGTH_SHORT).show();
        }
    }

    public void editData(Intent intent) {
        currentId = intent.getIntExtra("id", 1);
        Memo m = myDB.getMemo(currentId);
        et.setText(m.getNote());
        this.lat = m.getLat();
        this.lng = m.getLng();
        strUri = m.getImgUri();

        if (!strUri.equals("")) {
            btnPicture.setEnabled(false);
            ViewGroup.LayoutParams params = iv.getLayoutParams();
            params.width = 400;
            params.height = 400;

            try {
                Bitmap bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(),
                            Uri.parse(strUri));
                iv.setImageBitmap(bm);

                /*
                BitmapDrawable drawable = (BitmapDrawable) iv.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                iv.setImageBitmap(rotateImage(bitmap, 90));
                */
            }
            catch(Exception e) {
                iv.setImageResource(R.drawable.cn_tower);
            }
        }
        else {
            btnMap.setEnabled(false);
        }
    }

    public void insertData() {
        Calendar calendar = Calendar.getInstance();
        String dateStr = calendar.getTime().toString();

        boolean isInserted = myDB.insertMemo(dateStr, strUri, et.getText().toString(),
                                                this.lat, this.lng);

        if (isInserted == true) {
            Toast.makeText(DetailActivity.this, "Data inserted", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(DetailActivity.this, "Data not inserted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ViewGroup.LayoutParams params = iv.getLayoutParams();
        params.width = 400;
        params.height = 400;

        Uri imgUri = data.getData();
        strUri = imgUri.toString();

        //iv.setImageURI(imgUri);

        try {
            Bitmap bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgUri);
            iv.setImageBitmap(bm);
        }
        catch (Exception e) {

        }

        /*
        BitmapDrawable drawable = (BitmapDrawable) iv.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        iv.setImageBitmap(rotateImage(bitmap, 90));
*/

        btnPicture.setEnabled(false);
        btnMap.setEnabled(true);
        connectLocationService();
    }

    private void connectLocationService() {
        mLocationClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mLocationClient.connect();
    }

    public Bitmap rotateImage(Bitmap src, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);

        return Bitmap.createBitmap(src, 0, 0, src.getWidth(),
                src.getHeight(), matrix, true);
    }

    public void deleteBtnClick(View view) {
        myDB.delete(currentId);
        finish();
    }

    public void pictureBtnClick(View view) {
        String manufacturer = Build.MANUFACTURER;

        if (manufacturer.equals("unknown")) {
            Toast.makeText(DetailActivity.this, "No camera found!", Toast.LENGTH_SHORT).show();

            strUri = "unknown";
            btnPicture.setEnabled(false);
            btnMap.setEnabled(true);
            ViewGroup.LayoutParams params = iv.getLayoutParams();
            params.width = 400;
            params.height = 400;
            iv.setImageResource(R.drawable.cn_tower);
            connectLocationService();
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, 1);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
        }
        Location currentLocation = LocationServices.FusedLocationApi
                .getLastLocation(mLocationClient);
        if (currentLocation == null) {
            Toast.makeText(this, "Couldn't connect Google-Map!", Toast.LENGTH_LONG).show();
        } else {
            this.lat = currentLocation.getLatitude();
            this.lng = currentLocation.getLongitude();
            Toast.makeText(this, "Latitude = " + lat +", Longitude = " + lng,
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    public void mapBtnClick(View view) {
        Intent intent = new Intent(DetailActivity.this, MapActivity.class);
        intent.putExtra("lat", this.lat);
        intent.putExtra("lng", this.lng);
        startActivity(intent);
    }
}
