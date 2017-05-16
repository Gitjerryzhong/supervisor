package cn.edu.bnuz.supervisor

class SupervisePublic {
    Boolean isLegacy
    String teacherId
    String teacherName
    String supervisorDate
    String evaluateLevel
    String typeName
    Integer termId
    String  departmentName
    String courseName
    String courseOtherInfo


    static mapping = {
        table 'dv_supervise_public'
    }
}
