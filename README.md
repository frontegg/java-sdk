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
        configuration.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization", "x-frontegg-source"));
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

