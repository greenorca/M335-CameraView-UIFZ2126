package com.wiss.cameraview;

import static androidx.core.content.FileProvider.getUriForFile;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 22;

    Button btnpicture;

    ImageView imageView;

    private static final int SINGLE_CHOICE = android.R.layout.simple_list_item_single_choice;


    ActivityResultLauncher<Intent> activityResultLauncher;
    private ListView myListView;
    private ArrayAdapter<MyPictureFile> pictureFileArrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnpicture = findViewById(R.id.btncamera_id);
        imageView = findViewById(R.id.image);
        btnpicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                activityResultLauncher.launch(cameraIntent);
            }
        });

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Bundle extras = result.getData().getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                WeakReference<Bitmap> result_1 = new WeakReference<>(Bitmap.createScaledBitmap(imageBitmap,
                            imageBitmap.getWidth(), imageBitmap.getHeight(), false).
                            copy(Bitmap.Config.RGB_565, true));
                Bitmap bm = result_1.get();
                Uri imageUri = saveImage(bm, MainActivity.this);
                imageView.setImageURI(imageUri);
            }
        });

        myListView = findViewById(R.id.listView);
        myListView.setOnItemClickListener((v,adapter, index,id)->{
            MyPictureFile selection = (MyPictureFile)v.getItemAtPosition(index);
            if (selection==null)
                return;
            Uri uri = getUriForFile(MainActivity.this,
                    getPackageName()+".fileprovider",
                    selection.getFile()
                    );
            imageView.setImageURI(uri);
                });
        listPictureFiles(MainActivity.this);
    }

    private void listPictureFiles(Context context){

        File dir = context.getFilesDir();
        File[] files = dir.listFiles();
        List<MyPictureFile> imgFiles = new ArrayList<>();
        for (File f : files){

            if (f.getName().endsWith("my_images")){
                for ( File img : f.listFiles()){
                    imgFiles.add(new MyPictureFile(img));
                }
            }

        }
        pictureFileArrayAdapter = new ArrayAdapter<>(this, SINGLE_CHOICE, imgFiles);
        myListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        myListView.setAdapter(pictureFileArrayAdapter);

    }

    private void onActivityResult(Intent data) {
        Bundle extras = data.getExtras();
        Bitmap imageBitmap = (Bitmap) extras.get("data");
        imageView.setImageBitmap(imageBitmap);
    }

    private Uri saveImage(Bitmap image, Context context) {
        File imagefolder = new File(context.getFilesDir(), "my_images");
        Uri contentUri = null;
        try {
            imagefolder.mkdirs();
            String fileName = (new Date().toString()+".jpg").replaceAll(" ","-");
            File file = new File(imagefolder, "IMG_"+fileName);
            FileOutputStream stream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.flush();
            stream.close();
            contentUri = getUriForFile(context,
                    context.getPackageName()+".fileprovider", file);
            pictureFileArrayAdapter.add(new MyPictureFile(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentUri;
    }
}

