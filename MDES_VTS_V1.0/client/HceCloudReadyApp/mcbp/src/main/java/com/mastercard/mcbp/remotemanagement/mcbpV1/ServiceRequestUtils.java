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

package com.mastercard.mcbp.remotemanagement.mcbpV1;

import java.util.HashMap;
import java.util.Map.Entry;

/**
 * @deprecated Use MDES build flavour instead
 * */
@Deprecated
public class ServiceRequestUtils {
    public static final String RESULTS_ID = "RESULT";
    public static final String RESET_MPA_ID = "RESETMPA";
    public static final String TRANSACTION_CREDENTIALS_USED_ID = "TRANSACTIONCREDENTIALSUSED";
    public static final String SUSPEND_ID = "SUSPEND";
    public static final String RESUME_ID = "RESUME";
    public static final String REMOTE_WIPE = "REMOTEWIPE";
    public static final String RESET_MOBILE_PIN_ID = "RESETMOBILEPIN";
    public static final String REPLENISH_ID = "REPLENISH";
    public static final String REGISTER_USER_ID = "REGISTERUSER";
    public static final String PROVISION_SUK_ID = "PROVISIONSUK";
    public static final String PROVISION_CP_ID = "PROVISIONCP";
    public static final String INITIALIZE_MPA_ID = "INITIALIZEMPA";
    public static final String GET_DEVICE_INFORMATION_ID = "GETDEVICEINFORMATION";
    public static final String DELETE_ID = "DELETE";
    public static final String CHANGE_MOBILE_PIN_ID = "CHANGEMOBILEPIN";
    public static final String ACTIVE_TRANSACTION_CREDENTIALS_ID = "ACTIVETRANSACTIONCREDENTIALS";
    public static final String ERROR_ID = "ERROR";


    private static HashMap<String, ServiceRequestEnum> serviceRequestValueMap;

    static {
        serviceRequestValueMap = new HashMap<>();
        serviceRequestValueMap.put(ACTIVE_TRANSACTION_CREDENTIALS_ID,
                                        ServiceRequestEnum.ACTIVETRANSACTIONCREDENTIALS);
        serviceRequestValueMap.put(CHANGE_MOBILE_PIN_ID, ServiceRequestEnum.CHANGEMOBILEPIN);
        serviceRequestValueMap.put(DELETE_ID, ServiceRequestEnum.DELETE);
        serviceRequestValueMap.
                put(GET_DEVICE_INFORMATION_ID, ServiceRequestEnum.GETDEVICEINFORMATION);
        serviceRequestValueMap.put(INITIALIZE_MPA_ID, ServiceRequestEnum.INITIALIZEMPA);
        serviceRequestValueMap.put(PROVISION_CP_ID, ServiceRequestEnum.PROVISIONCP);
        serviceRequestValueMap.put(PROVISION_SUK_ID, ServiceRequestEnum.PROVISIONSUK);
        serviceRequestValueMap.put(REGISTER_USER_ID, ServiceRequestEnum.REGISTERUSER);
        serviceRequestValueMap.put(REPLENISH_ID, ServiceRequestEnum.REPLENISH);
        serviceRequestValueMap.put(RESET_MOBILE_PIN_ID, ServiceRequestEnum.RESETMOBILEPIN);
        serviceRequestValueMap.put(RESULTS_ID, ServiceRequestEnum.RESULTS);
        serviceRequestValueMap.put(RESUME_ID, ServiceRequestEnum.RESUME);
        serviceRequestValueMap.put(REMOTE_WIPE, ServiceRequestEnum.REMOTEWIPE);
        serviceRequestValueMap.put(SUSPEND_ID, ServiceRequestEnum.SUSPEND);
        serviceRequestValueMap.put(TRANSACTION_CREDENTIALS_USED_ID,
                                        ServiceRequestEnum.TRANSACTIONCREDENTIALSUSED);
        serviceRequestValueMap.put(RESET_MPA_ID, ServiceRequestEnum.RESETMPA);
    }

    public static ServiceRequestEnum getServiceRequestIntValue(String serviceRequestId) {
        return serviceRequestValueMap.get(serviceRequestId);
    }

    public static String getServiceRequestId(ServiceRequestEnum serviceRequestValue) {
        for (Entry<String, ServiceRequestEnum> entry : serviceRequestValueMap.entrySet()) {
            if (serviceRequestValue.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public enum ServiceRequestEnum {
        ACTIVETRANSACTIONCREDENTIALS, CHANGEMOBILEPIN, DELETE, GETDEVICEINFORMATION, INITIALIZEMPA,
        PROVISIONCP, PROVISIONSUK, REGISTERUSER, REPLENISH, RESETMOBILEPIN, RESULTS, RESUME,
        SUSPEND, TRANSACTIONCREDENTIALSUSED, RESETMPA, REMOTEWIPE, NETWORK_ERROR, GET_TASK_STATUS
    }

}
