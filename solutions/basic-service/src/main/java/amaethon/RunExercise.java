package amaethon;

import uk.co.real_logic.aeron.driver.MediaDriver;

/**
 * Runner for the exercise that has a contained Media Driver, AuctionService and AutomatedClient
 */
public class RunExercise
{
    public static final String SUBMISSION_CHANNEL = "aeron:udp?remote=localhost:43456";
    public static final int SUBMISSION_STREAM_ID = 1;

    public static void main(final String[] args) throws Exception
    {
        final MediaDriver.Context ctx = new MediaDriver.Context();
        final MediaDriver mediaDriver = MediaDriver.launch(ctx.dirsDeleteOnExit(true));
        final AuctionService service = new AuctionService(SUBMISSION_CHANNEL, SUBMISSION_STREAM_ID);
        final Thread serviceThread = new Thread(service);

        serviceThread.setName("AuctionService");
        serviceThread.start();

        try (final AutomatedClient client = new AutomatedClient(SUBMISSION_CHANNEL, SUBMISSION_STREAM_ID))
        {
            final AuctionHouse house = service.house();

            house.addBidder("tmontgomery".getBytes(), 1, 0);
            house.addBidder("mjpt777".getBytes(), 2, 0);
            house.addBidder("RichardWarburton".getBytes(), 3, 0);

            // TODO: add some auctions and do some bids
            client.auction("Star Wars Force Awakens Pre-Pre-Release", 1000000000, "00:00:10");
            client.bid(0, 1, 1000000000 + 1);
            client.auction("Issue #1000", 0, "00:00:05");
            client.bid(1, 2, 1);
            client.bid(1, 3, 1);
            client.bid(1, 1, 2);

            // TODO: verify some state in the house
            // TODO: wait and let some auctions complete
            // TODO: verify some state in the house

            Thread.sleep(11000);
        }

        service.shutdown();
        serviceThread.join();
        service.close();
        mediaDriver.close();
    }
}
