package amaethon;

import amaethon.generated.AuctionDecoder;
import amaethon.generated.BidDecoder;
import amaethon.generated.MessageHeaderDecoder;
import uk.co.real_logic.aeron.Aeron;
import uk.co.real_logic.aeron.Subscription;
import uk.co.real_logic.aeron.common.concurrent.logbuffer.DataHandler;
import uk.co.real_logic.aeron.common.concurrent.logbuffer.Header;
import uk.co.real_logic.agrona.DirectBuffer;

public class AuctionService implements Runnable, AutoCloseable, DataHandler
{
    private static final int MESSAGE_TEMPLATE_VERSION = 0;

    private final MessageHeaderDecoder messageHeaderDecoder = new MessageHeaderDecoder();
    private final AuctionDecoder auctionDecoder = new AuctionDecoder();
    private final BidDecoder bidDecoder = new BidDecoder();
    private final byte[] tmpByteArray = new byte[1024];

    private final AuctionHouse house;
    private final Aeron aeron;
    private final Subscription subscription;

    private volatile boolean running = true;

    public AuctionService(final String submissionChannel, final int submissionStreamId)
    {
        house = new AuctionHouse(
            (auction) -> System.out.format("new auction: name=%s\n", auction.name()),
            (auction) -> System.out.format(
                "new high bid: name=%s, bidder=%d, bid=%d\n", auction.name(), auction.highBidder(), auction.highBid()),
            (auction) -> System.out.format(
                "auction won: name=%s, bidder=%d, bid=%d\n", auction.name(), auction.highBidder(), auction.highBid()));

        // TODO: for exercise, add Aeron
        aeron = Aeron.connect(new Aeron.Context());
        // TODO: for exercise, add Subscription
        subscription = aeron.addSubscription(submissionChannel, submissionStreamId, this::onData);
    }

    public AuctionHouse house()
    {
        return house;
    }

    public void shutdown()
    {
        running = false;
    }

    public void close()
    {
        subscription.close();
        aeron.close();
    }

    public void run()
    {
        while (running)
        {
            // TODO: for exercise, subscription polling
            final int fragmentsRead = subscription.poll(Integer.MAX_VALUE);
            final long now = System.nanoTime();

            house.advanceTime(now);

            // TODO: for exercise, add IdleStrategy
        }
    }

    public void onData(DirectBuffer buffer, int offset, int length, Header header)
    {
        // TODO: for exercise, handle data
        messageHeaderDecoder.wrap(buffer, offset, MESSAGE_TEMPLATE_VERSION);

        if (messageHeaderDecoder.templateId() == auctionDecoder.sbeTemplateId())
        {
            auctionDecoder.wrap(
                buffer,
                offset + messageHeaderDecoder.size(),
                messageHeaderDecoder.blockLength(),
                MESSAGE_TEMPLATE_VERSION);

            final long now = System.nanoTime();
            final int nameLength = auctionDecoder.getName(tmpByteArray, 0, tmpByteArray.length);

            house.add(tmpByteArray, nameLength, now + auctionDecoder.durationInNanos(), auctionDecoder.reserve());
        }
        else if (messageHeaderDecoder.templateId() == bidDecoder.sbeTemplateId())
        {
            bidDecoder.wrap(
                buffer,
                offset + messageHeaderDecoder.size(),
                messageHeaderDecoder.blockLength(),
                MESSAGE_TEMPLATE_VERSION);

            house.bid(bidDecoder.auctionId(), bidDecoder.bidderId(), bidDecoder.value());
        }
    }
}
