package amaethon;

import java.util.function.Consumer;

public interface Model
{
    // add an auction
    public int add(final byte[] name, final int nameLength, final long expiration, final long reserveValue);
    // exercise 3: add a new auction type
    //public long add(final byte[] name, final long expiration, final long price, final long quantity);

    // cancel an auction. Stays active (but cancelled) until time of auction end
    public void cancel(final int auctionId);

    // bid on an auction. True if high bid, False if not
    public boolean bid(final int auctionId, final long bidderId, final long value);
    // exercise 3: bid on fixed price auction. True if accepted, False if not
    //public boolean bid(final long auctionId, final long bidderId);

    // iterator over all non-inactive auctions (for activity feed and for advancing time on auctions)
    public void forEach(final Consumer<Auction> consumer);

    // done to add a new bidding account to the system
    public void addBidder(final byte[] name, final long bidderId, final long budget);
}