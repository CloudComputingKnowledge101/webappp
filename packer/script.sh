#!/bin/bash
MYSQLPWD=`sudo grep 'temporary password' /var/log/mysqld.log | awk 'NF{ print $NF }'`;
echo $MYSQLPWD;
mysql -uroot --password=$MYSQLPWD --connect-expired-password -e "ALTER USER 'root'@'localhost' IDENTIFIED BY 'rootPass@123\!';"



export MYSQL_PASSWORD=\"$MYSQLPWD\";
echo $MYSQL_PASSWORD;