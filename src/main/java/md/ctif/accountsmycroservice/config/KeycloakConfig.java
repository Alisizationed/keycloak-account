package md.ctif.accountsmycroservice.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

@Configuration
public class KeycloakConfig {
    @Bean
    public Keycloak keycloak() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) { }
                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) { }
                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() { return new java.security.cert.X509Certificate[0]; }
                }
        };

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

        HostnameVerifier allHostsValid = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        ResteasyClient resteasyClient = (ResteasyClient) ResteasyClientBuilder.newBuilder()
                .sslContext(sslContext)
                .hostnameVerifier(allHostsValid)
                .build();

        return KeycloakBuilder.builder()
                .serverUrl("https://www.mykeycloak.com:8040/auth")
                .realm("recipe-app")
                .clientId("second-client")
                .clientSecret("FpGjNzJDdMjr6sijBvMKk1DeSN61olkx")
                .grantType(OAuth2Constants.PASSWORD)
                .username("danu.anastasia@ceiti.md")
                .password("password")
                .resteasyClient(resteasyClient)
                .build();
    }
}
