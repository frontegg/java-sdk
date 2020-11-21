package com.frontegg.ws.sample;

import com.frontegg.sdk.common.model.FronteggHttpHeader;
import com.frontegg.sdk.common.model.FronteggHttpResponse;
import com.frontegg.sdk.middleware.IFronteggServiceDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController()
@RequestMapping("/frontegg/")
public class FronteggController {

    @Autowired
    private IFronteggServiceDelegate fronteggServiceDelegate;


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

        FronteggHttpResponse<Object> fronteggHttpResponse = fronteggServiceDelegate.doProcess(request, response);
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
}
