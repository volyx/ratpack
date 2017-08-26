package decoder.io.github.volyx.ratpack.model;
public class Gender implements com.jsoniter.spi.Decoder {
public static java.lang.Object decode_(com.jsoniter.JsonIterator iter) throws java.io.IOException { if (iter.readNull()) { return null; }
com.jsoniter.Slice field = com.jsoniter.CodegenAccess.readSlice(iter);
switch (field.len()) {
case 1: 
if (
field.at(0)==102
) {
return io.github.volyx.ratpack.model.Gender.f;
}
if (
field.at(0)==109
) {
return io.github.volyx.ratpack.model.Gender.m;
}
break;

}
throw iter.reportError("decode enum", field + " is not valid enum for io.github.volyx.ratpack.model.Gender");
}public java.lang.Object decode(com.jsoniter.JsonIterator iter) throws java.io.IOException {
return decode_(iter);
}
}
