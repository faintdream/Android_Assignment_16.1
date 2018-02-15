package com.akashdubey.threaddemo1;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.Permission;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText input;
    TextView output;
    Button save, delete;
    int permission;
    boolean permissionResult = false;
    File path,dest;
    String text;
    StringBuilder stringBuilder= new StringBuilder();
    OutputStreamWriter outputStreamWriter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        input = (EditText) findViewById(R.id.inputET);
        output = (TextView) findViewById(R.id.outputTV);
        save = (Button) findViewById(R.id.saveBtn);
        delete = (Button) findViewById(R.id.deleteBtn);

        save.setOnClickListener(this);
        delete.setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= 20) {
            permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (permission != PackageManager.PERMISSION_GRANTED) {
                permissionResult = forcePermission();
            }
        }else{
            permissionResult=true;
        }


    }


    @Override
    public void onClick(View view) {

        if (permissionResult) {
            switch (view.getId()) {

                case R.id.saveBtn:
                    writeContent();
                    break;
                case R.id.deleteBtn:
                    deleteTheFile();
            }
        } else {
            Toast.makeText(this, "This App needs permission RW permission to operate", Toast.LENGTH_SHORT).show();
        }

    }

    void writeContent() {
         text=input.getText().toString();
         path= new File(String.valueOf(Environment.getExternalStorageDirectory()));

         // we need to convert file path toString(), otherwise file is never created by android
         dest=new File(path.toString(),"valenties.txt");
        try {
            FileOutputStream fileOutputStream=new FileOutputStream(dest,true);
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        new WriteContent().execute(dest);

    }

    void deleteTheFile() {
        new DeleteFile().execute(dest);
    }

    boolean forcePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }

    }

    public class WriteContent extends AsyncTask<File,Integer,File>{


        @Override
        protected File doInBackground(File... files) {

            try {
                FileOutputStream fileOutputStream= new FileOutputStream(files[0]);
                outputStreamWriter= new OutputStreamWriter(fileOutputStream);
                outputStreamWriter.write(text);
                outputStreamWriter.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return  dest;
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);

            try{
                BufferedReader bufferedReader= new BufferedReader(new FileReader(file));
                String line;
                while((line=bufferedReader.readLine())!=null){
                // note before we sue stringBuilder it needs to be initialized !
                    stringBuilder.append(line);
                    stringBuilder.append('\n');
                }

                bufferedReader.close();
                output.setText(stringBuilder.toString());
                Toast.makeText(MainActivity.this, "file"+file.toString()+"created", Toast.LENGTH_SHORT).show();
            }  catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class DeleteFile extends AsyncTask<File,Integer,String>{

        @Override
        protected String doInBackground(File... files) {

            if(getExternalFilesDir(files[0].toString()).exists()){
                files[0].delete();

            }
            return "Success";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Toast.makeText(MainActivity.this, "Delete File Response: "+s, Toast.LENGTH_LONG).show();
        }
    }
}
