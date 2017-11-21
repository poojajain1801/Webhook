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

package com.mastercard.mcbp.transactiondecisionmanager.input;

import com.mastercard.mobile_api.bytes.ByteArray;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for the CVM Result container
 */
public class CvmResultsTest {

    private static CvmResults cvmResult(final String cvmResults, int msi) {
        final MobileSupportIndicator mobileSupportIndicator = MobileSupportIndicator.of((byte)msi);
        final ByteArray input = ByteArray.of(cvmResults);
        return CvmResults.of(input.getBytes(), mobileSupportIndicator);
    }

    @Test
    public void testIsNoCvmToBePerformed() throws Exception {
        // V2 Terminals
        Assert.assertEquals(true,  cvmResult("1F0000", 0x00).isNoCvmToBePerformed());
        Assert.assertEquals(false, cvmResult("1F0001", 0x00).isNoCvmToBePerformed());
        Assert.assertEquals(true,  cvmResult("1F0002", 0x00).isNoCvmToBePerformed());
        Assert.assertEquals(false, cvmResult("3F0002", 0x00).isNoCvmToBePerformed());
        Assert.assertEquals(false, cvmResult("3F0001", 0x00).isNoCvmToBePerformed());
        Assert.assertEquals(false, cvmResult("3F0000", 0x00).isNoCvmToBePerformed());
        Assert.assertEquals(false, cvmResult("2F0002", 0x00).isNoCvmToBePerformed());
        Assert.assertEquals(true,  cvmResult("DF0002", 0x00).isNoCvmToBePerformed());

        // V3 Terminals
        Assert.assertEquals(true,  cvmResult("3F0002", 0x01).isNoCvmToBePerformed());
        Assert.assertEquals(false, cvmResult("3F0000", 0x01).isNoCvmToBePerformed());
        Assert.assertEquals(false, cvmResult("3F0001", 0x01).isNoCvmToBePerformed());
        Assert.assertEquals(false, cvmResult("1F0000", 0x01).isNoCvmToBePerformed());
        Assert.assertEquals(true,  cvmResult("1F0002", 0x01).isNoCvmToBePerformed());
        Assert.assertEquals(false, cvmResult("1F0001", 0x01).isNoCvmToBePerformed());
        Assert.assertEquals(false, cvmResult("150000", 0x01).isNoCvmToBePerformed());
        Assert.assertEquals(false, cvmResult("150001", 0x01).isNoCvmToBePerformed());
        Assert.assertEquals(false, cvmResult("150002", 0x01).isNoCvmToBePerformed());
        Assert.assertEquals(true,  cvmResult("3F0002", 0x03).isNoCvmToBePerformed());
        Assert.assertEquals(false, cvmResult("3F0000", 0x03).isNoCvmToBePerformed());
        Assert.assertEquals(false, cvmResult("3F0001", 0x03).isNoCvmToBePerformed());
        Assert.assertEquals(false, cvmResult("1F0000", 0x03).isNoCvmToBePerformed());
        Assert.assertEquals(true,  cvmResult("1F0002", 0x03).isNoCvmToBePerformed());
        Assert.assertEquals(false, cvmResult("1F0001", 0x03).isNoCvmToBePerformed());
        Assert.assertEquals(false, cvmResult("150000", 0x03).isNoCvmToBePerformed());
        Assert.assertEquals(false, cvmResult("150001", 0x03).isNoCvmToBePerformed());
        Assert.assertEquals(false, cvmResult("150002", 0x03).isNoCvmToBePerformed());
        Assert.assertEquals(true,  cvmResult("3F0002", 0x02).isNoCvmToBePerformed());
        Assert.assertEquals(false, cvmResult("3F0000", 0x02).isNoCvmToBePerformed());
        Assert.assertEquals(false, cvmResult("3F0001", 0x02).isNoCvmToBePerformed());
        Assert.assertEquals(false, cvmResult("1F0000", 0x02).isNoCvmToBePerformed());
        Assert.assertEquals(true,  cvmResult("1F0002", 0x02).isNoCvmToBePerformed());
        Assert.assertEquals(false, cvmResult("1F0001", 0x02).isNoCvmToBePerformed());
        Assert.assertEquals(false, cvmResult("150000", 0x02).isNoCvmToBePerformed());
        Assert.assertEquals(false, cvmResult("150001", 0x02).isNoCvmToBePerformed());
        Assert.assertEquals(false, cvmResult("150002", 0x02).isNoCvmToBePerformed());
    }

    @Test
    public void testIsOnlinePinToBePerformed() throws Exception {
        Assert.assertEquals(true,  cvmResult("020000", 0x00).isOnlinePinToBePerformed());
        Assert.assertEquals(false, cvmResult("020001", 0x00).isOnlinePinToBePerformed());
        Assert.assertEquals(false, cvmResult("020002", 0x00).isOnlinePinToBePerformed());
    }

    @Test
    public void testIsSignatureToBePerformed() throws Exception {
        Assert.assertEquals(true,  cvmResult("1E0000", 0x00).isSignatureToBePerformed());
        Assert.assertEquals(false, cvmResult("1E0001", 0x00).isSignatureToBePerformed());
        Assert.assertEquals(false, cvmResult("1E0002", 0x00).isSignatureToBePerformed());
        Assert.assertEquals(true,  cvmResult("030000", 0x00).isSignatureToBePerformed());
        Assert.assertEquals(true,  cvmResult("050000", 0x00).isSignatureToBePerformed());
        Assert.assertEquals(false, cvmResult("030001", 0x00).isSignatureToBePerformed());
        Assert.assertEquals(false, cvmResult("050001", 0x00).isSignatureToBePerformed());
        Assert.assertEquals(true,  cvmResult("030002", 0x00).isSignatureToBePerformed());
        Assert.assertEquals(true,  cvmResult("050002", 0x00).isSignatureToBePerformed());
    }

    @Test
    public void testIsSignatureOnlyToBePerformed() throws Exception {
        Assert.assertEquals(true,  cvmResult("1E0000", 0x00).isSignatureOnlyToBePerformed());
        Assert.assertEquals(false, cvmResult("1E0001", 0x00).isSignatureOnlyToBePerformed());
        Assert.assertEquals(false, cvmResult("1E0002", 0x00).isSignatureOnlyToBePerformed());
    }

    @Test
    public void testIsCdCvmAndSignatureToBePerformed() throws Exception {
        Assert.assertEquals(true,  cvmResult("030000", 0x00).isCdCvmAndSignatureToBePerformed());
        Assert.assertEquals(true,  cvmResult("050000", 0x00).isCdCvmAndSignatureToBePerformed());
        Assert.assertEquals(false, cvmResult("030001", 0x00).isCdCvmAndSignatureToBePerformed());
        Assert.assertEquals(false, cvmResult("050001", 0x00).isCdCvmAndSignatureToBePerformed());
        Assert.assertEquals(true,  cvmResult("030002", 0x00).isCdCvmAndSignatureToBePerformed());
        Assert.assertEquals(true,  cvmResult("050002", 0x00).isCdCvmAndSignatureToBePerformed());
    }

    @Test
    public void testIsCdCvmOnlyToBePerformed() throws Exception {
        Assert.assertEquals(false, cvmResult("010000", 0x00).isCdCvmOnlyToBePerformed());
        Assert.assertEquals(false, cvmResult("040000", 0x00).isCdCvmOnlyToBePerformed());
        Assert.assertEquals(false, cvmResult("010001", 0x00).isCdCvmOnlyToBePerformed());
        Assert.assertEquals(false, cvmResult("040001", 0x00).isCdCvmOnlyToBePerformed());
        Assert.assertEquals(true,  cvmResult("010002", 0x00).isCdCvmOnlyToBePerformed());
        Assert.assertEquals(true,  cvmResult("040002", 0x00).isCdCvmOnlyToBePerformed());
    }

    @Test
    public void testIsCdCvmToBePerformed() throws Exception {
        Assert.assertEquals(false, cvmResult("010000", 0x00).isCdCvmToBePerformed());
        Assert.assertEquals(false, cvmResult("040000", 0x00).isCdCvmToBePerformed());
        Assert.assertEquals(false, cvmResult("010001", 0x00).isCdCvmToBePerformed());
        Assert.assertEquals(false, cvmResult("040001", 0x00).isCdCvmToBePerformed());
        Assert.assertEquals(true,  cvmResult("010002", 0x00).isCdCvmToBePerformed());
        Assert.assertEquals(true,  cvmResult("040002", 0x00).isCdCvmToBePerformed());
        Assert.assertEquals(true,  cvmResult("030000", 0x00).isCdCvmToBePerformed());
        Assert.assertEquals(true,  cvmResult("050000", 0x00).isCdCvmToBePerformed());
        Assert.assertEquals(false, cvmResult("030001", 0x00).isCdCvmToBePerformed());
        Assert.assertEquals(false, cvmResult("050001", 0x00).isCdCvmToBePerformed());
        Assert.assertEquals(true,  cvmResult("030002", 0x00).isCdCvmToBePerformed());
        Assert.assertEquals(true,  cvmResult("050002", 0x00).isCdCvmToBePerformed());
    }

    @Test
    public void testIsCvmResultSuccess() throws Exception {
        Assert.assertEquals(false, cvmResult("010000", 0x00).isCvmResultSuccess());
        Assert.assertEquals(false, cvmResult("010001", 0x00).isCvmResultSuccess());
        Assert.assertEquals(true,  cvmResult("010002", 0x00).isCvmResultSuccess());
    }

    @Test
    public void testIsCvmResultUnknown() throws Exception {
        Assert.assertEquals(true,  cvmResult("FF0000", 0x00).isCvmResultUnknown());
        Assert.assertEquals(false, cvmResult("FF0001", 0x00).isCvmResultUnknown());
        Assert.assertEquals(false, cvmResult("FF0002", 0x00).isCvmResultUnknown());
    }
}