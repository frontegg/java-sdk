package com.frontegg.ws.sample;

import com.frontegg.sdk.middleware.IFronteggService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController()
@RequestMapping("/frontegg")
public class TestContreoller {

    @Autowired
    private IFronteggService fronteggMiddleware;


    @RequestMapping(value = "/{path}", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<?> doProcess(@PathVariable String path,
                                       HttpServletRequest request,
                                       HttpServletResponse response) {

        return new ResponseEntity<>(
                fronteggMiddleware.doProcess(request, response),
                HttpStatus.OK
        );
    }
}
