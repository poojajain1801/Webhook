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

package com.mastercard.mcbp.remotemanagement.mdes;

import com.mastercard.mcbp.remotemanagement.mdes.models.RemoteManagementSessionData;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.Utils;

import java.util.Date;

/**
 * Encapsulated session related attributes
 */
public class SessionContext {
    /**
     * The 29-byte remote management session code used by the Mobile Payment App to generate an
     * authentication code and to derive the Mobile Session Keys when communicating with MDES.
     */
    private ByteArray mSessionCode;
    /**
     * Estimated expiry time of remote management session code after first use.
     */
    private String mEstimatedExpiryTimestamp;
    /**
     * The date/time when the remote management session code will expire.
     * In ISO 8601 extended format as one of the following:
        YYYY-MM-DDThh:mm:ss[.sss]Z
        YYYY-MM-DDThh:mm:ss[.sss]±hh:mm
        Where [.sss] is optional and can be 1 to 3 digits.
     */
    private String mActualExpiryTimestamp;
    /**
     * MPA to CMS counter
     */
    private int mMpaToCmsCounter = -1;
    /**
     * CMS to MPA counter
     */
    private int mCmsToMpaCounter = -1;
    /**
     * Response host URL - the specific MDES host that originated a request or response
     */
    private String mResponseHost;
    /**
     * Flag to indicate first time use of session code
     */
    private boolean isUsed = false;
    /**
     * Number of seconds this session code is valid after first use.
     */
    private int validForSeconds = 0;

    /**
     * Constructor.
     */
    private SessionContext() {

    }

    /**
     * Initialize the MPA to CMS counter with given value.
     */
    public void initializeM2CCounter(int value) {
        this.mMpaToCmsCounter = value;
    }

    /**
     * Initialize the CMS to MPA counter with given value.
     */
    public void initializeC2MCounter(int value) {
        this.mCmsToMpaCounter = value;
    }

    /**
     * @return Session code.
     */
    public ByteArray getSessionCode() {
        return mSessionCode;
    }

    /**
     *
     * Set session code.
     */
    public void setSessionCode(ByteArray sessionCode) {
        mSessionCode = sessionCode;
    }

    /**
     *
     * @return estimated expiry timestamp.
     */
    public String getEstimatedExpiryTimestamp() {
        return mEstimatedExpiryTimestamp;
    }

    /**
     * Set estimated expiry timestamp.
     *
     */
    public void setEstimatedExpiryTimestamp(String estimatedExpiryTimestamp) {
        mEstimatedExpiryTimestamp = estimatedExpiryTimestamp;
    }

    /**
     * @return the date/time when the remote management session code will expire.
     */
    public String getActualExpiryTimestamp() {
        return mActualExpiryTimestamp;
    }

    /**
     * Set the date/time when the remote management session code will expire.
     *
     * @param actualExpiryTimestamp Actual Expiry Timestamp
     */
    public void setActualExpiryTimestamp(String actualExpiryTimestamp) {
        mActualExpiryTimestamp = actualExpiryTimestamp;
    }

    /**
     * @return MPA to CMS counter.
     */
    public int getMpaToCmsCounter() {
        return mMpaToCmsCounter;
    }

    /**
     * Increment MPA to CMS counter by one.
     */
    public void incrementMpaToCmsCounter() {
        ++this.mMpaToCmsCounter;
    }

    /**
     * @return CMS to MPA counter.
     */
    public int getCmsToMpaCounter() {
        return mCmsToMpaCounter;
    }

    /**
     * Set CMS to MPA counter.
     *
     * @param cmsToMpaCounter CMS to MPA counter
     */
    public void setCmsToMpaCounter(int cmsToMpaCounter) {
        mCmsToMpaCounter = cmsToMpaCounter;
    }

    /**
     * @return True if current session is valid. False otherwise.
     */
    public boolean isValidSession() {
        return isUsed ? (getEstimatedExpiryTimestamp() != null &&
                TimeUtils.isBefore(getEstimatedExpiryTimestamp())) :
                TimeUtils.isBefore(getActualExpiryTimestamp());
    }

    /**
     * Clear all values in current session context object.
     */
    public void clear() {
        Utils.clearByteArray(mSessionCode);
        this.mActualExpiryTimestamp = null;
        this.mEstimatedExpiryTimestamp = null;
        this.mMpaToCmsCounter = -1;
        this.mCmsToMpaCounter = -1;
        isUsed = false;
    }

    /**
     * This method provides a object of {@link SessionContext} class.
     */
    public static SessionContext of(final RemoteManagementSessionData remoteManagementSessionData) {
        final SessionContext mSessionContext = new SessionContext();
        mSessionContext.setSessionCode(ByteArray.of(remoteManagementSessionData.getSessionCode()));
        mSessionContext.setActualExpiryTimestamp(remoteManagementSessionData.getExpiryTimestamp());
        mSessionContext.initializeC2MCounter(0);
        mSessionContext.initializeM2CCounter(0);
        mSessionContext.validForSeconds = remoteManagementSessionData.getValidForSeconds();
        mSessionContext.isUsed = false;
        return mSessionContext;
    }

    /**
     * @return get response host.
     */
    public String getResponseHost() {
        return mResponseHost;
    }

    /**
     * Set or update response host.
     *
     * @param responseHost response host.
     */
    public void updateResponseHost(String responseHost) {
        if (responseHost != null) {
            mResponseHost = responseHost;
        }
    }

    /**
     * @param used Flag indicating whether the session code has been used
     */
    public void setUsed(boolean used) {
        this.isUsed = used;
    }

    /**
     * Calculate the estimated expiry time for the remote management session.
     * validForSeconds is the number of seconds after which the remote management session code
     * will expire after first use.
     */
    public void createExpiryTimeStamp() {
        final long newExpiryTimeStamp = new Date().getTime() + (this.validForSeconds * 1000);
        setEstimatedExpiryTimestamp(
                TimeUtils.getFormattedDate(new Date(newExpiryTimeStamp)));
    }

    /**
     * @return True if the session code has been used. False otherwise.
     */
    public boolean isUsed() {
        return isUsed;
    }
}
