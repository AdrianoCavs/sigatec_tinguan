package sigatec

class StudentGradeClassSubject {

    Student student
    ClassSubject classSubject
    double abscencePercentage
    double p1
    double p2
    double p3

    static constraints = {
        student(nullable: false, blank: false)
        classSubject(nullable: false, blank: false)
        abscencePercentage(nullable: false, blank: false)
        p1(nullable: false, blank: false)
        p2(nullable: false, blank: false)
        p3(nullable: false, blank: false)
    }
}
