create user MDES_TARGET_DB identified by password 
quota unlimited on system;

grant connect,resource to MDES_TARGET_DB;
grant select on v_$session to MDES_TARGET_DB;
grant create view to MDES_TARGET_DB;
grant create procedure to MDES_TARGET_DB;
grant create synonym to MDES_TARGET_DB;
grant create trigger to MDES_TARGET_DB;
grant create job to MDES_TARGET_DB;
-------------------------------------------------------------------------
--Logging Table
CREATE SEQUENCE serial;
CREATE TABLE MDES_TARGET_DB.log_etlsync
(
  sync_id number DEFAULT serial.nextval,
  sync_name character varying(50) NOT NULL,
  start_synctime timestamp (6),
  end_synctime timestamp (6),
  status character varying(10) DEFAULT 'Failure',
  CONSTRAINT sync_id_pk PRIMARY KEY (sync_id)
);
-------------------------------------------------------------------------	   
--Core Tables 
-------------------------------------------------------------------------
CREATE TABLE MDES_TARGET_DB.DWH_USER_DETAILS
  (	
    ID          	     				character varying(255),
    USER_NAME           				character varying(255) ,
    CLIENT_WALLET_ACCOUNT_ID            character varying(255),
    CLIENT_DEVICE_ID             		character varying(255),
    USER_STATUS            				character varying(255),
	USER_STATUS_DISPLAY					character varying(255),
    CREATED_ON 							TIMESTAMP (6)  NOT NULL,
	MODIFIED_ON							TIMESTAMP (6)  NOT NULL,
    PAYMENT_APP_INSTANCE_ID 			character varying(255),
	CONSTRAINT DWH_USER_DETAILS_PK PRIMARY KEY (ID),
	CONSTRAINT UNIQUE_CONSTRAINTS UNIQUE (USER_NAME)
  );
  
  CREATE INDEX DWH_USER_DETAILS_ID ON MDES_TARGET_DB.DWH_USER_DETAILS (CREATED_ON);
  CREATE INDEX DWH_USER_DETAILS_STATUS ON MDES_TARGET_DB.DWH_USER_DETAILS (USER_STATUS);
  
  
CREATE TABLE MDES_TARGET_DB.DWH_DEVICE_INFO
{
	ID 									character varying(255),
	PAYMENT_APP_INSTANCE_ID 			character varying(255),
    OS_NAME 							character varying(255),
    OS_VERSION 							character varying(255),
    NFC_CAPABLE 						character varying(255),
    IMEI 								character varying(255),
    CLIENT_DEVICE_ID 					character varying(255),
    V_CLIENT_ID 						character varying(255),
    DEVICE_MODEL 						character varying(255),
    HOST_DEVICE_ID 						character varying(255),
    VISA_ENABLED						character varying(255),
    MASTERCARD_ENABLED 					character varying(255),
    VISA_MESSAGE 						character varying(255),
    MASTERCARD_MESSAGE 					character varying(255),
    DEVICE_STATUS 						character varying(255),
	DEVICE_STATUS_DISPLAY				character varying(255),
	CREATED_ON 							TIMESTAMP (6)  NOT NULL,
	MODIFIED_ON							TIMESTAMP (6)  NOT NULL,
    CONSTRAINT DWH_DEVICE_INFO_PK PRIMARY KEY (ID),
    CONSTRAINT DEVICE_UNIQUE_CONSTRAINTS UNIQUE (IMEI)
);

CREATE TABLE MDES_TARGET_DB.DWH_CARD_DETAILS
(
	ID   								character varying(255),
	PAYMENT_APP_INSTANCE_ID       		character varying(255),
	TOKEN_UNIQUE_REFERENCE 				character varying(255),
	PAN_UNIQUE_REFERENCE 				character varying(255),
	TOKEN_INFO 							character varying(255),
	TOKEN_STATUS 						character varying(255),
	TOKEN_STATUS_DISPLAY				character varying(255),
	CREATED_ON 							TIMESTAMP (6),
	MODIFIED_ON							TIMESTAMP (6),
	CONSTRAINT DWH_CARD_DETAILS_PK PRIMARY KEY (ID),
	CONSTRAINT CPAYMENT_APP_INSTANCE_ID_KEY UNIQUE (PAYMENT_APP_INSTANCE_ID),
	CONSTRAINT CARD_UNIQUE_CONSTRAINTS UNIQUE (TOKEN_UNIQUE_REFERENCE)
);

CREATE TABLE MDES_TARGET_DB.DWH_CARD_DETAILS_VISA
(
	ID 									character varying(255),
	USER_NAME 							character varying(255),
	CARDNUMBERSUFFIX 					character varying(255),
	VPANENROLLMENTID 					character varying(255),
	STATUS 								character varying(255),
	STATUS_DISPLAY								character varying(255),
	CREATED_ON 							TIMESTAMP (6),
	MODIFIED_ON							TIMESTAMP (6),
  CONSTRAINT PK_CARD_DETAILS_VISA PRIMARY KEY (ID),
  CONSTRAINT FK_USER_CARD_DETAIL FOREIGN KEY (USER_NAME)
  REFERENCES MDES_TARGET_DB.DWH_USER_DETAILS (USER_NAME),
  CONSTRAINT VPAN_UNIQUE_CONSTRAINTS UNIQUE (VPANENROLLMENTID)
);
  
 
 				

				
				
				