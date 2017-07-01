#!/bin/bash
echo ""
echo "  SiteWhere Standalone Instance (MongoDB/HiveMQ)"
echo ""

# Start HiveMQ
nohup /opt/hivemq/bin/run.sh &

# Start MongoDB and wait for it to become available.
nohup /usr/bin/mongod --smallfiles  &
echo 'Waiting for MongoDB to start...' && while ! nc -vz localhost 27017 2> /dev/null; do sleep 1; done

# Start SiteWhere
/opt/sitewhere/bin/startup.sh
