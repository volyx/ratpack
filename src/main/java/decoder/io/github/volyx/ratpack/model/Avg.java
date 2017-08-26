package decoder.io.github.volyx.ratpack.model;
public class Avg implements com.jsoniter.spi.Decoder {
public static java.lang.Object decode_(com.jsoniter.JsonIterator iter) throws java.io.IOException { java.lang.Object existingObj = com.jsoniter.CodegenAccess.resetExistingObject(iter);
byte nextToken = com.jsoniter.CodegenAccess.readByte(iter);
if (nextToken != '{') {
if (nextToken == 'n') {
com.jsoniter.CodegenAccess.skipFixedBytes(iter, 3);
return null;
} else {
nextToken = com.jsoniter.CodegenAccess.nextToken(iter);
if (nextToken == 'n') {
com.jsoniter.CodegenAccess.skipFixedBytes(iter, 3);
return null;
}
} // end of if null
} // end of if {
nextToken = com.jsoniter.CodegenAccess.readByte(iter);
if (nextToken != '"') {
if (nextToken == '}') {
return (existingObj == null ? new io.github.volyx.ratpack.model.Avg() : (io.github.volyx.ratpack.model.Avg)existingObj);
} else {
nextToken = com.jsoniter.CodegenAccess.nextToken(iter);
if (nextToken == '}') {
return (existingObj == null ? new io.github.volyx.ratpack.model.Avg() : (io.github.volyx.ratpack.model.Avg)existingObj);
} else {
com.jsoniter.CodegenAccess.unreadByte(iter);
}
} // end of if end
} else { com.jsoniter.CodegenAccess.unreadByte(iter); }// end of if not quote
java.lang.Double _avg_ = null;
do {
switch (com.jsoniter.CodegenAccess.readObjectFieldAsHash(iter)) {
case 510002283: 
_avg_ = (java.lang.Double)java.lang.Double.valueOf(iter.readDouble());
continue;
}
iter.skip();
} while (com.jsoniter.CodegenAccess.nextTokenIsComma(iter));
io.github.volyx.ratpack.model.Avg obj = (existingObj == null ? new io.github.volyx.ratpack.model.Avg() : (io.github.volyx.ratpack.model.Avg)existingObj);
obj.avg = _avg_;
return obj;
}public java.lang.Object decode(com.jsoniter.JsonIterator iter) throws java.io.IOException {
return decode_(iter);
}
}
