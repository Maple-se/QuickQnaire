package com.maple.quickqnairebackend.config;


import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
/**
 * Created by zong chang on 2024/12/22 15:53
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */
@OpenAPIDefinition(
        info = @Info(
                title = "QuickQnaire-API接口文档",
                version = "1.0",
                description = "这是一个快速问卷调查的API接口文档，方便配合前端开发",
                contact = @Contact(name = "Maple")
        ),
        security = @SecurityRequirement(name = "JWT"),
        externalDocs = @ExternalDocumentation(description = "参考文档",
                url = "https://github.com/swagger-api/swagger-core/wiki/Swagger-2.X---Annotations"
        )
)
@SecurityScheme(type = SecuritySchemeType.HTTP, name = "JWT", scheme = "bearer", in = SecuritySchemeIn.HEADER)
public class SpringDocConfig {
}
