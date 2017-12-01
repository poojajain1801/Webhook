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

package com.mastercard.mcbp.data;

import android.util.Log;

import com.mastercard.mcbp.card.McbpCard;
import com.mastercard.mcbp.utils.lde.Utils;
import com.mastercard.mcbp.utils.logs.McbpLogger;
import com.mastercard.mcbp.utils.logs.McbpLoggerFactory;
import com.mastercard.mobile_api.payment.cld.CardSide;
import com.mastercard.mobile_api.payment.cld.Text;

import java.util.List;
import java.util.Locale;

/**
 * Class that wraps {@link McbpCard} but also gives additional
 * information or simplifies the access to it.
 */
public class McbpCardData {
    /**
     * Logging tag.
     */
    private static final String TAG = McbpCardData.class.getName();

    /**
     * The card that the data will be extracted from
     */
    private final McbpCard mCard;

    /**
     * PAN number of the card.
     */
    private String mPan;

    /**
     * Cardholder name of the card.
     */
    private String mCardholderName;

    /**
     * Expiry month of the card.
     */
    private String mExpiryMonth;

    /**
     * Expiry year of the card.
     */
    private String mExpiryYear;

    /**
     * CVC of the card.
     */
    private String mCvc;

    /**
     * Name of the card background image.
     */
    private String mCardBackgroundName;

    /**
     * Custom application metadata associated with the card.
     */
    private String mMetadata;

    /**
     * Regular expression to match a PAN in locked form.
     */
    private static final String REGEX_PAN_LOCKED = "[0-9]{4} [\\*]{4} [\\*]{4} [0-9]{4}";

    /**
     * Regular expression to match a PAN in unlocked form.
     */
    private static final String REGEX_PAN = "[0-9]{4} [0-9]{4} [0-9]{4} [0-9]{4}";

    /**
     * Regular expression to match an expiry date in locked form.
     */
    private static final String REGEX_EXPIRES_LOCKED = "[\\*]{2}/[\\*]{2}";

    /**
     * Regular expression to match an expiry date in unlocked form.
     */
    private static final String REGEX_EXPIRES = "(1[0-2]|0[1-9])/[0-9]{2}";

    /**
     * Regular expression to match CVC number in locked form.
     */
    private static final String REGEX_CVC_LOCKED = "[\\*]{3,4}";

    /**
     * Regular expression to match CVC number in unlocked form.
     */
    private static final String REGEX_CVC = "[0-9]{3,4}";

    /**
     * Static piece of text generally found within the card profile denoting a label for card
     * expiry.
     */
    private static final String EXPIRES = "EXPIRES";

    /**
     * Static piece of text generally found within the card profile denoting a label for cardholder
     * name.
     */
    private static final String CARDHOLDER_NAME = "CARDHOLDER NAME";

    /**
     * McbpLogger
     */
    private final McbpLogger mLogger = McbpLoggerFactory.getInstance().getLogger(this);

    /**
     * Constructor that will automatically extract data from the card.
     *
     * @param card Instance of {@link McbpCard} to extra the data
     *             for.
     */
    public McbpCardData(McbpCard card) {
        // Keep a reference to the card
        mCard = card;

        // Load the card details into memory
        loadCardDetails();

        // Process the front side of the card
        processFrontSide();

        // Process the back side of the card
        processBackSide();
    }

    /**
     * Constructor that allows the card details to be passed in.  This has no physical card attached
     * to it and should only be used for display purposes.
     *
     * @param pan PAN for the card.
     * @param cvc CVC for the card.
     */
    public McbpCardData(String pan,
                        String cvc) {
        mCard = null;
        mPan = pan;
        mCvc = cvc;
    }

    /**
     * Load the necessary card details that we know about
     */
    private void loadCardDetails() {
        loadPan();

        mMetadata = mCard.getCardMetadata();
    }

    /**
     * Load the PAN number, this can be found by looking at the first part of the digitized card Id.
     */
    private void loadPan() {
        String panWithoutPadding = Utils.retrievePanFromDigitizedCardId(mCard.getDigitizedCardId());
        String actualPan = panWithoutPadding.replace("F", "");

        final int panLength = actualPan.length();

        final String panPart1 = actualPan.substring(0, 4);
        String panPart2 = null;
        String panPart3 = null;
        String panPart4 = null;
        String panPart5 = null;

        if (panLength <= 12) {
            panPart2 = actualPan.substring(4, panLength - 4);
            panPart3 = actualPan.substring(panLength - 4, panLength);
            panPart4 = "";
            panPart5 = "";
        } else if (panLength > 12 && panLength <= 16) {
            panPart2 = actualPan.substring(4, 8);
            panPart3 = actualPan.substring(8, 12);
            panPart4 = actualPan.substring(12, panLength);
            panPart5 = "";
        } else if (panLength > 16 && panLength <= 19) {
            panPart2 = actualPan.substring(4, 8);
            panPart3 = actualPan.substring(8, 12);
            panPart4 = actualPan.substring(12, 16);
            panPart5 = actualPan.substring(16, panLength);
        }

        mPan = panPart1 + " " + panPart2 + " " + panPart3 + " " + panPart4;
        if (panPart5 != null && !panPart5.equals("")) {
            mPan += " " + panPart5;
        }
    }

    /**
     * Extract the necessary elements we are interested in from the front of the card.
     */
    private void processFrontSide() {
        // Grab the front of the card
        CardSide frontOfCard = mCard.getCardLayout().getFrontSide();

        // Pull out the name of the background image
        mCardBackgroundName = new String(frontOfCard.getCardBackground().getBackgroundValue());

        // The CLD object stores text as raw objects, we'll convert them later
        List rawObjectList = frontOfCard.getText();

        // Loop through each Text object and attempt to determine what it is
        for (Object rawObject : rawObjectList) {
            try {
                // Attempt to convert it to a Text object
                Text text = (Text) rawObject;

                // Check the conversion was successful
                if (text != null) {
                    // Pull out the string value of the piece of text
                    String textValue = text.getTextValue();

                    // Ensure it actually has some text
                    if (textValue == null || textValue.isEmpty()) {
                        continue;
                    }

                    // Check whether it matches any static text we are aware of
                    if (textValue.toUpperCase(Locale.ENGLISH).equals(EXPIRES)) {
                        // Log to keep compiler happy
                        Log.d(TAG, "Found static text for expiry");
                    } else if (textValue.toUpperCase(Locale.ENGLISH).equals(CARDHOLDER_NAME)) {
                        // Log to keep compiler happy
                        Log.d(TAG, "Found static text for cardholder name");
                    } else if (textValue.matches(REGEX_PAN_LOCKED)) {
                        // Matches the locked PAN regular expression
                        // Intentional no-op - the PAN is acquired from the digitized card Id
                    } else if (textValue.matches(REGEX_EXPIRES_LOCKED)) {
                        // Matches the locked expiry regular expression, split it to get the month
                        // and year as separate parts
                        String[] parts = textValue.split("/");
                        mExpiryMonth = parts[0];
                        mExpiryYear = parts[1];
                    } else {
                        // Not a recognised piece of text so assume it's the cardholder name
                        // unless it's already populated
                        if (mCardholderName == null || mCardholderName.isEmpty()) {
                            mCardholderName = textValue;
                        }
                    }
                }
            } catch (ClassCastException e) {
                mLogger.d(Log.getStackTraceString(e));
            }
        }
    }

    /**
     * Extract the necessary elements we are interested in from the back of the card.
     */
    private void processBackSide() {
        // Grab the back of the card
        CardSide backOfCard = mCard.getCardLayout().getBackSide();

        // The CLD object stores text as raw objects, we'll convert them later
        List rawObjectList = backOfCard.getText();

        // Loop through each Text object and attempt to determine what it is
        for (Object rawObject : rawObjectList) {
            try {
                // Attempt to convert it to a Text object
                Text text = (Text) rawObject;

                // Check the conversion was successful
                if (text != null) {
                    // Pull out the string value of the piece of text
                    String textValue = text.getTextValue();

                    // Ensure it actually has some text
                    if (textValue == null || textValue.isEmpty()) {
                        continue;
                    }

                    // Check whether it matches anything we recognise
                    if (textValue.matches(REGEX_CVC_LOCKED)) {
                        // Matches the locked CVC regular expression
                        mCvc = textValue;
                    }
                }
            } catch (ClassCastException e) {
                mLogger.d(Log.getStackTraceString(e));
            }
        }
    }

    /**
     * Get the card.
     *
     * @return Instance of {@link McbpCardData} that was initially
     * provided to the constructor.
     */
    public McbpCard getCard() {
        return mCard;
    }

    /**
     * Get the PAN of the card.
     *
     * @return The PAN of the card.
     */
    public String getPan() {
        return mPan;
    }

    /**
     * Get the cardholder name of the card.
     *
     * @return The cardholder name of the card.
     */
    public String getCardHolderName() {
        return mCardholderName;
    }

    /**
     * Get the expiry month of the card.
     *
     * @return The expiry month of the card.
     */
    public String getExpiryMonth() {
        return mExpiryMonth;
    }

    /**
     * Get the expiry year of the card.
     *
     * @return The expiry year of the card.
     */
    public String getExpiryYear() {
        return mExpiryYear;
    }

    /**
     * Get the name of the background image to use for the card.
     *
     * @return Name of the background image to use for the card.
     */
    public String getCardBackgroundName() {
        return mCardBackgroundName;
    }

    /**
     * Get the CVC of the card.
     *
     * @return The CVC of the card.
     */
    public String getCvc() {
        return mCvc;
    }

    public String getMetadata() {
        return mMetadata;
    }

    /**
     * Get the number of payment tokens remaining on this card.
     *
     * @return The number of payment tokens remaining on this card.
     */
    public int getPaymentTokensRemaining() {
        return mCard.numberPaymentsLeft();
    }
}
