/*
 *  Copyright (c) 2017, MasterCard International Incorporated and/or its
 *  affiliates. All rights reserved.
 *
 *  The contents of this file may only be used subject to the MasterCard
 *  Mobile Payment SDK for MCBP and/or MasterCard Mobile MPP UI SDK
 *  Materials License.
 *
 *  Please refer to the file LICENSE.TXT for full details.
 *
 *  TO THE EXTENT PERMITTED BY LAW, THE SOFTWARE IS PROVIDED "AS IS", WITHOUT
 *  WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 *  WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NON INFRINGEMENT. TO THE EXTENT PERMITTED BY LAW, IN NO EVENT SHALL
 *  MASTERCARD OR ITS AFFILIATES BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 *  IN THE SOFTWARE.
 */
package com.comviva.hceservice.common;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.mastercard.mpsdk.componentinterface.McbpLogger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;

public class DefaultMcbpLogger implements McbpLogger {

    /**
     * Name of logger file
     */
    private static final String FILE_NAME = "debugInfo";

    private static final String NULL_MESSAGE = "Log message is empty/null";

    /**
     * Enable / disable logging to a file. Logs are saved in /sdCard/mpsdk-logs/
     */
    private static boolean FILE_LOG = true;

    /**
     * Tag value
     */
    private final String mTag;
    private final Context mApplicationContext;
    /**
     * File
     */
    private File mLogFolder = null;

    /**
     * Flag if write permission is denied, previous to android 6.0
     */
    private static boolean WRITE_PERMISSION_DENIED;

    /**
     * Default constructor
     */
    public DefaultMcbpLogger(final String tag, Context applicationContext) {
        if (tag == null) {
            mTag = "DefaultLog";
        } else {
            mTag = tag;
        }

        mApplicationContext = applicationContext;

        if (FILE_LOG && isExternalStorageWritable()) {
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

    @Override
    public void d(String message, Object... args) {
        if (TextUtils.isEmpty(message)) {
            Log.d(getCallerMethodInfo("d"), NULL_MESSAGE);
            processFileLog(NULL_MESSAGE);
            return;
        }
 /*       String formattedMessage = String.format(message, args);
        int maxLogSize = 1000;
        for (int i = 0; i <= formattedMessage.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i + 1) * maxLogSize;
            end = end > formattedMessage.length() ? formattedMessage.length() : end;
            Log.d(getCallerMethodInfo("d"), formattedMessage.substring(start, end));
        }
        processFileLog(formattedMessage);*/
    }

    @Override
    public void e(Throwable throwable, String message, Object... args) {

        if (throwable != null) {
            Log.e(getCallerMethodInfo("e"), throwable.getMessage(), throwable);
            processFileLog(throwable.getMessage());
        }

        if (TextUtils.isEmpty(message)) {
            Log.e(getCallerMethodInfo("e"), NULL_MESSAGE);
            processFileLog(NULL_MESSAGE);
            return;
        }

        String formattedMessage = String.format(message, args);
        Log.e(getCallerMethodInfo("e"), formattedMessage);
        processFileLog(formattedMessage);
    }

    @Override
    public void e(String message, Object... args) {
        if (TextUtils.isEmpty(message)) {
            Log.e(getCallerMethodInfo("e"), NULL_MESSAGE);
            processFileLog(NULL_MESSAGE);
            return;
        }

        String formattedMessage = String.format(message, args);

        Log.e(getCallerMethodInfo("e"), formattedMessage);
        processFileLog(formattedMessage);

    }

    @Override
    public void i(String message, Object... args) {
        if (TextUtils.isEmpty(message)) {
            Log.i(getCallerMethodInfo("i"), NULL_MESSAGE);
            processFileLog(NULL_MESSAGE);
            return;
        }

        String formattedMessage = String.format(message, args);

        Log.i(getCallerMethodInfo("i"), formattedMessage);
        processFileLog(formattedMessage);

    }

    @Override
    public void v(String message, Object... args) {
        if (TextUtils.isEmpty(message)) {
            Log.v(getCallerMethodInfo("v"), NULL_MESSAGE);
            processFileLog(NULL_MESSAGE);
            return;
        }
        String formattedMessage = String.format(message, args);

        Log.v(getCallerMethodInfo("v"), formattedMessage);
        processFileLog(formattedMessage);

    }

    @Override
    public void w(String message, Object... args) {
        if (TextUtils.isEmpty(message)) {
            Log.w(getCallerMethodInfo("w"), NULL_MESSAGE);
            processFileLog(NULL_MESSAGE);
            return;
        }
        String formattedMessage = String.format(message, args);

        Log.w(getCallerMethodInfo("w"), formattedMessage);
        processFileLog(formattedMessage);

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
                if (!WRITE_PERMISSION_DENIED) {
                    System.out.println("Error in logging: " + e.getMessage());
                    WRITE_PERMISSION_DENIED = true;
                }
            } finally {
                if (bufferedWriter != null) {
                    try {
                        bufferedWriter.close();
                    } catch (IOException e) {
                        if (!WRITE_PERMISSION_DENIED) {
                            System.out.println("Error in logging: " + e.getMessage());
                            WRITE_PERMISSION_DENIED = true;
                        }
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

    /**
     * Returns tag that contains class name, method name and line number for the given method
     * that is on stacktrace.
     * The currentMethod must be invoked at some point so it is on method's calls stack
     *
     * @param currentMethod method from stacktrace, eg method from which getCallerMethodInfo is
     *                      called
     * @return [simple class name]:[method name]:[line number]
     */
    private static String getCallerMethodInfo(String currentMethod) {
        String tag = "";
        long threadId = Thread.currentThread().getId();
        final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        for (int i = 0; i < ste.length; i++) {
            if (ste[i].getMethodName().equals(currentMethod)) {
                String fullClassName = ste[i + 1].getClassName();
                String simpleClassName = fullClassName.substring(
                        fullClassName.lastIndexOf('.') + 1);
                tag = String.valueOf(threadId) + ":" + simpleClassName + ":" + ste[i
                        + 1].getMethodName()
                        + ":" + ste[i + 1].getLineNumber();
            }
        }
        return tag;
    }
}
