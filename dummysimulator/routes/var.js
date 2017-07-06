
exports.mockmdesres = { 
"responseHost" : "site1.mastercard.com", 
"responseId" : "123456", 
"eligibilityReceipt" : { 
"value" : "f9f027e5-629d-11e3-949a-0800200c9a66", 
"validForMinutes" : 30 
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
"tokenUniqueReference" : "DWSPMC000000000fcb2f4136b2f4136a0532d2f4136a0532",
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
exports.mocknotifChangePinres = { 
	"responseId": "123456",
	"responseHost": "site1.Mastercard.com",
	"result" : "SUCCESS",
	"reasonCode": "200",
	"reasonDescription": "Successful"
};
exports.mockreplinishmentResponse = {
"responseHost" : "site1.Mastercard.com",
"responseId" : "123456",
"rawTransactionCredentials" : {
"encryptedData" : "426EA576BC94A55D019A1F4484CEEF00966754BC336767D4CE20FFDD9A48B30CD19001C427682D0ED51FA8EB0AF70ECE489FFEAF8235042F5BAFFB767730FF7589F46F956BB920241A1B44D76E1C0B7E13061E5E1E128570206890B7BE21452A083968229BA958B15DD514D0F949F38151163B628503591C3D3AC2B255529E41E5A47C58CC1EB31D3FC69722483342FDB6004DAEA63B4F5D314C93E83156F77FE61F7CB92FB2C85DB49AEC14292F84AFD926DCA62CDAFC0E94D1ADF1D63C01F5F0B1CADAF846000EFDCA2E166705EE200A02E1892F3AC8368EDA9B2B823384A2D9F51E5215BEF9839AF235820478D933DAA97E84E0EC53015F3008748B5280433EFAA7888AF0447CA9EA95D3347CE367987F9F069E5F00F2C4BC7630AE59C9AEE5EDE3FFDAEEE21799D31E5C34552716261137E938DB32FFFC26A035283631E84115E6827F13EF4887EBE427BDCF4F7766FE9EF22131E58AAE93BF1B1D18E4234E26E675A185EC2A69E853FCFA4C8A598B92AC9CC7D8D83F0D5DEE27013A59B5C26C8D9E348C8D21F13FC390AAAC0767AD9F9229AC70E6AFA29D0F74AEBBAE8405D2276FA292E715B5A3BA1A7379BC2FD61749539DF332FE263CAF059160959D43325BEACABF09D8B29DDD00F28B6719E3DE1634F2A3F8F4442D52DAF959F5DCE60DF9EBFBAF47708473994BF70338F52C72BF16B340460CD0845B7FFD29ABD65750AFEDC020952869DE5590905E138FB6EAD55C5433962AD62D50ED35A05543C4435957697BF1A88605648BDA88B0FD86781FB340D16115928A6523434CC886A300C49EAE88CD86586448A4BDD4394001AE8FF31B5AACC0D6EB0E2D0970D2BFDF22976AA76B1A32D2C3D735C5D376CBD5061567BE92C01184DD99F63F9D4C8CFA9666C99790CEE4FD2A871380C757A3285648B41155D830EB57435306EFCCA742555E7F6B5F22B9CE2987B2631556E190D73ED5BDF00D4FD7E48BFADDB6AC87B340B8B2EFA34BF2517413F0E97B618FA850AF9886F81EB5DEB6F6853D6D0907FA98F24ACFE47E7E2E725FEBFF55D02886D6BA78311BADF5565702F9084D995037F34EDAC14530B266E87598AF1DFC63133F88F9DADC12594090AFC6DDFEFAB097081BDA83FDE00D6DE8B9442EA4FF6549AB911214EE277B0823B852E6F9F69786CD9FD66929B8C15289D9ED166F14B0E5B70AF030667DB9BD4C78AB7FC169A6B7E70494C6F48BC7B3F7BCC7E6099DE7E5CB8137761E294674B75D6CCCE5474A0FA846CA58B829E101F3FA92CA9DDDA9F5EB60502D1E310A7C200066D3E00C346606",
"ccmNonce" : "0102030405060708090A0B0C0D0E0F10",
"ccmKeyId" : "123456",
"ccmMac" : "8D4AA1B7276DA2A3"
}
};
exports.mockGetRegistrationCodeResp = { 	
	"responseHost": "site1.Mastercard.com",
	"result" : "SUCCESS",
	"reasonCode": "200",
	"registrationCode1" : "c1b1f68a-5d0e-4a38-8597-420d85301c46"
};
exports.mockRegisterwithTDSResp={
"responseHost" : "site1.Mastercard.com",
"authenticationCode" : "800200c9-629d-11e3-949a-0739d27e5a66",
"tdsUrl" : "loadbalanced.Mastercard.com"
};

exports.mockDeleteCardResp ={
"responseHost":"site1.Mastercard.com",
"responseId":"123456",
"tokens":[
{
"tokenUniqueReference":"DWSPMC000000000fcb2f4136b2f4136a0532d2f4136a0532",
"status":"DEACTIVATED"
}
]
};

exports.mockSuspendCardResp ={
	"responseHost":"site1.Mastercard.com",
	"responseId":"123456",
	"tokens":[
	{
		"tokenUniqueReference":"DWSPMC000000000fcb2f4136b2f4136a0532d2f4136a0532",
		"status":"SUSPENDED"
	}
	]
};

exports.mockSuspendCardResp ={
	"responseHost":"site1.Mastercard.com",
	"responseId":"123456",
	"tokens":[
	{
		"tokenUniqueReference":"DWSPMC000000000fcb2f4136b2f4136a0532d2f4136a0532",
		"status":"SUSPENDED"
	}
	]
};

exports.mockUnSuspendCardResp ={
	"responseHost":"site1.Mastercard.com",
	"responseId":"123456",
	"tokens":[
	{
		"tokenUniqueReference":"DWSPMC000000000fcb2f4136b2f4136a0532d2f4136a0532",
		"status":"ACTIVE"
	}
	]
};

exports.mockGetTransctions = {
"responseHost" : "site2.Mastercard.com",
"authenticationCode" : "800200c9-629d-11e3-949a-0739d27e5a66",
"lastUpdatedTag" : "eXl5eS1NTS1kZCdUJ0hIOm1tOnNzWg==",
"transactions" : [
{
"tokenUniqueReference" : "DWSPMC000000000fcb2f4136b2f4136a0532d2f4136a0532",
"recordId" : "123456",
"transactionIdentifier" : "6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b",
"transactionType" : "PURCHASE",
"amount" : 123.45,
"currencyCode" : "USD",
"authorizationStatus" : "CLEARED",
"transactionTimestamp" : "2014-12-25T12:00:00.000-07:00",
"merchantName" : "Bob's Burgers",
"merchantType" : "5812",
"merchantPostalCode" : "61000"
},
{
"tokenUniqueReference" : "DWSPMC000000000fcb2f4136b2f4136a0532d2f4136a0532",
"recordId" : "123457",
"transactionIdentifier" : "d4735e3a265e16eee03f59718b9b5d03019c07d8b6c51f90da3a666eec13ab35",
"transactionType" : "REFUND",
"amount" : -54.23,
"currencyCode" : "USD",
"authorizationStatus" : "CLEARED",
"transactionTimestamp" : "2014-12-25T12:01:32.000-07:00",
"merchantName" : "Bob's Burgers",
"merchantType" : "5812",
"merchantPostalCode" : "61000"
},
{
"tokenUniqueReference" : "DWSPMC000000000fcb2f4136b2f4136a0532d2f4136a0532",
"recordId" : "123458",
"transactionIdentifier" : "4e07408562bedb8b60ce05c1decfe3ad16b72230967de01f640b7e4729b49fce",
"transactionType" : "PURCHASE",
"amount" : 10.1,
"currencyCode" : "USD",
"authorizationStatus" : "REVERSED",
"transactionTimestamp" : "2014-12-26T12:05:10.000-07:00",
"merchantName" : "Bob's Burgers",
"merchantType" : "5812",
"merchantPostalCode" : "61000"
}
]
};

exports.mockRequestActivationCodeResp ={
	"responseHost":"site1.Mastercard.com",
	"responseId":"123456"
};
