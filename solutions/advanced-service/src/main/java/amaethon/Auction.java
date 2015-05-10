package amaethon;

import java.util.concurrent.TimeUnit;

public class Auction
{
    private static final long LINGER_TIMEOUT_NANOS = TimeUnit.SECONDS.toNanos(10);

    /*
     * INACTIVE means Auction not valid
     * ACTIVE means Auction is accepting bids
     * OVER means Auction is over (Auction will linger for a period of time before being re-used)
     */
    enum State
    {
        INACTIVE, ACTIVE, OVER;
    }

    enum Type
    {
        BASIC, FIXED_PRICE;
    }

    private byte[] nameInBytes = new byte[1024];
    private int nameLength;
    private int id;
    private int quantity;
    private long expiration;
    private long currentHighBid;
    private long currentHighBidder;
    private State state;
    private Type type;

    public Auction()
    {
        this.state = State.INACTIVE;
    }

    public void reset(
        final int id, final byte[] name, final int nameLength, final long expiration, final long reserveValue)
    {
        this.id = id;
        System.arraycopy(name, 0, this.nameInBytes, 0, name.length);
        this.nameLength = nameLength;
        this.expiration = expiration;
        this.currentHighBid = reserveValue;
        this.currentHighBidder = Bidder.INVALID_BIDDER;
        this.state = State.ACTIVE;
        this.type = Type.BASIC;
        this.quantity = 1;
    }

    public void reset(
        final int id, final byte[] name, final int nameLength, final long expiration, final long price, final int quantity)
    {
        this.id = id;
        System.arraycopy(name, 0, this.nameInBytes, 0, name.length);
        this.nameLength = nameLength;
        this.expiration = expiration;
        this.currentHighBid = price;
        this.currentHighBidder = Bidder.INVALID_BIDDER;
        this.state = State.ACTIVE;
        this.type = Type.FIXED_PRICE;
        this.quantity = quantity;
    }

    public boolean isInactive()
    {
        return (State.INACTIVE == state);
    }

    public boolean isActive()
    {
        return (State.ACTIVE == state);
    }

    public int id()
    {
        return id;
    }

    public String name()
    {
        return new String(nameInBytes, 0, nameLength);
    }

    public byte[] nameInBytes()
    {
        return nameInBytes;
    }

    public int nameLength()
    {
        return nameLength;
    }

    public long expiration()
    {
        return expiration;
    }

    public long highBid()
    {
        return currentHighBid;
    }

    public long highBidder()
    {
        return currentHighBidder;
    }

    public boolean isFixedPrice()
    {
        return (Type.FIXED_PRICE == type);
    }

    public int quantity()
    {
        return quantity;
    }

    // return 0 for nothing new or >0 for activity
    public int onAdvanceTime(final long now)
    {
        int result = 0;

        if (State.ACTIVE == state && now > expiration)
        {
            state = State.OVER;
            result = 1;
        }
        else if (State.OVER == state && now > (expiration + LINGER_TIMEOUT_NANOS))
        {
            state = State.INACTIVE;
        }
        else if (Type.FIXED_PRICE == type && 0 == quantity)
        {
            state = State.OVER;
            result = 1;
        }

        return result;
    }

    // True if high bid or successful fixed price bid. False if not.
    public boolean bid(final long bidder, final long value)
    {
        boolean result = false;

        if (Type.BASIC == type && State.ACTIVE == state && value > currentHighBid)
        {
            currentHighBid = value;
            currentHighBidder = bidder;
            result = true;
        }
        else if (Type.FIXED_PRICE == type && State.ACTIVE == state && currentHighBid == value && 0 < quantity)
        {
            quantity--;
            currentHighBidder = bidder;
            result = true;
        }

        return result;
    }

    public void cancel()
    {
        this.currentHighBidder = Bidder.INVALID_BIDDER;
        this.state = State.OVER;
    }
}
