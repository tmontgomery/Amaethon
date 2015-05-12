/*
 * Copyright 2015 Real Logic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

/* Generated SBE (Simple Binary Encoding) message codec */
package amaethon.generated;

import uk.co.real_logic.sbe.codec.java.*;
import uk.co.real_logic.agrona.MutableDirectBuffer;

public class GroupSizeEncodingEncoder
{
    private MutableDirectBuffer buffer;
    private int offset;
    private int actingVersion;

    public GroupSizeEncodingEncoder wrap(final MutableDirectBuffer buffer, final int offset, final int actingVersion)
    {
        this.buffer = buffer;
        this.offset = offset;
        this.actingVersion = actingVersion;
        return this;
    }

    public int size()
    {
        return 4;
    }

    public static int blockLengthNullValue()
    {
        return 65535;
    }

    public static int blockLengthMinValue()
    {
        return 0;
    }

    public static int blockLengthMaxValue()
    {
        return 65534;
    }
    public GroupSizeEncodingEncoder blockLength(final int value)
    {
        CodecUtil.uint16Put(buffer, offset + 0, value, java.nio.ByteOrder.LITTLE_ENDIAN);
        return this;
    }

    public static int numInGroupNullValue()
    {
        return 65535;
    }

    public static int numInGroupMinValue()
    {
        return 0;
    }

    public static int numInGroupMaxValue()
    {
        return 65534;
    }
    public GroupSizeEncodingEncoder numInGroup(final int value)
    {
        CodecUtil.uint16Put(buffer, offset + 2, value, java.nio.ByteOrder.LITTLE_ENDIAN);
        return this;
    }
}
