package com.example.mireaapp9;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.security.identity.DocTypeNotSupportedException;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String FILENAME_KEY = "filename";
    private static final String FILECONTENT_KEY = "filecontent";
    private static final String FILECONTENTFIELD_KEY = "filecontentfield";

    private int EXTERNAL_STORAGE_PERMISSION_CODE = 23;
    private TextInputEditText fileName;
    private TextInputEditText fileContent;
    private TextView fileContentField;

    private String filename;
    private String fileContents;
    private String fileContentFieldText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fileName = findViewById(R.id.fileName);
        fileContent = findViewById(R.id.fileContent);
        fileContentField = findViewById(R.id.fileContentField);

        Button saveButton = findViewById(R.id.saveButton);
        Button readButton = findViewById(R.id.readButton);
        Button deleteButton = findViewById(R.id.deleteButton);
        Button putButton = findViewById(R.id.addButton);

        if (savedInstanceState != null) {
            filename = savedInstanceState.getString(FILENAME_KEY);
            fileContents = savedInstanceState.getString(FILECONTENT_KEY);
            fileContentFieldText = savedInstanceState.getString(FILECONTENTFIELD_KEY);

            fileName.setText(filename);
            fileContent.setText(fileContents);
            fileContentField.setText(fileContentFieldText);
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fileName.getText().toString().matches("") && fileContent.getText().toString().matches("")) {
                    Toast.makeText(getApplicationContext(), "All empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (fileName.getText().toString().matches("")) {
                    Toast.makeText(getApplicationContext(), "Enter name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (fileContent.getText().toString().matches("")) {
                    Toast.makeText(getApplicationContext(), "Enter contents", Toast.LENGTH_SHORT).show();
                    return;
                }


                String filename = fileName.getText().toString();
                String fileContents = fileContent.getText().toString();

                Context context = getApplicationContext();

                try (FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE)) {
                    fos.write(fileContents.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();

                File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

                File file = new File(folder, filename);
                writeTextData(file, fileContents);

            }
        });


        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filename = fileName.getText().toString();

                if (filename.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Name empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                File file = new File(getFilesDir(), filename);

                if (file.exists()) {
                    StringBuilder text = new StringBuilder();
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(file));
                        String line;
                        while ((line = br.readLine()) != null) {
                            text.append(line);
                            text.append('\n');
                        }
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String fileData = text.toString();

                    fileContentField.setText(fileData);

                    Toast.makeText(getApplicationContext(), "Read", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "File doesnt exist", Toast.LENGTH_SHORT).show();
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filename = fileName.getText().toString();

                if (filename.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "File name empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Are you sure you want to delete '" + filename + "'?");
                builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File file = new File(getFilesDir(), filename);

                        if (file.exists()) {
                            if (file.delete()) { // Удаляем файл
                                Toast.makeText(getApplicationContext(), "File '" + filename + "' deleted", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "File doesnt exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("Нет", null);
                builder.show();
            }
        });

        putButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filename = fileName.getText().toString();
                String fileContents = fileContent.getText().toString();

                if (filename.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (fileContents.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                Context context = getApplicationContext();


                try (FileOutputStream fos = context.openFileOutput(filename, Context.MODE_APPEND)) {

                    fos.write(fileContents.getBytes());
                    fos.write("\n".getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(), "Info added to file", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void writeTextData(File file, String data) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(data.getBytes());
            Toast.makeText(this, "Done" + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void showPublicData(View view, String filename) {
        // Accessing the saved data from the downloads folder
        File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        // geeksData represent the file data that is saved publicly
        File file = new File(folder, filename);
        String data = getdata(file);
        if (data != null) {
            fileContentField.setText(data);
        } else {
            fileContentField.setText("No Data Found");
        }
    }

    private String getdata(File myfile) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(myfile);
            int i = -1;
            StringBuffer buffer = new StringBuffer();
            while ((i = fileInputStream.read()) != -1) {
                buffer.append((char) i);
            }
            return buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        filename = fileName.getText().toString();
        fileContents = fileContent.getText().toString();
        fileContentFieldText = fileContentField.getText().toString();
        outState.putString(FILENAME_KEY, filename);
        outState.putString(FILECONTENT_KEY, fileContents);
        outState.putString(FILECONTENTFIELD_KEY, fileContentFieldText);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        filename = savedInstanceState.getString(FILENAME_KEY);
        fileContents = savedInstanceState.getString(FILECONTENT_KEY);
    }
}