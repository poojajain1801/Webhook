var http = require('http');
var paymentAppInstanceId = 'B9B44F4AE0A7A7320B611829A9E800000001503326363042';

var appRouter = function(app) {

app.get("/", function(req, res) {
    res.send("Hello World");
});

app.get("/account", function(req, res) {
    var accountMock = {
        "username": "nraboy",
        "password": "1234",
        "twitter": "@nraboy"
    }
    if(!req.query.username) {
        return res.send({"status": "error", "message": "missing username"});
    } else if(req.query.username != accountMock.username) {
        return res.send({"status": "error", "message": "wrong username"});
    } else {
        return res.send(accountMock);
    }
});

app.post("/account", function(req, res) {
    if(!req.body.username || !req.body.password || !req.body.twitter) {
        return res.send({"status": "error", "message": "missing a parameter"});
    } else {
        return res.send(req.body);
    }
});

app.post("/mdes", function(req, res) { 
var mdesRes = require('./var.js').mockmdesres;
 return res.json(mdesRes);
   /* if(!req.body.responseHost || !req.body.requestId || !req.body.paymentAppInstanceId || !req.body.paymentAppId || !req.body.deviceInfo || !req.body.consumerLanguage ) {
        return res.send({"status": "error", "message": "missing a parameter"});
    } else {
        return res.json(mdesRes);
    }*/
});

app.post("/addCard", function(req, res) { 

/////////////////////////////////////////////////////////
// do a POST request
// create the JSON object
jsonObject = JSON.stringify({ 
	"responseHost" : "comviva.mdes",
    "requestId" : "123456",
    "paymentAppProviderId" : "547102052016",
    "paymentAppInstanceId" : paymentAppInstanceId,
    "tokenUniqueReference" : "DWSPMC000000000fcb2f4136b2f4136a0532d2f4136a0532",
    "tokenType" : "CLOUD",
    "taskId" : "3dacc64b-9703-4201-af40-02e636e3ff3b",
    "tokenCredential" : {
        "encryptedData" : "E27BF23B49420D31A4AFE4068036D5FFF4377F8962D4A1AC9571BA0FF106F341BADF3A344AE3CF4521E3B9DAEEC64EC9D75BF4FA1FFEDC9E2FEC25A555D5233FC645218190A635A7BEE1A352C4F0375A906F89EA2D614A9439B71FB4BAEF592AEF09FF22FA9396612D674B8D6E8FB7460B1BBB5A6632FA56AE83818175ACF3C47DEFC1F87F2EA71D8701CD7E717C4516ADE0CC984010E2D24F604779EFA9F58C32B38425A3FEFBDEF91D7D1F6F15DE6D2E8BC6C6F093A6CF2C80E5158EDE56891FE5771481147D25B50B47D8B3E9BDF7503B22BFFEEC474E9C6EADA22A00D656441B371DA07CE51F39D1D85B81E9299290A7DBF7D1A5985C543DB2DB000BCE198AFADC18A032BCD8505C421C56A471D991C03EE4CD7656468ED8C8C413B36E487999904BF1E9B4848EDD1A3570211E6DAE3703FA23520380C98C24A3D160050FB62834968DAF98F3FAB85F38719D7A841072512EA7391D012DEC6DC14864D6B12C88B393815EE5DBE75FC6D50A43916078050FD8A59E8BE33A17A49E8128829DB3C51F8A85EC60ED075B9C528CCF148B3DCF8F6A10C88CD9A1A3EE894DA54207548EA112942DB284BA6384CB05604B02418790E4466BDE9E23B663B330D4D83938E3A30A00A7060D936E0D6FF5C3C5BF69511A543523B749CE03DA0A43BBA687604DF49E3FF7289B81496B358D508CEB4A6FF1823498EF899B72216CBBA6949586324ABD62212CF76AFEA4A4D75A65501C232A2A23DABCECE67C948434648E5AB50E1DFD3AC461A24017671C7B3E4610C7ECC79C2C5E643E6C4066D9C293702D9D705224B4B544BE9E2B3E5EB2F54F5FF28510A641C89C7392DA54876FF648D42A4C7F799840D99544F0363E8291953A68A421F282A65247D2342BB52419E2ADD80533087DF5D6E17EB22CFB7B873954C9AC47FBECA298A96EC1C078ABC54762BCE22BEF253590F5A2CCB73F6A7FB9F544747C95155482DFDB4D4CF17D86C77EA131265351E9DEFC30957F3DA1EC4F6D610AB6857FAFF10883CEE1B206AC4C39FFAE866D3FA786DDE5A9EC0F6E1C8C56C3E15B5FDF6EAB842416DE487F98DFDD082AF69D8547062D3368429C18332FD1ECDD90FC7F8FE8749770A535D54130D9EF7CC495114BB0B5F94A25EF75A8C40B037FCE2789EE71E7963244566433527414D4E1401ECAC66E4153F2506068C49D4B2C371E109702B57D286E214D77453F11C6674DB5CEA2FAA6C62E92B6248B1E9556313BF515C350E0618623258E1298C101911FEFF18177BB1563A18BF72ADB4196F3A92501878F599E7520CC900A16EE1657AFFD7F2E6F0A247AD7831862615C0AD9163C596D0D54B9F86243F71CD5E97D7E9557345E81AD8E9DD3EF6FE9A61B1DB2D2B6399D83535C82D5FAF05F90CC815F9034A50E61474EFD0F174B069F71CB1478BD45C3501FF1FE9115B3BD6BB2BA8DAD3D49D9B802F648CDFFCEAA6DA5B1C42D1A2EE222ADC5B40D563D388AD19033A9F6EB1C715D2C4C5E8CB0414A96406B0499A9CAA6652F01C0A8AD2B14933A60FE70AC89FC59B58A8E4C3D5DD34307206AD0F4EAD76944E2E09E4BFBB177033D252DC71551DB3D69C369FCA27C2C75A1FF080A0FC4D7779B0A65A8913A4CC4145C7B42C5D6FBAA3B3CBE8CBA2CDF69A39470692A65A3A802FF7D923350BD966F538B04ACA560419768FE4DDB1157BEB23042D34681640F39CB46E16823F3A8646F144BA47A74B67F8E92160F1416D80F672871A5DC77F052583E5DCB76BD11C6F4F427CC34200936F9AA0880069602FD1FA122BD684EFD62565AF4CEC0B4BFBDC54D6306ECB6C33C88F22EE6DA39FD98151B18A8C458A11B5ADF53970FAE040989098C00CB023819BCD843FCB321F0125AD1AF283ECBEB58ED65FDB98C317B2FD8E044D78B9172849DF526AD33ED8A1FDDDDDECB623517CA9EC7C950730FD6E8F39C950250522F6B3E2934ECDB5FD3B9604293BD150CCDABA50967760D60B2E9D10E1C78ADC1BB658AF9E1B355212A81FE3EC1176813CA0C33D087092575E2E360035B8C4B53368F7FAC292AA99DC7760B4E8D2A42085302FF3EA8A1AD7BC59A4C437C91DD6B848C6BFC019944B809EE674F2F2ED39BD8E8CE2BB54FD873F65384D0D2E3A4D683D62361B0CC95AF8495776DC9FF4E327DB9AB6DFDFD3343107B27D502D382BACE73E7D9C9C09C4818499378BDA9DC123D500D1F53610909E7F0238D90368E7730C4A8F0EF9F7C870BD664963C4ACE714F3AECC5C88EACB5833B89B9E7C92083CE45EDEE6A840D6AD0BBBF516F0DF049E618875C5703652F3E2517284163FB67B65B9B47DA1297F854109054018CB0C0A7449CEF7153BEFB99078101614C9F588691B26FB1FA4D3924BD55E7E0DF9236785B68D72367AE062841AEDF864DF1C4B173081890AB67D7E2F0D59BDD021016FA8376FCFA35C1A6178C43CD2E075F51571BD4F21149FBED839B7FAE60C7016FBCE124333FD7CC2E9347B954470D8F68AB4B41316F112433ADA8C6ECAD73B820FBD5A450EE8DBD04115CBCE4472696A72F52CDBD2DDFD0A454300BF0780769769FAFD5536F23BA69E1EA8E825427AE9DF79CBA970C9B103BB3FECDA25D787FF9EA0A9E67FFF4EFC61E9E589E2B92183998338AB23305CF8F5172BC1DEFF7791DFD5EE815A5A0E2E12675269F57314349DB77562468C0451FB519907224CC09E6A3AB87052B80375B3C53C34830A27162D148F402BB9431C7D761964704090C59D8172B7002BA48DAAD80C2DADAD282A7CB91B3423794CD7B74E6C0577A91E01DC683AC1C4D5A44CEF2F4E219D8C79C66D3F13151CF17756D6D183D4BFB2AECDACA28D1B5EB67DF97F421FEF475D44E71CFBFF08FE98800797AF29691A609E312759581B4EC9DC8BEE8CEF657968F5E20B00EB43D47AB59036D5702897B0214D034DFC24CED9BCB9F1C1AED38B055EAEB3C711ADB06556C409392893370BCF405ED049268348BCBFB3062A1AC1DDB93C09D3E6C5EC3DB9787FF0B579CAFA947EC313037A950E9DAE0F6F1F0DCF2A431AFE9E3BF30F4CA3988323BC55F1BA13438A8FA692A418DB6B0CB3859F6F8ED944A6A46735811CC205BA1E7D857211DA9D03AFEF65656CBC1BBE782E1FB5AC482A35B10DAB63279BAA094A4546F4D2DB24498C0AF48341557C01C0228D5C489E8AADFA2D7399AAC70EF2E7FD1F11A8BFAB9486E9E87D93F8480289296A68DC5F461BF9601FAA3B9A22539535BDFB305452ADF717BE1684C10C250A1BA0970BC17D4DC345EA1F01E47AB114F688C7F81E9E4F07419852255F37517B5968B0B437C698FB8CA2A7CD51148816C9C0D6373BBB4B289F48ED27999C1C06DD74021A16CD5BD5D40239C4610DE27F835F6562A2B4FBB630229827E0F0A3983DBA9284E91F7E9B5D75ADC9792B15445892164B454ED8377BDC017D9F01C79EAF8B294AB342D0C18BB6AF8954E861683BFFFEF9F1EB6D42C417FDE3FEB6AAAE3F5B6812EC04C279436EC406DD2BA7878BBE69CF43089DCE84D723C6552A479C8D9FF9636E99B0C1A2465AE39C115F375FEBBC0671CEEA7FE92AF1AE03DE3F4F27A8D06A7F2E2673FE883554DECE3D57840AE4D0CCFBCFE501E4AA82DE514A3FAB6762AFF244A37767425407A1356A87BDCAD5F0364053ADC2AB1B87A0B0D1D3770DF0B41D2D44441AA620F1FE600AA4661B8FDEA3B1DED4B28C13D0C7417D2EECC6DE792EC081A0C85C8CC6350A9E41BFA874D3AEC76A4415F07F66C6BFDBA0599309B1ED14776391702AF70EE806C906C367B6E0D1E06D4C6BBDE893D43C093D4D47CA45C0ACE960431A5E9E368F7EB514687D8D3911BF92FA98B71E06678AFD7A092857465687DAD4B79CF0CD9CCCAD94F0C43B3FC4C46689902191A458C5A26BD67E4B02555C963215EFEDF6C8511E08310E707B772A612102BBB069A24A140BFEE5800C09CFC2B1E27218AFEFD76B78C28BA9C122379579810EE731ADE4D17EA49DF49AE63F2C4540DD1B99C976CDDF0B3DCC41C590071BB7E81C24F19B7BA04347436B9113BAC2E93E423CEF4D5AD7DED0D856B94DA810A0836E8CF4C70DE6B3898F6616238BFC223E2BE3057F669CDB106F6CFE4F485353E5FC87FB06225E8CE497C6C87820BB962E9090CE9142A70B9DD7E9D5097A42667569A1AE9BD2872174B8F0EA82CEA2C481E5E34FBA7F72CA218CB0C09CA67A4BEA97BE5DDCF70C618757C8CF69BBE5EA931405CFEB3BA946E0EBB6014EA6EE68572934A1777BB4DC58083EF6F824C8F86BC8A023E52BCF61FDBE693837B13B2579EA23F8EB5A2523A370B1EC64ABD3E0A0C36B5B99585684133A5BD25B315E6EB38F6EB8E08228D7ADA6F0A5D74013EDC8A35E54613F1C6F7D13EEAD06EDA22D53AB5BEFFB16476BAC57208C3313A47485EE12E695AD506D4BC986A03BA2ED97A8771A7DDE91F654394746900B426789842DAEC16D7E4E545B16E3F6FF4F5E8C7EAF414C2456DB62EA0072DA259044AF83FC8F7DC5DC66CDC0F379AD277E8FF749F0641387E3A13376195BAE6C5267FFF7B094876FD81AF16A0C871F442D9D2E487440C9FA5145D5EF607B4646CBC448DC46F1BAF51479932F9A50D56A7AFB4A25056E2DAE07904AEDE35F9A65F31E4C52C5838FCB573EC02EBFFC780E59FBFC06C206A7CCF79FD7573DE3473FB247805A89D238EB1D9A29243F4A4328BEE4BBC820DE710145409E68813073BFFCD2B42DF0175FF7731557963E39A14BF5D06520789C602AD4C5AE48D990B4D4812330003771EB901695329B66DE8611D83FEC43EEDD506461E4FA64E137BECC233419387298268722DA9B799FC50EFD876FC001B233F4D0D08A93B40C46A1DC75F6C2CC58C997F10A4C9C5ECABC6C6A32BF147103FF81C624BFA9AFE01BB33B424327696D99193CDA894D80551DE13743216A59101930C07A9A4910A2A5D41EEA5CA578825E0A316D4A2FE4417D69E4DD01F851F5DAD4522CFF98C51D79A161B99DEE5F40235F21DEA2BC72F49D9D0623E275CB8331A87CD80D34927B90FE71E7384A74E9C2CEB930355889C308895F531755AC6A593C9DF0C8F6AB38DEAAA3C7AB53D2F29C4F212653BD42B84486AE6D98A9C35BF98665BC8F945EB88B7FE5D2AAAE3646398C97F8E361FE38AB1BBFD677249C371FEF9C0815D5382F6C4E97D95359C95200575260168190533F237E764DDB40333220624D1D9FC",
        "ccmKeyId": "123456",
        "ccmNonce": "0102030405060708090A0B",
        "ccmMac" : "123456789012345678901234"
    } });

// prepare the header
var postheaders = {
    'Content-Type' : 'application/json',
    'Content-Length' : Buffer.byteLength(jsonObject, 'utf8')
};

// the post options
var optionspost = {
    //host : '192.168.1.3',
	host : 'localhost',
    port : 9099,
    path : '/mdes/credentials/1/0/provision',
    method : 'POST',
    headers : postheaders
};

console.info('Options prepared:');
console.info(optionspost);
console.info('Do the POST call');

// do the POST call
var reqPost = http.request(optionspost, function(res) {
    console.log("statusCode: ", res.statusCode);
    // uncomment it for header details
//  console.log("headers: ", res.headers);

    res.on('data', function(d) {
        console.info('POST result:\n');
        process.stdout.write(d);
        console.info('\n\nPOST completed');
    });
});

// write the json data
reqPost.write(jsonObject);
reqPost.end();
reqPost.on('error', function(e) {
    console.error(e);
});

/////////////////////////////////////////////////////////
var addCardRes = require('./var.js').mockaddCardres;
return res.json(addCardRes);
   /* if(!req.body.responseHost || !req.body.requestId || !req.body.paymentAppInstanceId || !req.body.paymentAppId || !req.body.deviceInfo || !req.body.consumerLanguage ) {
        return res.send({"status": "error", "message": "missing a parameter"});
    } else {
        return res.json(mdesRes);
    }*/
});

app.post("/login", function(req, res) {
    req.checkBody(
        "loginInput.msisdn", 
        "Enter a valid mobile number.").matches(/^\d+$/);

    var errors = req.validationErrors();
  if (errors) {
    var loginResError = require('./var.js').mockloginreserror; 
    res.send(loginResError);
    return;
  } else {
    // normal processing here
    var loginRes = require('./var.js').mockloginres; 
    return res.send(loginRes);
  }
});

app.post("/sendMoney", function(req, res) {
var sendMoneyRes = require('./var.js').mocksendmoneyres;   
    if(!req.body.p2pSendMoneyInput.uniqueId || !req.body.p2pSendMoneyInput.amount) {
        return res.send({"status": "error", "message": "missing a parameter"});
    } else {
        return res.send(sendMoneyRes);
    }
});

app.get("/mdes/digitization/1/0/asset", function(req, res) {
	var assetValue = {
		type: 'text/plain',
		data: 'I agree to T&C'
	};
	
	var content = [assetValue];
	
	var mediaContent = {
		mediaContents: content, 
		reasonCode:"200", 
		reasonDescription:"Successful"
	};
	
	var array = ["a9f027e5-629d-11e3-949a-0800200c9a66"];
	
    if(!req.query.assetId) {
        return res.send({ "reasonCode":"200", "reasonDescription":"assetId not provided" });
    } else if(req.query.assetId == array[0]) {
        return res.send(mediaContent);
    } else {
        return res.send({"reasonCode":"200", "reasonDescription":"Asset not found"});
    }
}); 

app.post("/mdes/credentials/1/0/notifyProvisioningResult", function(req, res) {
	var notifyProvisioningRes = require('./var.js').mocknotifyprovisioningres;  
	return res.json(notifyProvisioningRes);
}); 
app.post("/mdes/credentials/1/0/notifyPinChangeResult", function(req, res) {
    var notifyProvisioningRes = require('./var.js').mocknotifChangePinres;  
    return res.json(notifyProvisioningRes);
});
app.post("/mdes/credentials/1/0/replenish", function(req, res) {
    var replinishmentResponse = require('./var.js').mockreplinishmentResponse;  
    return res.json(replinishmentResponse);
}); 
app.post("/mdes/tds/1/0/" + paymentAppInstanceId + "/getRegistrationCode", function(req, res) {
    console.info('getRegistrationCode Invoked');
    var getRegCodeRes = require('./var.js').mockGetRegistrationCodeResp;  
		
	// Invoke Notify transaction Details to PaymentAppServer
	jsonObject = JSON.stringify({ 
		"requestId" : "123456",
		"tokenUniqueReference" : "DWSPMC000000000fcb2f4136b2f4136a0532d2f4136a0532",
		"registrationCode2" : "a1b1f68a-5d0e-4a38-9817-420d85301c46",
		"tdsUrl" : "site2.mastercard.com",
		"paymentAppInstanceId" : paymentAppInstanceId
	});

	// prepare the header
	var postheaders = {
		'Content-Type' : 'application/json',
		'Content-Length' : Buffer.byteLength(jsonObject, 'utf8')
	};
	// the post options
	var optionspost = {
		host : 'localhost',
		port : 9176,
		path : '/payment-app/api/card/notifyTransactionDetails',
		method : 'POST',
		headers : postheaders
	};

	console.info('Options prepared:');
	console.info(optionspost);
	console.info('Do the POST call');
	// do the POST call
	var reqPost = http.request(optionspost, function(res) {
		console.log("statusCode: ", res.statusCode);
		res.on('data', function(d) {
			console.info('POST result:\n');
			process.stdout.write(d);
			console.info('\n\nPOST completed');
		});
	});

// write the json data
reqPost.write(jsonObject);
reqPost.end();
reqPost.on('error', function(e) {
    console.error(e);
});
	
    return res.json(getRegCodeRes);
}); 
app.post("/mdes/tds/1/0/" + paymentAppInstanceId + "/register", function(req, res) {
    console.info('getRegistrationCode Invoked');
    var registerWithTDSResp = require('./var.js').mockRegisterwithTDSResp;  
    return res.json(registerWithTDSResp);
}); 
app.post("/mdes/tds/1/0/" + paymentAppInstanceId + "/getTransactions", function(req, res) {
    console.info('getRegistration Invoked');
    var getTransctionResp = require('./var.js').mockGetTransctions;  
    return res.json(getTransctionResp);
});

app.post("/mdes/digitization/1/0/activate", function(req, res) {
	console.info('Activate Invoked');
	var activateRes = require('./var.js').mockactivateres;	
	return res.json(activateRes);
});

app.post("/mdes/digitization/1/0/delete", function(req, res) {
    console.info('Delete Card Invoked');
    var deletecardResp = require('./var.js').mockDeleteCardResp; 
    
    return res.json(deletecardResp);
});

app.post("/mdes/digitization/1/0/suspend", function(req, res) {
    console.info('Suspend Card Invoked');
    var suspendCardResp = require('./var.js').mockSuspendCardResp; 
    
    return res.json(suspendCardResp);
});

app.post("/mdes/digitization/1/0/unsuspend", function(req, res) {
    console.info('UnSuspend Card Invoked');
    var unsuspendCardResp = require('./var.js').mockUnSuspendCardResp; 
    
    return res.json(unsuspendCardResp);
});

app.post("/mdes/digitization/1/0/requestActivationCode", function(req, res) {
    console.info('Request Activation Code Invoked');
    var requestActivationCodeResp = require('./var.js').mockRequestActivationCodeResp; 
    
    return res.json(requestActivationCodeResp);
});

app.post("/mdes/tds/1/0/" + paymentAppInstanceId + "/unregister", function(req, res) {
    console.info('Unregister TDS Invoked');
    var unregisterTdsResp = require('./var.js').mockUnregisterTdsResp; 
    
    return res.json(unregisterTdsResp);
});

}
 
module.exports = appRouter;