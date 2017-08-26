package encoder.io.github.volyx.ratpack.model;
public class Avg extends com.jsoniter.spi.EmptyEncoder {
public void encode(Object obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {
if (obj == null) { stream.writeNull(); return; }
stream.write((byte)'{');
encode_((io.github.volyx.ratpack.model.Avg)obj, stream);
stream.write((byte)'}');
}
public static void encode_(io.github.volyx.ratpack.model.Avg obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {
boolean notFirst = false;
if (obj.avg != null) {
if (notFirst) { stream.write(','); } else { notFirst = true; }
stream.writeRaw("\"avg\":", 6);
stream.writeVal((java.lang.Double)obj.avg);
}
}
}
