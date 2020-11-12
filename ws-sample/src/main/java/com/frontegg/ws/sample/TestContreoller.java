package com.frontegg.ws.sample;

import com.frontegg.sdk.middleware.IFronteggMiddleware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class TestContreoller {

    @Autowired
    private IFronteggMiddleware fronteggMiddleware;


    @RequestMapping(value = "/{path}", method = {RequestMethod.GET, RequestMethod.POST})
    public String doProcess(@PathVariable String path, HttpServletRequest request, HttpServletResponse response) {
        fronteggMiddleware.doProcess(request, response);
        return String.valueOf(response.getStatus());
    }
}
