package com.example.android.facedetection;

import android.app.Application;
//import android.media.FaceDetector;

import com.google.firebase.FirebaseApp;
//import com.google.mlkit.vision.face.FaceDetectorOptions;

public class FaceDetection extends Application {
    public static final String RESULT_TEXT = "RESULT_TEXT";
    public static final String RESULT_DIALOG = "RESULT_DIALOG";

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }
}
