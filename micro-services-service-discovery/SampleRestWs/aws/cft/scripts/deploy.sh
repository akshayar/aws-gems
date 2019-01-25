#!/bin/bash
export APP_NAME=$3
$(sed -e "s/\${APP_NAME}/${APP_NAME}/g"  ./awslogs.conf > awslogs-${APP_NAME}.conf)
sudo yum update -y
sudo yum install awslogs -y
sudo cp awslogs-${APP_NAME}.conf /etc/awslogs/awslogs.conf
sudo awslogsd &
sudo yum -y install java-1.8.0
sudo yum -y remove java-1.7.0-openjdk

mkdir -p /home/ec2-user
cd /home/ec2-user
aws s3 cp s3://${1}/$2 . 
chmod 777 $2
sudo java -Djava.net.preferIPv4Stack=true -jar $2  