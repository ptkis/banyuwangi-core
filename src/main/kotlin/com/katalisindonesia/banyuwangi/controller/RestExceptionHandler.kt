package com.katalisindonesia.banyuwangi.controller

import io.undertow.util.BadRequestException
import mu.KotlinLogging
import org.apache.commons.lang3.exception.ExceptionUtils
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.sql.SQLIntegrityConstraintViolationException
import javax.persistence.EntityNotFoundException
import javax.validation.ConstraintViolationException

private val log = KotlinLogging.logger {}

/*
 * Created with IntelliJ IDEA.
 * User: thomas
 * Date: 12/08/18
 * Time: 15.15
 * To change this template use File | Settings | File Templates.
 */
@ControllerAdvice
class RestExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(value = [EntityNotFoundException::class])
    protected fun handleNotFoundConflict(
        ex: EntityNotFoundException, request: WebRequest
    ): ResponseEntity<Any> {
        log.info("cannot found entity", ex)
        return handleExceptionInternal(
            ex, WebResponse(success = false, message = ex.message.orEmpty(), data=null),
            HttpHeaders(), HttpStatus.NOT_FOUND, request)
    }

    @ExceptionHandler(value = [DataIntegrityViolationException::class])
    protected fun handleDataIntegrityViolation(
        ex: DataIntegrityViolationException, request: WebRequest
    ): ResponseEntity<Any> {
        log.info("constraint violation error during $request", ex)

        return handleExceptionInternal(
            ex, WebResponse(success = false, message = ex.message?:"", data=null),
            HttpHeaders(), HttpStatus.BAD_REQUEST, request)
    }
    @ExceptionHandler(value = [ConstraintViolationException::class])
    protected fun handleConstraintViolation(
        ex: ConstraintViolationException, request: WebRequest
    ): ResponseEntity<Any> {
        log.info("constraint violation error during $request", ex)

        val msg = ex.constraintViolations.joinToString("; ") { it.message }

        return handleExceptionInternal(
            ex, WebResponse(success = false, message = msg, data=null),
            HttpHeaders(), HttpStatus.BAD_REQUEST, request)
    }
    @ExceptionHandler(value = [ SQLIntegrityConstraintViolationException::class])
    protected fun handleSqlConstraintViolation(
        ex: SQLIntegrityConstraintViolationException, request: WebRequest
    ): ResponseEntity<Any> {
        log.info("constraint violation error during $request", ex)

        return handleExceptionInternal(
            ex, WebResponse(success = false, message = ex.message?:"", data=null),
            HttpHeaders(), HttpStatus.BAD_REQUEST, request)
    }

    @ExceptionHandler(value = [AccessDeniedException::class])
    protected fun handleAccessDenied(
        ex: AccessDeniedException, request: WebRequest
    ): ResponseEntity<Any> {
        log.debug("access denied error during $request", ex)

        return handleExceptionInternal(
            ex, WebResponse(success = false, message = ex.message ?: "Access denied", data=null),
            HttpHeaders(), HttpStatus.FORBIDDEN, request)
    }

    @ExceptionHandler(value = [BadRequestException::class])
    protected fun handleBadRequest(
        ex: BadRequestException, request: WebRequest
    ): ResponseEntity<Any> {
        log.debug("bad request error during $request", ex)

        return handleExceptionInternal(
            ex, WebResponse(success = false, message = ex.message ?: "Bad Request", data=null),
            HttpHeaders(), HttpStatus.BAD_REQUEST, request)
    }

    @ExceptionHandler(value = [Exception::class])
    protected fun handleInternalConflict(
        ex: Exception, request: WebRequest
    ): ResponseEntity<Any> {
        if (ex is EntityNotFoundException) {
            return handleNotFoundConflict(ex, request)
        }
        if (ex is ConstraintViolationException) {
            return handleConstraintViolation(ex, request)
        }
        if (ex is DataIntegrityViolationException) {
            return handleDataIntegrityViolation(ex, request)
        }
        if (ex is SQLIntegrityConstraintViolationException) {
            return handleSqlConstraintViolation(ex, request)
        }
        if (ex is AccessDeniedException) {
            return handleAccessDenied(ex, request)
        }
        if (ex is BadRequestException) {
            return handleBadRequest(ex, request)
        }
        log.error("internal error during request $request", ex)
        return handleExceptionInternal(
            ex, WebResponse(success = false, message = ExceptionUtils.getRootCauseMessage(ex).orEmpty(), data=null),
            HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request)
    }
}
