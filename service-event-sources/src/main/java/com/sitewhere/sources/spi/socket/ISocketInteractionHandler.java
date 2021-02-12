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
package com.sitewhere.sources.spi.socket;

import java.net.Socket;

import com.sitewhere.sources.spi.IInboundEventReceiver;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Interface for handling socket communication with a remote device.
 * 
 * @param <T>
 */
public interface ISocketInteractionHandler<T> extends ITenantEngineLifecycleComponent {

    /**
     * Delegates processing of socket information. Commands parsed from the socket
     * should be passed to {@link IInboundEventReceiver} onEventPayloadReceived()
     * method.
     * 
     * @param socket
     * @param receiver
     * @throws SiteWhereException
     */
    public void process(Socket socket, IInboundEventReceiver<T> receiver) throws SiteWhereException;
}