package encoder.io.github.volyx.ratpack.model;
public class Gender extends com.jsoniter.spi.EmptyEncoder {
public void encode(Object obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {
if (obj == null) { stream.writeNull(); return; }
stream.write((byte)'\"');
encode_((io.github.volyx.ratpack.model.Gender)obj, stream);
stream.write((byte)'\"');
}
public static void encode_(java.lang.Object obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {
if (obj == null) { stream.writeNull(); return; }
stream.writeRaw(obj.toString());
}
}
