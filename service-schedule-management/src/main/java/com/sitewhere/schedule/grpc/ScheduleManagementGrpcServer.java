/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.schedule.grpc;

import com.sitewhere.microservice.grpc.MultitenantGrpcServer;
import com.sitewhere.schedule.spi.grpc.IScheduleManagementGrpcServer;
import com.sitewhere.schedule.spi.microservice.IScheduleManagementMicroservice;
import com.sitewhere.spi.microservice.grpc.IGrpcSettings;

/**
 * Hosts a GRPC server that handles schedule management requests.
 */
public class ScheduleManagementGrpcServer extends MultitenantGrpcServer implements IScheduleManagementGrpcServer {

    public ScheduleManagementGrpcServer(IScheduleManagementMicroservice microservice) {
	super(new ScheduleManagementRouter(microservice), IGrpcSettings.DEFAULT_API_PORT,
		IGrpcSettings.DEFAULT_API_HEALTH_PORT);
    }
}