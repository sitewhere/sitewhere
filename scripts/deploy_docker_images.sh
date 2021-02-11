#/bin/bash

set -ex

TAG=3.0

if [ "$#" -eq 1 ]; then
  TAG=$1 
fi

allMicroservices=(
 "service-asset-management"
 "service-batch-operations"
 "service-command-delivery"
 "service-device-management"
 "service-device-registration"
 "service-device-state"
 "service-event-management"
 "service-event-sources"
 "service-inbound-processing"
 "service-instance-management"
 "service-label-generation"
 "service-outbound-connectors"
 "service-schedule-management"
)

function tagAndPublishDocker() {
  docker tag sitewhere/${1}:${TAG} sitewhere/${1}:latest
  docker push sitewhere/${1}:${TAG}
  docker push sitewhere/${1}:latest
}

function tagAndPublishQuay() {
  docker tag docker.io/sitewhere/${1}:${TAG} quay.io/sitewhere/${1}:${TAG}
  docker tag docker.io/sitewhere/${1}:${TAG} quay.io/sitewhere/${1}:latest
  docker push quay.io/sitewhere/${1}:${TAG}
  docker push quay.io/sitewhere/${1}:latest
}

echo "Publishing SiteWhere Images to Docker.io with tag $1 and latest"

echo "$DOCKER_REGISTRY_PASSWORD" | docker login -u "$DOCKER_REGISTRY_USERNAME" --password-stdin
for ms in ${allMicroservices[@]}; do
  tagAndPublishDocker $ms
done

echo "Publishing SiteWhere Images to quay.io with tag $1 and latest"

echo "$QUAY_REGISTRY_PASSWORD" | docker login -u "$QUAY_REGISTRY_USERNAME" --password-stdin
for ms in ${allMicroservices[@]}; do
  tagAndPublishQuay $ms
done