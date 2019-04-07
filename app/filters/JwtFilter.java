package filters;

import auth.JwtValidator;
import auth.VerifiedJwt;
import play.libs.F;
import play.libs.streams.Accumulator;
import play.mvc.EssentialAction;
import play.mvc.EssentialFilter;
import play.routing.HandlerDef;
import play.routing.Router;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;

import static play.mvc.Results.forbidden;


@Singleton
public class JwtFilter extends EssentialFilter {
    private static final String ROUTE_MODIFIER_NO_JWT_FILTER_TAG = "noJwtFilter";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";
    private static final String ERR_AUTHORIZATION_HEADER = "ERR_AUTHORIZATION_HEADER";
    private JwtValidator jwtValidator;

    @Inject
    public JwtFilter(JwtValidator jwtValidator) {
        this.jwtValidator = jwtValidator;
    }

    @Override
    public EssentialAction apply(EssentialAction next) {
        return EssentialAction.of(request -> {

            if (request.attrs().containsKey(Router.Attrs.HANDLER_DEF)) {
                HandlerDef handler = request.attrs().get(Router.Attrs.HANDLER_DEF);
                List<String> modifiers = handler.getModifiers();

                if (modifiers.contains(ROUTE_MODIFIER_NO_JWT_FILTER_TAG)) {
                    return next.apply(request);
                }
            }

            Optional<String> authHeader = request.getHeaders().get(HEADER_AUTHORIZATION);

            if (!authHeader.filter(ah -> ah.contains(BEARER)).isPresent()) {
                return Accumulator.done(forbidden(ERR_AUTHORIZATION_HEADER));
            }

            String token = authHeader.map(ah -> ah.replace(BEARER, "")).orElse("");
            F.Either<JwtValidator.Error, VerifiedJwt> res = jwtValidator.verify(token);

            if (res.left.isPresent()) {
                return Accumulator.done(forbidden(res.left.get().toString()));
            }

            return next.apply(request
                    .withAttrs(request.attrs()
                            .put(Attrs.VERIFIED_JWT, res.right.get())
                    ));
        });
    }
}