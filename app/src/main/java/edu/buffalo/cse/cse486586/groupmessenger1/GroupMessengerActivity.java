package edu.buffalo.cse.cse486586.groupmessenger1;

import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.Context;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import static android.widget.Toast.*;
import static edu.buffalo.cse.cse486586.groupmessenger1.R.id.button4;

/**
 * GroupMessengerActivity is the main Activity for the assignment.
 *
 * @author stevko
 *
 */
public class GroupMessengerActivity extends Activity {

    final Context context = this;
    static final int SERVER_PORT = 10000;
    public static  int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_messenger);


        /*
         * TODO: Use the TextView to display your messages. Though there is no grading component
         * on how you display the messages, if you implement it, it'll make your debugging easier.
         */
        TextView tv = (TextView) findViewById(R.id.textView1);
        tv.setMovementMethod(new ScrollingMovementMethod());
        
        /*
         * Registers OnPTestClickListener for "button1" in the layout, which is the "PTest" button.
         * OnPTestClickListener demonstrates how to access a ContentProvider.
         */
        findViewById(R.id.button1).setOnClickListener(
                new OnPTestClickListener(tv, getContentResolver()));





        TelephonyManager tel = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
        final String myPort = String.valueOf((Integer.parseInt(portStr) * 2));
        try {
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            new ServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverSocket);
        } catch (IOException e) {
            //e.printStackTrace();
            Log.e("E", "Can't create a ServerSocket");
            return;
        }

        final EditText t = (EditText)findViewById(R.id.editText1);

        final View.OnClickListener buttonclick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String msg = t.getText().toString() + "\n" ;


                t.setText(""); // This is one way to reset the input box.
                TextView localTextView = (TextView) findViewById(R.id.textView1);
                localTextView.append("\t" + msg); // This is one way to display a string.
                //TextView remoteTextView = (TextView) findViewById(R.id.textView1);
                //remoteTextView.append("\n");

                //   Uri uri = getContentResolver().insert(GroupMessengerProvider.CONTENT_URI,objcontentValues);
                new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, msg, myPort);
            }
        };

        findViewById(button4).setOnClickListener(buttonclick);





        
        /*
         * TODO: You need to register and implement an OnClickListener for the "Send" button.
         * In your implementation you need to get the message from the input box (EditText)
         * and send it to other AVDs.
         */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_group_messenger, menu);
        return true;
    }


    private class ServerTask extends AsyncTask<ServerSocket, String, Void> {

        @Override
        protected Void doInBackground(ServerSocket... sockets) {
            ServerSocket serverSocket = sockets[0];
            Socket socket = null;
            String message = null;
            boolean status = true;
            //  do {
            try {
                while(status) {
                    socket = serverSocket.accept();
                    ObjectInputStream inputstream = new ObjectInputStream(socket.getInputStream());
                    String m = (String) inputstream.readObject();
                    ContentValues c = new ContentValues();
                    c.put("key",Integer.toString(count));
                    c.put("value",m);
                    count++;
                    //  alert(m.get("key").toString());
                    Uri uri = getContentResolver().insert(GroupMessengerProvider.CONTENT_URI, c);
                }
            } catch (IOException e) {
                Log.e("E", "IOException");
            } catch (ClassNotFoundException e) {
                Log.e("E", "ClassNotFoundException");
            }
            // } while(!socket.isInputShutdown());
            return null;
        }


    }

    public void alert(String param){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        alertDialogBuilder.setMessage(param);
        AlertDialog alert11 = alertDialogBuilder.create();
        alert11.show();
    }


    private class ClientTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... msgs) {
            try {
                String[] arr = new String[]{"11108","11112","11116","11120","11124"};

                for(int i = 0 ; i < arr.length ; i++){
                    Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(arr[i]));

                    //String msgToSend = msgs[0];
                    ObjectOutputStream outputstream = new ObjectOutputStream(socket.getOutputStream());
                    outputstream.writeObject(msgs[0]);

                    outputstream.flush();
                    socket.close();


                }


            } catch (UnknownHostException e) {
                Log.e("E", "ClientTask UnknownHostException");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("E", "ClientTask socket IOException");
            }

            return null;
        }
    }
}