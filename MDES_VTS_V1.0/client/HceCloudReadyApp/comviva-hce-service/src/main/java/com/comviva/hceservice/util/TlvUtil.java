package com.comviva.hceservice.util;

public class TlvUtil {
    public static int getAmountAuthIndex(byte[] pdolValue) {
        int tagLen;
        int firstLenByte;
        int cursor = 0;
        int pdolIndex = 0;
        int currentTag;
        while (cursor < pdolValue.length) {
            currentTag = getCurrentTag(pdolValue, cursor);

            if(currentTag == 0x9F02) {
                break;
            }
            tagLen = getTagLength(pdolValue[cursor]);
            firstLenByte = pdolValue[cursor+tagLen];
            if(firstLenByte <= 0x7F) {
                pdolIndex += pdolValue[cursor+tagLen];
                cursor += tagLen + 1;
            } else {
                switch (firstLenByte) {
                    case 0x81:
                        pdolIndex += pdolValue[cursor+tagLen+1] & 0xFF;
                        cursor += tagLen + 1;
                        break;

                    case 0x82:
                        pdolIndex += ((pdolValue[cursor+tagLen+1] & 0xFF) << 8) | (pdolValue[cursor+tagLen+2] & 0xFF);
                        cursor += tagLen + 2;
                        break;

                    case 0x83:
                        pdolIndex += (((pdolValue[cursor+tagLen+1] & 0xFF) << 16) | ((pdolValue[cursor+tagLen+2] & 0xFF) << 8) | (pdolValue[cursor+tagLen+3] & 0xFF));
                        cursor += tagLen + 3;
                        break;
                }
            }
        }
        return pdolIndex;
    }

    private static int getTagLength(byte firstByte) {
        if((firstByte & 0x1F) != 0x1F) {
            return 1;
        } else {
            return 2;
        }
    }

    private static int getCurrentTag(byte[] tlv, int offset) {
        if((tlv[offset] & 0x1F) != 0x1F) {
            return tlv[offset] & 0xFF;
        } else {
            return (((tlv[offset] & 0xFF) << 8) | (tlv[offset+1] & 0xFF));
        }
    }

    private static int getTagValue(byte[] tlv) {
        if((tlv[0] & 0x1F) != 0x1F) {
            return 1;
        } else {
            return 2;
        }
    }

    private static int getTlvDataLength(byte[] tlv, int offsetLength) {
        int firstLenByte = tlv[offsetLength] & 0xFF;

        if(firstLenByte <= 0x7F) {
            return tlv[offsetLength];
        } else {
            switch (firstLenByte) {
                case 0x81:
                    return tlv[offsetLength+1] & 0xFF;

                case 0x82:
                    return ((tlv[offsetLength+1] & 0xFF) << 8) | (tlv[offsetLength+2] & 0xFF);

                case 0x83:
                    return (((tlv[offsetLength+1] & 0xFF) << 16) | ((tlv[offsetLength+2] & 0xFF) << 8) | (tlv[offsetLength+3] & 0xFF));
            }
        }
        return -1;
    }

}
