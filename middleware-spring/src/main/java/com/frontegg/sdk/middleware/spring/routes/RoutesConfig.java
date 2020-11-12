package com.frontegg.sdk.middleware.spring.routes;

import java.util.List;

public class RoutesConfig {

    private List<VendorClientPublicRouts> vendorClientPublicRoutes;


    public List<VendorClientPublicRouts> getVendorClientPublicRoutes() {
        return vendorClientPublicRoutes;
    }

    public void setVendorClientPublicRoutes(List<VendorClientPublicRouts> vendorClientPublicRoutes) {
        this.vendorClientPublicRoutes = vendorClientPublicRoutes;
    }
}
