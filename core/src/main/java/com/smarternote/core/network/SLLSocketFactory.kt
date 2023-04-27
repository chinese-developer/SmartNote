@file:Suppress("unused")

package com.smarternote.core.network

import android.annotation.SuppressLint
import java.io.IOException
import java.io.InputStream
import java.security.KeyManagementException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.KeyManager
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager


object SLLSocketFactory {
  /**
   * 创建SSLParams
   *
   * @param certificates 本地证书流
   * @param bksFile 用于双向验证，本地bks证书
   * @param password 本地证书密码
   * @return 创建的SSLParams
   */
  fun getSslSocketFactory(
    certificates: Array<InputStream?>,
    bksFile: InputStream?,
    password: String?
  ): SSLParams {
    val sslParams = SSLParams()
    return try {
      val trustManagers = prepareTrustManager(*certificates)
      val keyManagers = prepareKeyManager(bksFile, password)
      val sslContext = SSLContext.getInstance("TLS")
      val trustManager: X509TrustManager = if (trustManagers != null) {
        SafeTrustManager(chooseTrustManager(trustManagers))
      } else {
        UnSafeTrustManager()
      }
      sslContext.init(keyManagers, arrayOf<TrustManager>(trustManager), null)
      sslParams.sSLSocketFactory = sslContext.socketFactory
      sslParams.trustManager = trustManager
      sslParams
    } catch (e: NoSuchAlgorithmException) {
      throw AssertionError(e)
    } catch (e: KeyManagementException) {
      throw AssertionError(e)
    } catch (e: KeyStoreException) {
      throw AssertionError(e)
    }
  }

  private fun prepareTrustManager(vararg certificates: InputStream?): Array<TrustManager>? {
    if (certificates.isEmpty()) return null
    try {
      val certificateFactory = CertificateFactory.getInstance("X.509")
      val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
      keyStore.load(null)
      for ((index, certificate) in certificates.withIndex()) {
        val certificateAlias = (index).toString()
        keyStore.setCertificateEntry(
          certificateAlias,
          certificateFactory.generateCertificate(certificate)
        )
        try {
          certificate?.close()
        } catch (e: IOException) {
          e.printStackTrace()
        }
      }
      val trustManagerFactory: TrustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
      trustManagerFactory.init(keyStore)
      return trustManagerFactory.trustManagers
    } catch (e: Exception) {
      e.printStackTrace()
    }
    return null
  }

  private fun prepareKeyManager(bksFile: InputStream?, password: String?): Array<KeyManager>? {
    try {
      if (bksFile == null || password == null) return null
      val clientKeyStore = KeyStore.getInstance("BKS")
      clientKeyStore.load(bksFile, password.toCharArray())
      val keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
      keyManagerFactory.init(clientKeyStore, password.toCharArray())
      return keyManagerFactory.keyManagers
    } catch (e: Exception) {
      e.printStackTrace()
    }
    return null
  }

  private fun chooseTrustManager(trustManagers: Array<TrustManager>): X509TrustManager? {
    for (trustManager in trustManagers) {
      if (trustManager is X509TrustManager) {
        return trustManager
      }
    }
    return null
  }

  class SSLParams {
    lateinit var sSLSocketFactory: SSLSocketFactory
    lateinit var trustManager: X509TrustManager
  }

  private class UnSafeHostnameVerifier : HostnameVerifier {
    @SuppressLint("BadHostnameVerifier") override fun verify(
      hostname: String,
      session: SSLSession
    ): Boolean {
      return true
    }
  }

  @SuppressLint("CustomX509TrustManager")
  private class UnSafeTrustManager : X509TrustManager {
    @SuppressLint("TrustAllX509TrustManager")
    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
    }

    @SuppressLint("TrustAllX509TrustManager")
    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
    }

    override fun getAcceptedIssuers(): Array<X509Certificate> {
      return arrayOf()
    }
  }

  @SuppressLint("CustomX509TrustManager")
  private class SafeTrustManager(localTrustManager: X509TrustManager?) :
    X509TrustManager {
    private val defaultTrustManager: X509TrustManager?
    private val localTrustManager: X509TrustManager?

    init {
      val var4 = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
      var4.init(null as KeyStore?)
      defaultTrustManager = chooseTrustManager(var4.trustManagers)
      this.localTrustManager = localTrustManager
    }

    @SuppressLint("TrustAllX509TrustManager")
    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
    }

    @Throws(CertificateException::class)
    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
      try {
        defaultTrustManager!!.checkServerTrusted(chain, authType)
      } catch (ce: CertificateException) {
        localTrustManager!!.checkServerTrusted(chain, authType)
      }
    }

    override fun getAcceptedIssuers(): Array<X509Certificate> {
      return arrayOf()
    }
  }
}