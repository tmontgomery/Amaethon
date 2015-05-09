/* Generated SBE (Simple Binary Encoding) message codec */
package amaethon.generated;

import uk.co.real_logic.sbe.codec.java.*;
import uk.co.real_logic.agrona.MutableDirectBuffer;

@GroupOrder({AuctionListEncoder.ActiveAuctionsEncoder.class})
public class AuctionListEncoder
{
    public static final int BLOCK_LENGTH = 0;
    public static final int TEMPLATE_ID = 6;
    public static final int SCHEMA_ID = 1;
    public static final int SCHEMA_VERSION = 0;

    private final AuctionListEncoder parentMessage = this;
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

    public AuctionListEncoder wrap(final MutableDirectBuffer buffer, final int offset)
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

    private final ActiveAuctionsEncoder activeAuctions = new ActiveAuctionsEncoder();

    public static long activeAuctionsId()
    {
        return 1;
    }

    public ActiveAuctionsEncoder activeAuctionsCount(final int count)
    {
        activeAuctions.wrap(parentMessage, buffer, count);
        return activeAuctions;
    }

    public static class ActiveAuctionsEncoder
    {
        private static final int HEADER_SIZE = 4;
        private final GroupSizeEncodingEncoder dimensions = new GroupSizeEncodingEncoder();
        private AuctionListEncoder parentMessage;
        private MutableDirectBuffer buffer;
        private int blockLength;
        private int actingVersion;
        private int count;
        private int index;
        private int offset;

        public void wrap(final AuctionListEncoder parentMessage, final MutableDirectBuffer buffer, final int count)
        {
            this.parentMessage = parentMessage;
            this.buffer = buffer;
            actingVersion = SCHEMA_VERSION;
            dimensions.wrap(buffer, parentMessage.limit(), actingVersion);
            dimensions.blockLength((int)32);
            dimensions.numInGroup((int)count);
            index = -1;
            this.count = count;
            blockLength = 32;
            parentMessage.limit(parentMessage.limit() + HEADER_SIZE);
        }

        public static int sbeHeaderSize()
        {
            return HEADER_SIZE;
        }

        public static int sbeBlockLength()
        {
            return 32;
        }

        public ActiveAuctionsEncoder next()
        {
            if (index + 1 >= count)
            {
                throw new java.util.NoSuchElementException();
            }

            offset = parentMessage.limit();
            parentMessage.limit(offset + blockLength);
            ++index;

            return this;
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
        public ActiveAuctionsEncoder auctionId(final int value)
        {
            CodecUtil.int32Put(buffer, offset + 0, value, java.nio.ByteOrder.LITTLE_ENDIAN);
            return this;
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
        public ActiveAuctionsEncoder durationLeft(final long value)
        {
            CodecUtil.int64Put(buffer, offset + 4, value, java.nio.ByteOrder.LITTLE_ENDIAN);
            return this;
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
        public ActiveAuctionsEncoder highBidderId(final long value)
        {
            CodecUtil.int64Put(buffer, offset + 12, value, java.nio.ByteOrder.LITTLE_ENDIAN);
            return this;
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
        public ActiveAuctionsEncoder highBid(final long value)
        {
            CodecUtil.int64Put(buffer, offset + 20, value, java.nio.ByteOrder.LITTLE_ENDIAN);
            return this;
        }

        public static int quantityLeftNullValue()
        {
            return -2147483648;
        }

        public static int quantityLeftMinValue()
        {
            return -2147483647;
        }

        public static int quantityLeftMaxValue()
        {
            return 2147483647;
        }
        public ActiveAuctionsEncoder quantityLeft(final int value)
        {
            CodecUtil.int32Put(buffer, offset + 28, value, java.nio.ByteOrder.LITTLE_ENDIAN);
            return this;
        }
    }
}
