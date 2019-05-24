package com.pu.facebook;

import android.Manifest;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class EditPostActivity extends AppCompatActivity {

    Button btnImage, btnSavePost;
    ImageView ivImage;
    File imageFile;
    Bitmap myBitmap;
    DatabaseHandler databaseHandler;
    String ImageName;
    public static int userid;
    EditText etPostDesc;
    String postDesc, PostId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        databaseHandler = new DatabaseHandler(this);
        ivImage = (ImageView) findViewById(R.id.ivPostImage);
        etPostDesc = (EditText) findViewById(R.id.etPost);
        postDesc = getIntent().getStringExtra("desc");
        ImageName = getIntent().getStringExtra("image");
        PostId = getIntent().getStringExtra("postid");
        etPostDesc.setText(postDesc);
        try {
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            // path to /data/data/yourapp/app_data/imageDir
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            File f = new File(directory.getAbsolutePath(), ImageName);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));

            ivImage.setImageBitmap(b);

        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }


        btnImage = (Button) findViewById(R.id.btnImage);
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int permission = ContextCompat.checkSelfPermission(EditPostActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                int permission1 = ContextCompat.checkSelfPermission(EditPostActivity.this,
                        Manifest.permission.CAMERA);
                int premission2 = ContextCompat.checkSelfPermission(EditPostActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);

                if (permission != PackageManager.PERMISSION_GRANTED || permission1 != PackageManager.PERMISSION_GRANTED || premission2 != PackageManager.PERMISSION_GRANTED) {
                    Log.i("data2", "Permission to record denied");

                    if (ActivityCompat.shouldShowRequestPermissionRationale(EditPostActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(EditPostActivity.this,
                            Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(EditPostActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(EditPostActivity.this);
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
                saveToInternalStorage(myBitmap);
                databaseHandler.editPost(postDesc, ImageName, Integer.parseInt(PostId));
                Intent i = new Intent(EditPostActivity.this, MainActivity.class);
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

                    Toast.makeText(EditPostActivity.this, "You have to grant CAMERA and SD card Write permission for image upload", Toast.LENGTH_SHORT).show();

                } else {

                    showImageOptions();
                }
                return;
            }


        }
    }

    private void showImageOptions() {

        final CharSequence[] options = {"Take Photo", "Choose from Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(EditPostActivity.this);
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

        if (resultCode == EditPostActivity.this.RESULT_OK) {

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
                    Bitmap bm = MediaStore.Images.Media.getBitmap(EditPostActivity.this.getContentResolver(), imageUri);
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

    private String saveToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());

        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        ImageName = randomString() + ".jpg";
        File mypath = new File(directory, ImageName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    public static String randomString() {
        String DATA = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        Random RANDOM = new Random();
        StringBuilder sb = new StringBuilder(5);

        for (int i = 0; i < 5; i++) {
            sb.append(DATA.charAt(RANDOM.nextInt(DATA.length())));
        }

        return sb.toString();
    }
}
