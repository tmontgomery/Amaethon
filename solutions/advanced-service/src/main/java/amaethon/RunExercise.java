package amaethon;

import uk.co.real_logic.aeron.driver.MediaDriver;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * Runner for the exercise that has a contained Media Driver, AuctionService and AutomatedClient
 */
public class RunExercise
{
    public static final String SUBMISSION_CHANNEL = "aeron:udp?remote=localhost:43456";
    public static final int SUBMISSION_STREAM_ID = 1;
    public static final String ACTIVITY_FEED_CHANNEL = "aeron:udp?remote=localhost:43457"; // could be multicast
    public static final int ACTIVITY_FEED_STREAM_ID = 1;

    public static void main(final String[] args) throws Exception
    {
        final MediaDriver.Context ctx = new MediaDriver.Context();
        final MediaDriver mediaDriver = MediaDriver.launch(ctx.dirsDeleteOnExit(true));
        final AuctionService service =
            new AuctionService(SUBMISSION_CHANNEL, SUBMISSION_STREAM_ID, ACTIVITY_FEED_CHANNEL, ACTIVITY_FEED_STREAM_ID);
        final Thread serviceThread = new Thread(service);
        final AuctionHouse house = service.house();

        serviceThread.setName("AuctionService");
        serviceThread.start();

        try (final AutomatedClient client =
                 new AutomatedClient(SUBMISSION_CHANNEL, SUBMISSION_STREAM_ID, ACTIVITY_FEED_CHANNEL, ACTIVITY_FEED_STREAM_ID))
        {
            house.addBidder("tmontgomery".getBytes(), 1, 0);
            house.addBidder("mjpt777".getBytes(), 2, 0);
            house.addBidder("RichardWarburton".getBytes(), 3, 0);

            client.auction("Star Wars Force Awakens Pre-Pre-Release", 1000000000, "00:00:10");
            client.bid(0, 1, 1000000000 + 1);
            client.auction("Issue #1000", 0, "00:00:05");
            client.auction("Fox Force Member", 10000, 5, "00:00:07");
            client.bid(1, 2, 1);
            client.bid(1, 3, 1);
            client.bid(2, 2, 10000);
            client.bid(1, 1, 2);
            client.bid(2, 2, 10000);
            client.bid(2, 3, 10000);
            client.bid(2, 3, 10000);
            client.bid(2, 1, 10000);

            final long now = System.nanoTime();
            final long end = now + TimeUnit.SECONDS.toNanos(15);

            while (System.nanoTime() < end)
            {
                client.pollActivityFeed();
                LockSupport.parkNanos(TimeUnit.MICROSECONDS.toNanos(50));
            }
        }

        service.shutdown();
        serviceThread.join();
        service.close();
        mediaDriver.close();
    }
}
