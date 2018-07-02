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
    USER_ID          	     			character varying(255),
    CLIENT_WALLET_ACCOUNT_ID            character varying(255),
    STATUS            					character varying(255),
	STATUS_DISPLAY						character varying(255),
    CREATED_ON 							TIMESTAMP (6),
	MODIFIED_ON							TIMESTAMP (6),
	CONSTRAINT DWH_USER_DETAILS_PK PRIMARY KEY (CLIENT_WALLET_ACCOUNT_ID),
	CONSTRAINT USER_UNIQUE_CONSTRAINTS UNIQUE (USER_ID)
  );
  
  CREATE INDEX DWH_USER_DETAILS_ID ON MDES_TARGET_DB.DWH_USER_DETAILS (USER_ID);
  CREATE INDEX DWH_USER_DETAILS_STATUS ON MDES_TARGET_DB.DWH_USER_DETAILS (STATUS);
  
  
CREATE TABLE MDES_TARGET_DB.DWH_DEVICE_INFO
{
	CLIENT_DEVICE_ID 									character varying(255),
	PAYMENT_APP_INSTANCE_ID 							character varying(255),
    PAYMENT_APP_ID 										character varying(255),
    RNS_REGISTRATION_ID 								character varying(255),
    OS_NAME 											character varying(255),
    OS_VERSION 											character varying(255),
    NFC_CAPABLE 										character varying(255),
    IMEI 												character varying(255),
    V_CLIENT_ID 										character varying(255),
    DEVICE_MODEL 										character varying(255),
    HOST_DEVICE_ID										character varying(255),
    IS_VISA_ENABLED 									character varying(255),
    IS_MASTERCARD_ENABLED 								character varying(255),
    STATUS 												character varying(255),
    STATUS_DISPLAY										character varying(255),
	DEVICE_NAME											character varying(255),
	CLIENT_WALLET_ACCOUNT_ID
	CREATED_ON 											TIMESTAMP (6),
	MODIFIED_ON											TIMESTAMP (6),
    CONSTRAINT DWH_DEVICE_INFO_PK PRIMARY KEY (CLIENT_DEVICE_ID)
);

CREATE TABLE MDES_TARGET_DB.DWH_CARD_DETAILS
(
	CARD_ID   											character varying(255),
	CARD_IDENTIFIER       								character varying(255),
	CARD_SUFFIX 										character varying(255),
	TOKEN_SUFFIX 										character varying(255),
	CARD_TYPE 											character varying(255),
	VISA_PROVISION_TOKEN_ID 							character varying(255),
	MASTER_PAYMENT_APP_INSTANCE_ID						character varying(255),
	MASTER_TOKEN_INFO									character varying(255),
	MASTER_TOKEN_UNIQUE_REFERENCE						character varying(255),
	CLIENT_DEVICE_ID									character varying(255),
	REPLENISH_ON										TIMESTAMP (6),
	STATUS												character varying(255),
	STATUS_DISPLAY										character varying(255),
	CREATED_ON 											TIMESTAMP (6),
	MODIFIED_ON											TIMESTAMP (6),
	CONSTRAINT DWH_CARD_DETAILS_PK PRIMARY KEY (CARD_ID),
	CONSTRAINT CARD_UNIQUE_CONSTRAINTS UNIQUE (PAN_UNIQUE_REFERENCE)
);









  
 
 				

				
				
				