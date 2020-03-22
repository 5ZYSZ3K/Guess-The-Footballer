package com.example.guessthefootballerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    String[][] array = new String[2][25];
    String result = "";
    Bitmap btmap;
    Button[] answersButtons;
    int correctnumber;
    int answer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        correctnumber = 0;
        answersButtons = new Button[]{findViewById(R.id.answer1), findViewById(R.id.answer2), findViewById(R.id.answer3), findViewById(R.id.answer4)};
        answer = 1;
        textDownloader txtdownload = new textDownloader();
        try {
            result = txtdownload.execute("https://www.transfermarkt.pl/spieler-statistik/wertvollstespieler/marktwertetop").get();
        } catch(InterruptedException e){
            e.printStackTrace();
            result = null;
        } catch (ExecutionException e){
            e.printStackTrace();
            result = null;
        }
        String beginning = "https://tmssl.akamaized.net//images/portrait/small/";
        Pattern p = Pattern.compile(beginning+"(.*?)\" title=\"");
        Matcher m = p.matcher(result);
        int i=0;
        while(m.find() && i <25){
            array[0][i] = beginning+m.group(1);
            i++;
        }
        for(i=0; i<25; i++){
            String helper = array[0][i].substring(array[0][i].length()-6);
            Pattern a1 = Pattern.compile(helper+"\" title=\"(.*?)\" alt=\"");
            Matcher m1 = a1.matcher(result);
            while(m1.find()){
                array[1][i] = m1.group(1);
            }
            System.out.println(array[0][i]);
            System.out.println(array[1][i]);
        }
        /*

        for (int j = 0; j < 25; j++) {

        }*/
        losu(answersButtons[0]);
    }
    public void losu(View v){
        Button b = (Button)v;
        ImageView img = findViewById(R.id.footballerpicture);
        imageDownloader imgdownloader = new imageDownloader();
        if (b.getText().toString().equals(array[1][correctnumber])) {
            Toast.makeText(MainActivity.this, "Correct!", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(MainActivity.this, "Wrong! The correct answer was: "+array[1][correctnumber], Toast.LENGTH_SHORT).show();
        }
        correctnumber = (int)(Math.random()*25);
        try {
            btmap = imgdownloader.execute(array[0][correctnumber]).get();
            img.setImageBitmap(btmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int correctplace = (int)(Math.random()*4);
        for(int i=0; i<4; i++){
            if (i != correctplace) {
                if(Math.random()<0.5) {
                    answersButtons[i].setText(array[1][(int) (Math.random() * correctnumber)]);
                }
                else answersButtons[i].setText(array[1][(int) (Math.random() * (25-correctnumber)+correctnumber)]);
            }
            else {
                answersButtons[i].setText(array[1][correctnumber]);
            }
        }
    }
    public class imageDownloader extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls){
            URL url;
            HttpURLConnection urlconnection;
            try{
                url = new URL(urls[0]);
                urlconnection = (HttpURLConnection)url.openConnection();
                urlconnection.connect();
                InputStream inputstream = urlconnection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(inputstream);
                return myBitmap;
            } catch(Exception e){
                return null;
            }
        }
    }
    public class textDownloader extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls){
            String everything = "";
            URL url;
            HttpURLConnection urlconnection;
            try{
                url = new URL(urls[0]);
                urlconnection = (HttpURLConnection)url.openConnection();
                urlconnection.connect();
                InputStream in = urlconnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while(data != -1){
                    everything += (char) data;
                    data = reader.read();
                }
                return everything;
            } catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }
    }
}
