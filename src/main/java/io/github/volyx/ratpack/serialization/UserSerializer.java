package io.github.volyx.ratpack.serialization;

import io.github.volyx.ratpack.model.Gender;
import io.github.volyx.ratpack.model.User;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.core.util.ReadResolvable;
import net.openhft.chronicle.hash.serialization.BytesReader;
import net.openhft.chronicle.hash.serialization.BytesWriter;
import net.openhft.chronicle.wire.WireIn;
import net.openhft.chronicle.wire.WireOut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

public final class UserSerializer
        implements BytesWriter<User>, BytesReader<User>,
        ReadResolvable<UserSerializer> {

    private Logger logger = LoggerFactory.getLogger(UserSerializer.class);

    public static final UserSerializer INSTANCE = new UserSerializer();

    private UserSerializer() {
    }

    @Override
    public void write(Bytes out, User toWrite) {
        out.writeInt(toWrite.id);
        out.writeUtf8(toWrite.email);
        out.writeUtf8(toWrite.first_name);
        out.writeUtf8(toWrite.last_name);
        out.writeLong(toWrite.birth_date);
        out.writeUtf8(toWrite.gender.name());
    }

    @Nonnull
    @Override
    public User read(Bytes in, User using) {
        try {
            int id = in.readInt();
            User user = new User();
            user.id = id;
            user.email = in.readUtf8();
            user.first_name = in.readUtf8();
            user.last_name = in.readUtf8();
            user.birth_date = in.readLong();
            user.gender = Gender.valueOf(in.readUtf8());
            return user;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
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
    public UserSerializer readResolve() {
        return INSTANCE;
    }
}