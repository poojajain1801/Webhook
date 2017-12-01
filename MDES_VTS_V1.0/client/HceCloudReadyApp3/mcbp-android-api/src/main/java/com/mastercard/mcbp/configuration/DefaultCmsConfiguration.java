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

package com.mastercard.mcbp.configuration;

import com.mastercard.mcbp.remotemanagement.CmsConfiguration;

/**
 * Default class implementing the configuration required for the CMS service
 */
public class DefaultCmsConfiguration implements CmsConfiguration {

    /**
     * URL of where to find the CMS service
     */
    public static String URL = "http://ech-10-157-132-80.devcloud.mastercard.com/cms";

    /**
     * Initialise the default configuration
     */
    public DefaultCmsConfiguration() {

    }

    /**
     * Initialise the default configuration with a specific issuer identifier and CMS service
     * location
     *
     * @param url URL of where to find the CMS service
     */
    public DefaultCmsConfiguration(final String url) {
        URL = url;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String urlInit() {
        return URL;
    }
}
