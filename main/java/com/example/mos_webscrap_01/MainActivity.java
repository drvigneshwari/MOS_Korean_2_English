package com.example.mos_webscrap_01;

//region Library Imports
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.jar.Attributes;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.io.IOException;
//endregion

public class MainActivity extends AppCompatActivity {

    //region Declaration
    private Button btn_Scrap;
    private TextView txt_ScrapResult;
    private TextView txt_FormattedResult;
    private TextView txt_list;
    private TextView txt_Count;
    private TextView txt_DBContent;

    private String sDBName ="";
    private String sDBURL ="";
    private String sServerName="";
    public Connection conn = null;
    private String sSQLQuery ="";
    private String[] sWords;

    DBAdapterClass dbHhelper;
    //endregion

    //region OnCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Initializing views
        initializeViews();

        //DB Helperclass
        dbHhelper = new DBAdapterClass(this);

        //region Scroll function
        txt_ScrapResult.setMovementMethod(new ScrollingMovementMethod());
        txt_FormattedResult.setMovementMethod(new ScrollingMovementMethod());
        txt_list.setMovementMethod(new ScrollingMovementMethod());
        txt_DBContent.setMovementMethod(new ScrollingMovementMethod());
        //endregion


        //Content Scrap
        btn_Scrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWebsite();
        }
        });
    }
    //endregion

    //region Content scraping
    private void getWebsite() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final StringBuilder builder = new StringBuilder();
                try {
                    Document doc = Jsoup.connect("https://news.naver.com/main/read.nhn?mode=LSD&mid=shm&sid1=101&oid=001&aid=0011575066").get();
                    String title = doc.title();
                    //Elements links = doc.select("a[href]");
                    Elements links = doc.select("div[class=_article_body_contents]");
                    builder.append(title).append("\n");

                    for (Element link : links) {
                        builder.append("\n").append(link.text());

                    }
                } catch (IOException e) {
                    builder.append("Error : ").append(e.getMessage()).append("\n");
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String sUnformattedText = builder.toString();
                        String sFormattedText="";

                        txt_ScrapResult.setText(sUnformattedText);
                        sFormattedText = formatContent(sUnformattedText);
                        txt_FormattedResult.setText(sFormattedText);
                        sWords = sFormattedText.trim().split("\\s+");
                        Arrays.toString(sWords);
                        for(int i = 0; i < sWords.length; i++) {
                            txt_list.append(sWords[i]);
                            txt_list.append("\n");
                        }
                        txt_Count.setText(String.valueOf(sWords.length));
                        addToDatabase();
                    }
                });
            }
        }).start();


    }
    //endregion

    //region Database
    //Add Data to SQLite
    public void addToDatabase(){


    //Toast.makeText(MainActivity.this, "Inside the addToDatabase function", Toast.LENGTH_SHORT).show();

        try{
            //Toast.makeText(MainActivity.this, "Calling InsertData function", Toast.LENGTH_SHORT).show();
            Log.d("INSERTION","insertData() is being executed");
            dbHhelper.insertData(sWords);

            //Toast.makeText(MainActivity.this, "Calling GetData function", Toast.LENGTH_SHORT).show();
            txt_DBContent.setText(dbHhelper.getData());
            Log.d("Retrieving Data from DB","getData() is being executed");
        }catch(Exception e){

        }

    }
    //endregion

    //region Binding Views
    private void initializeViews() {
        txt_ScrapResult = (TextView) findViewById(R.id.txt_ScrapResult);
        btn_Scrap = (Button) findViewById(R.id.btn_Scrap);
        txt_FormattedResult = (TextView) findViewById(R.id.txt_FormattedResult);
        txt_list = (TextView) findViewById(R.id.txt_list);
        txt_Count = (TextView) findViewById(R.id.txt_Count);
        txt_DBContent = (TextView) findViewById(R.id.txt_DBContent);
    }
    //endregion

    //region Data Cleaning
    public String formatContent(String sContent){
        String sUnformattedText = sContent;
        String sFormattedText;


        sFormattedText = sUnformattedText.replaceAll("\\d", "");
        sFormattedText = sFormattedText.replaceAll("[A-Za-z0-9]","");
        sFormattedText = sFormattedText.replaceAll("\\p{P}","");
        sFormattedText = sFormattedText.replaceAll("^\\w","");
        sFormattedText = sFormattedText.replaceAll("=","");

        return sFormattedText;
    }
    //endregion

}
