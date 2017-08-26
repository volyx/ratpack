package encoder.io.github.volyx.ratpack.model;
public class Location extends com.jsoniter.spi.EmptyEncoder {
public void encode(Object obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {
if (obj == null) { stream.writeNull(); return; }
stream.write((byte)'{');
encode_((io.github.volyx.ratpack.model.Location)obj, stream);
stream.write((byte)'}');
}
public static void encode_(io.github.volyx.ratpack.model.Location obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {
boolean notFirst = false;
if (obj.country != null) {
if (notFirst) { stream.write(','); } else { notFirst = true; }
stream.writeRaw("\"country\":", 10);
stream.writeVal((java.lang.String)obj.country);
}
if (obj.place != null) {
if (notFirst) { stream.write(','); } else { notFirst = true; }
stream.writeRaw("\"place\":", 8);
stream.writeVal((java.lang.String)obj.place);
}
if (obj.city != null) {
if (notFirst) { stream.write(','); } else { notFirst = true; }
stream.writeRaw("\"city\":", 7);
stream.writeVal((java.lang.String)obj.city);
}
if (obj.distance != null) {
if (notFirst) { stream.write(','); } else { notFirst = true; }
stream.writeRaw("\"distance\":", 11);
stream.writeVal((java.lang.Integer)obj.distance);
}
if (obj.id != null) {
if (notFirst) { stream.write(','); } else { notFirst = true; }
stream.writeRaw("\"id\":", 5);
stream.writeVal((java.lang.Integer)obj.id);
}
}
}
