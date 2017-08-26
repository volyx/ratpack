package encoder.io.github.volyx.ratpack.model;

public class Visit_array extends com.jsoniter.spi.EmptyEncoder {
    public void encode(Object obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {
        if (obj == null) {
            stream.writeNull();
            return;
        }
        stream.write((byte) '[');
        encode_(obj, stream);
        stream.write((byte) ']');
    }

    public static void encode_(java.lang.Object obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {
        io.github.volyx.ratpack.model.Visit[] arr = (io.github.volyx.ratpack.model.Visit[]) obj;
        if (arr.length == 0) {
            return;
        }
        int i = 0;
        io.github.volyx.ratpack.model.Visit e = arr[i++];
        if (e == null) {
            stream.writeNull();
        } else {
            stream.write((byte) '{');
            encoder.io.github.volyx.ratpack.model.Visit.encode_(e, stream);
            stream.write((byte) '}');
        }
        while (i < arr.length) {
            stream.write(',');
            e = arr[i++];
            if (e == null) {
                stream.writeNull();
            } else {
                stream.write((byte) '{');
                encoder.io.github.volyx.ratpack.model.Visit.encode_(e, stream);
                stream.write((byte) '}');
            }
        }
    }
}
