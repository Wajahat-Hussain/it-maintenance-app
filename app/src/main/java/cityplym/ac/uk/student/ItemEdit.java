package cityplym.ac.uk.student;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import cityplym.ac.uk.student.camera.CameraActivity;
import cityplym.ac.uk.student.camera.CameraPreview;

public class ItemEdit extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        //Toast.makeText(this.getApplicationContext(), ""+Camera.getNumberOfCameras(), Toast.LENGTH_SHORT).show();

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if(permissionCheck == PermissionChecker.PERMISSION_DENIED){

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle("Camera permission necessary");
                alertBuilder.setMessage("Camera is required to read the boarcodes of items");
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(ItemEdit.this,
                                new String[]{Manifest.permission.CAMERA},
                                1);
                    }
                });

                AlertDialog alert = alertBuilder.create();
                alert.show();
            }else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //On user click ths "scan" button
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.fab) {// User has pressed the camera icon
            startCameraActivity();
        }
    }

    private void startCameraActivity(){
        Intent cam = new Intent(this, CameraActivity.class);
        startActivityForResult(cam, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if(data.hasExtra("val")){
                ((TextView)findViewById(R.id.resText)).setText(data.getExtras().getString("val"));
            }
            //Toast.makeText(this,data.getExtras().getString("val"),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 1){
            if(grantResults.length > 0){
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    return;
                }
            }
            Toast.makeText(this.getApplicationContext(), "Camera won't be usable without pemission", Toast.LENGTH_LONG).show();
            findViewById(R.id.fab).setEnabled(false);
        }
    }
}
