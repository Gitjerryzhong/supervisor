package cn.edu.bnuz.supervisor

class SuperviseCount {
    String teacherId
    String teacherName
    String  departmentName
    Integer superviseCount

    static mapping = {
        table 'dv_supervise_count'
    }
}
