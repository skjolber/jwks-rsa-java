package com.auth0.jwk;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TestRevokingKey {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldNotReturnRevokedKeys() throws IOException, JwkException {
        
        URLConnection first = mock(URLConnection.class);
        when(first.getInputStream())
            .thenReturn(getClass().getResourceAsStream("/revoke/jwkA.json"));

        MockURLStreamHandlerFactory.getInstance().setConnection(first);

        String keyA = "NkJCQzIyQzRBMEU4NjhGNUU4MzU4RkY0M0ZDQzkwOUQ0Q0VGNUMwQg";
        String keyB = "RUVBOTVEMEZBMTA5NDAzNEQzNTZGNzMyMTI4MzU1RkNFQzhCQTM0Mg";
        
        URL urlToJwks = new URL("mock://my-jwks.json");
        JwkProvider build = new JwkProviderBuilder(urlToJwks).build();        
        
        Jwk a = build.get(keyA);
        assertNotNull(a);

        URLConnection second = mock(URLConnection.class);
        when(second.getInputStream())
            .thenReturn(getClass().getResourceAsStream("/revoke/jwkB.json"));

        MockURLStreamHandlerFactory.getInstance().setConnection(second);
        
        Jwk b = build.get(keyB);
        assertNotNull(b);

        Jwk revokedA = build.get(keyA);
        assertNull("Expected key " + keyA + " revoked, got " + revokedA, revokedA);
    }
}
