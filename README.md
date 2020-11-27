# java-sdk

![alt text](https://fronteggstuff.blob.core.windows.net/frongegg-logos/logo-transparent.png)

Frontegg is a web platform where SaaS companies can set up their fully managed, scalable and brand aware - SaaS features and integrate them into their SaaS portals in up to 5 lines of code.

## Usage

#### Spring Boot Application

add maven dependency 
```xml
<dependency>
    <groupId>com.frontegg</groupId>
    <artifactId>middleware-spring</artifactId>
    <version>x.x.x</version>
</dependency>
```

Create configuration class 

```java

@EnableFrontegg
@Configuration
public class CustomConfiguration extends FronteggConfigurerAdapter {
    
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

For local running you need to provide cors configuration

```java
@EnableFrontegg
@Configuration
public class CustomConfiguration extends FronteggConfigurerAdapter {
    
    @Override
    protected void configure(Frontegg frontegg) throws Exception {
        frontegg.cors().configurationSource(corsConfigurationSource());
    }


    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST", "PUT", "PATCH", "OPTION"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "x-frontegg-source"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
```

If you need to change base endpoint for frontegg you need to override the following method in your configuration.
default path is `/frontegg`.
```
    @Override
    protected String getPath() {
        return "/custompath";
    }
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

The order of whiteListProvider loader is.
- yaml
- default

example of yaml:
```yaml
frontegg:
  whitelist:
    - /metadata
```