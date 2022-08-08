package com.example.musicplayer.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musicplayer.R;
import com.example.musicplayer.controll_alarm.NoteActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class setBackground extends AppCompatActivity {

    ImageView imageView;
    TextView addPhotoBtn, btnBack;
    Button btnSave;
    private boolean haveImage;
    private Uri imageUri;
    File file;
    final int REQUEST_PICK_IMAGE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        haveImage = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_background);

        addPhotoBtn = findViewById(R.id.addPhotoBtn);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);
        imageView = findViewById(R.id.photoView);

        if (!haveImage)
            btnSave.setEnabled(false);

        addPhotoBtn.setOnClickListener(view -> {
            haveImage = true;
            final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            final Intent pickIntent = new Intent(Intent.ACTION_PICK);
            pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            final Intent chooserIntent = Intent.createChooser(intent, "Select Image");
            startActivityForResult(chooserIntent, REQUEST_PICK_IMAGE);

            btnSave.setEnabled(true);
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(setBackground.this, MainActivity.class);
                startActivity(in);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetBackground();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if(data == null){
                return;
            }
            imageUri = data.getData();
            imageView.setImageURI(imageUri);

            file = new File(getRealPathFromURI(imageUri));
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);

            cursor.moveToFirst();

            String name = (cursor.getString(nameIndex));
            String size = (Long.toString(cursor.getLong(sizeIndex)));
            File files = new File(this.getFilesDir(), name);

            try {
                InputStream inputStream = this.getContentResolver().openInputStream(contentURI);
                FileOutputStream outputStream = new FileOutputStream(files);
                int read = 0;
                int maxBufferSize = 1 * 1024 * 1024;
                int bytesAvailable = inputStream.available();

                //int bufferSize = 1024;
                int bufferSize = Math.min(bytesAvailable, maxBufferSize);

                final byte[] buffers = new byte[bufferSize];
                while ((read = inputStream.read(buffers)) != -1) {
                    outputStream.write(buffers, 0, read);
                }
                inputStream.close();
                outputStream.close();
            } catch (Exception e) {
                Log.e("Exception", e.getMessage());
            }
            return files.getPath();
        }
    }

    private void SetBackground() {
        if(file == null)
            Toast.makeText(this, "Please choose a Image.",Toast.LENGTH_SHORT).show();
        else {
            final AlertDialog.Builder alert = new AlertDialog.Builder(this);
            View mView = getLayoutInflater().inflate(R.layout.dialog_set_background,null);
            View view = getLayoutInflater().inflate(R.layout.activity_main,null);
            alert.setView(mView);

            Button btnSaveMusic = (Button)mView.findViewById(R.id.btnSaveMusic);
            Button btnSaveAlarm = (Button)mView.findViewById(R.id.btnSaveAlarm);
            Button btnSaveAll = (Button)mView.findViewById(R.id.btnSaveAll);

            final AlertDialog alertDialog = alert.create();
            alertDialog.setCanceledOnTouchOutside(false);

            SharedPreferences share = getSharedPreferences("background",MODE_PRIVATE);
            SharedPreferences.Editor editor = share.edit();

            Intent in = new Intent(setBackground.this, MainActivity.class);
            btnSaveMusic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    editor.putString("background",file.getPath());
                    editor.apply();

                    Intent in = new Intent(setBackground.this, MainActivity.class);
                    Toast.makeText(setBackground.this,"Thiết lập thành công", Toast.LENGTH_SHORT).show();
                    startActivity(in);

                }
            });

            btnSaveAlarm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    editor.putString("background",file.getPath());
                    editor.apply();

                    Intent in = new Intent(setBackground.this, NoteActivity.class);
                    Toast.makeText(setBackground.this,"Thiết lập thành công", Toast.LENGTH_SHORT).show();
                    startActivity(in);

                }
            });

            btnSaveAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    editor.putString("background",file.getPath());
                    editor.apply();

                    Intent in = new Intent(setBackground.this, MainActivity.class);
                    Toast.makeText(setBackground.this,"Thiết lập thành công", Toast.LENGTH_SHORT).show();
                    startActivity(in);

                }
            });

            Window window = alertDialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);
            alertDialog.show();
        }
    }
}