package org.tuanit.config;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.v2.ApacheHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tuanit.TrustAllCertsManager;

import java.net.Proxy;
import java.security.GeneralSecurityException;

@Configuration
public class MainConfig {
    //@Autowired
    //TrustAllCertsManager trustAllCertsManager;

    //@Bean
    //public void setup() {
    //    trustAllCertsManager.disableSSLVerification();
    //}

    @Bean
    public NetHttpTransport netHttpTransport() throws GeneralSecurityException {
        NetHttpTransport.Builder httpTransportBuilder = new NetHttpTransport.Builder();
        httpTransportBuilder.doNotValidateCertificate();
        return httpTransportBuilder.build();
    }

    private HttpTransport buildTransport(String host, int port, String username, String password) {
        HttpClientBuilder clientBuilder = ApacheHttpTransport.newDefaultHttpClientBuilder();
        HttpHost proxy = new HttpHost(host, port);
        clientBuilder.setProxy(proxy);
        if (username != null && password != null) {
            CredentialsProvider proxyCredentialsProvider = new BasicCredentialsProvider();
            proxyCredentialsProvider.setCredentials(new AuthScope(host, port),
                    new UsernamePasswordCredentials(username, password));
            clientBuilder.setDefaultCredentialsProvider(proxyCredentialsProvider);
        }
        clientBuilder.setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy());
        clientBuilder.setRoutePlanner(new DefaultProxyRoutePlanner(proxy)); // The magic happens here
        return new ApacheHttpTransport(clientBuilder.build());
    }

}
