package com.android.fastandroidnetworkinglibdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;


public class WebSocketActivity extends AppCompatActivity {

    private static final String TAG = WebSocketActivity.class.getSimpleName();
    private TextView textView;
    private WebSocket webSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_socket);
        textView = (TextView) findViewById(R.id.textView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        connectWebSocket();
    }

    @Override
    protected void onStop() {
        super.onStop();
        disconnectWebSocket();
    }

    private void connectWebSocket() {
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build();

        Request request = new Request.Builder()
                .url("ws://echo.websocket.org")
                .build();
        webSocket = client.newWebSocket(request, getWebSocketListener());
    }

    private void disconnectWebSocket() {
        if (webSocket != null) {
            webSocket.cancel();
        }
    }

    private WebSocketListener getWebSocketListener() {
        return new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                webSocket.send("Hello...");
                webSocket.send("...World!");
                webSocket.send(ByteString.decodeHex("deadbeef"));
                webSocket.close(1000, "Goodbye, World!");
            }

            @Override
            public void onMessage(WebSocket webSocket, final String text) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.append("\n");
                        textView.append("MESSAGE: " + text);
                    }
                });
            }

            @Override
            public void onMessage(WebSocket webSocket,final ByteString bytes) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.append("\n");
                        textView.append("MESSAGE: " + bytes.hex());
                    }
                });
            }

            @Override
            public void onClosing(WebSocket webSocket,final int code,final String reason) {
                webSocket.close(1000, null);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.append("\n");
                        textView.append("CLOSE: " + code + " " + reason);
                    }
                });
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                t.printStackTrace();
            }
        };
    }
}
