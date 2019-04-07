package auth;

import play.libs.F;

public interface JwtValidator {
    F.Either<Error, VerifiedJwt> verify(String token);

    enum Error {
        ERR_INVALID_SIGNATURE_OR_CLAIM
    }
}
