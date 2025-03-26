package uk.gov.justice.digital.hmpps.mailboxregisterapi.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.info.BuildProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import uk.gov.justice.digital.hmpps.mailboxregisterapi.ROLE_MAILBOXES_RO
import uk.gov.justice.digital.hmpps.mailboxregisterapi.ROLE_SYSTEM_ADMIN

@Configuration
class OpenApiConfiguration(buildProperties: BuildProperties) {
  private val version: String = buildProperties.version

  @Value("\${info.app.name}")
  private lateinit var appName: String

  @Value("\${info.app.team.name}")
  private lateinit var teamName: String

  @Value("\${info.app.team.email}")
  private lateinit var teamEmail: String

  @Bean
  fun customOpenAPI(): OpenAPI = OpenAPI()
    .servers(
      listOf(
        Server().url("https://manage-custody-mailbox-register-api-dev.hmpps.service.justice.gov.uk").description("Development"),
        Server().url("http://localhost:8080").description("Local"),
      ),
    )
    .info(
      Info().title(appName).version(version)
        .contact(Contact().name(teamName).email(teamEmail)),
    )
    .components(
      Components().addSecuritySchemes(
        "system-admin-role",
        SecurityScheme().addBearerJwtRequirement(ROLE_SYSTEM_ADMIN),
      ).addSecuritySchemes(
        "mailboxes-ro-role",
        SecurityScheme().addBearerJwtRequirement(ROLE_MAILBOXES_RO),
      ),
    )
    .addSecurityItem(
      SecurityRequirement().addList("system-admin-role", listOf("read", "write")).addList("mailboxes-ro-role", listOf("read")),
    )
}

private fun SecurityScheme.addBearerJwtRequirement(role: String): SecurityScheme = type(SecurityScheme.Type.HTTP)
  .scheme("bearer")
  .bearerFormat("JWT")
  .`in`(SecurityScheme.In.HEADER)
  .name("Authorization")
  .description("A HMPPS Auth access token with the `$role` role.")
