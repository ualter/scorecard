#!/bin/bash

cmd=/usr/local/mysql/bin/mysqldump
DATE=$(date +%Y_%m_%d_%H%M)
$cmd --opt -u Scorecard -p$1 scorecard_prd > /Users/ualter/Downloads/scorecard_prd_$DATE.sql
