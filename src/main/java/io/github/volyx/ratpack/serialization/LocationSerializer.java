package io.github.volyx.ratpack.serialization;

import io.github.volyx.ratpack.model.Location;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.core.util.ReadResolvable;
import net.openhft.chronicle.hash.serialization.BytesReader;
import net.openhft.chronicle.hash.serialization.BytesWriter;
import net.openhft.chronicle.wire.WireIn;
import net.openhft.chronicle.wire.WireOut;

import javax.annotation.Nonnull;

public class LocationSerializer implements BytesWriter<Location>, BytesReader<Location>,
        ReadResolvable<io.github.volyx.ratpack.serialization.LocationSerializer> {

    public static final io.github.volyx.ratpack.serialization.LocationSerializer INSTANCE = new io.github.volyx.ratpack.serialization.LocationSerializer();

    private LocationSerializer() {
    }

    @Override
    public void write(Bytes out, Location toWrite) {
        out.writeInt(toWrite.id);
        out.writeUtf8(toWrite.country);
        out.writeUtf8(toWrite.city);
        out.writeUtf8(toWrite.place);
        out.writeInt(toWrite.distance);
    }

    @Nonnull
    @Override
    public Location read(Bytes in, Location using) {
        int id = in.readInt();
        Location location = new Location();
        location.id = id;
        location.country = in.readUtf8();
        location.city = in.readUtf8();
        location.place = in.readUtf8();
        location.distance = in.readInt();
        return location;
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
    public io.github.volyx.ratpack.serialization.LocationSerializer readResolve() {
        return INSTANCE;
    }
}