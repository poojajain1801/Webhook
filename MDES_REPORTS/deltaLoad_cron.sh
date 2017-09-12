#!/bin/sh

#Delta Load of Target which will run as per the configuration made on cron
sh kitchen.sh -rep MdesPentahoRepository -user admin -pass password -job dwmob_delta_load -dir MDESReportETL/dwmob/delta_load 
/home/pentaho/pentahoLogs/dwmob_delta_load_wallet_$(date +"%d%m%Y_%H%M%S").log