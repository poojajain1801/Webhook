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

package com.mastercard.mcbp.card.mpplite.mcbpv1;

import com.mastercard.mcbp.card.credentials.SingleUseKey;
import com.mastercard.mcbp.card.credentials.SingleUseKeyContent;
import com.mastercard.mobile_api.bytes.ByteArray;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.net.URI;
import java.util.LinkedHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Utility class to load test keys from a resource file
 */
public class TestKeys {
    /**
     * URI pointing to the resource file where test keys are stored
     */
    private URI uri;

    /**
     * Data structure which contains keys
     */
    private LinkedHashMap<String, LinkedHashMap<String, SingleUseKey>> keysStore;

    /**
     * Data structure which contains the Mobile Pin for each card / atc pair
     */
    private LinkedHashMap<String, LinkedHashMap<String, String>> pinStore;

    /**
     * Constructor.
     *
     * @param uri The URI of the file where test keys are stored
     */
    public TestKeys(URI uri) {
        if (uri == null) return;
        this.uri = uri;
        keysStore = new LinkedHashMap<>();
        pinStore = new LinkedHashMap<>();
        readKeys();
    }

    /**
     * Return the SUK with a given ATC for a given card name
     *
     * @param cardName The name of the card for which the SUK is being retrieved
     * @param atc      The ATC value of the SUK being retrieved
     * @return The entire SingleUseKey
     */
    public SingleUseKey getSuk(String cardName, String atc) {
        // Need to return a copy as the MPP Lite may destroy the keys after use.

        final LinkedHashMap<String, SingleUseKey> stringSingleUseKeyLinkedHashMap =
                keysStore.get(cardName);
        if (stringSingleUseKeyLinkedHashMap == null) {
            return null;
        }
        SingleUseKey original = stringSingleUseKeyLinkedHashMap.get(atc);
        String idn = original.getContent().getIdn().toHexString();
        String sClUmd = original.getContent().getSukContactlessUmd().toHexString();
        String sClMd = original.getContent().getSessionKeyContactlessMd().toHexString();
        String sRpUmd = original.getContent().getSukRemotePaymentUmd().toHexString();
        String sRpMd = original.getContent().getSessionKeyRemotePaymentMd().toHexString();

        return createSuk(atc, idn, sClUmd, sClMd, sRpUmd, sRpMd);
    }


    public String getPin(String cardName, String atc) {
        return pinStore.get(cardName).get(atc);
    }

    /**
     * Read keys from file and store them in memory
     */
    private boolean readKeys() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document document = null;

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(new File(uri));
        } catch (ParserConfigurationException | org.xml.sax.SAXException | java.io.IOException e) {
            e.printStackTrace();
        }

        assert document != null;
        assert document.getDocumentElement() != null;
        document.getDocumentElement().normalize();

        Element root = document.getDocumentElement();
        if (root == null) return false;

        NodeList keys = root.getChildNodes();

        for (int i = 0; i < keys.getLength(); i++) {
            readKeyValue(keys.item(i));
        }
        return true;
    }

    /**
     * Read the entire set of credentials for a given card
     * <p/>
     * Note: the implementation could be optimized, but given it is only used by the unit test
     * we will keep as it is for the time being.
     */
    private boolean readKeyValue(Node keys) {

        if (keys.getNodeType() == Node.ELEMENT_NODE) {
            Element eElement = (Element) keys;
            String cardName = eElement.getAttribute("card_name");
            NodeList credentials = keys.getChildNodes();
            LinkedHashMap<String, SingleUseKey> cardCredentials = new LinkedHashMap<>();
            LinkedHashMap<String, String> cardPin = new LinkedHashMap<>();
            for (int i = 0; i < credentials.getLength(); i++) {
                Node node = credentials.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element elem = (Element) node;
                    String atc = elem.getElementsByTagName("atc").item(0).getTextContent();
                    String idn = elem.getElementsByTagName("idn").item(0).getTextContent();
                    String sClUmd = elem.getElementsByTagName("sk_cl_umd").item(0).getTextContent();
                    String sClMd = elem.getElementsByTagName("sk_cl_md").item(0).getTextContent();
                    String sRpUmd = elem.getElementsByTagName("sk_rp_umd").item(0).getTextContent();
                    String sRpMd = elem.getElementsByTagName("sk_rp_umd").item(0).getTextContent();
                    String pin = elem.getElementsByTagName("pin").item(0).getTextContent();

                    cardCredentials.put(atc, createSuk(atc, idn, sClUmd, sClMd, sRpUmd, sRpMd));
                    cardPin.put(atc, pin);
                }
                keysStore.put(cardName, cardCredentials);
                pinStore.put(cardName, cardPin);
            }
        }
        return true;
    }

    // Utility function to generate a SingleUseKey object
    private SingleUseKey createSuk(String atc, String idn, String sClUmd,
                                   String sClMd, String sRpUmd, String sRpMd) {
        SingleUseKeyContent content = new SingleUseKeyContent();

        content.setAtc(ByteArray.of(atc));
        content.setIdn(ByteArray.of(idn));
        content.setSukContactlessUmd(ByteArray.of(sClUmd));
        content.setSessionKeyContactlessMd(ByteArray.of(sClMd));
        content.setSukRemotePaymentUmd(ByteArray.of(sRpUmd));
        content.setSessionKeyRemotePaymentMd(ByteArray.of(sRpMd));
        content.setHash(ByteArray.of("00"));
        content.setInfo(ByteArray.of("38"));

        SingleUseKey suk = new SingleUseKey();
        suk.setContent(content);

        return suk;
    }
}
