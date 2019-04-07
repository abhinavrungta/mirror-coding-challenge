package auth;

import com.auth0.jwt.interfaces.DecodedJWT;
import play.libs.Json;

import java.util.Date;

public class VerifiedJwt {
    private String header;
    private String payload;
    private String issuer;
    private String id;
    private Date expiresAt;

    public VerifiedJwt(DecodedJWT decodedJWT) {
        this.header = decodedJWT.getHeader();
        this.payload = decodedJWT.getPayload();
        this.issuer = decodedJWT.getIssuer();
        this.expiresAt = decodedJWT.getExpiresAt();
        this.id = decodedJWT.getClaim("id").asString();
    }

    public String getHeader() {
        return header;
    }

    public String getPayload() {
        return payload;
    }

    public String getIssuer() {
        return issuer;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public String getId() {
        return id;
    }

    public String toString() {
        return Json.toJson(this).toString();
    }
}