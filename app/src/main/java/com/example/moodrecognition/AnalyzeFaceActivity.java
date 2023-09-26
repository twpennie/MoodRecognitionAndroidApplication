package com.example.moodrecognition;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceContour;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class AnalyzeFaceActivity extends AppCompatActivity {

    ImageView previewImage;
    String stringUri;
    Uri imageUri;
    InputImage image;
    Bitmap imageBitmap;
    ArrayList<Rect> faceRectangles = new ArrayList<Rect>();
    TextView faceAnalysis;
    List<FaceContour> contour_list;

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

        // convert the image to a bitmap to use dimensions for drawing face rectangle
        imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

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
                                        for (Face face : faces){
                                            // all faces detected get added to a list of faces by default
                                            Rect bounds = face.getBoundingBox();
                                            faceRectangles.add(bounds);

                                            // TODO: Eventually add option to add pictures with multiple faces
                                            // then tap on that face to display that faces info
                                            Log.d("getFaceInfo", "Face detected");
                                            addFaceAnalysis(face);
                                        }
                                        drawRects();

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

    private void drawRects() {

        // create a Canvas for drawing rectangle around detected faces
        // background bitmap must be mutable for use in canvas
        Bitmap mutableBm = imageBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBm);

        for (Rect bounds : faceRectangles){
            // draw rectangle around face to show detection
            Paint myPaint = new Paint();
            myPaint.setColor(Color.rgb(0, 121, 107));
            myPaint.setStrokeWidth(15);
            myPaint.setStyle(Paint.Style.STROKE);

            canvas.drawRect(bounds, myPaint);

            LinearLayout rectangleLayout = (LinearLayout) findViewById(R.id.rectangleView);
            rectangleLayout.setBackground(new BitmapDrawable(mutableBm));
        }

    }

    // adds the face analysis stats to the textview
    private void addFaceAnalysis(Face face){
        faceAnalysis = findViewById(R.id.faceAnalysis);
        faceAnalysis.setTextColor(Color.WHITE);
        faceAnalysis.setTextSize(20);


        // for now this displays raw float values, TODO: change to have bar graphs
        String msg = "";
        msg += "Smiling Probability: " + face.getSmilingProbability() + "\n";
        msg += "Left Eye Open Probability: " + face.getLeftEyeOpenProbability() + "\n";
        msg += "Right Eye Open Probability: " + face.getRightEyeOpenProbability() + "\n";

        // TODO: also add a function to add face expression graphic based on smiling
        faceAnalysis.setText(msg);







    }

}