package com.example.android.facedetection;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.FirebaseApp;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

public class MainActivity extends AppCompatActivity {

    //Whenever using your own custom permissions then we need to use a flg value(which is an integer) to have a check
    //whether the user has granted our pp that permission or not
    private final static int REQUEST_IMAGE_CAPTURE = 124;
    private InputImage image;
    private FaceDetector detector;

    @SuppressLint("QueryPermissionsNeeded")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        FirebaseApp.initializeApp(this);
        Button cameraButton = findViewById(R.id.camera_button);

        cameraButton.setOnClickListener(v -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            //Checking if the user has given correct permission to our app
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        });
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){

            assert data != null;
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");
            detectFace(bitmap);
        }
    }

    private void detectFace(Bitmap bitmap) {
        // High-accuracy landmark detection and face classification
        FaceDetectorOptions highAccuracyOpts =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                        .setMinFaceSize(0.15f)
                        .enableTracking()
                        .build();

        try {
            //To create an InputImage object from a Bitmap object
            //The image is represented by a Bitmap object together with rotation degrees.
            image = InputImage.fromBitmap(bitmap,0);

            //Get an instance of FaceDetector
            detector = com.google.mlkit.vision.face.FaceDetection.getClient(highAccuracyOpts);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        detector.process(image).addOnSuccessListener(faces -> {
            String resultText = "";

            //Integer i if there are more than 1 faces in the List
            int i = 1;

            //Looping thru the list named faces using for each loop
            for (Face ignored :faces) {
                i++;
            }
            resultText = resultText.concat("\n"+(i-1)+" Faces Detected");

            if (faces.size() == 0)
                Toast.makeText(MainActivity.this, "NO FACES", Toast.LENGTH_SHORT).show();
            else if(faces.size() == 1)
                Toast.makeText(MainActivity.this,"Face Detected",Toast.LENGTH_SHORT).show();
            else{
                Bundle bundle = new Bundle();
                bundle.putString(FaceDetection.RESULT_TEXT,resultText);
                DialogFragment resultDialog = new ResultDialog();
                resultDialog.setArguments(bundle);
                resultDialog.setCancelable(false);
                resultDialog.show(getSupportFragmentManager(), FaceDetection.RESULT_DIALOG);
            }

        });

    }
}
