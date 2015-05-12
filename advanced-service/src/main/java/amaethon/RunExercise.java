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
        System.out.print("***\n*** Exercise incomplete\n***\n");
    }
}
