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

public class AuctionEncoder
{
    public static final int BLOCK_LENGTH = 16;
    public static final int TEMPLATE_ID = 1;
    public static final int SCHEMA_ID = 1;
    public static final int SCHEMA_VERSION = 0;

    private final AuctionEncoder parentMessage = this;
    private MutableDirectBuffer buffer;
    protected int offset;
    protected int limit;
    protected int actingBlockLength;
    protected int actingVersion;

    public int sbeBlockLength()
    {
        return BLOCK_LENGTH;
    }

    public int sbeTemplateId()
    {
        return TEMPLATE_ID;
    }

    public int sbeSchemaId()
    {
        return SCHEMA_ID;
    }

    public int sbeSchemaVersion()
    {
        return SCHEMA_VERSION;
    }

    public String sbeSemanticType()
    {
        return "";
    }

    public int offset()
    {
        return offset;
    }

    public AuctionEncoder wrap(final MutableDirectBuffer buffer, final int offset)
    {
        this.buffer = buffer;
        this.offset = offset;
        limit(offset + BLOCK_LENGTH);
        return this;
    }

    public int size()
    {
        return limit - offset;
    }

    public int limit()
    {
        return limit;
    }

    public void limit(final int limit)
    {
        buffer.checkLimit(limit);
        this.limit = limit;
    }

    public static long durationInNanosNullValue()
    {
        return -9223372036854775808L;
    }

    public static long durationInNanosMinValue()
    {
        return -9223372036854775807L;
    }

    public static long durationInNanosMaxValue()
    {
        return 9223372036854775807L;
    }
    public AuctionEncoder durationInNanos(final long value)
    {
        CodecUtil.int64Put(buffer, offset + 0, value, java.nio.ByteOrder.LITTLE_ENDIAN);
        return this;
    }

    public static long reserveNullValue()
    {
        return -9223372036854775808L;
    }

    public static long reserveMinValue()
    {
        return -9223372036854775807L;
    }

    public static long reserveMaxValue()
    {
        return 9223372036854775807L;
    }
    public AuctionEncoder reserve(final long value)
    {
        CodecUtil.int64Put(buffer, offset + 8, value, java.nio.ByteOrder.LITTLE_ENDIAN);
        return this;
    }

    public static int nameId()
    {
        return 3;
    }

    public static String nameCharacterEncoding()
    {
        return "UTF-8";
    }

    public static String nameMetaAttribute(final MetaAttribute metaAttribute)
    {
        switch (metaAttribute)
        {
            case EPOCH: return "unix";
            case TIME_UNIT: return "nanosecond";
            case SEMANTIC_TYPE: return "";
        }

        return "";
    }

    public int putName(final uk.co.real_logic.agrona.DirectBuffer src, final int srcOffset, final int length)
    {
        final int sizeOfLengthField = 1;
        final int limit = limit();
        limit(limit + sizeOfLengthField + length);
        CodecUtil.uint8Put(buffer, limit, (short)length);
        buffer.putBytes(limit + sizeOfLengthField, src, srcOffset, length);

        return length;
    }

    public int putName(final byte[] src, final int srcOffset, final int length)
    {
        final int sizeOfLengthField = 1;
        final int limit = limit();
        limit(limit + sizeOfLengthField + length);
        CodecUtil.uint8Put(buffer, limit, (short)length);
        buffer.putBytes(limit + sizeOfLengthField, src, srcOffset, length);

        return length;
    }

    public void name(final String value)
    {
        final byte[] bytes;
        try
        {
            bytes = value.getBytes("UTF-8");
        }
        catch (final java.io.UnsupportedEncodingException ex)
        {
            throw new RuntimeException(ex);
        }

        final int length = bytes.length;
        final int sizeOfLengthField = 1;
        final int limit = limit();
        limit(limit + sizeOfLengthField + length);
        CodecUtil.uint8Put(buffer, limit, (short)length);
        buffer.putBytes(limit + sizeOfLengthField, bytes, 0, length);
    }
}
