package cn.edu.bnuz.supervisor

class ObservationPublic {
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
        table 'dv_observation_public'
    }
}
