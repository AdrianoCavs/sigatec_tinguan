package sigatec

class ClassSubject {

    String subject

    static constraints = {
        subject(nullable: false, blank: false)
    }
}
