#!/bin/sh


#Full Load of Target which will run only once after Staging full load run completes
sh kitchen.sh -rep MDES_REPO -user admin -pass admin -job dwmob_full_load -dir MDESReportETL\dwmob\full_load
/home/pentaho/pentaho_logs_$(date +"%d%m%Y_%H%M%S").log