/*
 * Copyright 2015 Real Logic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package amaethon;

import uk.co.real_logic.agrona.collections.Long2ObjectHashMap;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * Encapsulation of the Auction House. Holds pre-allocated {@link Auction} instances, {@link Bidder} instances,
 * and callbacks for events.
 */
public class AuctionHouse implements Model
{
    private static final int INITIAL_NUMBER_OF_AUCTION_SLOTS = 16;

    private final Consumer<Auction> onAdvanceTimeFunc = this::onAdvanceTime;
    private final ArrayList<Auction> auctions = new ArrayList<>();
    private final Long2ObjectHashMap<Bidder> bidders = new Long2ObjectHashMap<>();

    private final Consumer<Auction> onNewAuctionFunc;
    private final Consumer<Auction> onNewHighBidFunc;
    private final Consumer<Auction> onAuctionOverFunc;

    private long currentTimeInNanos;

    private int activeAuctions = 0;

    public AuctionHouse(
        final Consumer<Auction> onNewAuction,
        final Consumer<Auction> onNewHighBid,
        final Consumer<Auction> onAuctionOver)
    {
        for (int i = INITIAL_NUMBER_OF_AUCTION_SLOTS - 1; i >= 0; i--)
        {
            auctions.add(new Auction());
        }

        onNewAuctionFunc = onNewAuction;
        onNewHighBidFunc = onNewHighBid;
        onAuctionOverFunc = onAuctionOver;
    }

    public long currentTimeInNanos()
    {
        return this.currentTimeInNanos;
    }

    public int activeAuctions()
    {
        return activeAuctions;
    }

    public void advanceTime(final long now)
    {
        currentTimeInNanos = now;
        forEach(onAdvanceTimeFunc);
    }

    // TODO: use for mocking and testing mostly
    public Auction auction(final int id)
    {
        return auctions.get(id);
    }

    public int add(final byte[] name, final int nameLength, final long expiration, final long reserveValue)
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

        auctions.get(id).reset(id, name, nameLength, expiration, reserveValue);
        onNewAuctionFunc.accept(auctions.get(id));
        activeAuctions++;

        return id;
    }

    public void cancel(int auctionId)
    {
        final Auction auction = auctions.get(auctionId);

        if (null != auction)
        {
            activeAuctions--;
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

            if (result)
            {
                onNewHighBidFunc.accept(auction);
            }
        }

        return result;
    }

    public void forEach(Consumer<Auction> consumer)
    {
        for (int i = auctions.size() - 1; i >= 0; i--)
        {
            final Auction auction = auctions.get(i);

            if (auction.isActive())
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
        if (auction.onAdvanceTime(currentTimeInNanos) > 0)
        {
            activeAuctions--;
            onAuctionOverFunc.accept(auction);
        }
    }
}
