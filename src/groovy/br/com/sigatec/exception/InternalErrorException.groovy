package br.com.sigatec.exception

/**
 * Created by tinguan on 03/03/16.
 */
class InternalErrorException extends SigaException{
    String code = 500
    String errorMessage = "Internal Error"

    public InternalErrorException() {
    }

    public InternalErrorException(String message) {
        super(message);
    }
}
