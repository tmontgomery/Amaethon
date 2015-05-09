/* Generated SBE (Simple Binary Encoding) message codec */
package amaethon.generated;

import uk.co.real_logic.sbe.codec.java.*;
import uk.co.real_logic.agrona.DirectBuffer;

public class AuctionOverDecoder
{
    public static final int BLOCK_LENGTH = 20;
    public static final int TEMPLATE_ID = 5;
    public static final int SCHEMA_ID = 1;
    public static final int SCHEMA_VERSION = 0;

    private final AuctionOverDecoder parentMessage = this;
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

    public AuctionOverDecoder wrap(
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


    public static int winningBidderIdId()
    {
        return 2;
    }

    public static String winningBidderIdMetaAttribute(final MetaAttribute metaAttribute)
    {
        switch (metaAttribute)
        {
            case EPOCH: return "unix";
            case TIME_UNIT: return "nanosecond";
            case SEMANTIC_TYPE: return "";
        }

        return "";
    }

    public static long winningBidderIdNullValue()
    {
        return -9223372036854775808L;
    }

    public static long winningBidderIdMinValue()
    {
        return -9223372036854775807L;
    }

    public static long winningBidderIdMaxValue()
    {
        return 9223372036854775807L;
    }

    public long winningBidderId()
    {
        return CodecUtil.int64Get(buffer, offset + 4, java.nio.ByteOrder.LITTLE_ENDIAN);
    }


    public static int winningBidId()
    {
        return 3;
    }

    public static String winningBidMetaAttribute(final MetaAttribute metaAttribute)
    {
        switch (metaAttribute)
        {
            case EPOCH: return "unix";
            case TIME_UNIT: return "nanosecond";
            case SEMANTIC_TYPE: return "";
        }

        return "";
    }

    public static long winningBidNullValue()
    {
        return -9223372036854775808L;
    }

    public static long winningBidMinValue()
    {
        return -9223372036854775807L;
    }

    public static long winningBidMaxValue()
    {
        return 9223372036854775807L;
    }

    public long winningBid()
    {
        return CodecUtil.int64Get(buffer, offset + 12, java.nio.ByteOrder.LITTLE_ENDIAN);
    }

}
