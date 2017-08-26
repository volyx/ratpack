package io.github.volyx.ratpack.json;

import com.jsoniter.spi.CodegenConfig;
import com.jsoniter.spi.TypeLiteral;
import io.github.volyx.ratpack.model.Avg;
import io.github.volyx.ratpack.model.EmptyObject;
import io.github.volyx.ratpack.model.Gender;
import io.github.volyx.ratpack.model.Location;
import io.github.volyx.ratpack.model.User;
import io.github.volyx.ratpack.model.Visit;

import java.util.List;

public class DemoCodegenConfig implements CodegenConfig {

    @Override
    public void setup() {
        // register custom decoder or extensions before codegen
        // so that we doing codegen, we know in which case, we need to callback
//        JsoniterSpi.registerFieldDecoder(User.class, "score", new Decoder.IntDecoder() {
//            @Override
//            public int decodeInt(JsonIterator iter) throws IOException {
//                return Integer.valueOf(iter.readString());
//            }
//        });
    }

    @Override
    public TypeLiteral[] whatToCodegen() {
        return new TypeLiteral[]{

                TypeLiteral.create(EmptyObject.class),
                TypeLiteral.create(Gender.class),
                TypeLiteral.create(User.class),
                TypeLiteral.create(User[].class),

                TypeLiteral.create(Location.class),
                TypeLiteral.create(Location[].class),

                TypeLiteral.create(Visit.class),
                TypeLiteral.create(Visit[].class),
                TypeLiteral.create(Avg.class),

//                TypeLiteral.create(UserUpdate.class),
//                TypeLiteral.create(LocationUpdate.class),
//                TypeLiteral.create(VisitUpdate.class)



        };
    }
}