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
package com.sitewhere.commands.destination.sms;

import com.sitewhere.commands.destination.CommandDestination;
import com.sitewhere.commands.encoding.string.JsonStringCommandExecutionEncoder;
import com.sitewhere.commands.spi.ICommandDeliveryProvider;
import com.sitewhere.commands.spi.ICommandDestination;

/**
 * Implementation of {@link ICommandDestination} that encodes and delivers
 * messages that are strings and {@link ICommandDeliveryProvider} requires
 * {@link SmsParameters}.
 */
public class SmsCommandDestination extends CommandDestination<String, SmsParameters> {

    public SmsCommandDestination() {
	setCommandExecutionEncoder(new JsonStringCommandExecutionEncoder());
	setCommandDeliveryParameterExtractor(new SmsParameterExtractor());
    }
}