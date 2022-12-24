 ACCOUNT_ID=`aws sts get-caller-identity --output text --query Account`
 $BASEDIR/docker-build-push.sh
 #docker push arawa3/eks-sample-ws
 #docker tag eks-sample-ws/kcl-consumer:latest ${ACCOUNT_ID}.dkr.ecr.us-east-1.amazonaws.com/eks-sample-ws/kcl-consumer:latest
 #docker push ${ACCOUNT_ID}.dkr.ecr.us-east-1.amazonaws.com/eks-sample-ws/kcl-consumer:latest
