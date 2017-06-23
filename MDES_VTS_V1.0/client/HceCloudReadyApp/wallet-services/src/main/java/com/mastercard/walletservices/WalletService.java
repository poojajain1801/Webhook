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

package com.mastercard.walletservices;

import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.exceptions.ErrorCode;
import com.mastercard.walletservices.mdes.DeleteCardRequest;
import com.mastercard.walletservices.mdes.DeleteCardResponse;
import com.mastercard.walletservices.mdes.DigitizeRequest;
import com.mastercard.walletservices.mdes.ManagementAPIRegisterRequest;
import com.mastercard.walletservices.mdes.ManagementAPIRegisterResponse;
import com.mastercard.walletservices.mdes.ManagementAPISetMobilePinRequest;
import com.mastercard.walletservices.mdes.ManagementAPISetMobilePinResponse;
import com.mastercard.walletservices.mdes.RnsInfo;
import com.mastercard.walletservices.mdes.SignupRequest;
import com.mastercard.walletservices.mdes.SignupResponse;
import com.mastercard.walletservices.mdes.UnregisterRequest;
import com.mastercard.walletservices.mdes.UnregisterResponse;
import com.mastercard.walletservices.utils.AsyncTaskExecutor;
import com.mastercard.walletservices.utils.TaskExecutor;
import com.mastercard.walletservices.utils.exceptions.CmsCommunicationException;
import com.mastercard.walletservices.utils.http.HttpsConnection;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * An initial wrapper around MDES wallet services.  Currently this is in place to support the flow
 * of the sample UI application so that MDES functionality can be added at a later date.<br>
 * This class allows the UI application to set the simulated result for adding card.
 */
public class WalletService {

    /**
     * Logging tag
     */
    private static final String TAG = WalletService.class.getName();

    /**
     * What result to use when an add card request is seen.
     */
    private AddCardResult mSimulatedAddCardResult;

    private boolean mIsDemoMode;

    /**
     * Parameter less constructor that will always return the Automatic approval when an add card
     * request is seen.
     */
    public WalletService() {
        mSimulatedAddCardResult = AddCardResult.AUTOMATIC_APPROVAL;
    }

    /**
     * Constructor that allows a simulated add card result to be used.
     *
     * @param simulatedAddCardResult The result to use when an add card request is seen.
     */
    public WalletService(AddCardResult simulatedAddCardResult) {
        mSimulatedAddCardResult = simulatedAddCardResult;
    }

    /**
     * Send a request to the Wallet Server to add a card.<br>
     * In this implementation it will return a pre-defined result as an example.
     *
     * @param pan            The PAN entered by the user.
     * @param cvc            The CVC entered by the user.
     * @param expiryMonth    The expiry month entered by the user.
     * @param expiryYear     The expiry year entered by the user.
     * @param cardholderName The cardholder name entered by the user.
     * @return The result of requesting to add a card
     */
    public AddCardResult addCard(final String pan, final String cvc,
                                 final String expiryMonth, final String expiryYear,
                                 final String cardholderName, final String paymentAppInstanceId,
                                 final String uri) {

        if (mIsDemoMode) {
            return mSimulatedAddCardResult;
        }
        new AsyncTaskExecutor().execute(new TaskExecutor() {
            @Override
            public void onPreExecute() {

            }

            @Override
            public AsyncTaskExecutor.ResultHolder doInBackground() {

                AsyncTaskExecutor.ResultHolder resultHolder = new AsyncTaskExecutor.ResultHolder();
                DigitizeRequest digitizeRequest =
                        buildDigitizeRequest(pan, cvc, expiryMonth, expiryYear, cardholderName,
                                             paymentAppInstanceId);

                String digitizeRequetsJson = digitizeRequest.toJsonString();

                try {
                    resultHolder.responseData = new HttpsConnection()
                            .withUrl(uri + "/digitize")
                            .withRequestData(digitizeRequetsJson).execute();
                } catch (Exception e) {
                    resultHolder.errorMessage = e.getMessage();
                }
                return resultHolder;
            }

            @Override
            public void onPostExecute(AsyncTaskExecutor.ResultHolder results) {
                // TODO: In future releases we add some sanity check here
            }
        });

        return mSimulatedAddCardResult;
    }

    /**
     * Send a request to the Wallet Server to activate a card.<br>
     * In this implementation it will not perform any action so the UI can assume the request was
     * successful.
     *
     * @param digitizedCardId Identifier of the card to be activated.
     * @param activationCode  The activation code entered by the user.
     */
    public void activate(String digitizedCardId, String activationCode) {
        // Intentionally empty
    }

    /**
     * Send a request to the Wallet Server to delete a card.<br>
     * In this implementation it will not perform any action so the UI can assume the request was
     * successful.
     *
     * @param cardId Identifier of the card to be deleted.
     */
    public void deleteCard(final String cardId, final String uri,
                           final DeleteCardListener deleteCardListener) {

        new AsyncTaskExecutor().execute(new TaskExecutor() {
            @Override
            public void onPreExecute() {
                deleteCardListener.onDeletionOfCardStarted();
            }

            @Override
            public AsyncTaskExecutor.ResultHolder doInBackground() {
                AsyncTaskExecutor.ResultHolder resultHolder = new AsyncTaskExecutor.ResultHolder();

                DeleteCardRequest deleteCardRequest = buildDeleteCardRequest(cardId);
                String deleteCardRequestJson = deleteCardRequest.toJsonString();

                try {
                    resultHolder.responseData =
                            new HttpsConnection().withUrl(uri + "/delete")
                                                 .withRequestData(deleteCardRequestJson)
                                                 .execute();
                } catch (Exception e) {
                    resultHolder.errorMessage = e.getMessage();
                }
                return resultHolder;
            }

            @Override
            public void onPostExecute(AsyncTaskExecutor.ResultHolder results) {
                if (results.responseData != null) {
                    String deleteCardResponseJson = new String(results.responseData);
                    DeleteCardResponse deleteCardResponse =
                            DeleteCardResponse.valueOf(deleteCardResponseJson);

                    if (deleteCardResponse.isError()) {
                        deleteCardListener
                                .onDeleteCardError(deleteCardResponse.getErrorDescription());
                    } else {
                        deleteCardListener.onDeleteCard("Success".getBytes());
                    }
                } else {
                    deleteCardListener.onDeleteCardError("Error");
                }
            }

        });
    }

    public void signup(final String userId, final String activationCode,
                       final String rnsMessageId, final String deviceInfo,
                       final String uri,
                       final WalletActivationEventListener walletActivationEventListener) {
        new AsyncTaskExecutor().execute(new TaskExecutor() {
            @Override
            public void onPreExecute() {
                walletActivationEventListener.onActivationStarted();
            }

            @Override
            public AsyncTaskExecutor.ResultHolder doInBackground() {
                AsyncTaskExecutor.ResultHolder resultHolder = new AsyncTaskExecutor.ResultHolder();

                SignupRequest signupRequest =
                        buildSignUpRequest(userId, activationCode, rnsMessageId, deviceInfo);

                String signUpJson = signupRequest.toJsonString();

                try {
                    resultHolder.responseData = new HttpsConnection()
                            .withUrl(uri + "/signup")
                            .withRequestData(signUpJson)
                            .execute();
                } catch (Exception e) {
                    if (e.getCause() != null) {
                        e.getCause().printStackTrace();
                    } else {
                        e.printStackTrace();
                    }
                    resultHolder.errorMessage = e.getMessage();
                }

                return resultHolder;
            }

            @Override
            public void onPostExecute(AsyncTaskExecutor.ResultHolder results) {
                try {
                    if (results.responseData != null) {
                        String signUpResponseJson = new String(results.responseData);

                        SignupResponse signupResponse = null;

                        signupResponse =
                                SignupResponse.valueOf(signUpResponseJson);

                        if (signupResponse.isError()) {
                            walletActivationEventListener
                                    .onActivationError(signupResponse.getErrorCause());
                        } else {
                            walletActivationEventListener.onWalletActivated(signUpResponseJson);
                        }
                    } else {
                        if (results.errorMessage != null) {
                            walletActivationEventListener.onActivationError(results.errorMessage);
                        } else {
                            walletActivationEventListener.onActivationError("Error");
                        }
                    }
                } catch (Exception exception) {

                    walletActivationEventListener.onActivationError("Error");
                }
            }
        });
    }

    public boolean isDemoMode() {
        return mIsDemoMode;
    }

    public void setDemoMode(final boolean isDemoMode) {
        this.mIsDemoMode = isDemoMode;
    }

    private DeleteCardRequest buildDeleteCardRequest(final String cardId) {
        DeleteCardRequest deleteCardRequest = new DeleteCardRequest();
        deleteCardRequest.setDigitizedCardId(cardId);
        return deleteCardRequest;
    }

    private SignupRequest buildSignUpRequest(final String userId,
                                             final String activationCode,
                                             final String rnsMessageId,
                                             final String deviceInfo) {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUserId(userId);
        signupRequest.setActivationCode(activationCode);
        signupRequest.setRnsRegistrationId(rnsMessageId);
        signupRequest.setDeviceInfoJson(deviceInfo);
        return signupRequest;
    }

    private DigitizeRequest buildDigitizeRequest(final String pan,
                                                 final String cvc,
                                                 final String expiryMonth,
                                                 final String expiryYear,
                                                 final String cardholderName,
                                                 final String paymentAppInstanceId) {

        DigitizeRequest digitizeRequest = new DigitizeRequest();
        digitizeRequest.setPan(pan);
        digitizeRequest.setExpiryMonth(expiryMonth);
        digitizeRequest.setExpiryYear(expiryYear);
        digitizeRequest.setCvc(cvc);
        digitizeRequest.setCardholderName(cardholderName);
        digitizeRequest.setPaymentAppInstanceId(paymentAppInstanceId);
        return digitizeRequest;
    }

    public CertificateData downloadPKCertificate(String url) {
        try {
            byte[] responseData = new HttpsConnection()
                    .withUrl(url + "/getPkCertificate/mastercard-public.cer")
                    .withMethod(
                            HttpsConnection.HTTP_METHOD_GET)
                    .execute();
            final byte[] certificateSha1 = sha1(responseData);
            //Create certificate
            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            InputStream mInputStream = new ByteArrayInputStream(responseData);
            final Certificate certificate = cf.generateCertificate(mInputStream);
            return new CertificateData(certificate.getPublicKey().getEncoded(), certificateSha1);
        } catch (CertificateException | CmsCommunicationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ManagementAPIRegisterResponse register(final String gcmTokenId,
                                                  final String paymentAppProviderId,
                                                  final String paymentAppInstanceId,
                                                  final String url,
                                                  final String deviceFingerPrint,
                                                  final ByteArray rgk,
                                                  final ByteArray certificateFingerPrint,
                                                  final ByteArray pin) {

        ManagementAPIRegisterRequest apiRegisterRequest =
                buildRegisterRequest(gcmTokenId, paymentAppProviderId, paymentAppInstanceId,
                                     deviceFingerPrint, rgk, certificateFingerPrint, pin);
        String registerJson = apiRegisterRequest.toJsonString();

        // Create the output object
        ManagementAPIRegisterResponse output = new ManagementAPIRegisterResponse();

        byte[] responseData = null;
        try {
            responseData = new HttpsConnection().withUrl(url + "/register")
                                                .withRequestData(registerJson)
                                                .execute();
        } catch (Exception e) {
            if (e.getCause() != null) {
                e.getCause().printStackTrace();
            } else {
                e.printStackTrace();
            }
            output.setErrorDescription(e.getMessage());
            output.setErrorCode(String.valueOf(ErrorCode.UNKNOWN_HTTP_ERROR_CODE));
        }

        // Did we get any response?
        if (responseData != null) {
            // Deserialize the response
            String registerResponse = new String(responseData);
            Log.d(TAG, "REGISTER - Response JSON: " + registerResponse);
            output = ManagementAPIRegisterResponse.valueOf(registerResponse);
        } else {
            // Set the error
            output.setErrorDescription("Error");
            output.setErrorCode(String.valueOf(ErrorCode.UNKNOWN_HTTP_ERROR_CODE));
        }
        return output;
    }


    private ManagementAPIRegisterRequest
    buildRegisterRequest(String gcmTokenId, String paymentAppProviderId,
                         String paymentAppInstanceId,
                         String deviceFingerPrint, ByteArray rgk,
                         ByteArray certificateFingerPrint, ByteArray pin) {
        RnsInfo rnsInfo = new RnsInfo();
        rnsInfo.setGcmRegistrationId(gcmTokenId);

        ManagementAPIRegisterRequest apiRegisterRequest = new ManagementAPIRegisterRequest();
        apiRegisterRequest.setRequestId(String.valueOf(System.currentTimeMillis()));
        apiRegisterRequest.setRnsInfo(rnsInfo);
        apiRegisterRequest.setPaymentAppId(paymentAppProviderId);
        apiRegisterRequest.setPaymentAppInstanceId(paymentAppInstanceId);
        apiRegisterRequest.setDeviceFingerprint(deviceFingerPrint);
        apiRegisterRequest.setRgk(rgk.toHexString());
        apiRegisterRequest.setNewMobilePin(pin != null ? pin.toHexString() : null);
        apiRegisterRequest.setCertificateFingerprint(certificateFingerPrint.toHexString());
        return apiRegisterRequest;
    }

    public void setPin(final ByteArray pin,
                       final String paymentAppInstanceId,
                       final WalletPinEventListener paymentInitiateFragment, final String url) {
        new AsyncTaskExecutor().execute(new TaskExecutor() {
            @Override
            public void onPreExecute() {

            }

            @Override
            public AsyncTaskExecutor.ResultHolder doInBackground() {
                AsyncTaskExecutor.ResultHolder resultHolder = new AsyncTaskExecutor.ResultHolder();
                ManagementAPISetMobilePinRequest mobilePinRequest =
                        new ManagementAPISetMobilePinRequest();
                mobilePinRequest.setNewMobilePin(pin.toHexString());
                mobilePinRequest.setPaymentAppInstanceId(paymentAppInstanceId);
                mobilePinRequest.setRequestId(String.valueOf(System.currentTimeMillis()));
                mobilePinRequest.setResponseHost(null);
                String requestJson = mobilePinRequest.toJSONString();
                ManagementAPISetMobilePinResponse output;
                byte[] responseData = null;
                try {
                    responseData = new HttpsConnection().withUrl(url + "/setMobilePin")
                                                        .withRequestData(requestJson)
                                                        .execute();
                } catch (Exception e) {
                    if (e.getCause() != null) {
                        e.getCause().printStackTrace();
                    } else {
                        e.printStackTrace();
                    }
                    resultHolder.errorMessage = e.getMessage();
                }
                if (responseData != null) {
                    output = ManagementAPISetMobilePinResponse.valueOf(new String(responseData));
                    if (!output.isSuccess()) {
                        resultHolder.errorMessage = output.getErrorDescription();
                    } else {
                        resultHolder.responseData = responseData;
                    }
                }
                return resultHolder;
            }

            @Override
            public void onPostExecute(AsyncTaskExecutor.ResultHolder results) {
                // Did we get any response?
                if (results.responseData != null) {
                    paymentInitiateFragment.onPinChangedSuccess();
                } else {
                    paymentInitiateFragment.onPinChangeFailed(results.errorMessage);
                }

            }
        });
    }

    public void unregister(final String paymentAppInstanceId, final String url,
                           final Runnable runnable) {
        new AsyncTaskExecutor().execute(new TaskExecutor() {
            @Override
            public void onPreExecute() {

            }

            @Override
            public AsyncTaskExecutor.ResultHolder doInBackground() {
                AsyncTaskExecutor.ResultHolder resultHolder = new AsyncTaskExecutor.ResultHolder();
                UnregisterRequest unregisterRequest = new UnregisterRequest();
                unregisterRequest.setPaymentAppInstanceId(paymentAppInstanceId);
                final String requestData = unregisterRequest.toJsonString();
                byte[] responseData = null;
                try {
                    responseData = new HttpsConnection().withUrl(url + "/unregister")
                                                        .withRequestData(requestData)
                                                        .execute();
                    resultHolder.responseData = responseData;
                } catch (Exception e) {
                    if (e.getCause() != null) {
                        e.getCause().printStackTrace();
                    } else {
                        e.printStackTrace();
                    }
                    resultHolder.errorMessage = e.getMessage();
                }
                return resultHolder;
            }

            @Override
            public void onPostExecute(AsyncTaskExecutor.ResultHolder results) {
                if (results.responseData != null) {
                    UnregisterResponse unregisterResponse =
                            UnregisterResponse.deserialize(new String(results.responseData));
                } else {
                    UnregisterResponse unregisterResponse = new UnregisterResponse();
                    unregisterResponse.setErrorDescription(results.errorMessage);
                    unregisterResponse
                            .setErrorCode(String.valueOf(ErrorCode.UNKNOWN_HTTP_ERROR_CODE));
                }
                runnable.run();
            }
        });
    }

    public final static byte[] sha1(final byte[] data) {
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(data);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.toString());
        }
    }

    /**
     * Returns a URLs from which to download the appropriate card arts for the given card number
     * and color.
     * <p/>
     * Card PAN is shown as an example
     * <p/>
     * If color is not provided it will be chosen by random and returned
     *
     * @param pan       the card pan
     * @param url       the base URL of CDN containing card art
     * @param colorName if the colorName is already known, can be provided here
     * @return Pair of color and the URLs list from which to retrieve the card arts
     */
    public Pair<String, List<Pair<String, String>>> getCardArtsUrls(String url, String pan,
                                                                    String colorName) {

        ArrayList<Pair<String, String>> result = new ArrayList<>();

        result.add(new Pair<>("mastercard.png", url + "mastercard.png"));

        if (!TextUtils.isEmpty(colorName)) {
            result.add(new Pair<>(colorName + ".png", url + colorName + ".png"));
            result.add(new Pair<>(colorName + "_back.png", url + colorName + "_back.png"));
        } else {

            // Sample logic for determining the correct card image to use
            String[] possibleColors = new String[]{
                    "tvk_2",
                    "tvk_3",
                    "tvk_4",
                    "tvk_5",
                    "tvk_6",
                    "tvk_7",
                    "tvk_8",
                    "tvk_9"
            };

            // Select a colorName
            colorName = possibleColors[new Random().nextInt(possibleColors.length)];
            result.add(new Pair<>(colorName + ".png", url + colorName + ".png"));
            result.add(new Pair<>(colorName + "_back.png", url + colorName + "_back.png"));
        }
        return new Pair<String, List<Pair<String, String>>>(colorName, result);
    }
}
