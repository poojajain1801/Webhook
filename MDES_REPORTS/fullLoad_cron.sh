#!/bin/sh

#Full Load of Target
sh kitchen.sh -rep MdesPentahoRepository -user admin -pass password -job dwmob_full_load -dir MDESReportETL/dwmob/full_load 
/home/pentaho/pentahoLogs/dwmob_full_load_wallet_$(date +"%d%m%Y_%H%M%S").log