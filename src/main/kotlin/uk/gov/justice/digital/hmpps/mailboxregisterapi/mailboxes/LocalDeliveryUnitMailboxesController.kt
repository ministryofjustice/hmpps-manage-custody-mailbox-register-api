package uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.hmpps.kotlin.common.ErrorResponse
import java.util.UUID

@RestController
@PreAuthorize("hasRole('MAILBOX_REGISTER_ADMIN')")
@RequestMapping(value = ["/local-delivery-unit-mailboxes"], produces = ["application/json"])
class LocalDeliveryUnitMailboxesController(
  private val localDeliveryUnitMailboxService: LocalDeliveryUnitMailboxService,
) {
  @PostMapping(value = [""])
  @ResponseStatus(code = HttpStatus.CREATED)
  @Operation(
    summary = "Creates a new local delivery unit mailbox",
    description = "Creates a new local delivery unit mailbox",
    security = [SecurityRequirement(name = "mailbox-register-api-ui-role")],
    responses = [
      ApiResponse(responseCode = "201", description = "The local delivery unit mailbox was created"),
      ApiResponse(
        responseCode = "400",
        description = "Bad request",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ValidationErrorResponse::class))],
      ),
      ApiResponse(
        responseCode = "401",
        description = "Unauthorized access to this endpoint",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
      ApiResponse(
        responseCode = "403",
        description = "Forbidden access to this endpoint",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
    ],
  )
  fun create(@Valid @RequestBody newMailbox: LocalDeliveryUnitMailboxForm) =
    localDeliveryUnitMailboxService.createMailbox(newMailbox)

  @GetMapping(value = [""])
  @ResponseStatus(code = HttpStatus.OK)
  @Operation(
    summary = "Lists all local delivery unit mailboxes",
    description = "Lists all local delivery unit mailboxes",
    security = [SecurityRequirement(name = "mailbox-register-api-ui-role")],
    responses = [
      ApiResponse(responseCode = "200", description = "A list of local delivery unit mailboxes"),
      ApiResponse(
        responseCode = "401",
        description = "Unauthorized access to this endpoint",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
      ApiResponse(
        responseCode = "403",
        description = "Forbidden access to this endpoint",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
    ],
  )
  fun list(): List<LocalDeliveryUnitMailbox> = localDeliveryUnitMailboxService.listMailboxes()

  @GetMapping(value = ["/{id}"])
  @ResponseStatus(code = HttpStatus.OK)
  @Operation(
    summary = "Gets a local delivery unit mailbox by ID",
    description = "Gets a local delivery unit mailbox by ID",
    security = [SecurityRequirement(name = "mailbox-register-api-ui-role")],
    responses = [
      ApiResponse(responseCode = "200", description = "The local delivery unit mailbox"),
      ApiResponse(
        responseCode = "401",
        description = "Unauthorized access to this endpoint",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
      ApiResponse(
        responseCode = "403",
        description = "Forbidden access to this endpoint",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
      ApiResponse(
        responseCode = "404",
        description = "The local delivery unit mailbox was not found",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
    ],
  )
  fun getById(@PathVariable(name = "id") id: UUID) = localDeliveryUnitMailboxService.getMailboxById(id)

  @PutMapping(value = ["/{id}"])
  @ResponseStatus(code = HttpStatus.OK)
  @Operation(
    summary = "Updates a local delivery unit mailbox",
    description = "Updates a local delivery unit mailbox",
    security = [SecurityRequirement(name = "mailbox-register-api-ui-role")],
    responses = [
      ApiResponse(responseCode = "200", description = "The local delivery unit mailbox was updated"),
      ApiResponse(
        responseCode = "400",
        description = "Bad request",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ValidationErrorResponse::class))],
      ),
      ApiResponse(
        responseCode = "401",
        description = "Unauthorized access to this endpoint",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
      ApiResponse(
        responseCode = "403",
        description = "Forbidden access to this endpoint",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
      ApiResponse(
        responseCode = "404",
        description = "The local delivery unit mailbox was not found",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
    ],
  )
  fun update(@PathVariable(name = "id") id: UUID, @Valid @RequestBody mailbox: LocalDeliveryUnitMailboxForm) =
    localDeliveryUnitMailboxService.updateMailbox(id, mailbox)

  @DeleteMapping(value = ["/{id}"])
  @ResponseStatus(code = HttpStatus.OK)
  @Operation(
    summary = "Deletes a local delivery unit mailbox",
    description = "Deletes a local delivery unit mailbox",
    security = [SecurityRequirement(name = "mailbox-register-api-ui-role")],
    responses = [
      ApiResponse(responseCode = "200", description = "The local delivery unit mailbox was deleted"),
      ApiResponse(
        responseCode = "401",
        description = "Unauthorized access to this endpoint",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
      ApiResponse(
        responseCode = "403",
        description = "Forbidden access to this endpoint",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
      ApiResponse(
        responseCode = "404",
        description = "The local delivery unit mailbox was not found",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
    ],
  )
  fun delete(@PathVariable(name = "id") id: UUID) = localDeliveryUnitMailboxService.deleteMailbox(id)
}
