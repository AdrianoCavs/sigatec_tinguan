package sigatec

class Student {

    String name
    Institution institution


    static constraints = {
        name(nullable: false, blank: false)
        institution(nullable: false, blank: false)
    }
}
