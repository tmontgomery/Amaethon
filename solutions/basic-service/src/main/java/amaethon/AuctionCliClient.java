package amaethon;

import amaethon.generated.AuctionEncoder;
import amaethon.generated.BidEncoder;
import amaethon.generated.MessageHeaderEncoder;
import jline.console.ConsoleReader;
import jline.console.UserInterruptException;
import jline.console.completer.StringsCompleter;
import uk.co.real_logic.agrona.concurrent.UnsafeBuffer;

import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class AuctionCliClient
{
    public static final MessageHeaderEncoder MESSAGE_HEADER_ENCODER = new MessageHeaderEncoder();
    public static final AuctionEncoder AUCTION_ENCODER = new AuctionEncoder();
    public static final BidEncoder BID_ENCODER = new BidEncoder();
    public static final UnsafeBuffer BUFFER = new UnsafeBuffer(ByteBuffer.allocateDirect(1024));
    public static final int MESSAGE_TEMPLATE_VERSION = 0;

    public static void main(final String[] args)
    {
        try
        {
            final ConsoleReader consoleReader = new ConsoleReader();
            final StringsCompleter completer = new StringsCompleter("auction", "bid", "clear", "quit", "exit");

            consoleReader.setPrompt("Amaethon> ");
            consoleReader.setHandleUserInterrupt(true);
            consoleReader.addCompleter(completer);

            final PrintWriter out = new PrintWriter(consoleReader.getOutput());

            String line;
            while ((line = consoleReader.readLine()) != null) {

                if (line.startsWith("auction "))
                {
                    String[] tokens = line.split(" ");

                    encodeAuction(tokens[1], tokens[2], tokens[3]);
                }
                else if (line.startsWith("bid "))
                {
                    String[] tokens = line.split(" ");

                    encodeBid(tokens[1], tokens[2], tokens[3]);
                }
                else if (line.startsWith("quit") || line.startsWith("exit"))
                {
                    out.println("Shutting down...");
                    out.flush();
                    break;
                }
                else if (line.startsWith("clear"))
                {
                    consoleReader.clearScreen();
                }
                else if ("".equals(line))
                {
                    // swallow
                }
                else
                {
                    out.println("\"" + line + "\" not supported");
                }

                out.flush();
            }
        }
        catch (UserInterruptException ex)
        {
            // TODO: handle ctlr-C
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public static int encodeAuction(
        final String name, final String reserve, final String duration)
    {
        final long reserveAsLong = Long.parseLong(reserve);
        final LocalTime time = LocalTime.parse(duration, DateTimeFormatter.ISO_LOCAL_TIME);
        final long durationInNanos =
            TimeUnit.HOURS.toNanos(time.getHour()) +
            TimeUnit.MINUTES.toNanos(time.getMinute()) +
            TimeUnit.SECONDS.toNanos(time.getSecond()) +
            TimeUnit.NANOSECONDS.toNanos(time.getNano());

        System.out.format("auction: name=%s, reserve=%d, duration=%d\n", name, reserveAsLong, durationInNanos);

        MESSAGE_HEADER_ENCODER.wrap(BUFFER, 0, MESSAGE_TEMPLATE_VERSION);
        AUCTION_ENCODER.wrap(BUFFER, MESSAGE_HEADER_ENCODER.size());

        MESSAGE_HEADER_ENCODER
            .blockLength(AUCTION_ENCODER.sbeBlockLength())
            .templateId(AUCTION_ENCODER.sbeTemplateId())
            .schemaId(AUCTION_ENCODER.sbeSchemaId())
            .version(AUCTION_ENCODER.sbeSchemaVersion());

        AUCTION_ENCODER
            .durationInNanos(durationInNanos)
            .reserve(reserveAsLong)
            .name(name);

        return MESSAGE_HEADER_ENCODER.size() + AUCTION_ENCODER.size();
    }

    public static int encodeBid(
        final String auction, final String bidder, final String value)
    {
        final int auctionId = Integer.parseInt(auction);
        final long bidderId = Long.parseLong(bidder);
        final long valueAsLong = Long.parseLong(value);

        System.out.format("bid: auctionId=%d, bidderId=%d, value=%d\n", auctionId, bidderId, valueAsLong);

        MESSAGE_HEADER_ENCODER.wrap(BUFFER, 0, MESSAGE_TEMPLATE_VERSION);
        BID_ENCODER.wrap(BUFFER, MESSAGE_HEADER_ENCODER.size());

        MESSAGE_HEADER_ENCODER
            .blockLength(BID_ENCODER.sbeBlockLength())
            .templateId(BID_ENCODER.sbeTemplateId())
            .schemaId(BID_ENCODER.sbeSchemaId())
            .version(BID_ENCODER.sbeSchemaVersion());

        BID_ENCODER
            .auctionId(auctionId)
            .bidderId(bidderId)
            .value(valueAsLong);

        return MESSAGE_HEADER_ENCODER.size() + BID_ENCODER.size();
    }
}
