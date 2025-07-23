package software.leonov.client.http;

import java.util.function.Consumer;

public interface HttpRequestInterceptor extends Consumer<HttpRequest> {

    public static final HttpRequestInterceptor DO_NOTHING = (request) -> {
    };

    @Override
    default void accept(final HttpRequest request) {
        process(request);
    }

    public void process(final HttpRequest request);
}