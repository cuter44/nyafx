package com.github.cuter44.nyafx.ssl;

import java.util.List;
import java.util.ArrayList;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.IOException;

import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.SSLContext;
import java.security.cert.CertificateException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.KeyManagementException;

/**
 * Import manual provided certificates.
 * Then overriding the system default, to avoid SSL validation error.
 * Or exporting them as KeyStore/TrustManagers for further use.
 * These are particularly useful while deploying
 * Notice that this class is NOT thread-safe.
 */
public class CertificateLoader
{
    protected ArrayList<Certificate> certificates;

    public CertificateLoader()
    {
        this.certificates = new ArrayList<Certificate>();

        return;
    }

    /**
     * @return count of certificates loaded.
     */
    public int size()
    {
        return(
            this.certificates.size()
        );
    }

    /** load X.509 certificates from resource path
     * @return this
     * @throws CertificateException if format of input file is incorrect
     */
    public CertificateLoader loadX509CertResource(String... resourcePaths)
        throws CertificateException, IOException
    {
        for (String resourcePath:resourcePaths)
        {
            InputStream stream = this.getClass()
                .getResourceAsStream(resourcePath);

            this.loadX509CertStream(stream);
        }

        return(this);
    }

    //public CertificateLoader loadCertFile(File file)
    //{
        //FileInputStream

        //return(this);
    //}

    public CertificateLoader loadX509CertStream(InputStream stream)
        throws CertificateException, IOException
    {
        CertificateFactory crtFactory = CertificateFactory.getInstance("X.509");
        BufferedInputStream buffer = new BufferedInputStream(stream);

        while (buffer.available() > 0)
            this.certificates.add(
                crtFactory.generateCertificate(buffer)
            );

        buffer.close();

        return(this);
    }

    //public CertificateLoader loadKeyStore(KeyStore keyStore)
    //{

        //return(this);
    //}

    //public CertificateLoader loadSystemDefault()
    //{

        //return(this);
    //}

    public List<Certificate> asCertificates()
    {
        return(
            (ArrayList<Certificate>)this.certificates.clone()
        );
    }

    /** Export loaded certificate as KeyStore
     * @throws KeyStoreException rarely occur
     * @throws IOException never occur
     * @throws NoSuchAlgorithmException never occur
     */
    public KeyStore asKeyStore()
        throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException
    {
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, null);

        for (int i=0; i<this.certificates.size(); i++)
            keyStore.setCertificateEntry("nyafx-cert-"+i, this.certificates.get(i));

        return(keyStore);
    }

    /** Export loaded certificates as TrustManager[]
     * @throws KeyStoreException rarely occur.
     * @throws NoSuchAlgorithmException never occur.
     * @throws IOException never occur.
     * @throws CertificateException if one of certificate is invalid, e.g. expired.
     */
    public TrustManager[] asTrustManagers()
        throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException
    {
        TrustManagerFactory tmFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmFactory.init(this.asKeyStore());

        return(tmFactory.getTrustManagers());
    }

    /**
     * Noticed that this function will override system default certificates loaded from $JAVA_HOME/lib/security/jssecacerts or $JAVA_HOME/lib/security/cacerts
     * To include them, you must call loadSystemDefault() (which is not implemented yet) before calling this function.
     * Besides, if code otherwhere also calling a SSLContext.setDefault(). They will compete with each other.
     * Currently there is no method to recover certificates from a ecapsulated SSLContext, which make it impossible to merge certicates with the already-loaded.
     * @param algorithm SSL version, can be SSLv3, TLSv1(default), etc. determined by what your target support.
     * @throws NoSuchAlgorithmException if algorithm not supported.
     * @throws KeyStoreException rarely occur.
     * @throws KeyManagementException rarely occur.
     * @throws IOException rarely occur.
     * @throws CertificateException if one of certificate is invalid, e.g. expired.
     */
    public CertificateLoader overrideDefaultSSLContext(String algorithm)
        throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException, CertificateException
    {
        SSLContext sslCtx = SSLContext.getInstance(algorithm);
        sslCtx.init(
            null,
            this.asTrustManagers(),
            null
        );

        SSLContext.setDefault(sslCtx);

        return(this);
    }



}
