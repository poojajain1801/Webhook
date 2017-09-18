#!/bin/sh
$ANDROID_HOME/platform-tools/adb push ./test_keys.xml           /data/local/tmp
$ANDROID_HOME/platform-tools/adb push ./test_cards.xml          /data/local/tmp
$ANDROID_HOME/platform-tools/adb push ./mcbp_card_test_data.xml /data/local/tmp

