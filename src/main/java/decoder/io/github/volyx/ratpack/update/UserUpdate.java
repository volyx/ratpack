package decoder.io.github.volyx.ratpack.update;

import io.github.volyx.ratpack.validate.Validator;

public class UserUpdate implements com.jsoniter.spi.Decoder {
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
                return (existingObj == null ? new io.github.volyx.ratpack.update.UserUpdate() : (io.github.volyx.ratpack.update.UserUpdate) existingObj);
            } else {
                nextToken = com.jsoniter.CodegenAccess.nextToken(iter);
                if (nextToken == '}') {
                    return (existingObj == null ? new io.github.volyx.ratpack.update.UserUpdate() : (io.github.volyx.ratpack.update.UserUpdate) existingObj);
                } else {
                    com.jsoniter.CodegenAccess.unreadByte(iter);
                }
            } // end of if end
        } else {
            com.jsoniter.CodegenAccess.unreadByte(iter);
        }// end of if not quote
        java.lang.String _email_ = null;
        java.lang.String _first_name_ = null;
        java.lang.String _last_name_ = null;
        io.github.volyx.ratpack.model.Gender _gender_ = null;
        java.lang.Long _birth_date_ = null;
        do {
            switch (com.jsoniter.CodegenAccess.readObjectFieldAsHash(iter)) {
                case -1970842681:
                    _email_ = (java.lang.String) iter.readString();
                    validator.validateNull(_email_);
                    continue;
                case -71357759:
                    _first_name_ = (java.lang.String) iter.readString();
                    validator.validateNull(_first_name_);
                    validator.validateFirstName(_first_name_);
                    continue;
                case 63294279:
                    _birth_date_ = (java.lang.Long) java.lang.Long.valueOf(iter.readLong());
                    continue;
                case 1587320192:
                    _gender_ = (io.github.volyx.ratpack.model.Gender) decoder.io.github.volyx.ratpack.model.Gender.decode_(iter);
                    continue;
                case 1760680185:
                    _last_name_ = (java.lang.String) iter.readString();
                    validator.validateNull(_last_name_);
                    validator.validateLastName(_last_name_);
                    continue;
            }
            iter.skip();
        } while (com.jsoniter.CodegenAccess.nextTokenIsComma(iter));
        io.github.volyx.ratpack.update.UserUpdate obj = (existingObj == null ? new io.github.volyx.ratpack.update.UserUpdate() : (io.github.volyx.ratpack.update.UserUpdate) existingObj);
        obj.email = _email_;
        obj.first_name = _first_name_;
        obj.last_name = _last_name_;
        obj.gender = _gender_;
        obj.birth_date = _birth_date_;
        return obj;
    }

    public java.lang.Object decode(com.jsoniter.JsonIterator iter) throws java.io.IOException {
        return decode_(iter);
    }
}
