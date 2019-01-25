#!/bin/bash
cd transaction-ws
gradle clean build -x test
aws s3 cp ./build/libs/transaction-ws-0.1.0.jar s3://aksh-test-deploy/
cd ../user-ws
gradle clean build -x test
aws s3 cp ./build/libs/user-ws-0.1.0.jar s3://aksh-test-deploy/
cd ..
aws s3 cp --recursive ./aws/cft/scripts/ s3://aksh-test-deploy/