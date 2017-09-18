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
package com.mastercard.mcbp.utils.task;

import android.os.AsyncTask;

class AndroidMcbpAsyncTask extends AsyncTask<Void, String, Void> implements McbpAsyncTask {

    private McbpTaskListener mExecutorListener;

    /*
     * (non-Javadoc)
     * 
     * @see android.os.AsyncTask#onPreExecute()
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mExecutorListener.onPreExecute();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    @Override
    protected void onPostExecute(final Void result) {
        super.onPostExecute(result);
        mExecutorListener.onPostExecute();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.os.AsyncTask#onProgressUpdate(Progress[])
     */
    @Override
    protected void onProgressUpdate(final String... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected Void doInBackground(final Void... params) {
        mExecutorListener.onRun();
        return null;
    }

    @Override
    public void execute(final McbpTaskListener executorListener) {
        this.mExecutorListener = executorListener;
        this.execute((Void) null);
    }

    @Override
    public int getState() {
        return super.getStatus().ordinal();
    }

    @Override
    public void cancel() {
        this.cancel(true);
    }

}
