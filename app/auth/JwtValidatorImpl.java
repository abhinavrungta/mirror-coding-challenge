package auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.typesafe.config.Config;
import play.libs.F;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class JwtValidatorImpl implements JwtValidator {
    private String secret;
    private JWTVerifier verifier;

    @Inject
    public JwtValidatorImpl(Config config) {
        this.secret = config.getString("play.http.secret.key");

        Algorithm algorithm = Algorithm.HMAC256(secret);
        verifier = JWT.require(algorithm)
                .withIssuer("Mirror")
                .build();
    }

    @Override
    public F.Either<Error, VerifiedJwt> verify(String token) {
        try {
            DecodedJWT jwt = verifier.verify(token);
            VerifiedJwt verifiedJwt = new VerifiedJwt(jwt);
            return F.Either.Right(verifiedJwt);
        } catch (JWTVerificationException exception) {
            return F.Either.Left(Error.ERR_INVALID_SIGNATURE_OR_CLAIM);
        }
    }
}
