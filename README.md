# aws-gems
This repository will be used for quick solutions of common problems faced in AWS world. 

## Micro-services based architecture - Multiple Service and service discovery
This is a common pattern where a thin client UI calls REST web services over HTTP/HTTPs. Services can call each other as well. There multiple way to architect this however the most common is to create self-contained web services each is highly available or even better load balanced. A service discovery component is created to enable services to call each other. An API gateway can also be added to the mix. <br> I am showing an example architecture using application load balancer and path based routing. Refer to the diagram below. 
![alt text](https://dmhnzl5mp9mj6.cloudfront.net/application-management_awsblog/images/img2.png)

### CloudFormation Template Vs Elastic Bean Stalk
EBS is a convenient way to create a web application using a click through console however it does not work for this architecture. The architecture needs multiple web applications deployed on different EC2s and an ALB fronting them. Each application deployed on EC2 will be part of a target group. ALB will route the request to right target group based on the URL path. This will be governed by ALB listener rules. The approach needs two set of application codes to be deployed. EBS approach does not support this. EBS allows one set of application deployable to be uploaded, hence EBS approach can not be used to achieve this architecture. 
<BR>
<BR>In the Cloudformation approach ALB, a listener and a default target group is created in one CloudFormation template. The listener ARN is exported as an output parameter. In another Cloudformation template autoscaling group, launch configuration, target group and listener rule is created for the web service. The reference to ALB listener is imported. The second set of stack will be created for each web service.
<BR>In this example amio-fmr-alb.json creates ALB , listener and default target group. amio-fmr-ec2.json creates autoscaling group, launch configuration, target group and listener rule for each web service. amio-fmr-ec2-iam.json created required role for each web service. 