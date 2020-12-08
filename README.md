# java-sdk

![alt text](https://fronteggstuff.blob.core.windows.net/frongegg-logos/logo-transparent.png)

Frontegg is a web platform where SaaS companies can set up their fully managed, scalable and brand aware - SaaS features and integrate them into their SaaS portals in up to 5 lines of code.

## Usage

#### Spring Boot Application
To run application with frontegg middleware there is only one dependency which will autoconfigure frontegg for you application.
Add maven dependency 
```xml
<dependency>
    <groupId>com.frontegg</groupId>
    <artifactId>middleware-springboot-autoconfigure</artifactId>
    <version>x.x.x</version>
</dependency>
```
Frontegg uses some headers, and you should add to your cors filter's allowed headers the following headers `Authorization` and `x-frontegg-source`
Or you can create some filter with the following configuration


```java
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MyCORSFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, PATCH, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, x-frontegg-source, Authorization");

        chain.doFilter(req, res);
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }

}
```

If you need to change base endpoint for frontegg you need to create/override frontEggFilter.
default path is `/frontegg`.
```java
@Configuration
class MyAppConfiguration {
    @Bean
    public FronteggFilter fronteggFilter(FronteggAuthenticationService authenticationService,
                                         IFronteggRouteService fronteggRouteService,
                                         FronteggServiceDelegate fronteggServiceDelegate,
                                         FronteggOptions options) {
        Assert.notNull(authenticationService, "authenticationService cannot be null");
        Assert.notNull(fronteggRouteService, "fronteggRouteService cannot be null");
        Assert.notNull(fronteggServiceDelegate, "delegate cannot be null");
        Assert.notNull(options, "frontegg options cannot be null");

        return new FronteggFilter("my-path", authenticationService, fronteggRouteService, fronteggServiceDelegate, options);
    }
}
```

To override FronteggOptions in your configuration class define your own bean
```java
@Configuration
class MyAppConfiguration {
    @Bean
    public FronteggOptions fronteggOptions() {
        FronteggOptions fronteggOptions = new FronteggOptions();
        fronteggOptions.setMaxRetries(maxRetries);
        fronteggOptions.setDisableCors(disableCors);
        fronteggOptions.setCookieDomainRewrite(cookieDomainRewrite);
        fronteggOptions.setClientId(clientID);
        fronteggOptions.setApiKey(apiKey);
        return fronteggOptions;
    }
}
```

all frontegg specific properties can be defined in the application.yml file
```yaml
frontegg:
  clientId: {your client id}
  apiKey: {your api key}
  basePath: /frontegg
  settings:
    disableCors: false
    maxRetries: 3
    cookieDomainRewrite:
``` 


### Configuration

| Option Name       | Type   | Description
|-------------------|:---:|---|
| **maxRetries**    | int | max retries in case of fail the request to frontegg api | 
| **disableCors**  | boolean | if cors enabled adds frontegg api appropriate headers to response |
| **cookieDomainRewrite**   | string | overrides domain from coolies |

#### Frontegg Urls and WhiteLists

Frontegg Urls and Whitelist base configuration is defined in config modules readme file.
For spring based application sdk provides `ConfigProvider` and `WhiteListProvider` beans for these configurations.
To provide custom `configProviders` you need to create/override `configProvider` and  `whiteListProvider` beans.

The order of configProvider loader is. 
- yaml 
- environment variables
- system variables
- default 

example of yaml 
```yaml
frontegg:
    config:
        urls:
          baseUrl: https://api.frontegg.com
          authenticationService: /vendors/auth/token
          auditsService: /audits/
          notificationService: /notification/
          tenantsService: /tenants/
          metadataService: /metadata/
          teamService: /team
          eventService: /event
          identityService: /identity
```

#### Version

frontegg library version - `0.0.1`

| Dependencies       | Version |
|-------------------|:---:|
| **spring-boot**    | 1.4.x | 
| **spring framework version**    | 4.x.x | 
| **java-servlet-api**  | 3.1.0 |
