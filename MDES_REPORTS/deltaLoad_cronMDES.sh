#!/bin/sh


#Full Load of Target which will run as per the configuration made on cron after Staging delta load run completes
sh kitchen.sh -rep MDES_REPO -user admin -pass admin -job dwmob_delta_load -dir MDESReportETL\dwmob\delta_load /home/pentaho/pentaho_logs_$(date +"%d%m%Y_%H%M%S").log