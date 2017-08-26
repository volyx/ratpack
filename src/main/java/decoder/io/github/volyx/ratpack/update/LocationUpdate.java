package decoder.io.github.volyx.ratpack.update;

import io.github.volyx.ratpack.validate.Validator;

public class LocationUpdate implements com.jsoniter.spi.Decoder {
    static Validator validator = object -> {};
    public static java.lang.Object decode_(com.jsoniter.JsonIterator iter) throws java.io.IOException {
        java.lang.Object existingObj = com.jsoniter.CodegenAccess.resetExistingObject(iter);
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
                return (existingObj == null ? new io.github.volyx.ratpack.update.LocationUpdate() : (io.github.volyx.ratpack.update.LocationUpdate) existingObj);
            } else {
                nextToken = com.jsoniter.CodegenAccess.nextToken(iter);
                if (nextToken == '}') {
                    return (existingObj == null ? new io.github.volyx.ratpack.update.LocationUpdate() : (io.github.volyx.ratpack.update.LocationUpdate) existingObj);
                } else {
                    com.jsoniter.CodegenAccess.unreadByte(iter);
                }
            } // end of if end
        } else {
            com.jsoniter.CodegenAccess.unreadByte(iter);
        }// end of if not quote
        java.lang.String _place_ = null;
        java.lang.String _country_ = null;
        java.lang.String _city_ = null;
        java.lang.Integer _distance_ = null;
        do {
            switch (com.jsoniter.CodegenAccess.readObjectFieldAsHash(iter)) {
                case -1517218351:
                    _country_ = (java.lang.String) iter.readString();
                    validator.validateNull(_country_);
                    validator.validateCountry(_country_);
                    continue;
                case -925486340:
                    _place_ = (java.lang.String) iter.readString();
                    validator.validateNull(_place_);
                    continue;
                case 230981954:
                    _city_ = (java.lang.String) iter.readString();
                    validator.validateNull(_city_);
                    validator.validateCity(_city_);
                    continue;
                case 783488098:
                    _distance_ = (java.lang.Integer) java.lang.Integer.valueOf(iter.readInt());
                    continue;
            }
            iter.skip();
        } while (com.jsoniter.CodegenAccess.nextTokenIsComma(iter));
        io.github.volyx.ratpack.update.LocationUpdate obj = (existingObj == null ? new io.github.volyx.ratpack.update.LocationUpdate() : (io.github.volyx.ratpack.update.LocationUpdate) existingObj);
        obj.place = _place_;
        obj.country = _country_;
        obj.city = _city_;
        obj.distance = _distance_;
        return obj;
    }

    public java.lang.Object decode(com.jsoniter.JsonIterator iter) throws java.io.IOException {
        return decode_(iter);
    }
}
