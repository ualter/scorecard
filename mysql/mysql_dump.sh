#!/bin/bash

cmd=/usr/local/mysql/bin/mysqldump
DATE=$(date +%Y%m%d_%H%M%S)
$cmd --opt -u Scorecard -p$1 scorecard_prd > /Users/ualter/Downloads/scorecard_prd_$DATE.sql

echo -n "Move Dump file to DropBox Backup Folder (y/n)?"
read answer
if [ "$answer" != "${answer#[Yy]}" ] ;then
    mv /Users/ualter/Downloads/scorecard_prd_$DATE.sql /Users/ualter/Dropbox/Backup/scorecard_prd_$DATE.sql
else
    //echo No
fi
echo ""
echo "Done!"
echo ""
