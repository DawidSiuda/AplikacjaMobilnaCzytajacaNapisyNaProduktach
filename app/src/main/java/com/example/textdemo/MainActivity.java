 package com.example.textdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    TextView textView;
    DatabaseAccess databaseAccess;

    List<String> quotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //find imageview
        imageView = findViewById(R.id.imageId);
        //find textview
        textView = findViewById(R.id.textId);
        //check app level permission is granted for Camera
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            //grant the permission
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 101);
        }

        // Create database
//        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
//        databaseAccess.open();
//        quotes = databaseAccess.getQuotes();
//        databaseAccess.close();

        databaseAccess = DatabaseAccess.getInstance(this);

    }

    public void doProcess(View view) {
        //open the camera => create an Intent object
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bundle bundle = data.getExtras();
        //from bundle, extract the image
        Bitmap bitmap = (Bitmap) bundle.get("data");
        //set image in imageview
        imageView.setImageBitmap(bitmap);
        //process the image
        //1. create a FirebaseVisionImage object from a Bitmap object
        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap);
        //2. Get an instance of FirebaseVision
        FirebaseVision firebaseVision = FirebaseVision.getInstance();
        //3. Create an instance of FirebaseVisionTextRecognizer
        FirebaseVisionTextRecognizer firebaseVisionTextRecognizer = firebaseVision.getOnDeviceTextRecognizer();
        //4. Create a task to process the image
        Task<FirebaseVisionText> task = firebaseVisionTextRecognizer.processImage(firebaseVisionImage);
        //5. if task is success



        task.addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
//            @Override
//            public void onSuccess(FirebaseVisionText firebaseVisionText) {
//                recognizedText= firebaseVisionText.getText();
////                // Handle successful uploads on complete
////                downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
////                System.out.println("Upload completed");
////                listenr.onSuccess(downloadurl)
//            }

            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                String recognizedText = firebaseVisionText.getText();
                String outputText = "";

                // outputText += "=========================\nRecognized text: \n=========================\n" + recognizedText + "\n\n\n";

                Pattern p = Pattern.compile("E {0,1}\\d{3,4}");
                Matcher m = p.matcher(recognizedText);

                outputText += "=========================\nFound ingredients: \n=========================\n";
                databaseAccess.open();
                while(m.find()) {
                    String ingredient = m.group();
                    ingredient = ingredient.replaceAll("\\s", "");
                    String text = databaseAccess.getIngredient(ingredient);
                    // outputText += m.group() + " -> " + ingredient + "  -> "+  text +  ": \"description\"\n";
                    outputText += text + "\n";
                }
                databaseAccess.close();

                textView.setText(outputText);
            }
        });


        //6. if task is failure
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}