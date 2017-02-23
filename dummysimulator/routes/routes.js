var http = require('http');
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
    "paymentAppInstanceId" : "123456789",
    "tokenUniqueReference" : "DWSPMC000000000fcb2f4136b2f4136a0532d2f4136a0532",
    "tokenType" : "CLOUD",
    "taskId" : "3dacc64b-9703-4201-af40-02e636e3ff3b",
    "tokenCredential" : {
        "encryptedData" : "DF9B066F3C5415F1DCA4B90417AF20026161797EA94C62AF51A680E42EE3BDF2A4280624E210E1A761AA7B35133389A6195F1FF5F8380C4433FA8A61C8523E5B2CA160F91702E361E3C0FECA4FC377CC499E4B23E67B3FDDA36AD6929B91DD0C731AF9A815910F3D64AC77D71151A08B4349BE5549C38C76B5DA3CEC94924371FA703A7249AE0DE0C45A9435A8F4F92418B784ED1F98B42B5C1282DCFC0DEAB608103F03064F90E0D949FCDF091EF6F7D122B8A34D9705BDA039991CFF2020285813AFEB8712FFB75AB1A189B516595DCA864880077A4AAF5E854EAB9DD21F1911668B49C4CA8C100C5B201EA290883F23A9C69B967C409C4AC5015738F184B6A4638C1F8252E6CDB89738D3314149ADD724CE6E8110113D987AC911B72C35BD755AF58A562098111DCD2F60F6AB770BCC017F4A3C82195AABC9921B943ACBB4C2C98E6AF9C1B25F6E8C6C5CC9637EE81174E53BD9D43C4B29E2C272F8B9A6DB7F726DF5786DB6B2CDF1F874738A5E585021410A4B31F88EEF4414DF62407EC4C3D6EEDFEB07404D318A5B4CA211CE3AF2D17546A10E6D2545EA7BFC1FD939B5E6D9A6908E0925D7F6DD292B258D2F49BCDF8AA538C933B6940B0C4DA074718A34BCC7F401E9626A63D187DD9FE5B843F610F20CCAEA30680DB177B1821452889D8AFB7EE6E8D51ACF8C8E7B79A1EFDE617F51234E8318FA83280CF4EFE4E2CE17D911DD67C071FAA4E8DAC4355A9AFC6CF73EB93D6FF78B8DBB0DFFCD20F721021462FC4FD0EDED67207A96712B757A827A06B7554FF1AC7667200BD49A7EE029A3CBD810399152E1BA89FD00705955B6453A275BE32D09167F4C96B26C893A22D89B8AC4072AB43B85D0DBBE569445E7188968B4BEDC137BBA4A24AB920A89C50A28BFF6922E5FD80742F932E784EC65A61233F4FCCB3DA1D383F0379764099B796A0679C47092CB2EB43E42E9115CD79EE5A9632E508CF3233EE151A226FCCF30CB3654E63862F3FD24F56311ACA489ADDCEBC390C405CF2E03CBB7563AC5D81BC25C02F98ADC5857D46F1F470307F6398040DA6B68E3016D9850693FB4C55A2E4B65EF921080D87168E3F95C564BC1D5467B927A0D3CF40AB7E89339DBCA3D1E7532709A35754548596DEDAB57B7970996DBD77DEB72353312B281FA2DCB925C678B0C5A7D001EBFDCCA1370F3B7117AB8BF454568E17A6D41291DB28A954877952563AFE3E6238DBBE3AF2AC6AF6E686D0E09D58A012F3A5F7EE938618778FAEB510CBAAAEF542C1EE7635C9E4EA4FC4E92DC5FD2824DCA8CA7EB6F3139131D52614E10D09ADE6213F4C3C5259C442EB93DA4B2195C528C165798661258994E060A70B1923B191A903B4A1F2A457C4AE6F0D29CB60C37647A7CF95FBEEE3110AD79FB7D08E58BBB09BBB6E6542CAA3B542C02C8BDC01D491CB477702CC78ABBD6B41F067DAC256296867D1E4506ED547CF766B095BF3CB29C98A3C8E5BC5845CF5552B6218E51D53303A610A6EF341C5E25DEE667B75F2EF64146E9232628BC574D6876314BF5C47F293CEEE1BAC679B59E8651059EF8C00B1F22FAEF63AA526CC51B29802897C0240E62D206A2FEEB8C76B2B779F9AECA6E38BD7740814190582E31683B700BC2248DD8FB994455E3DEA07FE0D53D77113639238D6AA77A5625E4DF1BBE9C0C759503A72AF0AF595C8D3E9D3E63CF3C54A5A71F0A47FC57A050AAD4374AFE796DD92168E6388F070FFAD6BA85FF79F28176D48F5DDCDE2DBEB13A01D7989BC0A4722D0BA47C4EE4C7A70F4029B47FC524B1679B7474FB861204ABD2DECB1D45706F46C7F58F250EF85EB8AFD71441E6DAB0BC9AAC016C37F6D102CA40AB09475D0BD30FF957B3D20BB9441AA6098BC59EA592C9B83F2B22B5245BE8BED73491566E7BC5575C02AB9D010A453F3DF3D75E87E43F2D7931BB62290E8C9065FEC6FA1141B933861D1754576D83FED706428D8322530E5B7F5EA0A3ABF716DA9B17B3187944081630DA28C4B999C546DA17CCDD2247FC8FD96FD44A429682155FAF70B0EFAF75FBE02137C80D93B2C04AD99250B1507BC99045482465ACE96A2F09F7BA3CB3CEB8C2B934DC6CFBF7B4AA4900AA1F06AC66057418DB7E32C24559B7CDC044DE1E86D83D2468E92EB5A88878CBAC3DD6AA366B57A6C2D0288FABCA165977984068EEDDE265F92158DCC4CE1E50A0F54C4ACC85B20BD3AD2EB65B8F28A09AF68157AC18C2920F9DA46AE472D560CBAA02AF2DF58C40F0802E5014BF9EF6AD86AC4F93FE411B4EBC7BB95FB23B53E9C4DBA30733E237B45DCD1A0F18E717EDD37E50F7E95323C944E07A9D570F860F5B86F1CA0D13014219E5F5357F34D7E6F982FCDC17FEC60BD568086BE823A7C530704E167744B97047E279A909A7EC469B59BF0549A76FB0CB58768640BF77D8CC59685F117D54FE421531F0F99D523CAD47A38A7203EA832117B14129A51A2EA1072D58B9A1CE0AB3DAE59F0EA6469E2D882CA8E2764ECA93E13B929A00B0108BAD8F5372BE3053781B525063484C226F0F13751D3E9AA674A83E277CAF53E95FBC01A5BA5E7513E0FB3643A79DE40265A6E634CC452DCCC5B8BE1F214E9CF1619C9D9F7F2AC732707CB3EAEBB3389C92DD985E139616CC1AD4BC56B95E96A4DAF3AAC909782FA0B16F752E599A2C9D0511BC857344BB9206217D1B543A1A8C95EAE25AC2E637DE212C91A3CFEA8F27EF6D12BEDC2C55D2F6324CA9E48AFE50B16027EAF466EA527CB2C46EC6E3B7357EF8938BA915E8B141F3E2AE75EABF55DAC8CAB9353A06EFBD7763C519ECF2CE1C77DBD86D28EBBEFEF612BEE2AC1C421982570C6621E492ACC40F8894D62A4EA9DB43D33A272B712FDDFCF5403A79EB1564A3294D0D32D6C26E93F8FDE42FF1A45AC45A1BFD696735F2BAB0CB9432A2F5839AA9148B7C911777D1067DACDFFBBE1686BC22E7B4580B020229B722B9CF384EECDB0B4F680827118EBFC54213C498632B8B8CD8EDE897CE7EBFE4D214FD819E6D0B32DBEF179AF10293746A70B3F3AD9EA7FC5111F9F05F13825A8C6AB0145C24E8C3F150C228F8507752D2D3108BAB9122A8070EB97CE384CE62F1889694C79E92C45B27FB5E9BC230EEFC6B3DD4319C7D14F01D1677F8121B8DD52BE8E9A83FCEDEB00B0A1E6E709ADC2C3F939C388EFBFB48F78F0D9CD18BE96DE6DC56BB4DEC3D4C3E7239B4E6AE0EBAAA111F3F7B3214C83E67F4D074145D62C46E24D8DEA9637EA3EF34952BC661D1C4D0C1A271E58CE31499038548F1A2F48E29F58B18AAE1904A673D2449DF8035827361494B1130456AAF2E90B6A2DBC48965C55BC44860D10748604D190A2DF930C9B5508BFC36151B35918514928B41C1312A5B611C2375783F9B619B5485592EAE60248500F81A3F1AD190676C04A45F754ECFB7DE34DDB39C479BC94B30578FB8A510DC640C1C1581AECB01766A9EA8BA16BE0E981B36F7B8FBAE818AF1937104B9E7D9A73A84E95CCFF2B185C485C83B8460A06B889701E9B813FF206AF5826719E24D71D413634B057AE05D13E0B6BF4F27EBDD1475AB97A8BF022113808FB30A4764E32F639BB5D2AA755C9865841D8BEE9E1A8119B685703B25A40380DD31CA77BDAE327C379A11A714714E92AFAC4177FFC97A73D7D05D27783E897E0CC7ED1E11B1EFF62E155DA4C1601527692F793DD2B1A3EFA3253ADB83B93A35E2069A6066B5ACBD4A341BA94BFB0413072571B36D32C4EDF0BDC7C8FBAFB458D9B104648EF8D98BB611E47F6FB198E7807CFDD46CBA315B2E5D98F69E7458008EEAFBD63FF894EB0FDDD1EBCB50C23F0581225441C8EC4AAC3BA616A9094C7283E793B0DA81D7C781B9059CC411F85B522F47068F80EBE76BBC709764ED86D67D47C4811AC80A483F5B7D8F4E94969468573E00B717F808840A1F88ABA8D54E5C64D4FC86869B58D133CB1D8E9A749065063E488AB4ABFD5EE67FCD977B5945936BED2C0091E2E1274B05C28EA0641CE0C39C96D62BAC6F2F9AA3D4B94D202192C3ACB8A960488E79B6C010AE3C1715CA7129DB55690C774341E909BA68DEC4020ECFA91C278D461D7A11F43088D903AF7223FABC423450519C3CE50ADAD21A17D35A276ACC6F9A5AA13EE10EA10C2296E1FE237E93E15B7060DAB87912995182EA6A261A8EA28E6159BBB46787447993756334BF92EBA409B6AFF830A7FDFA41EE97F40CA02A07DD117FAAED9F8A51098218E98DCFF69DE4F1ED2E1E1E05D3FA5B6F897C59EE24DCF13A1A2A52A6EF15D402E9BD954C03776319C6AA925E76BF352F56ECBD30E2E6C0B1B73AEFD8388DFB2EC2BBA60C2B1FF7467B8228EC849019267635A1F41214A48A55BC1B91C6FA1755E73173503C7B8ADFED0D8369DC86DBEE8EB267C2DC75B767EF4AB52C3438A141620F08AEE25BD647C809BD381C07B99B69C089B3AD1E706C1159A19B76FDA0605843EEECB7885D921D6155618ECB78FED05CE2C578D60A514B388AFE01E779644E186A6C396704641B59893BFDEB84C64E207B3FD2A6FE12A8EDFD247A710DEBEF9C92749CD6B4C06F6AD42843521C76341FC26B74F788EF48755402786BC56A6120621523628F1EE5367A86A304913799AD743D7956BD20A668718C3E9B4F418298BA0D3C99C77EB32247E68842FDB00C29E1633C38E6B38324FE98CE2F491AB2C3F744EB51C81BE805CAFAE286CFBAC5787B418C50507BF48CD86A4268E981DD9FF4C1D90E7D5332D9C4AE292E1105B93E58D519B84D5AC65826B4BC2F0AE5B6DE4538EC88D47AB5651806826C98140056F34057CD0383DD64EBD6BABC38BE45A74FBE718CD3E23C0FD2DCDD149DC4A7DB8546C7E2C2020D7269F9D379FAD924A49EF5F456573EB7B521B51B03F5CA93D25E1A306AB13C639E1D71F6C933B00DC91D99C3D1B4091FE434D50C344E508AE5D6B9622C538FF98A68D5A4187DE194095FEFBA807FF0F035C83DEEB27B30F7DAFD96314C583BD3C5D29DC2CD5CCEFE711B8801CA06BB4B07D35A2550B335ACD4DB398D30E1008E968435B2EC67D717BBEBEE8466B07168EA4B0F1128C2B0529F1BD7BE164448689A67FCC7C7AA0D15103AABF9A17E946FF1464602815873F4DFC6F6C77BD4EB78502D976B3DBFAC631DB37E1749D9554EB72FC0D3F517CC8FD8A34BA696FFAD3459C4B73998DCB736E35997334539B44FFD6A8AF25DF1F8C991",
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
    host : '172.19.2.24',
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
		type: 'text',
		data: 'I agree to T&C'
	};
	
	var content = [assetValue];
	var mediaContent = JSON.stringify({ 
		mediaContent: content, 
		reasonCode:"200", 
		reasonDescription:"Successful"
	});
	
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

app.post("/mdes/digitization/1/0/activate", function(req, res) {
	console.info('Activate Invoked');
	var activateRes = require('./var.js').mockactivateres;	
	return res.json(activateRes);
});

}
 
module.exports = appRouter;