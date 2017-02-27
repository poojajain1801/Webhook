
exports.mockmdesres = { 
"responseHost" : "site1.mastercard.com", 
"responseId" : "123456", 
"eligibilityReceipt" : { 
"value" : "f9f027e5-629d-11e3-949a-0800200c9a66", 
"validForMinutes" :  30 
}, 
"termsAndConditionsAssetId" : "a9f027e5-629d-11e3-949a-0800200c9a66", 
"applicableCardInfo" : { 
"isSecurityCodeApplicable" : true 
} 
};

exports.mockaddCardres={
"responseHost" : "site1.mastercard.com",
"responseId" : "123456",
"decision" : "APPROVED",
"authenticationMethods" :"",
"tokenUniqueReference" : "DWSPMC000000000132d72d4fcb2f4136a0532d3093ff1a45",
"panUniqueReference" : "FWSPMC000000000159f71f703d2141efaf04dd26803f922b",
"productConfig" : {
"brandLogoAssetId" : "800200c9-629d-11e3-949a-0739d27e5a66",
"isCoBranded" : "true",
"coBrandName" : "Co brand partner",
"coBrandLogoAssetId" : "dbc55444-496a-4896-b41c-5d5e2dd431e2",
"cardBackgroundCombinedAssetId" : "739d27e5-629d-11e3-949a-0800200c9a66",
"foregroundColor" : "000000",
"issuerName" : "Issuing Bank",
"shortDescription" : "Bank Rewards MasterCard",

"longDescription" : "Bank Rewards MasterCard with the super duper rewards program",
"customerServiceUrl" : "https://bank.com/customerservice",
"issuerMobileApp" : {
"openIssuerMobileAppAndroidIntent": {
"action": "com.mybank.bankingapp.action.OPEN_ISSUER_MOBILE_APP",
"packageName": "com.mybank.bankingapp",
"extraTextValue": "ew0KICAgICJwYXltZW50QXBwUHJvdmlkZXJJZCI6ICIxMjM0NTY3ODkiLA0KICAgICJwYXltZW50QXBwSWQiOiAiV2FsbGV0QXBwMSIsDQogICAgInBheW1lbnRBcHBJbnN0YW5jZUlkIjogIjEyMzQ1Njc4OSIsDQogICAgInRva2VuVW5pcXVlUmVmZXJlbmNlIjogIkRXU1BNQzAwMDAwMDAwMGZjYjJmNDEzNmIyZjQxMzZhMDUzMmQyZjQxMzZhMDUzMiINCn0="
}
},
"termsAndConditionsUrl" : "https://bank.com/termsAndConditions",
"privacyPolicyUrl" : "https://bank.com/privacy",
"issuerProductConfigCode" : "123456"
},
"tokenInfo" : {
"tokenPanSuffix": "1234",
"accountPanSuffix": "6789",
"tokenExpiry" : "1018",
"dsrpCapable" : true
},
"tdsRegistrationUrl" : "tds.mastercard.com",
"reasonCode": "200",
"reasonDescription": "Provision Successful"
};



exports.mockloginres = {
"responseHeader": {
"serviceId":"SI151006.5135.1df917ab-5098-4254-abf9-59bf2761a016",
"overallStatus":"TS"
},
"loginOutput":{
	"txnStatus": "200",
	"txnMessage": "Success",
	"uniqueId": "PT123.4331.3232"
}
};

exports.mockloginreserror = {
"responseHeader": {
"serviceId":"SI151006.5135.1df917ab-5098-4254-abf9-59bf2761a016",
"overallStatus":"TF"
},
"loginOutput":{
	"txnStatus": "721",
	"txnMessage": "Invalid Mobile Number",
}
};


exports.mocksendmoneyres = { 
"responseHeader": {
"serviceId":"SI151006.5135.1df917ab-5098-4254-abf9-59bf2761a123",
"overallStatus":"TS"
},
"p2pSendMoneyOutput":{
	"txnStatus": "200",
	"serviceCharge": "10",
	"transactionId": "123456778"
}
};

exports.mocknotifyprovisioningres = { 
	"requestId": "123456",
	"responseHost": "mahindracomviva.mdes",
	"reasonCode": "200",
	"reasonDescription": "Successful"
};

exports.mockactivateres = { 
	"responseId": "123456",
	"responseHost": "mahindracomviva.mdes",
	"result" : "SUCCESS",
	"reasonCode": "200",
	"reasonDescription": "Successful"
};

