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

import java.util.Arrays;

/**
 * Encapsulation of a bidder
 */
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
