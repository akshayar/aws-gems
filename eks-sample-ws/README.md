## EKS Java Web Service
### Build

```
//Build 
export MAVEN_HOME=<>
./docker-build.sh
```
### Setup Service Account
```
export ACCOUNT_ID=`aws sts get-caller-identity --output text --query Account`
export CLUSTER_NAME=emr-on-eks-fargate
export REGION=us-east-1
oidc_id=$(aws eks describe-cluster --name ${CLUSTER_NAME} --query "cluster.identity.oidc.issuer" --output text --region ${REGION} | cut -d '/' -f 5)
aws iam list-open-id-connect-providers --region ${REGION} | grep $oidc_id
eksctl utils associate-iam-oidc-provider --cluster ${CLUSTER_NAME} --region ${REGION}  --approve

aws iam create-policy --policy-name eks-sample --policy-document file://my-policy.json

eksctl create iamserviceaccount --name eks-sample-account --namespace default \
--cluster ${CLUSTER_NAME} --role-name "eks-sample-role" \
--attach-policy-arn arn:aws:iam::${ACCOUNT_ID}:policy/eks-sample --approve

aws iam get-role --role-name eks-sample-role --query Role.AssumeRolePolicyDocument

aws iam list-attached-role-policies --role-name eks-sample-role --query 'AttachedPolicies[].PolicyArn' --output text
export policy_arn=arn:aws:iam::${ACCOUNT_ID}:policy/eks-sample
aws iam get-policy --policy-arn $policy_arn
aws iam get-policy-version --policy-arn $policy_arn --version-id v1

kubectl describe serviceaccount eks-sample-account -n default

```
### Deploy
```
kubectl delete -f kube-deployment.yaml
kubectl apply -f kube-deployment.yaml
kubectl get pods
```
Sample Output
```
kubectl apply -f kube-deployment.yaml
deployment.apps/eks-sample-ws created
kubectl get pods
NAME                            READY   STATUS    RESTARTS   AGE
eks-sample-ws-54cbcc454-ddcqr   1/1     Running   0          14s

```
### Test
```
kubectl get pods
kubectl port-forward <pod-name>   8080:8080

curl http://localhost:8080

curl -X PUT  http://localhost:8080/s3

curl -X GET  http://localhost:8080/s3/1671886792133file.txt
```

```
curl http://localhost:8080
{s3=[], date=Sat Dec 24 13:37:05 UTC 2022, sts=arn:aws:sts::ACCOUNT_ID:assumed-role/eks-sample-role/aws-sdk-java-1671888972435, error=}%
curl -X PUT  http://localhost:8080/s3                      
{"1671887224294file.txt":{"versionId":null,"expirationTime":null,"expirationTimeRuleId":null,"contentMd5":"9E9ozeU55gm2YgLSNH46TQ==",....}%                                                                                                                            

curl -X GET  http://localhost:8080/s3/1671887224294file.txt
File Created at 1671887224294%         
```