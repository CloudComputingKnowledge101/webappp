#!/bin/bash
/*MYSQLPWD=`sudo grep 'temporary password' /var/log/mysqld.log | awk 'NF{ print $NF }'`;
echo $MYSQLPWD;
mysql -uroot --password=$MYSQLPWD --connect-expired-password -e "ALTER USER 'root'@'localhost' IDENTIFIED BY 'rootPass@123\!';"



export MYSQL_PASSWORD=\"$MYSQLPWD\";
echo $MYSQL_PASSWORD;*/


echo "export MYSQL_PASSWORD=rootPass123" | sudo tee -a /etc/profile.d/env_setup.sh

echo "export MYSQL_USERNAME=csye6225" | sudo tee -a /etc/profile.d/env_setup.sh


echo "export AWS_S3_BUCKET=csye6225" | sudo tee -a /etc/profile.d/env_setup.sh