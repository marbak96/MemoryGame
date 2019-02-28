package com.example.marcin.memorygame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;

public class GameActivity extends AppCompatActivity {

    private ArrayList<String> pathToPictures = new ArrayList<String>();
    private int previousPosition = -1;
    private int oldPreviousPosition = -1;
    private ImageView previousImg = null;
    private ImageView oldPreviousImg = null;
    private int foundPicturesCounter = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        GridView gridview = (GridView) findViewById(R.id.gameView);
        gridview.setAdapter(new ImageAdapter(this));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) { //zwracanie pozycji
                ImageView img = (ImageView) v; //konwertacja
                loadImageFromStorage(pathToPictures.get(position), img); //ładowanie obrazka
                if (position != previousPosition && position != oldPreviousPosition) {
                    if (oldPreviousPosition != -1) {
                        if (previousPosition != -1) {
                            comparisonPicture(img, position); // Porównanie
                        } else {
                            previousPosition = position;
                            previousImg = img;
                            if (foundPicturesCounter == 2) {  // Dwa ostatnie
                                comparisonPicture(img, position);
                                Toast.makeText(GameActivity.this, "You win game!!!", Toast.LENGTH_SHORT).show();
                                View again = findViewById(R.id.again);
                                again.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
                        oldPreviousImg = img;
                        oldPreviousPosition = position;
                    }
                }

            }
        });

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        pathToPictures = bundle.getStringArrayList("pathToPictures");
        pathToPictures.add(pathToPictures.get(0));
        pathToPictures.add(pathToPictures.get(1));
        pathToPictures.add(pathToPictures.get(2));
        Collections.shuffle(pathToPictures); // Losowanie
    }

    private void loadImageFromStorage(String path, ImageView img) {

        try {
            File f = new File(path);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            img.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void comparisonPicture(ImageView img, int position) {  // Porównanie
        if (pathToPictures.get(previousPosition) == pathToPictures.get(oldPreviousPosition)) {
            foundPicturesCounter++;
            previousImg.setVisibility(View.GONE);
            oldPreviousImg.setVisibility(View.GONE);
        } else {
            previousImg.setImageDrawable(getResources().getDrawable(R.drawable.picture1 + previousPosition)); //przykrywanie
            oldPreviousImg.setImageDrawable(getResources().getDrawable(R.drawable.picture1 + oldPreviousPosition));
        }
        previousPosition = -1;
        oldPreviousPosition = position;
        previousImg = null;
        oldPreviousImg = img;
    }

    public void onRefresh(View view){
        recreate();
    }
}
