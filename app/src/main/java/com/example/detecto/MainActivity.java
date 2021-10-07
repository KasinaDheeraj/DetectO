package com.example.detecto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.detecto.adapter.RVAdapter;
import com.example.detecto.data.MyDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private final int PICK_PHOTO_CODE=100;
    private static final String TESS_DATA = "tessdata";
    private static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/tesseract/";

    TessBaseAPI tessBaseAPI;
    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setStatusBarGradiant(this);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient_screen));

        promptPermission(2);

        Button capture=(Button) findViewById(R.id.readcapture);
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptPermission(1);
                startCapture();
            }
        });
        Button fromdir=(Button) findViewById(R.id.readfromim);
        fromdir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptPermission(3);
                startImageActivity();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        setUpRecyclerView("");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(loading!=null)
            loading.dismiss();
    }

    public static class DBResult{
        ArrayList<Integer> ids;
        ArrayList<String> Titles;
        ArrayList<String> body;
        public DBResult(ArrayList<Integer> i,ArrayList<String> t,ArrayList<String> b){
            ids=i;
            Titles=t;
            body=b;
        }
    }
    public void setUpRecyclerView(String q){
        MyDatabase db=new MyDatabase(this);
        DBResult dbr=db.getSearchRes(q);
        RVAdapter adapter =new RVAdapter(dbr.ids,dbr.Titles,dbr.body);
        adapter.setListener(new RVAdapter.Listener() {
            @Override
            public void onClickRV(int position) {
                db.deleteFromDB(position);
                Toast.makeText(MainActivity.this,"Delete Successful",Toast.LENGTH_SHORT).show();
                setUpRecyclerView("");
            }
        });
        RecyclerView rv=findViewById(R.id.recyclerview);
        LinearLayoutManager lm =new LinearLayoutManager(this);
        rv.setLayoutManager(lm);
        rv.setAdapter(adapter);
    }

    public void promptPermission(int c){
        switch(c) {
            case 1:
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                        Manifest.permission.CAMERA
                }, 99999);
            }
            break;
            case 2:
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, 9999);
            }
            break;
            case 3:
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE
                }, 999);
            }
            break;
        }

    }

    public void startCapture(){
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
        loading=new ProgressDialog(MainActivity.this);
        loading.setCancelable(false);
        loading.setMessage("Extracting Text...");
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loading.show();
            }
        },1000);
    }

    public void startImageActivity(){
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        if (intent.resolveActivity(getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                prepareTessData();
                startOCR(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }else if ((data != null) && requestCode == PICK_PHOTO_CODE) {
            Uri photoUri = data.getData();

            CropImage.activity(photoUri)
                    .start(MainActivity.this);
            loading=new ProgressDialog(MainActivity.this);
            loading.setCancelable(false);
            loading.setMessage("Extracting Text...");
            loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            loading.show();
        }
    }

    public void startOCR(Uri imageUri){
        try{
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            Bitmap bitmap =BitmapFactory.decodeFile(imageUri.getPath(), options);
            try {
                ExifInterface exif = new ExifInterface(imageUri.getPath());
                int exifOrientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL);

                Log.v("startOCR", "Orient: " + exifOrientation);

                int rotate = 0;

                switch (exifOrientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotate = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotate = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotate = 270;
                        break;
                }

                Log.v("StartOCR", "Rotation: " + rotate);

                if (rotate != 0) {

                    // Getting width & height of the given image.
                    int w = bitmap.getWidth();
                    int h = bitmap.getHeight();

                    // Setting pre rotate
                    Matrix mtx = new Matrix();
                    mtx.preRotate(rotate);

                    // Rotating Bitmap
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
                }

                // Convert to ARGB_8888, required by tess
                bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

            } catch (IOException e) {
                Log.e("startOCR", "Couldn't correct orientation: " + e.toString());
            }

            //The extraction which takes time so we use progress dialog
            String result = getTextTesseract(bitmap);

            if(result.equals("")){result ="No Text Detected!";}

            Intent intent =new Intent(this, TextActivity.class);
            Bundle extras=new Bundle();
            extras.putString("TITLE","");
            extras.putString("BODY",result);
            extras.putInt("id",-1);
            intent.putExtras(extras);
            loading.dismiss();
            startActivity(intent);
        }catch (Exception e){
            Log.e("STARTocr", e.getMessage());
        }
    }

    private String getTextTesseract(Bitmap bitmap){
        try{
            tessBaseAPI = new TessBaseAPI();
        }catch (Exception e){
            Log.e("getTesstext", e.getMessage());
        }
        tessBaseAPI.init(DATA_PATH,"eng");
        tessBaseAPI.setPageSegMode(TessBaseAPI.PageSegMode.PSM_AUTO);
        tessBaseAPI.setImage(bitmap);
        String retStr = "No result";
        try{
            retStr = tessBaseAPI.getUTF8Text();
        }catch (Exception e){
            Log.e("nn", e.getMessage());
        }
        tessBaseAPI.end();
        return retStr;
    }

    private void prepareTessData(){
        String lang="eng";
        AssetManager assetManager = getApplicationContext().getAssets();
        File file = new File(Environment.getExternalStorageDirectory()
                + "/tesseract/tessdata", lang + ".traineddata");
        if (!(file.exists())) {
            try {
                InputStream in = assetManager.open( lang+ ".traineddata");
                String sdCardPath = Environment.getExternalStorageDirectory()+ "/tesseract/tessdata";
                new File(sdCardPath).mkdirs();
                File outFile = new File(sdCardPath + "/", lang + ".traineddata");
                OutputStream out = new FileOutputStream(outFile);
                byte [] buff = new byte[1024];
                    int len ;
                    while(( len = in.read(buff)) > 0){
                        out.write(buff,0,len);
                    }
                    in.close();
                    out.close();
            } catch (IOException e) {
                Log.e("tag", "Failed to copy asset file: " + lang
                        + ".traineddata", e);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item,menu);
        MenuItem searchItem = menu.findItem(R.id.searchmenu);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint("Search Title");
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.Logoutmenu) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        }else if(item.getItemId()==R.id.addItemmenu){
            Intent intent =new Intent(MainActivity.this,TextActivity.class);
            Bundle extras=new Bundle();
            extras.putString("TITLE","");
            extras.putString("BODY","Description");
            extras.putInt("id",-1);
            intent.putExtras(extras);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        setUpRecyclerView(newText);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarGradiant(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            Drawable background = activity.getResources().getDrawable(R.drawable.gradient_screen);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setBackgroundDrawable(background);
        }
    }
}
