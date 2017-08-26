package decoder.io.github.volyx.ratpack.model;

public class Location_array implements com.jsoniter.spi.Decoder {
    public static java.lang.Object decode_(com.jsoniter.JsonIterator iter) throws java.io.IOException {
        com.jsoniter.CodegenAccess.resetExistingObject(iter);
        byte nextToken = com.jsoniter.CodegenAccess.readByte(iter);
        if (nextToken != '[') {
            if (nextToken == 'n') {
                com.jsoniter.CodegenAccess.skipFixedBytes(iter, 3);
                com.jsoniter.CodegenAccess.resetExistingObject(iter);
                return null;
            } else {
                nextToken = com.jsoniter.CodegenAccess.nextToken(iter);
                if (nextToken == 'n') {
                    com.jsoniter.CodegenAccess.skipFixedBytes(iter, 3);
                    com.jsoniter.CodegenAccess.resetExistingObject(iter);
                    return null;
                }
            }
        }
        nextToken = com.jsoniter.CodegenAccess.nextToken(iter);
        if (nextToken == ']') {
            return new io.github.volyx.ratpack.model.Location[0];
        }
        com.jsoniter.CodegenAccess.unreadByte(iter);
        io.github.volyx.ratpack.model.Location a1 = (io.github.volyx.ratpack.model.Location) decoder.io.github.volyx.ratpack.model.Location.decode_(iter);
        if (!com.jsoniter.CodegenAccess.nextTokenIsComma(iter)) {
            return new io.github.volyx.ratpack.model.Location[]{a1};
        }
        io.github.volyx.ratpack.model.Location a2 = (io.github.volyx.ratpack.model.Location) decoder.io.github.volyx.ratpack.model.Location.decode_(iter);
        if (!com.jsoniter.CodegenAccess.nextTokenIsComma(iter)) {
            return new io.github.volyx.ratpack.model.Location[]{a1, a2};
        }
        io.github.volyx.ratpack.model.Location a3 = (io.github.volyx.ratpack.model.Location) decoder.io.github.volyx.ratpack.model.Location.decode_(iter);
        if (!com.jsoniter.CodegenAccess.nextTokenIsComma(iter)) {
            return new io.github.volyx.ratpack.model.Location[]{a1, a2, a3};
        }
        io.github.volyx.ratpack.model.Location a4 = (io.github.volyx.ratpack.model.Location) Location.decode_(iter);
        if (!com.jsoniter.CodegenAccess.nextTokenIsComma(iter)) {
            return new io.github.volyx.ratpack.model.Location[]{a1, a2, a3, a4};
        }
        io.github.volyx.ratpack.model.Location a5 = (io.github.volyx.ratpack.model.Location) Location.decode_(iter);
        io.github.volyx.ratpack.model.Location[] arr = new io.github.volyx.ratpack.model.Location[10];
        arr[0] = a1;
        arr[1] = a2;
        arr[2] = a3;
        arr[3] = a4;
        arr[4] = a5;
        int i = 5;
        while (com.jsoniter.CodegenAccess.nextTokenIsComma(iter)) {
            if (i == arr.length) {
                io.github.volyx.ratpack.model.Location[] newArr = new io.github.volyx.ratpack.model.Location[arr.length * 2];
                System.arraycopy(arr, 0, newArr, 0, arr.length);
                arr = newArr;
            }
            arr[i++] = (io.github.volyx.ratpack.model.Location) decoder.io.github.volyx.ratpack.model.Location.decode_(iter);
        }
        io.github.volyx.ratpack.model.Location[] result = new io.github.volyx.ratpack.model.Location[i];
        System.arraycopy(arr, 0, result, 0, i);
        return result;
    }

    public java.lang.Object decode(com.jsoniter.JsonIterator iter) throws java.io.IOException {
        return decode_(iter);
    }
}
