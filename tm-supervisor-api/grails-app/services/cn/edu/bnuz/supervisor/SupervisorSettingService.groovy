package cn.edu.bnuz.supervisor

import cn.edu.bnuz.bell.http.NotFoundException
import cn.edu.bnuz.bell.master.Term
import cn.edu.bnuz.bell.organization.Teacher
import cn.edu.bnuz.bell.security.SecurityService
import grails.converters.JSON
import grails.transaction.Transactional

@Transactional
class SupervisorSettingService {
    SecurityService securityService

    def save(SupervisorCommand cmd) {
        Supervisor supervisor=Supervisor.get(cmd.supervisorId)
        if(supervisor){
            supervisor.setRoleType(cmd.roleType)
            supervisor.setTermId(cmd.termId)
        }else{
            def teacher = Teacher.get(cmd.userId)
            if(!teacher)  throw new NotFoundException()
            supervisor = Supervisor.findByTeacherAndTermIdAndRoleType(teacher,cmd.termId,cmd.roleType)
            if(supervisor) return null
            supervisor = new Supervisor(
                    teacher: teacher,
                    department: teacher.department,
                    termId: cmd.termId,
                    roleType: cmd.roleType
            )
        }
        supervisor?.save(flush:true)
    }

    def list() {
        Supervisor.executeQuery '''
select new Map(
  s.id as id,
  t.id as tId,
  t.name as tName,
  t.academicTitle as academicTitle,
  d.id as dId,
  d.name as dName,
  s.termId as termId,
  r.name as roleType
)
from Supervisor s join s.teacher t join s.department d,SupervisorRole r
where s.roleType = r.id
'''
    }


    def roleTypes(){
        SupervisorRole.executeQuery'''
select new Map(
  r.id as id,
  r.name as name
)
from SupervisorRole r
'''
    }

    def isCollegeSupervisor(String userId, Integer termId, String type){
        def result=Supervisor.executeQuery '''
select r.name
from Supervisor s join s.teacher t,SupervisorRole r
where s.roleType = r.id and s.termId = :termId and t.id = :userId
''',[userId:userId, termId: termId]
        return result ==[type]
    }

    def getSupervisorDepartment(String userId, Integer termId){
        Supervisor.executeQuery '''
select new map(
d.id as id,
d.name as name
)
from Supervisor s join s.teacher t join s.department d
where s.termId = :termId and t.id = :userId
''',[userId:userId, termId: termId]
    }

    def isAdmin(String userId){
        Supervisor supervisor=Supervisor.findByRoleType(0)
        return supervisor?.teacher.id == userId
    }

    def findRolesByUserIdAndTerm(String userId, Integer termId){
        Supervisor.executeQuery'''
select distinct new map(
role.id as id,
role.name as name
)
from Supervisor s, SupervisorRole role
where s.roleType = role.id and s.teacher.id = :userId and s.termId = :termId
''',[userId: userId,termId: termId]
    }

    def findAllRoles(){
        SupervisorRole.executeQuery'''
select new map(
role.id as id,
role.name as name
)
from SupervisorRole role
'''
    }

    def findCurrentSupervisors(Integer termId){
        def result= Supervisor.executeQuery'''
select distinct new map(
t.id as teacherId,
t.name as teacherName,
role.id as roleId,
role.name as roleName
)
from Supervisor s join s.teacher t, SupervisorRole role
where s.roleType = role.id and s.termId = :termId
''',[termId: termId]
        return result.groupBy {it.roleId}.entrySet()
    }

    def getSupervisorRole(String userId, Integer termId){
        Supervisor.executeQuery '''
select r.name
from Supervisor s join s.teacher t,SupervisorRole r
where s.roleType = r.id and s.termId = :termId and t.id = :userId
''',[userId:userId, termId: termId]
    }

    def getTerms(){
        Term.executeQuery'''
select DISTINCT t.id as termId
from Term t
order by t.id desc
'''
    }
    def delete(Long id){
        def form = Supervisor.get(id)
        if(form) {
            DelLog delLog = new DelLog(
                    userId:securityService.userId,
                    objName: form.class.name,
                    objId: form.id,
                    delDate: new Date(),
                    content: "${form as JSON}"
            )
            delLog.save()
            form.delete()
        }
    }

}
