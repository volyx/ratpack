package encoder.io.github.volyx.ratpack.update;
public class UserUpdate extends com.jsoniter.spi.EmptyEncoder {
public void encode(Object obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {
if (obj == null) { stream.writeNull(); return; }
stream.write((byte)'{');
encode_((io.github.volyx.ratpack.update.UserUpdate)obj, stream);
stream.write((byte)'}');
}
public static void encode_(io.github.volyx.ratpack.update.UserUpdate obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {
boolean notFirst = false;
if (obj.email != null) {
if (notFirst) { stream.write(','); } else { notFirst = true; }
stream.writeRaw("\"email\":", 8);
stream.writeVal((java.lang.String)obj.email);
}
if (obj.first_name != null) {
if (notFirst) { stream.write(','); } else { notFirst = true; }
stream.writeRaw("\"first_name\":", 13);
stream.writeVal((java.lang.String)obj.first_name);
}
if (obj.birth_date != null) {
if (notFirst) { stream.write(','); } else { notFirst = true; }
stream.writeRaw("\"birth_date\":", 13);
stream.writeVal((java.lang.Long)obj.birth_date);
}
if (obj.gender != null) {
if (notFirst) { stream.write(','); } else { notFirst = true; }
stream.writeRaw("\"gender\":", 9);
stream.write((byte)'\"');
encoder.io.github.volyx.ratpack.model.Gender.encode_((io.github.volyx.ratpack.model.Gender)obj.gender, stream);
stream.write((byte)'\"');
}
if (obj.last_name != null) {
if (notFirst) { stream.write(','); } else { notFirst = true; }
stream.writeRaw("\"last_name\":", 12);
stream.writeVal((java.lang.String)obj.last_name);
}
}
}
