package io.github.volyx.ratpack.serialization;

import io.github.volyx.ratpack.model.Visit;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.core.util.ReadResolvable;
import net.openhft.chronicle.hash.serialization.BytesReader;
import net.openhft.chronicle.hash.serialization.BytesWriter;
import net.openhft.chronicle.wire.WireIn;
import net.openhft.chronicle.wire.WireOut;

import javax.annotation.Nonnull;

public class VisitSerializer implements BytesWriter<Visit>, BytesReader<Visit>,
        ReadResolvable<io.github.volyx.ratpack.serialization.VisitSerializer> {

    public static final io.github.volyx.ratpack.serialization.VisitSerializer INSTANCE = new io.github.volyx.ratpack.serialization.VisitSerializer();

    private VisitSerializer() {
    }

    @Override
    public void write(Bytes out, Visit toWrite) {
        out.writeInt(toWrite.id);
        out.writeInt(toWrite.location);
        out.writeInt(toWrite.user);
        out.writeInt(toWrite.mark);
        out.writeLong(toWrite.visited_at);
    }

    @Nonnull
    @Override
    public Visit read(Bytes in, Visit using) {
        int id = in.readInt();
        Visit visit = new Visit();
        visit.id = id;
        visit.location = in.readInt();
        visit.user = in.readInt();
        visit.mark = in.readInt();
        visit.visited_at = in.readLong();
        return visit;
    }

    @Override
    public void writeMarshallable(@Nonnull WireOut wireOut) {
        // no fields to write
    }

    @Override
    public void readMarshallable(@Nonnull WireIn wireIn) {
        // no fields to read
    }

    @Override
    public io.github.volyx.ratpack.serialization.VisitSerializer readResolve() {
        return INSTANCE;
    }
}