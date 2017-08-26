package decoder.io.github.volyx.ratpack.model;
public class EmptyObject implements com.jsoniter.spi.Decoder {
public static java.lang.Object decode_(com.jsoniter.JsonIterator iter) throws java.io.IOException { java.lang.Object existingObj = com.jsoniter.CodegenAccess.resetExistingObject(iter);
if (iter.readNull()) { return null; }
io.github.volyx.ratpack.model.EmptyObject obj = (existingObj == null ? new io.github.volyx.ratpack.model.EmptyObject() : (io.github.volyx.ratpack.model.EmptyObject)existingObj);
if (!com.jsoniter.CodegenAccess.readObjectStart(iter)) {
return obj;
}
com.jsoniter.Slice field = com.jsoniter.CodegenAccess.readObjectFieldAsSlice(iter);
boolean once = true;
while (once) {
once = false;
iter.skip();
}
while (com.jsoniter.CodegenAccess.nextToken(iter) == ',') {
field = com.jsoniter.CodegenAccess.readObjectFieldAsSlice(iter);
iter.skip();
}
return obj;
}public java.lang.Object decode(com.jsoniter.JsonIterator iter) throws java.io.IOException {
return decode_(iter);
}
}
