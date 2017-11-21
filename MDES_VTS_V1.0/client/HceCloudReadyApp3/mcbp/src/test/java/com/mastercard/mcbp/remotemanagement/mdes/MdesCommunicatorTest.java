package com.mastercard.mcbp.remotemanagement.mdes;

import com.mastercard.mcbp.remotemanagement.mdes.models.RemoteManagementSessionData;
import com.mastercard.mcbp.remotemanagement.mdes.mpamanagementapi.MockLdeRemoteManagementService;
import com.mastercard.mcbp.utils.UnitTestMcbpLoggerFactory;
import com.mastercard.mcbp.utils.crypto.CryptoService;
import com.mastercard.mcbp.utils.crypto.CryptoServiceFactory;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mcbp.utils.logs.McbpLoggerFactory;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.exceptions.http.HttpException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static com.mastercard.mcbp.remotemanagement.mdes.MdesCommunicatorTest.RetryType.NO_RETRY;
import static com.mastercard.mcbp.remotemanagement.mdes.MdesCommunicatorTest.RetryType
        .RETRY_FOR_GATEWAY_TIMEOUT;
import static com.mastercard.mcbp.remotemanagement.mdes.MdesCommunicatorTest.RetryType
        .RETRY_FOR_REQUEST_TIMEOUT;
import static com.mastercard.mcbp.remotemanagement.mdes.MdesCommunicatorTest.RetryType
        .RETRY_FOR_SC_INTERNAL_SERVER_ERROR;
import static com.mastercard.mcbp.remotemanagement.mdes.MdesCommunicatorTest.RetryType
        .RETRY_FOR_SC_MOVED_TEMPORARILY;
import static com.mastercard.mcbp.remotemanagement.mdes.MdesCommunicatorTest.RetryType
        .RETRY_FOR_SC_SERVICE_UNAVAILABLE;
import static com.mastercard.mcbp.remotemanagement.mdes.MdesCommunicatorTest.RetryType
        .RETRY_FOR_SSL_ERROR;

/**
 * ****************************************************************************
 * Copyright (c) 2016, MasterCard International Incorporated and/or its
 * affiliates. All rights reserved.
 * <p/>
 * The contents of this file may only be used subject to the MasterCard
 * Mobile Payment SDK for MCBP and/or MasterCard Mobile MPP UI SDK
 * Materials License.
 * <p/>
 * Please refer to the file LICENSE.TXT for full details.
 * <p/>
 * TO THE EXTENT PERMITTED BY LAW, THE SOFTWARE IS PROVIDED "AS IS", WITHOUT
 * WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NON INFRINGEMENT. TO THE EXTENT PERMITTED BY LAW, IN NO EVENT SHALL
 * MASTERCARD OR ITS AFFILIATES BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 * *****************************************************************************
 */
public class MdesCommunicatorTest {

    private final CryptoService cryptoService;
    private final MockLdeRemoteManagementService ldeService;
    private int expectedRetryRemain;

    private RetryEventListener mRetryEventListener = new RetryEventListener() {
        @Override
        public void onRetry(int retryRemain) {
            System.out.println("Retry count: " + retryRemain);
            expectedRetryRemain--;
            MockHttpFactory.expectedRemainRetryCount = expectedRetryRemain;
            Assert.assertEquals(expectedRetryRemain, retryRemain);
        }
    };

    public MdesCommunicatorTest() throws McbpCryptoException, InvalidInput {
        cryptoService = CryptoServiceFactory.getDefaultCryptoService();
        McbpLoggerFactory.setInstance(new UnitTestMcbpLoggerFactory(), null);
        ldeService = new MockLdeRemoteManagementService();
        ldeService.insertDataEncryptionKey(ByteArray.of("FF"));
        ldeService.insertMacKey(ByteArray.of("FF"));
        ldeService.insertTransportKey(ByteArray.of("FF"));
        ldeService.insertMobileKeySetId("1234");


    }

    @Before
    public void setUp() throws Exception {
        expectedRetryRemain = 3;
    }

    @Test
    public void retryTestFor500() {

        mRetryEventListener = new RetryEventListener() {
            @Override
            public void onRetry(final int retryRemain) {
                System.out.println("Retry count: " + retryRemain);
                expectedRetryRemain--;
                // this logic is only to reproduce correct http response on specific retry remain
                // count in MockHttpFactory
                MockHttpFactory.expectedRemainRetryCount = expectedRetryRemain;
                Assert.assertEquals(expectedRetryRemain, retryRemain);

            }
        };

        final MdesCommunicator communicator = getCommunicator(RETRY_FOR_SC_INTERNAL_SERVER_ERROR);
        RemoteManagementSessionData sessionData = new RemoteManagementSessionData();
        sessionData.setSessionCode(ByteArray.of("FF"));

        try {
            final ByteArray communicate =
                    communicator.communicate(SessionContext.of(sessionData), "data", "url", false);
            Assert.assertEquals("ABAB", ByteArray.of(communicate).toHexString());
        } catch (HttpException e) {
            Assert.assertEquals(MockHttpFactory.COMMUNICATION_ERROR, e.getMessage());
            Assert.assertEquals(500, e.getErrorCode());
        } finally {
            Assert.assertEquals(1, expectedRetryRemain);
        }
    }

    private MdesCommunicator getCommunicator(RetryType retryType) {

        final MockHttpFactory mockHttpFactory =
                new MockHttpFactory(retryType);

        return new MdesCommunicator(mockHttpFactory,
                                    cryptoService,
                                    ldeService, mRetryEventListener)
                .withDeviceFingerPrint(ByteArray.of("FF"));
    }

    @Test
    public void retryTestFor503() {

        final MdesCommunicator communicator = getCommunicator(RETRY_FOR_SC_SERVICE_UNAVAILABLE);
        RemoteManagementSessionData sessionData = new RemoteManagementSessionData();
        sessionData.setSessionCode(ByteArray.of("FF"));

        try {
            communicator.communicate(SessionContext.of(sessionData), "data", "url", true);
        } catch (HttpException e) {
            Assert.assertEquals(MockHttpFactory.COMMUNICATION_ERROR, e.getMessage());
            Assert.assertEquals(503, e.getErrorCode());
        } finally {
            Assert.assertEquals(0, expectedRetryRemain);
        }
    }

    @Test
    public void retryTestFor408() {

        final MdesCommunicator communicator = getCommunicator(RETRY_FOR_REQUEST_TIMEOUT);
        RemoteManagementSessionData sessionData = new RemoteManagementSessionData();
        sessionData.setSessionCode(ByteArray.of("FF"));

        try {
            communicator.communicate(SessionContext.of(sessionData), "data", "url", true);
        } catch (HttpException e) {
            Assert.assertEquals(MockHttpFactory.COMMUNICATION_ERROR, e.getMessage());
            Assert.assertEquals(408, e.getErrorCode());
        } finally {
            Assert.assertEquals(0, expectedRetryRemain);
        }
    }

    @Test
    public void retryTestForSslError() {

        final MdesCommunicator communicator = getCommunicator(RETRY_FOR_SSL_ERROR);
        RemoteManagementSessionData sessionData = new RemoteManagementSessionData();
        sessionData.setSessionCode(ByteArray.of("FF"));

        try {
            communicator.communicate(SessionContext.of(sessionData), "data", "url", true);
        } catch (HttpException e) {
            Assert.assertEquals(MockHttpFactory.COMMUNICATION_ERROR, e.getMessage());
            Assert.assertEquals(1106, e.getErrorCode());
        } finally {
            Assert.assertEquals(0, expectedRetryRemain);
        }
    }

    @Test
    public void retryTestFor302() {

        final MdesCommunicator communicator = getCommunicator(RETRY_FOR_SC_MOVED_TEMPORARILY);
        RemoteManagementSessionData sessionData = new RemoteManagementSessionData();
        sessionData.setSessionCode(ByteArray.of("FF"));

        try {
            communicator.communicate(SessionContext.of(sessionData), "data", "url", true);
        } catch (HttpException e) {
            Assert.assertEquals(MockHttpFactory.COMMUNICATION_ERROR, e.getMessage());
            Assert.assertEquals(302, e.getErrorCode());
        } finally {
            Assert.assertEquals(0, expectedRetryRemain);
        }
    }

    @Test
    public void retryTestFor504() {

        final MdesCommunicator communicator = getCommunicator(RETRY_FOR_GATEWAY_TIMEOUT);
        RemoteManagementSessionData sessionData = new RemoteManagementSessionData();
        sessionData.setSessionCode(ByteArray.of("FF"));

        try {
            communicator.communicate(SessionContext.of(sessionData), "data", "url", true);
        } catch (HttpException e) {
            Assert.assertEquals(MockHttpFactory.COMMUNICATION_ERROR, e.getMessage());
            Assert.assertEquals(504, e.getErrorCode());
        } finally {
            Assert.assertEquals(0, expectedRetryRemain);
        }

    }

    @Test
    public void retryTestNoRetry() {

        final MdesCommunicator communicator = getCommunicator(NO_RETRY);
        RemoteManagementSessionData sessionData = new RemoteManagementSessionData();
        sessionData.setSessionCode(ByteArray.of("FF"));

        try {
            communicator.communicate(SessionContext.of(sessionData), "data", "url", true);
        } catch (HttpException e) {
            Assert.assertEquals(MockHttpFactory.COMMUNICATION_ERROR, e.getMessage());
            Assert.assertEquals(0, e.getErrorCode());
        } finally {
            Assert.assertEquals(3, expectedRetryRemain);
        }
    }

    public enum RetryType {
        RETRY_FOR_SSL_ERROR,
        RETRY_FOR_SC_MOVED_TEMPORARILY,
        RETRY_FOR_SC_INTERNAL_SERVER_ERROR,
        RETRY_FOR_SC_SERVICE_UNAVAILABLE,
        RETRY_FOR_REQUEST_TIMEOUT,
        RETRY_FOR_GATEWAY_TIMEOUT,
        SC_REQUEST_TIMEOUT,
        NO_RETRY
    }
}