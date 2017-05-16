package cn.edu.bnuz.supervisor

import cn.edu.bnuz.bell.master.TermService
import grails.transaction.Transactional

@Transactional
class SuperviseHistoryService {
    ScheduleForSupervisorService scheduleForSupervisorService
    TermService termService
    SupervisorSettingService supervisorSettingService

    def list(String userId){
        def term = termService.activeTerm
//        println term.id
        def isAdmin = supervisorSettingService.isAdmin(userId)
        def result = SupervisorLectureRecord.executeQuery '''
select new map(
  form.id as id,
  form.supervisorDate as supervisorDate,
  form.evaluateLevel as evaluateLevel,
  form.status as status,
  supervisor.id as supervisorId,
  supervisor.name as supervisorName,
  supervisorRole.name as typeName,
  schedule.id as scheduleId,
  courseClass.name as courseClassName,
  department.name as department,
  scheduleTeacher.id as teacherId,
  scheduleTeacher.name as teacherName,
  schedule.dayOfWeek as dayOfWeek,
  schedule.startSection as startSection,
  schedule.totalSection as totalSection,
  course.name as course,
  place.name as place
)
from SupervisorLectureRecord form
join form.supervisor supervisor
join form.taskSchedule schedule
join form.supervisorRole supervisorRole
join schedule.task task
join task.courseClass courseClass
join courseClass.course course
join courseClass.department department
join schedule.teacher scheduleTeacher
left join schedule.place place
where supervisor.id like :userId
  and courseClass.term.id <> :termId
order by form.supervisorDate
''', [userId: isAdmin?'%':userId, termId: term.id]
        return [isAdmin : isAdmin, list: result]
    }

    def getFormForShow(Long id) {
        def form = SupervisorLectureRecord.get(id)

        if(form) {

            def schedule = scheduleForSupervisorService.showSchedule(form.taskSchedule.id.toString())
            schedule.form = getFormInfo(form)
            schedule.evaluationSystem.each { group ->
                group.value.each { item ->
                    item.value = Evaluations.findByEvaluatItemAndSupervisorLectureRecord(EvaluatItems.load(item.id), form)?.value
                }
            }
            return schedule
        }
        return null
    }

    Map getFormInfo(SupervisorLectureRecord form) {
        return [
                id: form.id,
                scheduleId: form.taskSchedule.id,
                teacherId: form.teacher.id,
                supervisorName: form.supervisor.name,
                supervisorWeek: form.lectureWeek,
                totalSection: form.totalSection,
                teachingMethods: form.teachingMethods,
                supervisorDate: form.supervisorDate,
                type: form.supervisorRoleId,
                typeName: form.supervisorRole.name,
                place: form.place,
                earlier: form.earlier,
                late: form.late,
                leave: form.leave,
                dueStds: form.dueStds,
                attendantStds: form.attendantStds,
                lateStds: form.lateStds,
                leaveStds: form.leaveStds,
                evaluateLevel: form.evaluateLevel,
                evaluationText: form.evaluationText,
                suggest: form.suggest,
                status: form.status,
        ]

    }
}
