package filters;

import auth.VerifiedJwt;
import play.libs.typedmap.TypedKey;

public class Attrs {
    public static final TypedKey<VerifiedJwt> VERIFIED_JWT = TypedKey.create("verifiedJwt");
}
