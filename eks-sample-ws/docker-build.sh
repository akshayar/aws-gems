 echo "${BASH_SOURCE}"
 ${MAVEN_HOME}/bin/mvn clean install -DskipTests
 docker image rm arawa3/eks-sample-ws -f
 docker build . -f Dockerfile -t arawa3/eks-sample-ws
 docker image ls arawa3/eks-sample-ws
 docker push arawa3/eks-sample-ws
