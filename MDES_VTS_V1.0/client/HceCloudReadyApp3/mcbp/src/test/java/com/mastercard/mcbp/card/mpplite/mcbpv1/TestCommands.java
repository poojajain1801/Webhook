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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Insert class description here
 */
public class TestCommands {
    /**
     * URI pointing to the resource file where test mAtc are stored
     */
    private URI mUri;

    /**
     * Data structure which contains the list of mCommands for each test
     * < CardName, <TestName, Commands> >
     */
    private Map<String, Map<String, List<CommandResponseApdu>>> mCommands;

    /**
     * Data structure containing a reference to mAtc to be used for each test
     * < CardName, <TestName, KeyID>>
     */
    private Map<String, Map<String, String>> mAtc;

    /**
     * Data structure containing a reference to mCvmEntered to be used for each test
     * < CardName, <TestName, KeyID>>
     */
    private Map<String, Map<String, Boolean>> mCvmEntered;

    /**
     * Data structure containing a reference to mAmount to be used for each test
     * < CardName, <TestName, KeyID>>
     */
    private Map<String, Map<String, Long>> mAmount;

    /**
     * Data structure containing a reference to mCurrencyCode to be used for each test
     * < CardName, <TestName, KeyID>>
     */
    private Map<String, Map<String, Integer>> mCurrencyCode;

    /**
     * Data structure containing a reference to mAtc to be used for each test
     * < CardName, <TestName, KeyID>>
     */
    private Map<String, Map<String, String>> mMobilePin;

    /**
     * Data structure which contains the list of all tests
     */
    private ArrayList<String[]> mApduTestNameList;

    /**
     * Constructor.
     *
     * @param uri The URI of the file where test mAtc are stored
     */
    public TestCommands(URI uri) {
        if (uri == null) return;
        this.mUri = uri;
        mCommands = new LinkedHashMap<>();
        mAtc = new LinkedHashMap<>();
        mCvmEntered = new LinkedHashMap<>();
        mApduTestNameList = new ArrayList<>();
        mMobilePin = new LinkedHashMap<>();
        mAmount = new LinkedHashMap<>();
        mCurrencyCode = new LinkedHashMap<>();
        readTestCommands();
    }

    /**
     * Get the list of C-APDU / R-APDU for a given test of a given card
     *
     * @return the List of Command/Response APDUs for the current test
     */
    public List<CommandResponseApdu> getApduList(String cardName, String testName) {
        if (mCommands.get(cardName) == null) return null;
        return mCommands.get(cardName).get(testName);
    }

    /**
     * Get the Key ATC to be used for this test
     */
    public String getKeyAtc(String cardName, String testName) {
        if (mAtc.get(cardName) == null) return null;
        return mAtc.get(cardName).get(testName);
    }

    /**
     * Get the Mobile Pin to be used for this test
     */
    public String getMobilePin(String cardName, String testName) {
        if (mMobilePin.get(cardName) == null) return null;
        return mMobilePin.get(cardName).get(testName);
    }

    /**
     * Get the Cvm entered to be used for this test
     */
    public boolean isCvmEntered(String cardName, String testName) {
        if (mCvmEntered.get(cardName) == null) return true;
        return mCvmEntered.get(cardName).get(testName);
    }

    /**
     * Get the amount entered to be used for this test
     */
    public Long getAmount(String cardName, String testName) {
        if (mAmount.get(cardName) == null) return null;
        return mAmount.get(cardName).get(testName);
    }

    /**
     * Get the currency code entered to be used for this test
     */
    public Integer getCurrencyCode(String cardName, String testName) {
        if (mCurrencyCode.get(cardName) == null) return null;
        return mCurrencyCode.get(cardName).get(testName);
    }

    /**
     * Return a list of cardName / test Name
     */
    public Collection<String[]> getApduTestNameList() {
        String[][] parameters = new String[mApduTestNameList.size()][2];
        for (int i = 0; i < mApduTestNameList.size(); i++) {
            parameters[i] = mApduTestNameList.get(i);
        }
        return Arrays.asList(parameters);
    }

    /**
     * Read APDU mCommands from an xml file
     */
    private boolean readTestCommands() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document document = null;

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(new File(mUri));
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
            // For each card
            readCommands(keys.item(i));
        }
        return true;
    }

    /**
     * Read the entire set of credentials for a given card
     * <p>
     * Note: the implementation could be optimized, but given it is only used by the unit test
     * we will keep as it is for the time being.
     */
    private boolean readCommands(Node cards) {
        if (cards.getNodeType() == Node.ELEMENT_NODE) {
            Element eElement = (Element) cards;
            String cardName = eElement.getAttribute("name");

            // Create the data structure to support this card
            mCommands.put(cardName, new LinkedHashMap<String, List<CommandResponseApdu>>());
            mAtc.put(cardName, new LinkedHashMap<String, String>());
            mMobilePin.put(cardName, new LinkedHashMap<String, String>());
            mCvmEntered.put(cardName, new LinkedHashMap<String, Boolean>());
            mAmount.put(cardName, new LinkedHashMap<String, Long>());
            mCurrencyCode.put(cardName, new LinkedHashMap<String, Integer>());

            // Now get the tests for this card
            NodeList tests = cards.getChildNodes();
            // Process the list of <tests>
            for (int i = 0; i < tests.getLength(); i++) {
                if (tests.item(i).getNodeType() != Node.ELEMENT_NODE) continue;
                Node node = tests.item(i);
                Element testElem = (Element) node;
                String testName = testElem.getAttribute("name");
                String testType = testElem.getAttribute("type");
                // Create the data structure to support this test
                mCommands.get(cardName).put(testName, new ArrayList<CommandResponseApdu>());
                if (testType.equals("apdu")) {
                    // Add the test to the list
                    mApduTestNameList.add(new String[]{cardName, testName});
                    readAtcs(node, cardName, testName);
                    readCvmEntered(node, cardName, testName);
                    readAmount(node, cardName, testName);
                    readCurrencyCode(node, cardName, testName);
                    readMobilePin(node, cardName, testName);
                    readDependencies(node, cardName, testName);
                    readApdus(node, cardName, testName);
                }
            }
        }
        return true;
    }

    /**
     * Read currency code entered for a given test case
     */
    private void readCurrencyCode(final Node test, final String cardName, final String testName) {
        Map<String, Integer> currentTest = this.mCurrencyCode.get(cardName);

        if (test.getNodeType() != Node.ELEMENT_NODE) return;

        NodeList keys = ((Element) test).getElementsByTagName("currencyCode");
        if (keys == null || keys.getLength() < 1) {
            currentTest.put(testName, null);
        } else {
            Element elem = (Element) keys.item(0);
            currentTest.put(testName, Integer.parseInt(elem.getTextContent()));
        }
    }

    /**
     * Read amount entered for a given test case
     */
    private void readAmount(final Node test, final String cardName, final String testName) {
        Map<String, Long> currentTest = this.mAmount.get(cardName);

        if (test.getNodeType() != Node.ELEMENT_NODE) return;

        NodeList keys = ((Element) test).getElementsByTagName("amount");
        if (keys == null || keys.getLength() < 1) {
            currentTest.put(testName, null);
        } else {
            Element elem = (Element) keys.item(0);
            currentTest.put(testName, Long.parseLong(elem.getTextContent()));
        }
    }

    /**
     * Read if cvm is entered for a given test case
     */
    private void readCvmEntered(final Node test, final String cardName, final String testName) {
        Map<String, Boolean> currentTest = this.mCvmEntered.get(cardName);

        if (test.getNodeType() != Node.ELEMENT_NODE) return;

        NodeList keys = ((Element) test).getElementsByTagName("cvm");
        if (keys == null || keys.getLength() < 1) {
            currentTest.put(testName, true);
        } else {
            Element elem = (Element) keys.item(0);
            currentTest.put(testName, Boolean.valueOf(elem.getTextContent()));
        }
    }

    /**
     * Read the mAtc ID for a given test case
     */
    private void readAtcs(Node test, String cardName, String testName) {
        Map<String, String> currentTest = this.mAtc.get(cardName);

        if (test.getNodeType() != Node.ELEMENT_NODE) return;

        NodeList keys = ((Element) test).getElementsByTagName("atc");
        if (keys == null || keys.getLength() < 1) {
            currentTest.put(testName, "0001");
        } else {
            Element elem = (Element) keys.item(0);
            currentTest.put(testName, elem.getTextContent());
        }
    }

    /**
     * Read the mAtc ID for a given test case
     */
    private void readMobilePin(Node test, String cardName, String testName) {
        Map<String, String> currentTest = this.mMobilePin.get(cardName);

        if (test.getNodeType() != Node.ELEMENT_NODE) return;
        NodeList keys = ((Element) test).getElementsByTagName("pin");
        if (keys == null || keys.getLength() < 1) {
            currentTest.put(testName, "31323334");
        } else {
            Element elem = (Element) keys.item(0);
            currentTest.put(testName, elem.getTextContent());
        }
    }

    /**
     * Read all the dependencies for a given test case
     */
    private void readDependencies(Node test, String cardName, String testName) {
        List<CommandResponseApdu> apduList = mCommands.get(cardName).get(testName);
        if (test.getNodeType() != Node.ELEMENT_NODE) return;

        NodeList dependencies = ((Element) test).getElementsByTagName("dependency");

        for (int i = 0; i < dependencies.getLength(); i++) {
            if (test.getNodeType() != Node.ELEMENT_NODE) continue;
            Element elem = (Element) dependencies.item(i);
            // Add all the elements from this dependency
            apduList.addAll(mCommands.get(cardName).get(elem.getTextContent()));
        }
    }

    /**
     * Read a pair of C-APDU/R-APDU for a given test
     */
    private void readApdus(Node test, String cardName, String testName) {
        List<CommandResponseApdu> apduList = mCommands.get(cardName).get(testName);
        if (test.getNodeType() != Node.ELEMENT_NODE) return;

        NodeList commandApdus = ((Element) test).getElementsByTagName("c_apdu");
        NodeList responseApdus = ((Element) test).getElementsByTagName("r_apdu");

        if (commandApdus.getLength() != responseApdus.getLength()) {
            throw new RuntimeException("Invalid pairs C-APDU/R-APDU");
        }

        for (int i = 0; i < commandApdus.getLength(); i++) {
            if (test.getNodeType() != Node.ELEMENT_NODE) continue;
            Element elemCommand = (Element) commandApdus.item(i);
            Element elemResponse = (Element) responseApdus.item(i);
            String commandApdu = elemCommand.getTextContent();
            String responseApdu = elemResponse.getTextContent();
            apduList.add(new CommandResponseApdu(commandApdu, responseApdu));
        }
    }
}
