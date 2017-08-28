package io.github.volyx.ratpack.handler;

public class Exchange {
    public interface BodyImpl extends JsonSender, JsonParser {}
    public interface ErrorImpl extends ErrorSender {}

    private static final BodyImpl BODY = new BodyImpl() {};
    private static final ErrorImpl ERROR = new ErrorImpl() {};


    public static interface QueryParamImpl extends QueryParams {};
    private static final QueryParamImpl QUERYPARAMS = new QueryParamImpl(){};

    public static QueryParamImpl queryParams() {
        return QUERYPARAMS;
    }

    public static BodyImpl body() {
        return BODY;
    }
    public static ErrorImpl error() {
        return ERROR;
    }
}
