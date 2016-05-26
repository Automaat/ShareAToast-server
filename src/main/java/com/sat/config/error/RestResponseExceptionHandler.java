package com.sat.config.error;


import com.sat.web.BadRequestException;
import com.sat.web.ErrorInfo;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({BadRequestException.class})
    public ResponseEntity<ErrorInfo> handleBadRequestException(BadRequestException ex) {
        return new ResponseEntity<>(ex.getInfo(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ObjectOptimisticLockingFailureException.class, OptimisticLockingFailureException.class,
            DataIntegrityViolationException.class})
    public ResponseEntity<ErrorInfo> handleConflict(Exception ex) {
        return new ResponseEntity<>(ErrorInfo.CONFLICT, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<?> handleOtherException(Exception ex) {
        //probably this is redundant but it's added for safety purpose
        if (ex instanceof BadRequestException) {
            return handleBadRequestException((BadRequestException) ex);
        } else if (ex instanceof ObjectOptimisticLockingFailureException || ex instanceof OptimisticLockingFailureException || ex instanceof DataIntegrityViolationException) {
            return handleConflict(ex);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
