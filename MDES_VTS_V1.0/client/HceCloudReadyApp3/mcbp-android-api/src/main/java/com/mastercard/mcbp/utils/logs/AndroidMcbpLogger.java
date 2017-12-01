/*
 * Copyright (c) 2016, MasterCard International Incorporated and/or its
 * affiliates. All rights reserved.
 *
 * The contents of this file may only be used subject to the MasterCard
 * Mobile Payment SDK for MCBP and/or MasterCard Mobile MPP UI SDK
 * Materials License.
 *
 * Please refer to the file LICENSE.TXT for full details.
 *
 * TO THE EXTENT PERMITTED BY LAW, THE SOFTWARE IS PROVIDED "AS IS", WITHOUT
 * WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NON INFRINGEMENT. TO THE EXTENT PERMITTED BY LAW, IN NO EVENT SHALL
 * MASTERCARD OR ITS AFFILIATES BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package com.mastercard.mcbp.utils.logs;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.mastercard.mcbp.api.BuildConfig;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;

public class AndroidMcbpLogger implements McbpLogger {
    /**
     * DEBUG_LOG flag
     */
    private final static boolean DEBUG_LOG = BuildConfig.BUILD_TYPE.equals("debug");

    /**
     * Enable / disable logging to a file. Logs are saved in /sdCard/mpsdk-logs/
     */
    private final static boolean FILE_LOG = BuildConfig.BUILD_TYPE.equals("debug");
    /**
     * Tag value
     */
    private final String mTag;
    /**
     * File
     */
    private File mLogFolder = null;
    /**
     * Application context
     */
    private final Context mApplicationContext;
    /**
     * Name of logger file
     */
    private static final String FILE_NAME = "debugInfo";

    /**
     * Default constructor
     */
    public AndroidMcbpLogger(final Object obj, final Object context) {
        if (obj == null) {
            this.mTag = "DefaultLog";
        } else {
            this.mTag = obj.getClass().getName();
        }

        this.mApplicationContext = (Context) context;

        if (DEBUG_LOG && FILE_LOG && isExternalStorageWritable()) {
            mLogFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                                  "/mpsdk-logs");
            if (!mLogFolder.exists()) {
                if (!mLogFolder.mkdirs()) {
                    System.out.println("Unable to create the log folder");
                }
            }
        } else {
            System.out.println("SD Card not available for read and write");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void i(final String message) {
        Log.i(mTag, message);
        processFileLog(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void e(final String message) {
        Log.e(mTag, message);
        processFileLog(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void d(final String message) {
        if (DEBUG_LOG) {
            // Process logs greater than 4096 bytes
            // For readability we split messages at 2048
            int maxSize = 2048;

            for (int start = 0; start < message.length(); start += maxSize) {
                String nextLine = "";
                if (start != 0) {
                    nextLine = "--> ";
                }
                nextLine += message.substring(start, Math.min(message.length(), start + maxSize));
                Log.d(mTag, nextLine);
            }
            processFileLog(message);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnabled() {
        return DEBUG_LOG;
    }

    private void processFileLog(final String message) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionStatus =
                    mApplicationContext.checkSelfPermission(Manifest.permission
                                                                    .READ_EXTERNAL_STORAGE);
            if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            permissionStatus =
                    mApplicationContext.checkSelfPermission(Manifest.permission
                                                                    .WRITE_EXTERNAL_STORAGE);
            if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        if (FILE_LOG) {
            File file = new File(mLogFolder, FILE_NAME + ".log");
            BufferedWriter bufferedWriter = null;
            try {
                bufferedWriter = new BufferedWriter(new FileWriter(file, true));
                String currentDateTimeString =
                        DateFormat.getDateTimeInstance().format(System.currentTimeMillis());
                bufferedWriter.append(currentDateTimeString).append(" ").append(mTag).append(":")
                              .append(message);
                bufferedWriter.newLine();
            } catch (IOException e) {
                System.out.println("Error in logging: " + e.getMessage());
            } finally {
                if (bufferedWriter != null) {
                    try {
                        bufferedWriter.close();
                    } catch (IOException e) {
                        System.out.println("Error in logging: " + e.getMessage());
                    }
                }
            }
        }
    }


    /**
     * Checks if external storage is available for read and write
     */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
}
