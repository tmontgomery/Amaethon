/* Generated SBE (Simple Binary Encoding) message codec */
package amaethon.generated;

import uk.co.real_logic.sbe.codec.java.*;
import uk.co.real_logic.agrona.DirectBuffer;

public class NewHighBidDecoder
{
    public static final int BLOCK_LENGTH = 28;
    public static final int TEMPLATE_ID = 4;
    public static final int SCHEMA_ID = 1;
    public static final int SCHEMA_VERSION = 0;

    private final NewHighBidDecoder parentMessage = this;
    private DirectBuffer buffer;
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

    public NewHighBidDecoder wrap(
        final DirectBuffer buffer, final int offset, final int actingBlockLength, final int actingVersion)
    {
        this.buffer = buffer;
        this.offset = offset;
        this.actingBlockLength = actingBlockLength;
        this.actingVersion = actingVersion;
        limit(offset + actingBlockLength);

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

    public static int auctionIdId()
    {
        return 1;
    }

    public static String auctionIdMetaAttribute(final MetaAttribute metaAttribute)
    {
        switch (metaAttribute)
        {
            case EPOCH: return "unix";
            case TIME_UNIT: return "nanosecond";
            case SEMANTIC_TYPE: return "";
        }

        return "";
    }

    public static int auctionIdNullValue()
    {
        return -2147483648;
    }

    public static int auctionIdMinValue()
    {
        return -2147483647;
    }

    public static int auctionIdMaxValue()
    {
        return 2147483647;
    }

    public int auctionId()
    {
        return CodecUtil.int32Get(buffer, offset + 0, java.nio.ByteOrder.LITTLE_ENDIAN);
    }


    public static int highBidderIdId()
    {
        return 2;
    }

    public static String highBidderIdMetaAttribute(final MetaAttribute metaAttribute)
    {
        switch (metaAttribute)
        {
            case EPOCH: return "unix";
            case TIME_UNIT: return "nanosecond";
            case SEMANTIC_TYPE: return "";
        }

        return "";
    }

    public static long highBidderIdNullValue()
    {
        return -9223372036854775808L;
    }

    public static long highBidderIdMinValue()
    {
        return -9223372036854775807L;
    }

    public static long highBidderIdMaxValue()
    {
        return 9223372036854775807L;
    }

    public long highBidderId()
    {
        return CodecUtil.int64Get(buffer, offset + 4, java.nio.ByteOrder.LITTLE_ENDIAN);
    }


    public static int highBidId()
    {
        return 3;
    }

    public static String highBidMetaAttribute(final MetaAttribute metaAttribute)
    {
        switch (metaAttribute)
        {
            case EPOCH: return "unix";
            case TIME_UNIT: return "nanosecond";
            case SEMANTIC_TYPE: return "";
        }

        return "";
    }

    public static long highBidNullValue()
    {
        return -9223372036854775808L;
    }

    public static long highBidMinValue()
    {
        return -9223372036854775807L;
    }

    public static long highBidMaxValue()
    {
        return 9223372036854775807L;
    }

    public long highBid()
    {
        return CodecUtil.int64Get(buffer, offset + 12, java.nio.ByteOrder.LITTLE_ENDIAN);
    }


    public static int durationLeftId()
    {
        return 4;
    }

    public static String durationLeftMetaAttribute(final MetaAttribute metaAttribute)
    {
        switch (metaAttribute)
        {
            case EPOCH: return "unix";
            case TIME_UNIT: return "nanosecond";
            case SEMANTIC_TYPE: return "";
        }

        return "";
    }

    public static long durationLeftNullValue()
    {
        return -9223372036854775808L;
    }

    public static long durationLeftMinValue()
    {
        return -9223372036854775807L;
    }

    public static long durationLeftMaxValue()
    {
        return 9223372036854775807L;
    }

    public long durationLeft()
    {
        return CodecUtil.int64Get(buffer, offset + 20, java.nio.ByteOrder.LITTLE_ENDIAN);
    }

}
