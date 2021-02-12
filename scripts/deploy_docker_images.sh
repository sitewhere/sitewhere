#/bin/bash

set -ex

FEATURE_TAG=3.0
TAG=3.0

if [ "$#" -eq 1 ]; then
  TEMP=$1
  TAG=${TEMP:1}
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
  docker tag sitewhere/${1}:${2} sitewhere/${1}:latest
  docker tag sitewhere/${1}:${2} sitewhere/${1}:${FEATURE_TAG}
  docker push sitewhere/${1}:${2}
  docker push sitewhere/${1}:latest
  docker push sitewhere/${1}:${FEATURE_TAG}
}

function tagAndPublishQuay() {
  docker tag docker.io/sitewhere/${1}:${2} quay.io/sitewhere/${1}:${2}
  docker tag docker.io/sitewhere/${1}:${2} quay.io/sitewhere/${1}:latest
  docker tag docker.io/sitewhere/${1}:${2} quay.io/sitewhere/${1}:${FEATURE_TAG}
  docker push quay.io/sitewhere/${1}:${2}
  docker push quay.io/sitewhere/${1}:latest
  docker push quay.io/sitewhere/${1}:${FEATURE_TAG}
}

echo "Publishing SiteWhere Images to Docker.io with tag $TAG and latest"

echo "$DOCKER_REGISTRY_PASSWORD" | docker login -u "$DOCKER_REGISTRY_USERNAME" --password-stdin
for ms in ${allMicroservices[@]}; do
  tagAndPublishDocker $ms $TAG
done

echo "Publishing SiteWhere Images to quay.io with tag $TAG and latest"

echo "$QUAY_REGISTRY_PASSWORD" | docker login quay.io -u "$QUAY_REGISTRY_USERNAME" --password-stdin
for ms in ${allMicroservices[@]}; do
  tagAndPublishQuay $ms $TAG
done