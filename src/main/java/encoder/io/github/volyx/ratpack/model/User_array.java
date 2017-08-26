package encoder.io.github.volyx.ratpack.model;

public class User_array extends com.jsoniter.spi.EmptyEncoder {
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
        io.github.volyx.ratpack.model.User[] arr = (io.github.volyx.ratpack.model.User[]) obj;
        if (arr.length == 0) {
            return;
        }
        int i = 0;
        io.github.volyx.ratpack.model.User e = arr[i++];
        if (e == null) {
            stream.writeNull();
        } else {
            stream.write((byte) '{');
            encoder.io.github.volyx.ratpack.model.User.encode_(e, stream);
            stream.write((byte) '}');
        }
        while (i < arr.length) {
            stream.write(',');
            e = arr[i++];
            if (e == null) {
                stream.writeNull();
            } else {
                stream.write((byte) '{');
                encoder.io.github.volyx.ratpack.model.User.encode_(e, stream);
                stream.write((byte) '}');
            }
        }
    }
}
