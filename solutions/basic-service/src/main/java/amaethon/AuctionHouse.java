package amaethon;

import uk.co.real_logic.agrona.collections.Long2ObjectHashMap;

import java.util.ArrayList;
import java.util.function.Consumer;

public class AuctionHouse implements Model
{
    private static final int INITIAL_NUMBER_OF_AUCTION_SLOTS = 16;

    private final Consumer<Auction> onAdvanceTimeFunc = this::onAdvanceTime;
    private final ArrayList<Auction> auctions = new ArrayList<>();
    private final Long2ObjectHashMap<Bidder> bidders = new Long2ObjectHashMap<>();

    private long currentTimeInNanos;

    public AuctionHouse()
    {
        for (int i = INITIAL_NUMBER_OF_AUCTION_SLOTS - 1; i >= 0; i--)
        {
            auctions.add(new Auction());
        }
    }

    public long currentTimeInNanos()
    {
        return this.currentTimeInNanos;
    }

    public void advanceTime(final long now)
    {
        currentTimeInNanos = now;
        forEach(onAdvanceTimeFunc);
    }

    public int add(byte[] name, long expiration, long reserveValue)
    {
        int id = -1;

        for (int i = auctions.size() - 1; i >= 0; i--)
        {
            final Auction auction = auctions.get(i);

            if (auction.isInactive())
            {
                id = i;
            }
        }

        if (-1 == id)
        {
            id = auctions.size();
            auctions.add(id, new Auction());
        }

        auctions.get(id).reset(name, expiration, reserveValue);

        return id;
    }

    public void cancel(int auctionId)
    {
        final Auction auction = auctions.get(auctionId);

        if (null != auction)
        {
            auction.cancel();
        }
    }

    public boolean bid(int auctionId, long bidderId, long value)
    {
        boolean result = false;
        final Auction auction = auctions.get(auctionId);

        if (null != auction)
        {
            result = auction.bid(bidderId, value);
        }

        return result;
    }

    public void forEach(Consumer<Auction> consumer)
    {
        for (int i = auctions.size() - 1; i >= 0; i--)
        {
            final Auction auction = auctions.get(i);

            if (!auction.isInactive())
            {
                consumer.accept(auction);
            }
        }
    }

    public void addBidder(byte[] name, long bidderId, long budget)
    {
        if (null != bidders.get(bidderId))
        {
            throw new IllegalArgumentException("Bidder ID already taken: id=" + bidderId);
        }

        bidders.put(bidderId, new Bidder(name, bidderId, budget));
    }

    private void onAdvanceTime(final Auction auction)
    {
        // TODO: check return value to see if an auction ended
        auction.onAdvanceTime(currentTimeInNanos);
    }
}
