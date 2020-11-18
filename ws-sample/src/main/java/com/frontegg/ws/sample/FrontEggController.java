package com.frontegg.ws.sample;

import com.frontegg.sdk.common.model.FronteggHttpHeader;
import com.frontegg.sdk.common.model.FronteggHttpResponse;
import com.frontegg.sdk.common.util.HttpUtil;
import com.frontegg.sdk.middleware.IFronteggService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController()
@RequestMapping("/frontegg/")
public class FrontEggController {

    @Autowired
    private IFronteggService fronteggMiddleware;


    @CrossOrigin()
    @RequestMapping(value = "**", method = {
            RequestMethod.GET,
            RequestMethod.POST,
            RequestMethod.PATCH,
            RequestMethod.DELETE,
            RequestMethod.OPTIONS,
            RequestMethod.HEAD,
    })
    public ResponseEntity<?> doProcess(HttpServletRequest request,
                                       HttpServletResponse response) {

        FronteggHttpResponse<Object> fronteggHttpResponse = fronteggMiddleware.doProcess(request, response);
        populateHeaders(fronteggHttpResponse.getHeaders(), response);
        return new ResponseEntity<>(
                fronteggHttpResponse.getBody(),
                HttpStatus.resolve(fronteggHttpResponse.getStatusCode())
        );
    }

    public void populateHeaders(List<FronteggHttpHeader> headers, HttpServletResponse response) {
        for (FronteggHttpHeader header : headers) {
            response.setHeader(header.getName(), header.getValue());
        }
    }

//    private HttpHeaders resolverHeaders(List<FronteggHttpHeader> headers) {
//        HttpHeaders httpHeaders = new HttpHeaders();
//        headers.forEach(fronteggHttpHeader -> {
//            httpHeaders.put(fronteggHttpHeader.getName(), resolveMultiValueHeader(fronteggHttpHeader.getValue()));
//        });
//        return httpHeaders;
//    }

    private List<String> resolveMultiValueHeader(String value) {
        String[] array = value.split(";");
        return Arrays.asList(array);
    }
}
