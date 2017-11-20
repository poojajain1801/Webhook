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
 * Enum to store various profile states.
 */
public enum ProfileState {
    /**
     * Profile un-initialized state
     */
    UNINITIALIZED(0),
    /**
     * Profile initialize or Un-suspend state
     */
    INITIALIZED(1),
    /**
     * Profile suspend state.
     */
    SUSPENDED(2);

    /**
     * Equivalent int value for Enum constants.
     */
    final int value;

    ProfileState(int state){
        value = state;
    }

    /**
     * Get Enum int value.
     * @return Equivalent int value.
     */
    public int getValue(){
        return value;
    }

    /**
     * Int to Profile state conversion.
     * @param value Integer value.
     * @return Equivalent Profile state value.
     */
    public static ProfileState valueOf(int value) {
        switch (value) {
            case 0:
                return UNINITIALIZED;
            case 1:
                return INITIALIZED;
            case 2:
                return SUSPENDED;
            default:
                return UNINITIALIZED;
        }

    }
}
