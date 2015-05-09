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
