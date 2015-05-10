package amaethon;

import amaethon.generated.*;
import uk.co.real_logic.aeron.Aeron;
import uk.co.real_logic.aeron.Publication;
import uk.co.real_logic.aeron.Subscription;
import uk.co.real_logic.aeron.common.concurrent.logbuffer.DataHandler;
import uk.co.real_logic.aeron.common.concurrent.logbuffer.Header;
import uk.co.real_logic.agrona.DirectBuffer;
import uk.co.real_logic.agrona.concurrent.UnsafeBuffer;

import java.nio.ByteBuffer;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * NOTE: not thread safe.
 */
public class AutomatedClient implements AutoCloseable, DataHandler
{
    public static final int MAX_BUFFER_LENGTH = 1024;
    public static final int MESSAGE_TEMPLATE_VERSION = 0;

    private final MessageHeaderEncoder messageHeaderEncoder = new MessageHeaderEncoder();
    private final MessageHeaderDecoder messageHeaderDecoder = new MessageHeaderDecoder();
    private final AuctionEncoder auctionEncoder = new AuctionEncoder();
    private final FixedPriceAuctionEncoder fixedPriceAuctionEncoder = new FixedPriceAuctionEncoder();
    private final BidEncoder bidEncoder = new BidEncoder();
    private final NewAuctionDecoder newAuctionDecoder = new NewAuctionDecoder();
    private final NewHighBidDecoder newHighBidDecoder = new NewHighBidDecoder();
    private final FixedPriceAuctionUpdateDecoder fixedPriceAuctionUpdateDecoder = new FixedPriceAuctionUpdateDecoder();
    private final AuctionOverDecoder auctionOverDecoder = new AuctionOverDecoder();
    private final AuctionListDecoder auctionListDecoder = new AuctionListDecoder();
    private final UnsafeBuffer buffer = new UnsafeBuffer(ByteBuffer.allocateDirect(MAX_BUFFER_LENGTH));

    private final Aeron aeron;
    private final Publication submissionPublication;
    private final Subscription activityFeedSubscription;

    public AutomatedClient(
        final String submissionChannel,
        final int submissionStreamId,
        final String activityFeedChannel,
        final int activityFeedStreamId)
    {
        aeron = Aeron.connect(new Aeron.Context());
        submissionPublication = aeron.addPublication(submissionChannel, submissionStreamId);
        activityFeedSubscription = aeron.addSubscription(activityFeedChannel, activityFeedStreamId, this::onData);
    }

    public void close()
    {
        if (null != submissionPublication)
        {
            submissionPublication.close();
        }

        if (null != activityFeedSubscription)
        {
            activityFeedSubscription.close();
        }

        if (null != aeron)
        {
            aeron.close();
        }
    }

    public static long parseTimeToNanos(final String duration)
    {
        final LocalTime time = LocalTime.parse(duration, DateTimeFormatter.ISO_LOCAL_TIME);

        return TimeUnit.HOURS.toNanos(time.getHour()) +
            TimeUnit.MINUTES.toNanos(time.getMinute()) +
            TimeUnit.SECONDS.toNanos(time.getSecond()) +
            TimeUnit.NANOSECONDS.toNanos(time.getNano());
    }

    public void auction(final String name, final long reserve, final String duration)
    {
        final long durationInNanos = parseTimeToNanos(duration);

        messageHeaderEncoder.wrap(buffer, 0, MESSAGE_TEMPLATE_VERSION);
        auctionEncoder.wrap(buffer, messageHeaderEncoder.size());

        messageHeaderEncoder
            .blockLength(AuctionEncoder.BLOCK_LENGTH)
            .templateId(AuctionEncoder.TEMPLATE_ID)
            .schemaId(AuctionEncoder.SCHEMA_ID)
            .version(AuctionEncoder.SCHEMA_VERSION);

        auctionEncoder
            .durationInNanos(durationInNanos)
            .reserve(reserve)
            .name(name);

        final int length = messageHeaderEncoder.size() + auctionEncoder.size();

        while (submissionPublication.offer(buffer, 0, length) < 0)
        {
            // TODO: backoff?
        }

        System.out.format(
            "auction encode: name=%s, reserve=%d, duration=%d [length=%d bytes]\n", name, reserve, durationInNanos, length);
    }

    public void auction(final String name, final long price, final int quantity, final String duration)
    {
        final long durationInNanos = parseTimeToNanos(duration);

        messageHeaderEncoder.wrap(buffer, 0, MESSAGE_TEMPLATE_VERSION);
        fixedPriceAuctionEncoder.wrap(buffer, messageHeaderEncoder.size());

        messageHeaderEncoder
            .blockLength(FixedPriceAuctionEncoder.BLOCK_LENGTH)
            .templateId(FixedPriceAuctionEncoder.TEMPLATE_ID)
            .schemaId(FixedPriceAuctionEncoder.SCHEMA_ID)
            .version(FixedPriceAuctionEncoder.SCHEMA_VERSION);

        fixedPriceAuctionEncoder
            .durationInNanos(durationInNanos)
            .price(price)
            .quantity(quantity)
            .name(name);

        final int length = messageHeaderEncoder.size() + fixedPriceAuctionEncoder.size();

        while (submissionPublication.offer(buffer, 0, length) < 0)
        {
            // TODO: backoff?
        }

        System.out.format(
            "fp auction encode: name=%s, price=%d, quantity=%d, duration=%d [length=%d bytes]\n",
            name, price, quantity, durationInNanos, length);
    }

    public void bid(final int auctionId, final long bidderId, final long value)
    {
        messageHeaderEncoder.wrap(buffer, 0, MESSAGE_TEMPLATE_VERSION);
        bidEncoder.wrap(buffer, messageHeaderEncoder.size());

        messageHeaderEncoder
            .blockLength(BidEncoder.BLOCK_LENGTH)
            .templateId(BidEncoder.TEMPLATE_ID)
            .schemaId(BidEncoder.SCHEMA_ID)
            .version(BidEncoder.SCHEMA_VERSION);

        bidEncoder
            .auctionId(auctionId)
            .bidderId(bidderId)
            .value(value);

        final int length = messageHeaderEncoder.size() + bidEncoder.size();

        while (submissionPublication.offer(buffer, 0, length) < 0)
        {
            // TODO: backoff?
        }

        System.out.format(
            "bid encode: auctionId=%d, bidderId=%d, value=%d [length=%d bytes]\n", auctionId, bidderId, value, length);
    }

    public int pollActivityFeed()
    {
        return activityFeedSubscription.poll(1);
    }

    public void onData(final DirectBuffer buffer, final int offset, final int length, final Header header)
    {
        messageHeaderDecoder.wrap(buffer, offset, MESSAGE_TEMPLATE_VERSION);

        switch (messageHeaderDecoder.templateId())
        {
            case NewAuctionDecoder.TEMPLATE_ID:

                newAuctionDecoder.wrap(
                    buffer,
                    offset + messageHeaderDecoder.size(),
                    messageHeaderDecoder.blockLength(),
                    MESSAGE_TEMPLATE_VERSION);

                System.out.format(
                    "new auction decode: auctionId=%d, type=%d, duration=%d, reserveOrPrice=%d, quantity=%d, name=%s\n",
                    newAuctionDecoder.auctionId(),
                    newAuctionDecoder.auctionType().value(),
                    newAuctionDecoder.duration(),
                    newAuctionDecoder.reserveOrPrice(),
                    newAuctionDecoder.quantity(),
                    newAuctionDecoder.name());
                break;

            case NewHighBidDecoder.TEMPLATE_ID:

                newHighBidDecoder.wrap(
                    buffer,
                    offset + messageHeaderDecoder.size(),
                    messageHeaderDecoder.blockLength(),
                    MESSAGE_TEMPLATE_VERSION);

                System.out.format(
                    "new high bid decode: auctionId=%d, highBidderId=%d, highBid=%d, durationLeft=%d\n",
                    newHighBidDecoder.auctionId(),
                    newHighBidDecoder.highBidderId(),
                    newHighBidDecoder.highBid(),
                    newHighBidDecoder.durationLeft());
                break;

            case FixedPriceAuctionUpdateDecoder.TEMPLATE_ID:

                fixedPriceAuctionUpdateDecoder.wrap(
                    buffer,
                    offset + messageHeaderDecoder.size(),
                    messageHeaderDecoder.blockLength(),
                    MESSAGE_TEMPLATE_VERSION);

                System.out.format(
                    "fp auction update decode: auctionId=%d, bidderId=%d, durationLeft=%d, quantityLeft=%d\n",
                    fixedPriceAuctionUpdateDecoder.auctionId(),
                    fixedPriceAuctionUpdateDecoder.bidderId(),
                    fixedPriceAuctionUpdateDecoder.durationLeft(),
                    fixedPriceAuctionUpdateDecoder.quantityLeft());

                break;

            case AuctionOverDecoder.TEMPLATE_ID:

                auctionOverDecoder.wrap(
                    buffer,
                    offset + messageHeaderDecoder.size(),
                    messageHeaderDecoder.blockLength(),
                    MESSAGE_TEMPLATE_VERSION);

                System.out.format(
                    "auction over decode: auctionId=%d, winningBidderId=%d, winningBid=%d, quantityLeft=%d\n",
                    auctionOverDecoder.auctionId(),
                    auctionOverDecoder.winningBidderId(),
                    auctionOverDecoder.winningBid(),
                    auctionOverDecoder.quantityLeft());
                break;

            case AuctionListDecoder.TEMPLATE_ID:

                auctionListDecoder.wrap(
                    buffer,
                    offset + messageHeaderDecoder.size(),
                    messageHeaderDecoder.blockLength(),
                    MESSAGE_TEMPLATE_VERSION);

                final AuctionListDecoder.ActiveAuctionsDecoder activeAuctions = auctionListDecoder.activeAuctions();

                System.out.format("auction list decode: count=%d\n", activeAuctions.count());
                for (final AuctionListDecoder.ActiveAuctionsDecoder activeAuctionsDecoder : activeAuctions)
                {
                    System.out.format(
                        "    auctionId=%d, durationLeft=%d, highBidderId=%d, highBid=%d, quantityLeft=%d\n",
                        activeAuctionsDecoder.auctionId(),
                        activeAuctionsDecoder.durationLeft(),
                        activeAuctionsDecoder.highBidderId(),
                        activeAuctionsDecoder.highBid(),
                        activeAuctionsDecoder.quantityLeft());
                }
                break;
        }
    }
}
