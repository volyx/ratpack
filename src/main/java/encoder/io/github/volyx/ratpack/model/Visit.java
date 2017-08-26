package encoder.io.github.volyx.ratpack.model;
public class Visit extends com.jsoniter.spi.EmptyEncoder {
public void encode(Object obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {
if (obj == null) { stream.writeNull(); return; }
stream.write((byte)'{');
encode_((io.github.volyx.ratpack.model.Visit)obj, stream);
stream.write((byte)'}');
}
public static void encode_(io.github.volyx.ratpack.model.Visit obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {
boolean notFirst = false;
if (obj.visited_at != null) {
if (notFirst) { stream.write(','); } else { notFirst = true; }
stream.writeRaw("\"visited_at\":", 13);
stream.writeVal((java.lang.Long)obj.visited_at);
}
if (obj.mark != null) {
if (notFirst) { stream.write(','); } else { notFirst = true; }
stream.writeRaw("\"mark\":", 7);
stream.writeVal((java.lang.Integer)obj.mark);
}
if (obj.location != null) {
if (notFirst) { stream.write(','); } else { notFirst = true; }
stream.writeRaw("\"location\":", 11);
stream.writeVal((java.lang.Integer)obj.location);
}
if (obj.id != null) {
if (notFirst) { stream.write(','); } else { notFirst = true; }
stream.writeRaw("\"id\":", 5);
stream.writeVal((java.lang.Integer)obj.id);
}
if (obj.user != null) {
if (notFirst) { stream.write(','); } else { notFirst = true; }
stream.writeRaw("\"user\":", 7);
stream.writeVal((java.lang.Integer)obj.user);
}
}
}
