package cn.edu.bnuz.supervisor

import cn.edu.bnuz.bell.master.TermService
import cn.edu.bnuz.bell.organization.Teacher
import grails.transaction.Transactional

@Transactional
class SupervisorDepartmentService {
    TermService termService
    def messageSource

    def list(String departmentId, String roleType) {
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
where s.roleType = r.id and d.id = :departmentId and r.name = :roleType
''',[departmentId:departmentId, roleType: roleType]
    }

    def findTeacher(String query, String departmentId) {
        Teacher.executeQuery '''
select new Map(
  t.id as id,
  t.name as name,
  d.name as department
)
from Teacher t
join t.department d
where t.atSchool = true and d.id = :departmentId
and (t.id like :query or t.name like :query)
''', [query: "%${query}%", departmentId: departmentId]
    }

    def groupBySupervisor(String departmentId) {
        def term = termService.activeTerm

        def type =messageSource.getMessage("main.supervisor.college",null, Locale.CHINA)
        def result =SupervisorLectureRecord.executeQuery '''
select new map(
  supervisor.name as supervisor,
  count(*) as supervisorTimes,
  sum(form.totalSection) as totalSection
)
from SupervisorLectureRecord form
join form.supervisor supervisor
join form.taskSchedule schedule
join form.supervisorRole supervisorRole
join schedule.task task
join task.courseClass courseClass
join supervisor.department department
where form.status > 0
  and supervisorRole.name = :type
  and courseClass.term.id = :termId
  and department.id = :departmentId
group by supervisor
''', [termId: term.id, type: type, departmentId: departmentId]
        return [
                list: result,
        ]

    }
}
