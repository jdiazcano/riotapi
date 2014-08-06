/*
 * Copyright 2014 Malte Schütze
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

package net.boreeas.riotapi.rtmp.messages.control;

import lombok.Getter;
import lombok.ToString;
import net.boreeas.riotapi.rtmp.MessageType;
import net.boreeas.riotapi.rtmp.RtmpEvent;
import net.boreeas.riotapi.rtmp.serialization.AmfWriter;

import java.io.IOException;

/**
 * Created on 5/18/2014.
 */
@ToString
public class SetPeerBandwidth extends RtmpEvent {
    @Getter private int width;
    @Getter private int limitType;

    public SetPeerBandwidth(int width, int limitType) {
        super(MessageType.SET_PEER_BANDWIDTH);
        this.width = width;
        this.limitType = limitType;
    }

    @Override
    public void writeBody(AmfWriter writer) throws IOException {
        writer.writeInt(width);
        writer.write(limitType);
    }
}