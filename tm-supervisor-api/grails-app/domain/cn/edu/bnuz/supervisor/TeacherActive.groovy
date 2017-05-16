package cn.edu.bnuz.supervisor

class TeacherActive {
    String teacherId
    String teacherName
    String departmentName
    String academicTitle
    String isnew
    String hasSupervisor
    static mapping = {
        table 'dv_teacher_active'
    }
}
