/*******************************************************************************
 Copyright (c) Microsoft Open Technologies (Shanghai) Company Limited.  All rights reserved.

 The MIT License (MIT)

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.

 *******************************************************************************/
package com.sitewhere.azure.device.communication.client;

import org.apache.qpid.amqp_1_0.codec.AbstractDescribedTypeWriter;
import org.apache.qpid.amqp_1_0.codec.ValueWriter;
import org.apache.qpid.amqp_1_0.type.UnsignedLong;

public class SelectorFilterWriter extends
        AbstractDescribedTypeWriter<SelectorFilter> {

    private static final ValueWriter.Factory<SelectorFilter> FACTORY = new ValueWriter.Factory<SelectorFilter>() {

        @Override
        public ValueWriter<SelectorFilter> newInstance(ValueWriter.Registry registry) {
            return new SelectorFilterWriter(registry);
        }
    };

    private SelectorFilter value;

    public SelectorFilterWriter(final ValueWriter.Registry registry) {
        super(registry);
    }

    public static void register(ValueWriter.Registry registry) {
        registry.register(SelectorFilter.class, FACTORY);
    }

    @Override
    protected void onSetValue(final SelectorFilter value) {
        this.value = value;
    }

    @Override
    protected void clear() {
        value = null;
    }

    @Override
    protected Object getDescriptor() {
        return UnsignedLong.valueOf(0x00000137000000AL);
    }

    @Override
    protected ValueWriter<String> createDescribedWriter() {
        return getRegistry().getValueWriter(value.getValue());
    }
}
