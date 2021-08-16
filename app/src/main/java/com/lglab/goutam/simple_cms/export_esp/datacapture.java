package com.lglab.goutam.simple_cms.export_esp;

import android.content.SharedPreferences;
import android.util.Log;

import com.lglab.goutam.simple_cms.R;
import com.lglab.goutam.simple_cms.create.action.CreateStoryBoardActionLocationActivity;
import com.lglab.goutam.simple_cms.dialog.CustomDialogUtility;
import com.lglab.goutam.simple_cms.utility.ConstantPrefs;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class datacapture {
    public void capture(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                DatagramSocket udpSocket = null;
                try {
//                    SharedPreferences sharedPreferences = getSharedPreferences(ConstantPrefs.SHARED_PREFS.name(), MODE_PRIVATE);
//                    String hostPort = sharedPreferences.getString(ConstantPrefs.URI_TEXT.name(), "");
//                        int port = Integer.parseInt(hostNPort[1]);
                    int port = 1234;
                    Log.d("port no is", String.valueOf(port));
                    String msg = "";
                    udpSocket = new DatagramSocket(port);
                    byte[] message = new byte[8000];
                    Log.d("UDP client: ", "about to wait to receive");
                    Log.d("port no is ", String.valueOf(udpSocket.getLocalPort()));
                    DatagramPacket packet = new DatagramPacket(message, message.length);
                    udpSocket.receive(packet);
                    String text = new String(message, 0, packet.getLength());
                    udpSocket.setReuseAddress(true);
                    udpSocket.close();
                    msg = text;
                    Log.d("Received data", msg);
                    message = new byte[8000];
                }
//                catch (BindException e) {
//                    CustomDialogUtility.showDialog(CreateStoryBoardActionLocationActivity.this,
//                            getResources().getString(R.string.getting_unwanted_error_with_port));
//                    e.printStackTrace();
//                } catch (SocketException e) {
//                    CustomDialogUtility.showDialog(CreateStoryBoardActionLocationActivity.this,
//                            getResources().getString(R.string.unable_to_access_the_port));
//                    e.printStackTrace();
//                }
                catch (IOException e) {
                    Log.e("UDP client has IOException", "error: ", e);
                }
            }

        });
        thread.start();
    }
}
