package kh.springboot.member.model.exception;

public class MemberException extends RuntimeException {
    public MemberException() {}

    public MemberException(String s) {
        super(s);
    }
}
