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

package com.mastercard.walletservices.utils;

import android.os.AsyncTask;

/**
 * Generic wrapper class for {@link android.os.AsyncTask} to perform task in background and post
 * result on Main / UI thread.
 */
public class AsyncTaskExecutor extends AsyncTask<Void, Void, AsyncTaskExecutor.ResultHolder> {

    private TaskExecutor mTaskExecutor;

    public AsyncTaskExecutor() {
    }

    /*
     * (non-Javadoc)
     *
     * @see android.os.AsyncTask#doInBackground()
     */
    @Override
    protected ResultHolder doInBackground(Void... params) {
        return mTaskExecutor.doInBackground();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.os.AsyncTask#onPreExecute()
     */
    @Override
    protected void onPreExecute() {
        mTaskExecutor.onPreExecute();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.os.AsyncTask#onPostExecute()
     */
    @Override
    protected void onPostExecute(ResultHolder o) {
        mTaskExecutor.onPostExecute(o);
    }

    public void execute(TaskExecutor taskExecutor) {
        this.mTaskExecutor = taskExecutor;
        this.execute((Void[]) null);
    }

    public static class ResultHolder {
        public byte[] responseData;
        public String errorMessage;
    }
}
