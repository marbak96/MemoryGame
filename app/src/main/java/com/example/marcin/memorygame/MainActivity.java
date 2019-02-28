package com.example.marcin.memorygame;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private int pictureNumber = 0;
    private int viewCounter = 0;
    private ArrayList<String> pathToPictures = new ArrayList<String>(); //Lista scieżek


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void buttonClick(View view) {
        dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            assert extras != null;
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            pathToPictures.add(saveToInternalStorage(imageBitmap)); // Zapisuje ścieżke
            loadImageFromStorage(pathToPictures.get(viewCounter));
        }
    }

    public void goToAnotherActivity(View view) { // Po przycisku play
        Intent intent = new Intent(MainActivity.this, GameActivity.class); // otwiera nowe aactivity
        intent.putExtra("pathToPictures", pathToPictures); // Przekazuje ścieżki do gameactivity
        startActivity(intent);
    }

    private String saveToInternalStorage(Bitmap bitmapImage) { //zapisuje obraz i zwraca ścieżke
        pictureNumber++; //zwiększanie liczby obrazków
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        File mypath = new File(directory, "picture" + pictureNumber + ".jpg"); //numerowanie

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
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
        return directory.getAbsolutePath() + "/picture" + pictureNumber + ".jpg"; //zwraca ścieżke
    }

    private void loadImageFromStorage(String path) {

        try {
            File f = new File(path);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            ImageView img = (ImageView) findViewById(R.id.imageView+ viewCounter); // Zwiększanie obrazków i przenoszenie
            viewCounter++;
            if(viewCounter >=3){ // Gdy mam 3 obrazki wyświetla guzik do gry
                View buttonPlay = findViewById(R.id.play);
                buttonPlay.setVisibility(View.VISIBLE);
                View pictureButton = findViewById(R.id.takePicture);
                pictureButton.setVisibility(View.GONE);
            }
            img.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
