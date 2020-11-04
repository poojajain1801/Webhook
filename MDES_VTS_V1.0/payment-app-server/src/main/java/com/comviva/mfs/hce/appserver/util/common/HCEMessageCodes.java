package com.comviva.mfs.hce.appserver.util.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Constants contains message code
 */
@Configuration
@PropertySource("classpath:HCEcodes.properties")
public class HCEMessageCodes {

    /** The Constant SERVICE_FAILED. */
    private static String SERVICE_FAILED;
    @Value("${SERVICE_FAILED}")
    public void setServiceFailed(String serviceFailed){
        SERVICE_FAILED = serviceFailed;
    }
    public static String getServiceFailed() {
        return SERVICE_FAILED;
    }

    private static String INVALID_TOKEN_UNIQUE_REFERENCE;
    @Value("${INVALID_TOKEN_UNIQUE_REFERENCE}")
    public void setInvalidTokenUniqueReference(String invalidTokenUniqueReference){
        INVALID_TOKEN_UNIQUE_REFERENCE = invalidTokenUniqueReference ;
    }
    public static String getInvalidTokenUniqueReference() {
        return INVALID_TOKEN_UNIQUE_REFERENCE;
    }


    private static String TOKEN_NOT_REGISTERED;
    @Value("${TOKEN_NOT_REGISTERED}")
    public void setTokenNotRegistered(String tokenNotRegistered){
        TOKEN_NOT_REGISTERED = tokenNotRegistered;
    }
    public static String getTokenNotRegistered() {
        return TOKEN_NOT_REGISTERED;
    }

    private static String INCORRECT_CODE_RETRIES_EXCEEDED;
    @Value("${INCORRECT_CODE_RETRIES_EXCEEDED}")
    public void setIncorrectCodeRetriesExceeded(String incorrectOTP){
        INCORRECT_CODE_RETRIES_EXCEEDED = incorrectOTP ;
    }
    public static String getIncorrectCodeRetriesExceeded(){
        return INCORRECT_CODE_RETRIES_EXCEEDED;
    }

    private static String EXPIRED_CODE;
    @Value("${EXPIRED_CODE}")
    public void setExpiredCode(String incorrectOTP){
        EXPIRED_CODE = incorrectOTP;
    }
    public static String getExpiredCode(){
        return EXPIRED_CODE;
    }

    private static String INCORRECT_OTP;
    @Value("${INCORRECT_OTP}")
    public void setIncorrectOtp(String incorrectOTP){
        INCORRECT_OTP = incorrectOTP ;
    }

    public static String getIncorrectOtp(){
        return INCORRECT_OTP;
    }

    /** The Constant SUCCESS. */
    private static String SUCCESS;
    @Value("${SUCCESS}")
    public void setSuccess(String success){
        SUCCESS = success;
    }

    public static String getSUCCESS() {
        return SUCCESS;
    }

    /** The Constant SUCCESS. */
    private static String INSUFFICIENT_DATA;
    @Value("${INSUFFICIENT_DATA}")
    public void setInsufficientData(String insufficientData){
        INSUFFICIENT_DATA = insufficientData;
    }

    public static String getInsufficientData() {
        return INSUFFICIENT_DATA;
    }

    /** The Constant SUCCESS. */
    private static String INVALID_USER_AND_DEVICE;
    @Value("${INVALID_USER_AND_DEVICE}")
    public void setInvalidUserAndDevice(String invalidUserAndDevice){
        INVALID_USER_AND_DEVICE = invalidUserAndDevice;
    }

    public static String getInvalidUserAndDevice() {
        return INVALID_USER_AND_DEVICE;
    }


    /** The Constant INVALID_PROPERTY. */
    private static String INVALID_PROPERTY;
    @Value("${INVALID_PROPERTY}")
    public void setInvalidProperty(String invalidProperty){
        INVALID_PROPERTY = invalidProperty;
    }

    public static String getInvalidProperty() {
        return INVALID_PROPERTY;
    }

    /** The Constant USER_NOT_ACTIVE. */
    private static String INVALID_USER ;
    @Value("${INVALID_USER}")
    public void setInvalidUser(String invalidUser) {
        INVALID_USER = invalidUser;
    }

    public static String getInvalidUser() {
        return INVALID_USER;
    }

    /** The Constant INVALID_OPERATION. */
    private static String INVALID_OPERATION;
    @Value("${INVALID_OPERATION}")
    public void setInvalidOperation(String invalidOperation){
        INVALID_OPERATION = invalidOperation;
    }

    public static String getInvalidOperation() {
        return INVALID_OPERATION;
    }

    /** The Constant CLIENT_DEVICEID_EXIST. */
    private static String CLIENT_DEVICEID_EXIST;
    @Value("${CLIENT_DEVICEID_EXIST}")
    public void setClientDeviceidExist(String clientDeviceidExist) {
        CLIENT_DEVICEID_EXIST = clientDeviceidExist;
    }

    public static String getClientDeviceidExist() {
        return CLIENT_DEVICEID_EXIST;
    }

    /** The Constant INVALID_CLIENT_DEVICE_ID. */
    private static String INVALID_CLIENT_DEVICE_ID;
    @Value("${INVALID_CLIENT_DEVICE_ID}")
    public void setInvalidClientDeviceId(String invalidClientDeviceId) {
        INVALID_CLIENT_DEVICE_ID = invalidClientDeviceId;
    }

    public static String getInvalidClientDeviceId() {
        return INVALID_CLIENT_DEVICE_ID;
    }

    /** The Constant VISA_FINAL_CODE. */
    private static String DEVICE_REGISTRATION_FAILED;
    @Value("${DEVICE_REGISTRATION_FAILED}")
    public void setDeviceRegistrationFailed(String deviceRegistrationFailed) {
        DEVICE_REGISTRATION_FAILED= deviceRegistrationFailed;
    }

    public static String getDeviceRegistrationFailed() {
        return DEVICE_REGISTRATION_FAILED;
    }

    /** The Constant VISA_FINAL_CODE. */
    private static String DEVICE_NOT_REGISTERED;
    @Value("${DEVICE_NOT_REGISTERED}")
    public void setDeviceNotRegistered(String deviceNotRegistered) {
        DEVICE_NOT_REGISTERED = deviceNotRegistered;
    }

    public static String getDeviceNotRegistered() {
        return DEVICE_NOT_REGISTERED;
    }

    /** The Constant VISA_FINAL_CODE. */
    private static String CARD_ALREADY_REGISTERED;
    @Value("${CARD_ALREADY_REGISTERED}")
    public void setCardAlreadyRegistered(String cardAlreadyRegistered) {
        CARD_ALREADY_REGISTERED = cardAlreadyRegistered;
    }

    public static String getCardAlreadyRegistered() {
        return CARD_ALREADY_REGISTERED;
    }

    /** The Constant VISA_FINAL_CODE. */
    private static String UNABLE_TO_PARSE_REQUEST;
    @Value("${UNABLE_TO_PARSE_REQUEST}")
    public void setUnableToParseRequest(String unableToParseRequest) {
        UNABLE_TO_PARSE_REQUEST= unableToParseRequest;
    }

    public static String getUnableToParseRequest() {
        return UNABLE_TO_PARSE_REQUEST;
    }

    /** The Constant CARD_DETAILS_NOT_EXIST. */
    private static String CARD_DETAILS_NOT_EXIST;
    @Value("${CARD_DETAILS_NOT_EXIST}")
    public void setCardDetailsNotExist(String cardDetailsNotExist) {
        CARD_DETAILS_NOT_EXIST= cardDetailsNotExist;
    }

    public static String getCardDetailsNotExist() {
        return CARD_DETAILS_NOT_EXIST;
    }

    /** The Constant CARD_DETAILS_NOT_EXIST. */
    private static String FAILED_AT_THIRED_PARTY;
    @Value("${FAILED_AT_THIRED_PARTY}")
    public void setFailedAtThiredParty(String failedAtThiredParty) {
        FAILED_AT_THIRED_PARTY= failedAtThiredParty;
    }

    public static String getFailedAtThiredParty() {
        return FAILED_AT_THIRED_PARTY;
    }


    /** The Constant INVAILD_PAYMENTAPP_INSTANCE_ID */
    private static String INVALID_PAYMENT_APP_INSTANCE_ID;
    @Value("${INVALID_PAYMENT_APP_INSTANCE_ID}")
    public void setInvalidPaymentAppInstanceId(String invalidPaymentAppInstanceId) {
        INVALID_PAYMENT_APP_INSTANCE_ID= invalidPaymentAppInstanceId;
    }

    public static String getInvalidPaymentAppInstanceId() {
        return INVALID_PAYMENT_APP_INSTANCE_ID;
    }


    /** The Constant CARD_NOT_ELIGIBLE*/
    private static String CARD_NOT_ELIGIBLE;
    @Value("${CARD_NOT_ELIGIBLE}")
    public void setCardNotEligible(String cardNotEligible) {
        CARD_NOT_ELIGIBLE = cardNotEligible;
    }
    public static String getCardNotEligible() {
        return CARD_NOT_ELIGIBLE;
    }

    private static String DUPLICATE_REQUEST;
    @Value("${DUPLICATE_REQUEST}")
    private void setDuplicateRequest(String deplicateRequest){
        DUPLICATE_REQUEST = deplicateRequest;
    }
    public static String getDuplicateRequest() {
        return DUPLICATE_REQUEST;
    }
}
