package decoder.io.github.volyx.ratpack.model;
public class Visit implements com.jsoniter.spi.Decoder {
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
return (existingObj == null ? new io.github.volyx.ratpack.model.Visit() : (io.github.volyx.ratpack.model.Visit)existingObj);
} else {
nextToken = com.jsoniter.CodegenAccess.nextToken(iter);
if (nextToken == '}') {
return (existingObj == null ? new io.github.volyx.ratpack.model.Visit() : (io.github.volyx.ratpack.model.Visit)existingObj);
} else {
com.jsoniter.CodegenAccess.unreadByte(iter);
}
} // end of if end
} else { com.jsoniter.CodegenAccess.unreadByte(iter); }// end of if not quote
java.lang.Integer _id_ = null;
java.lang.Integer _location_ = null;
java.lang.Integer _user_ = null;
java.lang.Long _visited_at_ = null;
java.lang.Integer _mark_ = null;
do {
switch (com.jsoniter.CodegenAccess.readObjectFieldAsHash(iter)) {
case -1993887949: 
_visited_at_ = (java.lang.Long)java.lang.Long.valueOf(iter.readLong());
continue;
case -948247392: 
_mark_ = (java.lang.Integer)java.lang.Integer.valueOf(iter.readInt());
continue;
case 200649126: 
_location_ = (java.lang.Integer)java.lang.Integer.valueOf(iter.readInt());
continue;
case 926444256: 
_id_ = (java.lang.Integer)java.lang.Integer.valueOf(iter.readInt());
continue;
case 1618501362: 
_user_ = (java.lang.Integer)java.lang.Integer.valueOf(iter.readInt());
continue;
}
iter.skip();
} while (com.jsoniter.CodegenAccess.nextTokenIsComma(iter));
io.github.volyx.ratpack.model.Visit obj = (existingObj == null ? new io.github.volyx.ratpack.model.Visit() : (io.github.volyx.ratpack.model.Visit)existingObj);
obj.id = _id_;
obj.location = _location_;
obj.user = _user_;
obj.visited_at = _visited_at_;
obj.mark = _mark_;
return obj;
}public java.lang.Object decode(com.jsoniter.JsonIterator iter) throws java.io.IOException {
return decode_(iter);
}
}
