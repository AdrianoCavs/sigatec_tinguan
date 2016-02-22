package br.com.sigatec.exception

/**
 * Created by tinguan on 22/02/16.
 */
class InvalidPasswordException extends SigaException {
    String code = 401
    String errorMessage = "Invalid Password"

    public InvalidPasswordException() {
    }

    public InvalidPasswordException(String message) {
        super(message);
    }
}