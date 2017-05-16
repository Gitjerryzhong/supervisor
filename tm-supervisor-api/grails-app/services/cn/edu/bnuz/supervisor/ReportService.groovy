package cn.edu.bnuz.supervisor

import cn.edu.bnuz.bell.http.ForbiddenException
import cn.edu.bnuz.bell.master.TermService
import cn.edu.bnuz.bell.organization.Teacher
import grails.transaction.Transactional

@Transactional
class ReportService {
    TermService termService
    SupervisorSettingService supervisorSettingService
    def messageSource

    def groupByDepartment(String userId) {
        def term = termService.activeTerm
//        if(isCollegeSupervisor(userId,term.id)){
//            throw new ForbiddenException()
//        }

        def supervisor=messageSource.getMessage("main.supervisor.university",null, Locale.CHINA)
        def result =SupervisorLectureRecord.executeQuery '''
select new map(
  department.name as department,
  count(*) as supervisorTimes,
  sum(form.totalSection) as totalSection
)
from SupervisorLectureRecord form
join form.taskSchedule schedule
join form.supervisorRole supervisorRole
join schedule.task task
join task.courseClass courseClass
join courseClass.department department
where form.status > 0
  and supervisorRole.name = :type
  and courseClass.term.id = :termId
group by department.name
''', [termId: term.id, type: supervisor]
        return [
                isAdmin:supervisorSettingService.isAdmin(userId),
                list: result,
        ]

    }

    def groupBySupervisor(String userId) {
        def term = termService.activeTerm

        def supervisor=messageSource.getMessage("main.supervisor.university",null, Locale.CHINA)
        def result =SupervisorLectureRecord.executeQuery '''
select new map(
  supervisor.id as supervisorId,
  supervisor.name as supervisorName,
  department.name as departmentName,
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
group by supervisor,department
''', [termId: term.id, type: supervisor]
        return [
                list: result,
        ]

    }

    boolean isCollegeSupervisor(String userId, Integer termId){
        def collegeSupervisor=messageSource.getMessage("main.supervisor.college",null, Locale.CHINA)
        return supervisorSettingService.isCollegeSupervisor(userId, termId , collegeSupervisor)
    }

    def teacherActive(String userId){
        def result=TeacherActive.executeQuery'''
select new map(
ta.teacherId as teacherId,
ta.teacherName as teacherName,
ta.academicTitle as academicTitle,
ta.departmentName as departmentName,
ta.isnew as isnew
)
from TeacherActive ta
where ta.hasSupervisor is null
order by ta.departmentName,teacherName
'''
        return [
                isAdmin:supervisorSettingService.isAdmin(userId),
                list: result,
        ]
    }

    def getSupervisorRole(String userId){
        def term = termService.activeTerm
        if(supervisorSettingService.isAdmin(userId)){
            def adminSupervisor=messageSource.getMessage("main.supervisor.admin",null, Locale.CHINA)
            return [adminSupervisor]
        } else return supervisorSettingService.getSupervisorRole(userId, term.id)
    }

    def byTeacherForCollege(String userId) {
        def term = termService.activeTerm
        def supervisor=messageSource.getMessage("main.supervisor.college",null, Locale.CHINA)
//        if(!supervisorSettingService.isCollegeSupervisor(userId,term.id,supervisor)){
//            throw new ForbiddenException()
//        }

        def dept = Teacher.load(userId)?.department.id

        SupervisorLectureRecord.executeQuery '''
select new map(
  scheduleTeacher.id as teacherId,
  scheduleTeacher.name as teacherName,
  count(*) as supervisorTimes,
  department.name as departmentName
)
from SupervisorLectureRecord form
join form.teacher scheduleTeacher
join form.taskSchedule schedule
join form.supervisorRole supervisorRole
join schedule.task task
join task.courseClass courseClass
join courseClass.department department
where form.status > 0
  and supervisorRole.name = :type
  and courseClass.term.id = :termId
  and (scheduleTeacher.department.id = :dept or department.id = :dept)
group by scheduleTeacher,department
''', [termId: term.id, type: supervisor, dept:dept]

    }

    def byTeacherForUniversity(){
        SuperviseCount.executeQuery '''
select new map(
    view.teacherId as teacherId,
    view.teacherName as teacherName,
    view.departmentName as departmentName,
    view.superviseCount as supervisorTimes
)
from SuperviseCount view
'''
    }

}
