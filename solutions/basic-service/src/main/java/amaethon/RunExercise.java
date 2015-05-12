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
        final AuctionHouse house = service.house();

        serviceThread.setName("AuctionService");
        serviceThread.start();

        try (final AutomatedClient client = new AutomatedClient(SUBMISSION_CHANNEL, SUBMISSION_STREAM_ID))
        {
            house.addBidder("tmontgomery".getBytes(), 1, 0);
            house.addBidder("mjpt777".getBytes(), 2, 0);
            house.addBidder("RichardWarburton".getBytes(), 3, 0);

            client.auction("Star Wars Force Awakens Pre-Pre-Release", 1000000000, "00:00:10");
            client.bid(0, 1, 1000000000 + 1);
            client.auction("Issue #1000", 0, "00:00:05");
            client.bid(1, 2, 1);
            client.bid(1, 3, 1);
            client.bid(1, 1, 2);

            Thread.sleep(11000);
        }

        service.shutdown();
        serviceThread.join();
        service.close();
        mediaDriver.close();
    }
}
