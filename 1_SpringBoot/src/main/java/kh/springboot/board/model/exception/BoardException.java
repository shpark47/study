package kh.springboot.board.model.exception;

public class BoardException extends RuntimeException {
    public BoardException() {
    }

    public BoardException(String message) {
        super(message);
    }
}
