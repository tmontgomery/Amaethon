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
import uk.co.real_logic.aeron.logbuffer.FragmentHandler;
import uk.co.real_logic.aeron.logbuffer.Header;
import uk.co.real_logic.agrona.CloseHelper;
import uk.co.real_logic.agrona.DirectBuffer;
import uk.co.real_logic.agrona.concurrent.BackoffIdleStrategy;
import uk.co.real_logic.agrona.concurrent.IdleStrategy;
import uk.co.real_logic.agrona.concurrent.UnsafeBuffer;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

public class AuctionService implements Runnable, AutoCloseable, FragmentHandler
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
    private final FixedPriceAuctionDecoder fixedPriceAuctionDecoder = new FixedPriceAuctionDecoder();
    private final NewAuctionEncoder newAuctionEncoder = new NewAuctionEncoder();
    private final NewHighBidEncoder newHighBidEncoder = new NewHighBidEncoder();
    private final FixedPriceAuctionUpdateEncoder fixedPriceAuctionUpdateEncoder = new FixedPriceAuctionUpdateEncoder();
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
    private final Publication activityFeedPublication;

    private long timeOfLastAuctionList;

    private volatile boolean running = true;

    public AuctionService(
        final String submissionChannel,
        final int submissionStreamId,
        final String activityFeedChannel,
        final int activityFeedStreamId)
    {
        house = new AuctionHouse(this::onNewAuction, this::onNewHighBid, this::onFixedPriceUpdate, this::onAuctionOver);

        aeron = Aeron.connect(new Aeron.Context());
        submissionSubscription = aeron.addSubscription(submissionChannel, submissionStreamId);
        // TODO: for exercise, add Publication
        activityFeedPublication = aeron.addPublication(activityFeedChannel, activityFeedStreamId);
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
            final int fragmentsRead = submissionSubscription.poll(this, Integer.MAX_VALUE);
            final long now = System.nanoTime();

            house.advanceTime(now);

            // TODO: check time of last AuctionList and send if needed
            if (now > (timeOfLastAuctionList + AUCTION_LIST_INTERVAL))
            {
                sendAuctionList();
                timeOfLastAuctionList = now;
            }

            idleStrategy.idle(fragmentsRead);
        }
    }

    public void onFragment(DirectBuffer buffer, int offset, int length, Header header)
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
        else if (FixedPriceAuctionDecoder.TEMPLATE_ID == messageHeaderDecoder.templateId())
        {
            // TODO: for exercise, decode fp auction
            fixedPriceAuctionDecoder.wrap(
                buffer,
                offset + messageHeaderDecoder.size(),
                messageHeaderDecoder.blockLength(),
                MESSAGE_TEMPLATE_VERSION);

            final long now = System.nanoTime();
            final int nameLength = fixedPriceAuctionDecoder.getName(tmpByteArray, 0, tmpByteArray.length);

            house.add(
                tmpByteArray,
                nameLength,
                now + fixedPriceAuctionDecoder.durationInNanos(),
                fixedPriceAuctionDecoder.price(),
                fixedPriceAuctionDecoder.quantity());
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
        // TODO: for exercise, send new auction event
        messageHeaderEncoder.wrap(activityFeedBuffer, 0, MESSAGE_TEMPLATE_VERSION);

        messageHeaderEncoder
            .blockLength(NewAuctionEncoder.BLOCK_LENGTH)
            .schemaId(NewAuctionEncoder.SCHEMA_ID)
            .templateId(NewAuctionEncoder.TEMPLATE_ID)
            .version(NewAuctionEncoder.SCHEMA_VERSION);

        newAuctionEncoder.wrap(activityFeedBuffer, messageHeaderEncoder.size());

        final AuctionType auctionType = auction.isFixedPrice() ? AuctionType.FixedPrice : AuctionType.Basic;

        newAuctionEncoder
            .auctionId(auction.id())
            .duration(auction.expiration() - System.nanoTime())
            .reserveOrPrice(auction.highBid())
            .quantity(auction.quantity())
            .auctionType(auctionType);

        newAuctionEncoder.putName(auction.nameInBytes(), 0, auction.nameLength());

        final int length = messageHeaderEncoder.size() + newAuctionEncoder.size();

        while (activityFeedPublication.offer(activityFeedBuffer, 0, length) < 0)
        {
            // TODO: idle strategy
        }
    }

    private void onNewHighBid(final Auction auction)
    {
        // TODO: for exercise, send new high bid event
        messageHeaderEncoder.wrap(activityFeedBuffer, 0, MESSAGE_TEMPLATE_VERSION);

        messageHeaderEncoder
            .blockLength(NewHighBidEncoder.BLOCK_LENGTH)
            .schemaId(NewHighBidEncoder.SCHEMA_ID)
            .templateId(NewHighBidEncoder.TEMPLATE_ID)
            .version(NewHighBidEncoder.SCHEMA_VERSION);

        newHighBidEncoder.wrap(activityFeedBuffer, messageHeaderEncoder.size());

        newHighBidEncoder
            .auctionId(auction.id())
            .highBidderId(auction.highBidder())
            .highBid(auction.highBid())
            .durationLeft(auction.expiration() - System.nanoTime());

        final int length = messageHeaderEncoder.size() + newHighBidEncoder.size();

        while (activityFeedPublication.offer(activityFeedBuffer, 0, length) < 0)
        {
            // TODO: idle strategy
        }
    }

    private void onFixedPriceUpdate(final Auction auction)
    {
        // TODO: for exercise, send fixed price auction update
        messageHeaderEncoder.wrap(activityFeedBuffer, 0, MESSAGE_TEMPLATE_VERSION);

        messageHeaderEncoder
            .blockLength(FixedPriceAuctionUpdateEncoder.BLOCK_LENGTH)
            .schemaId(FixedPriceAuctionUpdateEncoder.SCHEMA_ID)
            .templateId(FixedPriceAuctionUpdateEncoder.TEMPLATE_ID)
            .version(FixedPriceAuctionUpdateEncoder.SCHEMA_VERSION);

        fixedPriceAuctionUpdateEncoder.wrap(activityFeedBuffer, messageHeaderEncoder.size());

        fixedPriceAuctionUpdateEncoder
            .auctionId(auction.id())
            .bidderId(auction.highBidder())
            .durationLeft(auction.expiration() - System.nanoTime())
            .quantityLeft(auction.quantity());

        final int length = messageHeaderEncoder.size() + fixedPriceAuctionUpdateEncoder.size();

        while (activityFeedPublication.offer(activityFeedBuffer, 0, length) < 0)
        {
            // TODO: idle strategy
        }
    }

    private void onAuctionOver(final Auction auction)
    {
        // TODO: for exercise, send auction over event
        messageHeaderEncoder.wrap(activityFeedBuffer, 0, MESSAGE_TEMPLATE_VERSION);

        messageHeaderEncoder
            .blockLength(AuctionOverEncoder.BLOCK_LENGTH)
            .schemaId(AuctionOverEncoder.SCHEMA_ID)
            .templateId(AuctionOverEncoder.TEMPLATE_ID)
            .version(AuctionOverEncoder.SCHEMA_VERSION);

        auctionOverEncoder.wrap(activityFeedBuffer, messageHeaderEncoder.size());

        auctionOverEncoder
            .auctionId(auction.id())
            .winningBidderId(auction.highBidder())
            .winningBid(auction.highBid())
            .quantityLeft(auction.quantity());

        final int length = messageHeaderEncoder.size() + auctionOverEncoder.size();

        while (activityFeedPublication.offer(activityFeedBuffer, 0, length) < 0)
        {
            // TODO: idle strategy
        }
    }

    private void sendAuctionList()
    {
        // TODO: for exercise, send list of auctions
        messageHeaderEncoder.wrap(activityFeedBuffer, 0, MESSAGE_TEMPLATE_VERSION);

        messageHeaderEncoder
            .blockLength(AuctionListEncoder.BLOCK_LENGTH)
            .schemaId(AuctionListEncoder.SCHEMA_ID)
            .templateId(AuctionListEncoder.TEMPLATE_ID)
            .version(AuctionListEncoder.SCHEMA_VERSION);

        auctionListEncoder.wrap(activityFeedBuffer, messageHeaderEncoder.size());

        final AuctionListEncoder.ActiveAuctionsEncoder activeAuctionsEncoder =
            auctionListEncoder.activeAuctionsCount(house.activeAuctions());

        // TODO: need to make this cleaner and get rid of the allocation of the lambda
        house.forEach(
            (auction) ->
            {
                activeAuctionsEncoder.next()
                    .auctionId(auction.id())
                    .durationLeft(auction.expiration() - System.nanoTime())
                    .highBidderId(auction.highBidder())
                    .highBid(auction.highBid())
                    .quantityLeft(auction.quantity());
            });

        final int length = messageHeaderEncoder.size() + auctionListEncoder.size();

        while (activityFeedPublication.offer(activityFeedBuffer, 0, length) < 0)
        {
            // TODO: idle strategy
        }
    }
}
