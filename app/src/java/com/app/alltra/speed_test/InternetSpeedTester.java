package com.app.alltra.speed_test;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.app.alltra.R;
import com.app.alltra.utils.Dialogs;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ContentHandler;
import java.net.URL;
import java.net.URLConnection;

public class InternetSpeedTester {
    private static final int BUFFER_SIZE = 8192; // Size of the buffer for reading/writing data
    private static final int TIMEOUT = 5000; // Timeout for establishing the connection (in milliseconds)
    private static final String PING_HOST = "8.8.8.8"; // Replace with the host for ping test
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    public static void measureSpeed(final SpeedTestListener listener) {
        Thread thread = new Thread(() -> {
            SpeedTestResult result = new SpeedTestResult();

            try {
                // Measure download speed
                long startTime = System.currentTimeMillis();
                //TODO the URL doesn't work on opera
                URL downloadUrl = new URL("https://speed.hetzner.de/100MB.bin"); // Replace with the URL of a sample file to download
                URLConnection downloadConnection = downloadUrl.openConnection();
                downloadConnection.setConnectTimeout(TIMEOUT);

                int totalBytesRead = 0;
                try (InputStream inputStream = new BufferedInputStream(downloadConnection.getInputStream(), BUFFER_SIZE)) {
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        // Read data from the input stream
                        // You can process the data here if needed
                        // For example, let's calculate the total number of bytes read
                        totalBytesRead += bytesRead;
                    }
                }

                long endTime = System.currentTimeMillis();
                long downloadTime = endTime - startTime;
                double downloadSpeed = calculateSpeed(totalBytesRead, downloadTime);

                result.setDownloadSpeed(downloadSpeed);

                // Measure upload speed
                startTime = System.currentTimeMillis();
                URL uploadUrl = new URL("https://www.ovh.net/files/1Gio.dat"); // Replace with the URL for uploading data
                URLConnection uploadConnection = uploadUrl.openConnection();
                uploadConnection.setConnectTimeout(TIMEOUT);
                uploadConnection.setDoOutput(true);

                try (OutputStream outputStream = new BufferedOutputStream(uploadConnection.getOutputStream(), BUFFER_SIZE)) {
                    byte[] buffer = new byte[BUFFER_SIZE];
                    // Write data to the output stream
                    outputStream.write(buffer);
                    outputStream.flush();
                }

                endTime = System.currentTimeMillis();
                long uploadTime = endTime - startTime;
                double uploadSpeed = calculateSpeed(BUFFER_SIZE, uploadTime);

                result.setUploadSpeed(uploadSpeed);

                // Measure ping
                long pingTime = measurePing();

                result.setPingTime(pingTime);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("SpeedTest", Log.getStackTraceString(e));
                // Show error dialog on the main thread
                new Handler(Looper.getMainLooper()).post(() -> Dialogs.showErrorDialog(context, context.getString(R.string.generic_error)));
            }

            new Handler(Looper.getMainLooper()).post(() -> listener.onSpeedTestCompleted(result));
        });
        thread.start();
    }

    private static double calculateSpeed(int dataSize, long timeInMillis) {
        double bytesPerSecond = (double) dataSize / (timeInMillis / 1000.0);
        return (bytesPerSecond / 1024.0) * 8.0; // Convert to kilobits per second (Kbps)
    }

    private static long measurePing() throws IOException {
        long startTime = System.currentTimeMillis();

        Process process = Runtime.getRuntime().exec("ping -c 4 " + PING_HOST);
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }

    public static void setContext(Context context) {
        InternetSpeedTester.context = context;
    }

    public static class SpeedTestResult {
        private double downloadSpeed;
        private double uploadSpeed;
        private long pingTime;

        public long getPingTime() {
            return pingTime;
        }

        public void setPingTime(long pingTime) {
            this.pingTime = pingTime;
        }
        public double getDownloadSpeed() {
            return downloadSpeed;
        }

        public void setDownloadSpeed(double downloadSpeed) {
            this.downloadSpeed = downloadSpeed;
        }

        public double getUploadSpeed() {
            return uploadSpeed;
        }

        public void setUploadSpeed(double uploadSpeed) {
            this.uploadSpeed = uploadSpeed;
        }
    }

    public interface SpeedTestListener {
        void onSpeedTestCompleted(SpeedTestResult result);
    }
}