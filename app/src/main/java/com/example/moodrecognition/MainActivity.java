package com.example.moodrecognition;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    Button BSelectImage;

    // constant to compare
    // the activity result code
    int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // define button and set onclick listener to chooseImage function
        BSelectImage = findViewById(R.id.BSelectImage);

        BSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });
    }




    // this function is triggered when user selects an image
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // passes the image URI to the new activity as a string and starts new activity
        Uri selectedImageUri = data.getData();
        String stringUri = selectedImageUri.toString();
        if (selectedImageUri != null){
            // start analyze image activity and pass image data
            Intent intent = new Intent(MainActivity.this, AnalyzeFaceActivity.class);
            intent.putExtra("Image", stringUri);
            MainActivity.this.startActivity(intent);
        }
    }
}

