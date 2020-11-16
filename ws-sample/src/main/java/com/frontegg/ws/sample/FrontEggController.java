package com.frontegg.ws.sample;

import com.frontegg.sdk.common.model.FronteggHttpResponse;
import com.frontegg.sdk.middleware.IFronteggService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController()
@RequestMapping("/frontegg/")
public class FrontEggController {

    @Autowired
    private IFronteggService fronteggMiddleware;


    @CrossOrigin()
    @RequestMapping(value = "**", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<?> doProcess(HttpServletRequest request,
                                       HttpServletResponse response) {

        FronteggHttpResponse<Object> fronteggHttpResponse = fronteggMiddleware.doProcess(request, response);
        return new ResponseEntity<>(fronteggHttpResponse.getBody(), HttpStatus.resolve(fronteggHttpResponse.getStatusCode()));
    }
}
