package grails.plugin.springsecurity.rest

import com.nimbusds.jose.EncryptionMethod
import com.nimbusds.jose.JWEAlgorithm
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.crypto.MACSigner
import grails.plugin.springsecurity.rest.token.generation.jwt.AbstractJwtTokenGenerator
import grails.plugin.springsecurity.rest.token.generation.jwt.DefaultRSAKeyProvider
import grails.plugin.springsecurity.rest.token.generation.jwt.EncryptedJwtTokenGenerator
import grails.plugin.springsecurity.rest.token.generation.jwt.IssuerClaimProvider
import grails.plugin.springsecurity.rest.token.generation.jwt.RSAKeyProvider
import grails.plugin.springsecurity.rest.token.generation.jwt.SignedJwtTokenGenerator
import grails.plugin.springsecurity.rest.token.storage.jwt.JwtTokenStorageService
import grails.spring.BeanBuilder

trait TokenGeneratorSupport {

    SignedJwtTokenGenerator setupSignedJwtTokenGenerator() {
        String secret = 'foobar'*10
        def jwtTokenStorageService = new JwtTokenStorageService(jwtService: new JwtService(jwtSecret: secret))
        return new SignedJwtTokenGenerator(defaultExpiration: 3600, jwtSecret: secret, signer: new MACSigner(secret), jwtTokenStorageService: jwtTokenStorageService, customClaimProviders: [], jwsAlgorithm: JWSAlgorithm.HS256)
    }

    EncryptedJwtTokenGenerator setupEncryptedJwtTokenGenerator() {
        RSAKeyProvider keyProvider = new DefaultRSAKeyProvider()
        def jwtTokenStorageService = new JwtTokenStorageService(jwtService: new JwtService(keyProvider: keyProvider))
        return new EncryptedJwtTokenGenerator(defaultExpiration: 3600, jwtTokenStorageService: jwtTokenStorageService, keyProvider: keyProvider, customClaimProviders: [], jweAlgorithm: JWEAlgorithm.RSA_OAEP, encryptionMethod: EncryptionMethod.A128GCM)
    }

}