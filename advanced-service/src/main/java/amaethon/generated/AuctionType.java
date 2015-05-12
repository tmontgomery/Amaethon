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

/* Generated SBE (Simple Binary Encoding) message codec */
package amaethon.generated;

public enum AuctionType
{
    Basic((byte)0),
    FixedPrice((byte)1),
    NULL_VAL((byte)-128);

    private final byte value;

    AuctionType(final byte value)
    {
        this.value = value;
    }

    public byte value()
    {
        return value;
    }

    public static AuctionType get(final byte value)
    {
        switch (value)
        {
            case 0: return Basic;
            case 1: return FixedPrice;
        }

        if ((byte)-128 == value)
        {
            return NULL_VAL;
        }

        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
