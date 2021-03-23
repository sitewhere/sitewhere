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
package com.sitewhere.instance.grpc.user;

import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.instance.spi.user.grpc.IUserManagementGrpcServer;
import com.sitewhere.microservice.grpc.GrpcServer;
import com.sitewhere.spi.microservice.grpc.IGrpcSettings;
import com.sitewhere.spi.microservice.user.IUserManagement;

/**
 * Hosts a GRPC server that handles user management requests.
 */
public class UserManagementGrpcServer extends GrpcServer implements IUserManagementGrpcServer {

    public UserManagementGrpcServer(IInstanceManagementMicroservice microservice, IUserManagement userManagement) {
	super(new UserManagementImpl(microservice, userManagement), IGrpcSettings.USER_MANAGEMENT_API_PORT,
		IGrpcSettings.USER_MANAGEMENT_API_HEALTH_PORT);
    }
}