package com.example.javed.thermalcomfort;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity
{

    private static SeekBar seek_bar;
    private static TextView text_view;
    private static EditText user_id;
    private boolean threadcomplete = false;
    private boolean connectionstatus = false;


    private static final String hostname="192.168.1.117";
    private static final int portnumber = 61000;

    private Socket socket = null;

    private static final String debugString = "debug";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        new Thread()
        {
            @Override
            public void run() {

                try
                {
                    //connecting
                    Log.i(debugString, "Attempting to connect");
                    socket = new Socket(hostname, portnumber);
                    Log.i(debugString, "Connection established");
                    connectionstatus = true;

                    //Send message to server
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    bw.write("This is a message from client.");
                    bw.newLine();
                    bw.flush();

                    //Receive message from server
                    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    System.out.println("Message from the server: " + br.readLine());


                }
                catch (IOException e)
                {

                    Log.e(debugString, e.getMessage());

                    connectionstatus = false;
                }
                finally
                {
                    threadcomplete = true;

                }
            }

        }.start();

        seekbbarr();


//        while (!threadcomplete)
//            continue;
//        if (connectionstatus)
//        {
//
//            Toast.makeText(MainActivity.this, "Connected Successfully!", Toast.LENGTH_SHORT).show();
//
//        }
//        else
//        {
//
//            Toast.makeText(MainActivity.this,"Failed to Connect.", Toast.LENGTH_SHORT).show();
//
//        }

    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }


    public void seekbbarr()
    {
        seek_bar = (SeekBar)findViewById(R.id.seekBar);
        text_view = (TextView)findViewById(R.id.textView);
        text_view.setText("Level: "+ (seek_bar.getProgress()));//-50)/10);


        seek_bar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener()
                {
                    int thermal_value;
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
                    {
                        thermal_value = progress;
                        text_view.setText("Level: "+ (progress));//-50)/10);
                        //    Toast.makeText(MainActivity.this,"SeekBar in Progress",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar)
                    {
                        //  Toast.makeText(MainActivity.this,"SeekBar in StartTracking",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar)
                    {
                        text_view.setText("Level: "+ (thermal_value));//-50)/10);
                        //   Toast.makeText(MainActivity.this,"SeekBar in StopTracking",Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    public void onSubmit(View v)
    {
        BufferedWriter bw = null;
        BufferedReader br = null;
        user_id = (EditText) findViewById(R.id.editText);


        //Send message to server
        if (connectionstatus)
        {

            try {

                //Send data to server
                String level = String.valueOf(seek_bar.getProgress());
                bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                bw.write(user_id.getText().toString() + " " + level);
                bw.newLine();
                bw.flush();

                //Show "Submitted Successfully" message
                Toast.makeText(MainActivity.this, "Submitted Successfully", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                Log.e(debugString, e.getMessage());

                //Show "failed to connect" message
                Toast.makeText(MainActivity.this, "Failed to connect", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {

            Toast.makeText(MainActivity.this, "Failed to Connect.", Toast.LENGTH_SHORT).show();

        }

    }

    public void onReset(View v)
    {

        seek_bar.setProgress(50);
        seek_bar.refreshDrawableState();

    }
}
