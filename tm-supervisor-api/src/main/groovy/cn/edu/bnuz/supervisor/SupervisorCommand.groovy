package cn.edu.bnuz.supervisor

/**
 * Created by jerry on 2016/12/27.
 */
class SupervisorCommand {
    Integer supervisorId
    String userId
    Integer termId
    Integer roleType
    String departmentId
    def tostring(){
        return "supervisorId:${supervisorId},userId:${userId},termId:${termId},observerType:${roleType};departmentId:${departmentId}"
    }

}
