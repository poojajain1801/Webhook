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

package com.mastercard.mcbp.core;

import android.content.Context;
import android.content.SharedPreferences;

import com.mastercard.mcbp.utils.PropertyStorageFactory;

import java.util.Map;
import java.util.Set;

/**
 * Custom implementation to manage shared preference features
 */
public class AndroidPropertyStorageFactoryImpl extends PropertyStorageFactory {
    private static final String PREFERENCE_STORE_NAME = "sdk-preference";
    private SharedPreferences mSharedPreferences;

    public AndroidPropertyStorageFactoryImpl(Context context) {
        this.mSharedPreferences = context.getSharedPreferences(PREFERENCE_STORE_NAME,
                                                               Context.MODE_PRIVATE);
    }

    @Override
    public void putProperty(final String key, final String value) {
        mSharedPreferences.edit().putString(key, value).apply();
    }

    @Override
    public void putPropertySet(final String key, final Set<String> value) {
        mSharedPreferences.edit().putStringSet(key, value).apply();
    }

    @Override
    public String getProperty(final String key, String defaultValue) {
        return mSharedPreferences.getString(key, defaultValue);
    }

    @Override
    public Set<String> getPropertySet(final String key, Set<String> defaultValue) {
        return mSharedPreferences.getStringSet(key, defaultValue);
    }

    @Override
    public boolean isContainsKey(final String key) {
        return mSharedPreferences.contains(key);
    }

    @Override
    public void removeProperty(final String key) {
        mSharedPreferences.edit().remove(key).apply();
    }

    @Override
    public Map<String, ?> getAll() {
        return mSharedPreferences.getAll();
    }

    @Override
    public void removeAll() {
        mSharedPreferences.edit().clear().apply();
    }
}
