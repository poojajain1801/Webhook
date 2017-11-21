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

package com.mastercard.mcbp.utils;

import java.util.Map;
import java.util.Set;

public abstract class PropertyStorageFactory {

    private static PropertyStorageFactory INSTANCE;

    public static PropertyStorageFactory getInstance() {
        return INSTANCE;
    }

    public static void setInstance(PropertyStorageFactory instance) {
        INSTANCE = instance;
    }

    public abstract void putProperty(String key, String value);

    public abstract void putPropertySet(String key, Set<String> value);

    public abstract String getProperty(String key, String defaultValue);

    public abstract Set<String> getPropertySet(String key, Set<String> defaultValue);

    public abstract boolean isContainsKey(String key);

    public abstract void removeProperty(String key);

    public abstract Map<String, ?> getAll();

    public abstract void removeAll();
}
