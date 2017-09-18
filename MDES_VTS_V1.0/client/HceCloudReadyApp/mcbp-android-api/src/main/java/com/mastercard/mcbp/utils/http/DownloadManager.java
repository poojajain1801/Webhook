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

package com.mastercard.mcbp.utils.http;

import com.mastercard.mcbp.utils.logs.McbpLogger;
import com.mastercard.mcbp.utils.logs.McbpLoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Allows applications to easily download files
 */
public enum DownloadManager {

    INSTANCE;

    /**
     * Logger
     */
    private McbpLogger logger = McbpLoggerFactory.getInstance().getLogger(this);

    /**
     * Download a file from a URL
     *
     * @param fileUrl the URL to download the file from
     * @return the contents of the file
     */
    private byte[] download(String fileUrl) throws IOException {

        long startTime = System.currentTimeMillis();

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        // Download the file
        URL url = new URL(fileUrl);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        int responseCode = httpConn.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = null;
            try {
                // Read all the data from the url
                inputStream = httpConn.getInputStream();

                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
            } finally {

                safeCloseInputStream(inputStream);
            }
            logger.d("download ready in"
                     + ((System.currentTimeMillis() - startTime) / 1000)
                     + " sec");
        } else {

            logger.e("Failed with HTTP response: " + responseCode);
        }

        // Ensure the connection is terminated
        httpConn.disconnect();

        // Return the bytes if we have any
        if (output.size() > 0) {
            return output.toByteArray();
        } else {
            return null;
        }
    }

    private void safeCloseInputStream(final InputStream inputStream) {
        if (inputStream != null)
            try {
                inputStream.close();
            } catch (IOException e) {
                logger.e("Error: Closing input stream in DownloadManager.java: " + e.getMessage());
            }

    }

    /**
     * Trigger the download of the file using the source file name as the destination file name
     *
     * @param fileName the name of the file to be saved to disk
     * @param url      the url from which to download the file
     * @param path     the path where the file should be saved
     * @return true if Download successful
     */
    public boolean downloadToDisk(String fileName, String url, File path) throws IOException {
        logger.d("Download (" + url + ") to disk - saving to " + path.getAbsolutePath() + "/" +
                 fileName);

        // Download the file
        byte[] bytes = download(url);

        if (bytes != null) {

            // Save the bytes to a file output stream
            File file = new File(path, fileName);

            // If this file already exists, don't download again
            if (file.exists()) {
                return true;
            }

            // Save the bytes to a file on disk
            OutputStream output = new FileOutputStream(file);
            try {
                output.write(bytes);
            } finally {
                output.close();
            }

            // Download successful
            return true;
        } else {
            // Download failed
            return false;
        }
    }
}
