package cn.edu.bnuz.supervisor

import cn.edu.bnuz.bell.master.TermService
import cn.edu.bnuz.bell.organization.Teacher
import grails.transaction.Transactional

@Transactional
class SupervisorDepartmentService {
    TermService termService
    def messageSource

    def list(String departmentId, String roleType) {
        Observer.executeQuery '''
select new Map(
  s.id as id,
  t.id as tId,
  t.name as tName,
  t.academicTitle as academicTitle,
  d.id as dId,
  d.name as dName,
  s.termId as termId,
  r.name as observerType
)
from Observer s join s.teacher t join s.department d,ObserverType r
where s.observerType = r.id and d.id = :departmentId and r.name = :observerType
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
        def result =ObservationForm.executeQuery '''
select new map(
  observer.name as observer,
  count(*) as supervisorTimes,
  sum(form.totalSection) as totalSection
)
from ObservationForm form
join form.observer observer
join form.taskSchedule schedule
join form.observerType observerType
join schedule.task task
join task.courseClass courseClass
join observer.department department
where form.status > 0
  and observerType.name = :observerType
  and courseClass.term.id = :termId
  and department.id = :departmentId
group by observer
''', [termId: term.id, type: type, departmentId: departmentId]
        return [
                list: result,
        ]

    }
}
