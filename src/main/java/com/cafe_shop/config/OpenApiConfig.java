package com.cafe_shop.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Cafe Shop API",
                version = "v1",
                description = """
                        All endpoints in one page. Click **Authorize**, enter: `Bearer <accessToken>` \
                        (token is saved in the browser).

                        1. `POST /api/auth/register` or `POST /api/auth/login`
                        2. Copy `accessToken` from response `data`
                        3. Authorize once, then try any API
                        """
        ),
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {
}
