package sigatec

class Institution {

    String name

    static constraints = {
        name(nullable: false, blank: false)
    }
}
