----------------------------------SysMessage Insert scripts-----------------------------------------------------
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE) values (null,'Transaction Success','200','1');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE) values (null,'Device is not eligible','207','1');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE) values (null,'Unable to process request','500','1');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE) values (null,'User activation required','201','1');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE) values (null,'Invalid activation code','202','1');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE) values (null,'Invalid user and device details','203','1');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE) values (null,'User is activated','204','1');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE) values (null,'Invalid User','205','1');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE) values (null,'invalid operation','262','1');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE) values (null,'Insufficient data','300','1');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE) values (null,'User already registered','501','1');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE) values (null,'Device Id already exist','701','1');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE) values (null,'Device registration failed','702','1');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE) values (null,'Invalid Device Id','703','1');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE) values (null,'Device not registered','704','1');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE) values (null,'Card details already registered ','705','1');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE) values (null,'Invalid request. Please cross verify the request ','706','1');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE) values (null,'Card details not exist ','707','1');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE) values (null,'Request failed at third party ','708','1');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE) values (null,'Invalid Paymentapp Instance ID ','2000','1');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE) values (null,'Card is not eligible for digitization ','2001','1');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE) values (null,' Token is not Registered for transaction history ','2002','1');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE) values (null,'Invalid Token Unique Reference','2003','1');


Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'The value provided for one or more request parameters is considered invalid.','709','1','invalidParameter');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'The API server could not understand the request ','710','1','invalidRequest');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'Missing required field ','711','1','incompleteRequest');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'Authentication error','712','1','inputValidationError');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'The purpose of the token does not match the intended usage of the token','713','1','tokenUsageViolation');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'An error occured on the server','714','1','serviceError');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'The server is currently unable to handle the request due to a temporary overloading or maintenance of the server','715','1','notReady');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'MST presentation type is not supported for this PAN. Remove this presentation type and retry the request','716','1','mstNotSupported');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'The PAN information provided is considered stale','717','1','provisionDataExpired');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'Card is declined','718','1','declined');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'Further operations for this card are no longer allowed. Contact your bank to resolve this issue','719','1','notAllowed');
/*
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'تعتبر معلومات PAN 'المقدمة قديمة','720','2','provisionDataExpired');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'No further operations are allowed for this card. Contact your bank to resolve this issue.','721','2','notAllowed');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'تأكيد فشل إجراء التوفير. إعادة المحاولة التزويد','722','2','serviceError');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'Temporary issue due to downtime - retry operation after waiting','500','2','serviceError');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'Temporary issue due to downtime - retry operation after waiting','503','2','serviceError');
*/
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'The input value for parameters is in invalid format - validate the input data','723','1','invalidParameters');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'This token does not currently support ODA. Could be supported in the future.','724','1','notSupported');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'The requested CVM method is not available at this time','725','1','cvmNotAvailable');
/*
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'رمز التوثيق غير صالح أو انتهت صلاحيته (عدم تطابق OTP)','726','2','notAllowed');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'رمز التوثيق غير صالح أو انتهت صلاحيته','409','2','invalidParameters');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'معلمة غير صالحة مقدمة في طلب واجهة برمجة التطبيقات','400','2','invalidParameters');
*/
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'Returned if validation failed for eithe rclientWalletAccountEmailAddress or lastDigits.','726','1','inputValidationError');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'Returned if authorize vProvisionedTokenId as belonging to the Token Requestor fails','727','1','AuthError');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'Visa declined this transaction. If you see this too frequently, and you think you are getting this in error please contact the issuer.','728','1','cardNotEligible');

Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'The JSON could not be parsed','729','1','INVALID_JSON');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'The field name is not valid for the object.','730','1','UNRECOGNIZED_FIELD');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'The request failed to present a valid cert to access the API','731','1','AUTHORIZATION_FAILED');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'The field is not in the correct format','732','1','INVALID_FIELD_FORMAT');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'The value does not fall between the minimum and maximum length for the field','733','1','INVALID_FIELD_LENGTH');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'The value is not allowed for the field','734','1','INVALID_FIELD_VALUE');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'The requested response host is invalid','735','1','INVALID_RESPONSE_HOST');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'Issuer did not respond in time. Try again','736','1','NO_RESPONSE_FROM_ISSUER');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'A required field is missing','737','1','MISSING_REQUIRED_FIELD');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'There was an error decrypting the encrypted payload','738','1','CRYPTOGRAPHY_ERROR');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'The system had an internal exception','739','1','INTERNAL_SERVICE_FAILURE');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'The PAN format is not valid, or other data associated with the PAN was incorrect or entered incorrectly','740','1','INVALID_PAN');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'The expiry date is required for this product but was missing','741','1','MISSING_EXPIRY_DATE');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'The provisioning of PAN to the device has failed ','742','1','PROVISION_FAILED');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'The PAN is not in an approved account range ','743','1','PAN_INELIGIBLE');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'The device is not supported for use','744','1','DEVICE_INELIGIBLE');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'The PAN is not allowed to be provisioned to the device','745','1','PAN_INELIGIBLE_FOR_DEVICE');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'The PAN has already been provisioned to the maximum number of devices','746','1','PAN_PROVISIONING_COUNT_EXCEEDED');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'The eligibility receipt is expired or the value cannot be found','747','1','INVALID_ELIGIBILITY_RECEIPT');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'Invalid Task Id','748','1','INVALID_TASK_ID');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'Invalid Terms and Conditions id','749','1','INVALID_TERMS_AND_CONDITIONS');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'Invalid Token UniqueReference','750','1','INVALID_TOKEN_UNIQUE_REFERENCE');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'Invalid PAN UniqueReference','751','1','INVALID_PAN_UNIQUE_REFERENCE');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'The token is in an invalid status for the requested operation','752','1','INVALID_TOKEN_STATUS');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'AID PIX value was not correct for the account','753','1','INVALID_AID_CARD_TYPE');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'The cardlet ID could not be found','754','1','INVALID_CARDLET_ID');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'Call failed –unrecognized URI or required parameters not present','755','1','INVALID_METHOD');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'Incorrect Tokenization Authentication Value','756','1','INCORRECT_TAV');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'Authentication Code has expired or was invalidated','757','1','EXPIRED_CODE');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'The data supplied for the request was invalid','758','1','INVALID_DATA');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'No Active Tokens','759','1','NO_ACTIVE_TOKENS');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'The count of alternate payment credentials permitted at a time for the token has exceeded the limit','760','1','ALT_CREDENTIALS_COUNT_EXCEEDED');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'Cryptographic data has already been exchanged','761','1','INVALID_CRYPTOGRAPHIC_DATA');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'The token pan in the request could not be found or the token is expired.','762','1','TOKEN_PAN_NOT_FOUND');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'The activation method could not be found','763','1','INVALID_AUTHENTICATION_METHOD');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'The operation requested is invalid for the token','764','1','INVALID_WORKFLOW');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'The PAN has already been provisioned to the device or the same request is currently being processed','765','1','DUPLICATE_REQUEST');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'AID RID value was not correct.','766','1','INVALID_AID_RID');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'Token Activated Successfully','767','1','200');


Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE) values (null,'نجاح المعاملات','200','2');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE) values (null,'الجهاز غير مؤهل','207','2');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE) values (null,'غير قادر على معالجة الطلب','500','2');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE) values (null,'تفعيل المستخدم المطلوبة','201','2');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE) values (null,'رمز التفعيل غير صالح','202','2');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE) values (null,'تفاصيل المستخدم والجهاز غير صالحة','203','2');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE) values (null,'يتم تنشيط المستخدم','204','2');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE) values (null,'مستخدم غير صالح','205','2');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE) values (null,'عملية غير صالحة','262','2');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE) values (null,'البيانات غير كافية','300','2');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE) values (null,'مستخدم مسجل مسبقا','501','2');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE) values (null,'معرف الجهاز موجود بالفعل','701','2');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE) values (null,'فشل تسجيل الجهاز','702','2');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE) values (null,'معرف الجهاز غير صالح','703','2');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE) values (null,'الجهاز غير مسجل','704','2');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE) values (null,'تفاصيل البطاقة مسجلة بالفعل','705','2');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE) values (null,'طلب غير صالح. يرجى عبر التحقق من الطلب','706','2');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE) values (null,'تفاصيل البطاقة غير موجودة','707','2');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE) values (null,'فشل الطلب في جهة خارجية','708','2');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE) values (null,'معرف غير صالح في Paymentapp','2000','2');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE) values (null,'البطاقة غير مؤهلة للحصول على رقمنة','2001','2');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE) values (null,' الرمز غير مسجل في سجل المعاملات ','2002','2');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE) values (null,'مرجع مميز مميز غير صالح','2003','2');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'القيمة المقدمة لمعلمة طلب واحدة أو أكثر تعتبر غير صالحة.','709','2','invalidParameter');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'لم يتمكن خادم API من فهم طلب ','710','2','invalidRequest');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'حقل مطلوب مفقود ','711','2','incompleteRequest');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'خطأ مصادقة','712','2','inputValidationError');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'لا يتوافق الغرض من الرمز المميز مع الاستخدام المقصود للرمز','713','2','tokenUsageViolation');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'حدث خطأ على الخادم','714','2','serviceError');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'يتعذر على الخادم حاليًا معالجة الطلب بسبب التحميل الزائد المؤقت أو صيانة الخادم','715','2','notReady');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'نوع عرض MST غير معتمد لـ PAN هذا. قم بإزالة نوع العرض التقديمي هذا ثم أعد محاولة طلب','716','2','mstNotSupported');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'تعتبر معلومات PAN المقدمة قديمة','717','2','provisionDataExpired');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'تم رفض البطاقة','718','2','declined');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'لم يعد يُسمح بإجراء المزيد من العمليات لهذه البطاقة. اتصل بالمصرف الذي تتعامل معه لحل هذه المشكلة','719','2','notAllowed');
/*
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'تعتبر معلومات PAN المقدمة قديمة','720','2','provisionDataExpired');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'لم يعد يُسمح بإجراء المزيد من العمليات لهذه البطاقة. اتصل بالمصرف الذي تتعامل معه لحل هذه المشكلة.','721','2','notAllowed');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'تأكيد فشل إجراء التوفير. إعادة المحاولة التزويد','722','2','serviceError');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'مشكلة مؤقتة بسبب التوقف - إعادة المحاولة بعد الإنتظار','500','2','serviceError');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'مشكلة مؤقتة بسبب التوقف - إعادة المحاولة بعد الإنتظار','503','2','serviceError');
*/
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'تكون قيمة الإدخال للمعلمات بتنسيق غير صالح - تحقق من صحة بيانات الإدخال','723','2','invalidParameters');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'هذا الرمز المميز لا يدعم حاليًا المساعدة الإنمائية الرسمية. من الممكن أن يدعمها في المستقبل.','724','2','notSupported');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'طريقة CVM المطلوبة غير متوفرة في هذا الوقت','725','2','cvmNotAvailable');
/*
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'رمز التوثيق غير صالح أو انتهت صلاحيته (عدم تطابق OTP)','726','2','notAllowed');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'رمز التوثيق غير صالح أو انتهت صلاحيته','409','2','invalidParameters');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'معلمة غير صالحة مقدمة في طلب واجهة برمجة التطبيقات','400','2','invalidParameters');
*/
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'عاد إذا فشل التحقق من الصحة ل eithe rclientWalletAccountEmailAddress أو الأرقام الأخيرة','400','2','inputValidationError');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'تمت إعادتها في حالة تخويل vProvisionedTokenId كما ينتمي إلى Token Requestor','401','2','AuthError');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'رفضت فيزا هذه المعاملة. إذا كنت ترى ذلك بشكل متكرر ، وتعتقد أنك تحصل على هذا الخطأ ، فيرجى الاتصال بالمصدر','728','2','cardNotEligible');

Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'لا يمكن تحليل JSON','729','2','INVALID_JSON');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'اسم المجال غير صالح للكائن','730','2','UNRECOGNIZED_FIELD');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'أخفق الطلب في تقديم شهادة صالحة للدخول إلى واجهة برمجة التطبيقات','731','2','AUTHORIZATION_FAILED');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'الحقل ليس بالتنسيق الصحيح','732','2','INVALID_FIELD_FORMAT');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'لا تسقط القيمة بين الحد الأدنى والحد الأقصى لطول الحقل','733','2','INVALID_FIELD_LENGTH');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'القيمة غير مسموح بها للحقل','734','2','INVALID_FIELD_VALUE');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'مضيف الاستجابة المطلوبة غير صالح','735','2','INVALID_RESPONSE_HOST');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'المصدر لم يستجب في الوقت المناسب. حاول ثانية','736','2','NO_RESPONSE_FROM_ISSUER');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'حقل مطلوب مفقود','737','2','MISSING_REQUIRED_FIELD');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'حدث خطأ يفك تشفير الحمولة المشفرة','738','2','CRYPTOGRAPHY_ERROR');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'كان النظام استثناء داخلي','739','2','INTERNAL_SERVICE_FAILURE');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'تنسيق PAN غير صالح ، أو البيانات الأخرى المرتبطة بـ PAN غير صحيحة أو تم إدخالها بشكل غير صحيح','740','2','INVALID_PAN');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'تاريخ انتهاء الصلاحية مطلوب لهذا المنتج ولكنه مفقود','741','2','MISSING_EXPIRY_DATE');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'فشل توفير PAN إلى الجهاز','742','2','PROVISION_FAILED');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'PAN ليس في نطاق حساب معتمد ','743','2','PAN_INELIGIBLE');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'الجهاز غير مدعوم للاستخدام','744','2','DEVICE_INELIGIBLE');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'لا يُسمح بحساب PAN على الجهاز','745','2','PAN_INELIGIBLE_FOR_DEVICE');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'تم تزويد PAN مسبقًا بالحد الأقصى لعدد الأجهزة','746','2','PAN_PROVISIONING_COUNT_EXCEEDED');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'انتهت صلاحية إيصال الاستحقاق أو لا يمكن العثور على القيمة','747','2','INVALID_ELIGIBILITY_RECEIPT');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'معرف المهمة غير صالح','748','2','INVALID_TASK_ID');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'بنود وشروط غير صالحة','749','2','INVALID_TERMS_AND_CONDITIONS');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'رمز غير صالح UniqueReference','750','2','INVALID_TOKEN_UNIQUE_REFERENCE');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'بطاقة PAN غير صالحة UniqueReference','751','2','INVALID_PAN_UNIQUE_REFERENCE');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'الرمز المميز في حالة غير صالحة للعملية المطلوبة','752','2','INVALID_TOKEN_STATUS');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'كانت قيمة PID AID غير صحيحة للحساب','753','2','INVALID_AID_CARD_TYPE');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'لا يمكن العثور على معرف البطاقة','754','2','INVALID_CARDLET_ID');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'فشل الاتصال - معرف الموارد المنتظم غير المعترف به أو المعلمات المطلوبة غير موجودة','755','2','INVALID_METHOD');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'قيمة مصادقة توحيد الدقة غير صحيحة','756','2','INCORRECT_TAV');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'انتهت صلاحية كود التوثيق أو تم إبطاله','757','2','EXPIRED_CODE');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'البيانات التي تم توفيرها للطلب غير صالحة','758','2','INVALID_DATA');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'لا توجد رموز نشطة','759','2','NO_ACTIVE_TOKENS');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'تجاوز عدد بيانات اعتماد الدفع البديلة المسموح بها في وقت الرمز المميز الحد','760','2','ALT_CREDENTIALS_COUNT_EXCEEDED');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'تم بالفعل تبادل البيانات التشفير','761','2','INVALID_CRYPTOGRAPHIC_DATA');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'تعذر العثور على عموم الرمز المميز في الطلب أو انتهت صلاحية الرمز المميز','762','2','TOKEN_PAN_NOT_FOUND');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'تعذر العثور على طريقة التنشيط','763','2','INVALID_AUTHENTICATION_METHOD');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'العملية المطلوبة غير صالحة للرمز','764','2','INVALID_WORKFLOW');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'تم تزويد PAN مسبقًا بالجهاز أو أن الطلب نفسه قيد المعالجة حاليًا','765','2','DUPLICATE_REQUEST');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'قيمة RID AID غير صحيحة','766','2','INVALID_AID_RID');
Insert into SYS_MESSAGES (BEARER,MESSAGE,MESSAGE_CODE,LANGUAGE_CODE,REASON_CODE) values (null,'تم تنشيط الرمز المميز بنجاح','767','2','200');
















