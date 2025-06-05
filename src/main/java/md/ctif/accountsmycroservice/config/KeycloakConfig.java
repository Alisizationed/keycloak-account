package md.ctif.accountsmycroservice.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

@Configuration
public class KeycloakConfig {
    @Value("keycloak.server-url")
    private String keycloakUrl;
    @Value("keycloak.realm")
    private String realm;
    @Value("keycloak.admin-client-id")
    private String keycloakClientId;
    @Value("keycloak.admin-client-secret")
    private String keycloakClientSecret;
    @Value("keycloak.admin-user-username")
    private String keycloakUsername;
    @Value("keycloak.admin-user-password")
    private String keycloakPassword;
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
                .serverUrl(keycloakUrl)
                .realm(realm)
                .clientId(keycloakClientId)
                .clientSecret(keycloakClientSecret)
                .grantType(OAuth2Constants.PASSWORD)
                .username(keycloakUsername)
                .password(keycloakPassword)
                .resteasyClient(resteasyClient)
                .build();
    }
}
