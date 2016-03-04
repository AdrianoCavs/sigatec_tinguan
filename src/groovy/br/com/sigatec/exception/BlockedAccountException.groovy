package br.com.sigatec.exception

/**
 * Created by tinguan on 03/03/16.
 */
class BlockedAccountException extends SigaException{
    String code = 403
    String errorMessage = "Forbidden"

    public BlockedAccountException() {
    }

    public BlockedAccountException(String message) {
        super(message);
    }
}
