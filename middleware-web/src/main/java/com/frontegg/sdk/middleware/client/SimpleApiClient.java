package com.frontegg.sdk.middleware.client;

import com.frontegg.sdk.api.client.IApiClient;
import com.frontegg.sdk.common.model.FronteggHttpHeader;
import com.frontegg.sdk.common.model.FronteggHttpResponse;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.impl.DefaultHttpRequestFactory;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SimpleApiClient implements IApiClient {
    @Override
    public <T> Optional<T> get(String url, Class<T> clazz) {
        return Optional.empty();
    }

    @Override
    public <T> Optional<T> get(String url, Map<String, String> headers, Class<T> clazz) {
        return Optional.empty();
    }

    @Override
    public <T, R> Optional<T> post(String url, Class<T> clazz, R body) {
        return Optional.empty();
    }

    @Override
    public <T, R> Optional<T> post(String url, Class<T> clazz, R body, Map<String, String> headers) {
        return Optional.empty();
    }

    @Override
    public <T> FronteggHttpResponse<T> service(String url,
                                               HttpServletRequest request,
                                               HttpServletResponse response,
                                               Map<String, String> headers,
                                               Class<T> clazz) {

        HttpClient httpClient = HttpClientBuilder.create()
                .build();

        //HttpResponse httpResponse = httpClient.execute();
        //return convert(httpResponse);
        return null;
    }

    private <T> ResponseHandler<T> responseHandler(Class<T> clazz) {
        return null;
    }

    private HttpRequest buildRequest(String url, HttpServletRequest request, Map<String, String> headers) throws MethodNotSupportedException {
        HttpRequest httpRequest  = DefaultHttpRequestFactory.INSTANCE.newHttpRequest(request.getMethod(), url);
        populateRequestHeaders(httpRequest, headers);

        return httpRequest;
    }

    private void populateRequestHeaders(HttpRequest request, Map<String, String> headers) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            request.addHeader(entry.getKey(), entry.getValue());
        }
    }

    private static <T> FronteggHttpResponse<T> convert(HttpResponse httpResponse) {
        FronteggHttpResponse<T> response = new FronteggHttpResponse<>();
        response.setBody(convertBody(httpResponse));
        response.setStatusCode(httpResponse.getStatusLine().getStatusCode());
        response.setHeaders(convertHeaders(httpResponse.getAllHeaders()));
        return response;

    }

    private static <T> T convertBody(HttpResponse httpResponse) {
        try {

            if (httpResponse.getEntity().getContentLength() == 0) return null;

            InputStream is = httpResponse.getEntity().getContent();
            ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(is));
            return (T) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static List<FronteggHttpHeader> convertHeaders(Header[] allHeaders) {
        List<FronteggHttpHeader> fronteggHttpHeaders = new ArrayList<>();;
        for (Header header: allHeaders) {
            fronteggHttpHeaders.add(new FronteggHttpHeader(header.getName(), header.getValue()));
        }
        return fronteggHttpHeaders;
    }
}
