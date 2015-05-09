package amaethon;

import java.util.Arrays;

public class Bidder
{
    public static final long INVALID_BIDDER = -1;

    private final byte[] name;
    private final long id;
    private long budget;

    public Bidder(final byte[] name, final long bidderId, final long budget)
    {
        this.name = Arrays.copyOf(name, name.length);
        this.id = bidderId;
        this.budget = budget;
    }

    public byte[] name()
    {
        return name;
    }

    public long id()
    {
        return id;
    }

    public long budget()
    {
        return budget;
    }

    public void decrementBudget(final long price)
    {
        budget -= price;
    }
}
