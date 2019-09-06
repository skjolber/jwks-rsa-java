package com.auth0.jwk;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

class MockURLStreamHandlerFactory implements URLStreamHandlerFactory {

    private static MockURLStreamHandlerFactory factory = new MockURLStreamHandlerFactory();
    
    public static MockURLStreamHandlerFactory getInstance() {
        return factory;
    }
    
    static {
        // Although somewhat of a hack, this approach gets the job done - this method can 
        // only be called once per virtual machine, but that is sufficient for now.

        URL.setURLStreamHandlerFactory(factory);
    }
    
    // The weak reference is just a safeguard against objects not being released
    // for garbage collection
    private WeakReference<URLConnection> value;

    public void setConnection(URLConnection urlConnection) {
        this.value = new WeakReference<URLConnection>(urlConnection);
    }

    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        return "mock".equals(protocol) ? new URLStreamHandler() {
            protected URLConnection openConnection(URL url) throws IOException {
                if(value != null) {
                    try {
                        return value.get();
                    } finally {
                        value.clear();
                    }
                }
                return null;
            }
        } : null;
    }
}