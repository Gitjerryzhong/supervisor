package cn.edu.bnuz.supervisor

class ObservationPriority {
    String teacherId
    String teacherName
    String departmentName
    String academicTitle
    String isnew
    String hasSupervisor
    static mapping = {
        table 'dv_observation_priority'
    }
}
