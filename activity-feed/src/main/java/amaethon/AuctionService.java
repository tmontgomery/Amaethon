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

import amaethon.generated.*;
import uk.co.real_logic.aeron.Aeron;
import uk.co.real_logic.aeron.Publication;
import uk.co.real_logic.aeron.Subscription;
import uk.co.real_logic.aeron.common.concurrent.logbuffer.DataHandler;
import uk.co.real_logic.aeron.common.concurrent.logbuffer.Header;
import uk.co.real_logic.agrona.CloseHelper;
import uk.co.real_logic.agrona.DirectBuffer;
import uk.co.real_logic.agrona.concurrent.BackoffIdleStrategy;
import uk.co.real_logic.agrona.concurrent.IdleStrategy;
import uk.co.real_logic.agrona.concurrent.UnsafeBuffer;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

public class AuctionService implements Runnable, AutoCloseable, DataHandler
{
    private static final long IDLE_MAX_SPINS = 0;
    private static final long IDLE_MAX_YIELDS = 0;
    private static final long IDLE_MIN_PARK_NS = TimeUnit.NANOSECONDS.toNanos(1);
    private static final long IDLE_MAX_PARK_NS = TimeUnit.MILLISECONDS.toNanos(1);
    private static final long AUCTION_LIST_INTERVAL = TimeUnit.SECONDS.toNanos(1);
    private static final int MESSAGE_TEMPLATE_VERSION = 0;
    private static final int ACTIVITY_FEED_BUFFER_LENGTH = 1024;

    private final MessageHeaderDecoder messageHeaderDecoder = new MessageHeaderDecoder();
    private final MessageHeaderEncoder messageHeaderEncoder = new MessageHeaderEncoder();
    private final AuctionDecoder auctionDecoder = new AuctionDecoder();
    private final BidDecoder bidDecoder = new BidDecoder();
    private final NewAuctionEncoder newAuctionEncoder = new NewAuctionEncoder();
    private final NewHighBidEncoder newHighBidEncoder = new NewHighBidEncoder();
    private final AuctionOverEncoder auctionOverEncoder = new AuctionOverEncoder();
    private final AuctionListEncoder auctionListEncoder = new AuctionListEncoder();
    private final UnsafeBuffer activityFeedBuffer =
        new UnsafeBuffer(ByteBuffer.allocateDirect(ACTIVITY_FEED_BUFFER_LENGTH));
    private final byte[] tmpByteArray = new byte[1024];
    private final IdleStrategy idleStrategy =
        new BackoffIdleStrategy(IDLE_MAX_SPINS, IDLE_MAX_YIELDS, IDLE_MIN_PARK_NS, IDLE_MAX_PARK_NS);

    private final AuctionHouse house;
    private final Aeron aeron;
    private final Subscription submissionSubscription;
    private Publication activityFeedPublication;

    private long timeOfLastAuctionList;

    private volatile boolean running = true;

    public AuctionService(
        final String submissionChannel,
        final int submissionStreamId,
        final String activityFeedChannel,
        final int activityFeedStreamId)
    {
        house = new AuctionHouse(this::onNewAuction, this::onNewHighBid, this::onAuctionOver);

        aeron = Aeron.connect(new Aeron.Context());
        submissionSubscription = aeron.addSubscription(submissionChannel, submissionStreamId, this::onData);
        // TODO:
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
        CloseHelper.quietClose(submissionSubscription);
        CloseHelper.quietClose(activityFeedPublication);
        CloseHelper.quietClose(aeron);
    }

    public void run()
    {
        while (running)
        {
            final int fragmentsRead = submissionSubscription.poll(Integer.MAX_VALUE);
            final long now = System.nanoTime();

            house.advanceTime(now);

            // check time of last AuctionList and send if needed
            if (now > (timeOfLastAuctionList + AUCTION_LIST_INTERVAL))
            {
                sendAuctionList();
                timeOfLastAuctionList = now;
            }

            idleStrategy.idle(fragmentsRead);
        }
    }

    public void onData(DirectBuffer buffer, int offset, int length, Header header)
    {
        messageHeaderDecoder.wrap(buffer, offset, MESSAGE_TEMPLATE_VERSION);

        if (AuctionDecoder.TEMPLATE_ID == messageHeaderDecoder.templateId())
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
        else if (BidDecoder.TEMPLATE_ID == messageHeaderDecoder.templateId())
        {
            bidDecoder.wrap(
                buffer,
                offset + messageHeaderDecoder.size(),
                messageHeaderDecoder.blockLength(),
                MESSAGE_TEMPLATE_VERSION);

            house.bid(bidDecoder.auctionId(), bidDecoder.bidderId(), bidDecoder.value());
        }
    }

    private void onNewAuction(final Auction auction)
    {
        // TODO:
    }

    private void onNewHighBid(final Auction auction)
    {
        // TODO:
    }

    private void onAuctionOver(final Auction auction)
    {
        // TODO:
    }

    private void sendAuctionList()
    {
        // TODO:
    }
}
