package encoder.io.github.volyx.ratpack.model;
public class EmptyObject extends com.jsoniter.spi.EmptyEncoder {
public void encode(Object obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {
if (obj == null) { stream.writeNull(); return; }
stream.write((byte)'{', (byte)'}');
encode_((io.github.volyx.ratpack.model.EmptyObject)obj, stream);
}
public static void encode_(io.github.volyx.ratpack.model.EmptyObject obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {
}
}
