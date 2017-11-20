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

package com.mastercard.mcbp.card.profile;

/**
 * Enum to store pin state of card or wallet.
 */
public enum PinState {

    /**
     * PIN not set state
     */
    PIN_NOT_SET(0),

    /**
     * PIN set state
     */
    PIN_SET(1);

    private final int pinState;

    /**
     * Constructor
     *
     * @param state integer value of pin state.
     */
    PinState(int state) {
        this.pinState = state;
    }

    /**
     * @return equivalent integer value for Pin state.
     */
    public int getValue() {
        return pinState;
    }

    /**
     * Integer to {@link PinState} conversion.
     *
     * @param value integer value.
     * @return equivalent PinState.0 is value for PIN_NOT_SET & 1 is value of PIN_SET. For all
     * other value method returns PIN_NOT_SET.
     */
    public static PinState valueOf(int value) {
        switch (value) {
            case 0:
                return PIN_NOT_SET;
            case 1:
                return PIN_SET;
            default:
                return PIN_NOT_SET;
        }
    }
}
