package com.pu.facebook;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.pu.facebook.database.DatabaseHandler;
import com.pu.facebook.database.PostRepo;
import com.pu.facebook.util.BitmapUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class AddPostActivity extends AppCompatActivity {

    Button btnImage, btnSavePost;
    ImageView ivImage;
    File imageFile;
    Bitmap myBitmap;
    DatabaseHandler databaseHandler;
    String ImageName;
    public static int userid;
    EditText etPostDesc;
    String postDesc;
    private PostRepo postRepo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        postRepo = new PostRepo(getApplicationContext());
        databaseHandler = new DatabaseHandler(this);
        ivImage = (ImageView) findViewById(R.id.ivPostImage);
        etPostDesc = (EditText) findViewById(R.id.etPost);
        btnImage = (Button) findViewById(R.id.btnImage);
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int permission = ContextCompat.checkSelfPermission(AddPostActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                int permission1 = ContextCompat.checkSelfPermission(AddPostActivity.this,
                        Manifest.permission.CAMERA);
                int premission2 = ContextCompat.checkSelfPermission(AddPostActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);

                if (permission != PackageManager.PERMISSION_GRANTED || permission1 != PackageManager.PERMISSION_GRANTED || premission2 != PackageManager.PERMISSION_GRANTED) {
                    Log.i("data2", "Permission to record denied");

                    if (ActivityCompat.shouldShowRequestPermissionRationale(AddPostActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(AddPostActivity.this,
                            Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(AddPostActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddPostActivity.this);
                        builder.setMessage("Permission to access the CAMERA and SD-CARD is required for AddPostActivity.this app.")
                                .setTitle("Permission required");

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {

                                makeRequest();

                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();

                    } else {

                        makeRequest();

                    }

                } else {
                    makeRequest();

                }
            }
        });

        btnSavePost = (Button) findViewById(R.id.btnSave);
        btnSavePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postDesc = etPostDesc.getText().toString();
                if (TextUtils.isEmpty(postDesc)) {
                    etPostDesc.setError("Invalid");
                    return;
                }
                ImageName = BitmapUtil.saveToInternalStorage(getApplicationContext(), myBitmap);
                postRepo.insertPost(postDesc, ImageName, userid);
                Intent i = new Intent(AddPostActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    protected void makeRequest() {
        requestPermissions(
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                300);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 300: {
                Log.d("llllll", "i am here fdhdf11");
                if (grantResults.length == 0
                        || grantResults[0] !=
                        PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(AddPostActivity.this, "You have to grant CAMERA and SD card Write permission for image upload", Toast.LENGTH_SHORT).show();

                } else {

                    showImageOptions();
                }
                return;
            }


        }
    }

    private void showImageOptions() {

        final CharSequence[] options = {"Take Photo", "Choose from Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(AddPostActivity.this);
        builder.setTitle("Upload Post Image");
        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 1);
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(
                            Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(
                            intent,
                            2);
                }
            }
        });
        builder.show();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                if (thumbnail != null) {
                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                }
                File destination = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpeg");
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                    imageFile = destination;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // byte[] b = bytes.toByteArray();
                myBitmap = BitmapFactory.decodeFile(destination.getAbsolutePath());
                ivImage.setImageBitmap(myBitmap);


            } else if (requestCode == 2) {
                Uri imageUri = data.getData();

                try {
                    Bitmap bm = MediaStore.Images.Media.getBitmap(AddPostActivity.this.getContentResolver(), imageUri);
                    int nh = (int) (bm.getHeight() * (512.0 / bm.getWidth()));
                    bm = Bitmap.createScaledBitmap(bm, 512, nh, true);
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    if (bm != null) {
                        bm.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                        System.out.print(bytes.size());
                    }
                    File destination = new File(Environment.getExternalStorageDirectory(),
                            System.currentTimeMillis() + ".jpeg");
                    FileOutputStream fo;
                    try {
                        destination.createNewFile();
                        fo = new FileOutputStream(destination);
                        fo.write(bytes.toByteArray());
                        fo.close();
                        imageFile = destination;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    myBitmap = BitmapFactory.decodeFile(destination.getAbsolutePath());
                    ivImage.setImageBitmap(myBitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
