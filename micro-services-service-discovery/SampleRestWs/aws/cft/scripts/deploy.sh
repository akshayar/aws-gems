#!/bin/bash
sudo yum update -y
sudo yum -y install java-1.8.0
sudo yum -y remove java-1.7.0-openjdk
mkdir -p /home/ec2-user/
cd /home/ec2-user/
aws s3 cp s3://${1}/$2 . 
chmod 777 $2
sudo nohup java -Djava.net.preferIPv4Stack=true -jar $2  

