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

import amaethon.generated.AuctionEncoder;
import amaethon.generated.BidEncoder;
import amaethon.generated.MessageHeaderEncoder;
import uk.co.real_logic.aeron.Aeron;
import uk.co.real_logic.aeron.Publication;
import uk.co.real_logic.agrona.concurrent.UnsafeBuffer;

import java.nio.ByteBuffer;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * Auction client to send auction commands.
 *
 * NOTE: not thread safe.
 */
public class AutomatedClient implements AutoCloseable
{
    public static final int MAX_BUFFER_LENGTH = 1024;
    public static final int MESSAGE_TEMPLATE_VERSION = 0;

    private final MessageHeaderEncoder messageHeaderEncoder = new MessageHeaderEncoder();
    private final AuctionEncoder auctionEncoder = new AuctionEncoder();
    private final BidEncoder bidEncoder = new BidEncoder();
    private final UnsafeBuffer buffer = new UnsafeBuffer(ByteBuffer.allocateDirect(MAX_BUFFER_LENGTH));

    private final Aeron aeron;
    private final Publication publication;

    public AutomatedClient(final String channel, final int streamId)
    {
        aeron = Aeron.connect(new Aeron.Context());
        publication = aeron.addPublication(channel, streamId);
    }

    public void close()
    {
        if (null != publication)
        {
            publication.close();
        }

        if (null != aeron)
        {
            aeron.close();
        }
    }

    public void auction(final String name, final long reserve, final String duration)
    {
        final LocalTime time = LocalTime.parse(duration, DateTimeFormatter.ISO_LOCAL_TIME);
        final long durationInNanos =
            TimeUnit.HOURS.toNanos(time.getHour()) +
            TimeUnit.MINUTES.toNanos(time.getMinute()) +
            TimeUnit.SECONDS.toNanos(time.getSecond()) +
            TimeUnit.NANOSECONDS.toNanos(time.getNano());

        messageHeaderEncoder.wrap(buffer, 0, MESSAGE_TEMPLATE_VERSION);
        auctionEncoder.wrap(buffer, messageHeaderEncoder.size());

        messageHeaderEncoder
            .blockLength(AuctionEncoder.BLOCK_LENGTH)
            .templateId(AuctionEncoder.TEMPLATE_ID)
            .schemaId(AuctionEncoder.SCHEMA_ID)
            .version(AuctionEncoder.SCHEMA_VERSION);

        auctionEncoder
            .durationInNanos(durationInNanos)
            .reserve(reserve)
            .name(name);

        final int length = messageHeaderEncoder.size() + auctionEncoder.size();

        while (publication.offer(buffer, 0, length) < 0)
        {
            // TODO: backoff?
        }

        System.out.format(
            "auction encode: name=%s, reserve=%d, duration=%d [length=%d bytes]\n", name, reserve, durationInNanos, length);
    }

    public void bid(final int auctionId, final long bidderId, final long value)
    {
        messageHeaderEncoder.wrap(buffer, 0, MESSAGE_TEMPLATE_VERSION);
        bidEncoder.wrap(buffer, messageHeaderEncoder.size());

        messageHeaderEncoder
            .blockLength(BidEncoder.BLOCK_LENGTH)
            .templateId(BidEncoder.TEMPLATE_ID)
            .schemaId(BidEncoder.SCHEMA_ID)
            .version(BidEncoder.SCHEMA_VERSION);

        bidEncoder
            .auctionId(auctionId)
            .bidderId(bidderId)
            .value(value);

        final int length = messageHeaderEncoder.size() + bidEncoder.size();

        while (publication.offer(buffer, 0, length) < 0)
        {
            // TODO: backoff?
        }

        System.out.format(
            "bid encode: auctionId=%d, bidderId=%d, value=%d [length=%d bytes]\n", auctionId, bidderId, value, length);
    }
}
