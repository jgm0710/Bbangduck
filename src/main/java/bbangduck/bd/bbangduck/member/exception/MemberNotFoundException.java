package bbangduck.bd.bbangduck.member.exception;

public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException() {
        super("해당 회원이 존재하지 않습니다.");
    }

    public MemberNotFoundException(String email) {
        super("해당 Email 을 통해 조회되는 회원이 존재하지 않습니다. Email : "+email);
    }
}
