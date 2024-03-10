package com.example.se2einzelaufgabe;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private String matrikelnummer;
    private TextView inputMatrikelnummer;
    private final String serverName = "se2-submission.aau.at";
    private final int serverPort = 20080;
    private Button button;
    private TextView answerServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inputMatrikelnummer=findViewById(R.id.inputMatrikelnummer);
        button=findViewById(R.id.button1);
        answerServer=findViewById(R.id.textView4);

        // Button-Klick-Ereignis
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectServer();
            }
        });


    }
    private void connectServer () {

        matrikelnummer=inputMatrikelnummer.getText().toString();

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    //Verbindung zum Server herstellen
                    Socket socket =  new Socket(serverName, serverPort);

                    //Eingabe zum Server schicken
                    DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    out.println(matrikelnummer);

                    //Antwort vom Server empfangen
                    BufferedReader inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String response = inputStream.readLine();

                    outputStream.close();
                    out.close();
                    inputStream.close();
                    socket.close();

                    // Update UI on the main thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            answerServer.setText(response);
                        }
                    });

                } catch (Exception e) {
                    // Update UI on the main thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            answerServer.setText("Error: " + e.getMessage());
                        }
                    });
                }


            }
        }).start();
    }

}