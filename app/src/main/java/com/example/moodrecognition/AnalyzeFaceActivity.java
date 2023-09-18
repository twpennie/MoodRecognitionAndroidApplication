package com.example.moodrecognition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.io.IOException;
import java.util.List;

public class AnalyzeFaceActivity extends AppCompatActivity {

    ImageView previewImage;
    String stringUri;
    Uri imageUri;
    InputImage image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze_face);
        previewImage = findViewById(R.id.previewImage);

        Log.d("onCreate", "Creation Successful");

        // get string Uri from intent
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("Image")) {
            stringUri = extras.getString("Image");
        }

        // convert string to URI and display imageURI on preview view
        imageUri = Uri.parse(stringUri);
        previewImage.setImageURI(imageUri);


        // initialize face detector
        try {
            initFaceDetect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initFaceDetect() throws IOException {

        // configure options for face detector model
        FaceDetectorOptions highAccuracyOpts =
                new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .build();

        // convert URI to InputImage to be passed to firebase model
        try {
            image = InputImage.fromFilePath(this.getApplicationContext(), imageUri);
        } catch (IOException e){
            e.printStackTrace();
        }

        // get instance of FaceDetector
        FaceDetector detector = FaceDetection.getClient(highAccuracyOpts);

        // process the image
        Task<List<Face>> result =
                detector.process(image)
                        .addOnSuccessListener(
                                new OnSuccessListener<List<Face>>() {
                                    @Override
                                    public void onSuccess(List<Face> faces) {
                                        // successfully detected face
                                        getFaceInfo(faces);
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        e.printStackTrace();
                                    }
                                });




    }

    private void getFaceInfo(List<Face> faces) {

        for (Face face : faces){
            Rect bounds = face.getBoundingBox();
            Log.d("getFaceInfo", "Face detected");
        }

    }
}