package cn.edu.bnuz.supervisor

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.http.ForbiddenException
import cn.edu.bnuz.bell.http.NotFoundException
import cn.edu.bnuz.bell.master.TermService
import cn.edu.bnuz.bell.operation.TaskSchedule
import cn.edu.bnuz.bell.organization.Teacher
import grails.converters.JSON
import grails.transaction.Transactional

@Transactional
class SupervisorFormService {
    TermService termService
    ScheduleForSupervisorService scheduleForSupervisorService
    SupervisorSettingService supervisorSettingService

    SupervisorLectureRecord create(String userId, SupervisorFormCommand cmd){
        //防止重复录入
        SupervisorLectureRecord form =
                SupervisorLectureRecord.findBySupervisorAndSupervisorDateAndTaskSchedule(
                        Teacher.load(userId),
                        cmd.supervisorDate,
                        TaskSchedule.load(cmd.scheduleId)
                )
        if(form) {
            throw new BadRequestException()
        }
        def isAdmin = supervisorSettingService.isAdmin(userId)
        if(isAdmin && !cmd.supervisorId){
            throw new BadRequestException()
        }
        def now = new Date()
        form = new SupervisorLectureRecord(
                supervisor: isAdmin?Teacher.load(cmd.supervisorId):Teacher.load(userId),
                teacher: Teacher.load(cmd.teacherId),
                taskSchedule: TaskSchedule.load(cmd.scheduleId),
                lectureWeek: cmd.supervisorWeek,
                totalSection: cmd.totalSection,
                teachingMethods: cmd.teachingMethods,
                supervisorDate: cmd.supervisorDate,
                recordDate: now,
                supervisorRole: SupervisorRole.load(cmd.type),
                place: cmd.place,
                status: cmd.status?:0,
                earlier: cmd.earlier,
                late:  cmd.late,
                leave:  cmd.leave,
                dueStds: cmd.dueStds,
                attendantStds:  cmd.attendantStds,
                lateStds: cmd.lateStds,
                leaveStds:  cmd.leaveStds,
                evaluateLevel:  cmd.evaluateLevel,
                evaluationText: cmd.evaluationText,
                suggest:  cmd.suggest,
                operator: isAdmin?userId:null,
                termId: termService.activeTerm.id
        )

        cmd.evaluations.each { item ->
            form.addToEvaluations( new Evaluations(
                    evaluatItem: EvaluatItems.load(item.id),
                    value:  item.value
                )
            )
        }

        form.save()
    }

    SupervisorLectureRecord  update(String userId, SupervisorFormCommand cmd){

        SupervisorLectureRecord form = SupervisorLectureRecord.get(cmd.id)
        if(!form) {
            throw new NotFoundException()
        }
        if (form.supervisor.id != userId && !supervisorSettingService.isAdmin(userId)) {
            throw new ForbiddenException()
        }
        if(this.cantUpdate(form)){
//            println form.status
            return null
        }

        form.lectureWeek=cmd.supervisorWeek
        form.totalSection= cmd.totalSection
        form.teachingMethods= cmd.teachingMethods
        form.supervisorDate= cmd.supervisorDate
        form.supervisorRole= SupervisorRole.load(cmd.type)
        form.earlier= cmd.earlier
        form.late=  cmd.late
        form.leave=  cmd.leave
        form.dueStds= cmd.dueStds
        form.attendantStds=  cmd.attendantStds
        form.lateStds= cmd.lateStds
        form.leaveStds=  cmd.leaveStds
        form.evaluateLevel=  cmd.evaluateLevel
        form.evaluationText= cmd.evaluationText
        form.suggest=  cmd.suggest
        form.status = cmd.status?:0
        form.updateOperator = userId
        form.updateDate = new Date()

        cmd.evaluations.each { item ->
            def evaluation = Evaluations.findByEvaluatItemAndSupervisorLectureRecord(EvaluatItems.load(item.id),form)
            evaluation.value=item.value
        }

        form.save()
    }

    def list(String userId, Integer termId){
        def term = termService.activeTerm
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
  and courseClass.term.id = :termId
order by form.supervisorDate
''', [userId: isAdmin?'%':userId, termId: termId?:term.id]
        return [isAdmin : isAdmin,
                list: result,
                activeTerm: termId?:term.id,
                terms: supervisorSettingService.terms]
    }

    def getFormForEdit(String userId, Long id) {
        def form = SupervisorLectureRecord.get(id)
        if(form) {
            if (userId != form.supervisor.id && !supervisorSettingService.isAdmin(userId)) {
                throw new ForbiddenException()
            }
            if (form.status) {
                throw new BadRequestException()
            }

            def schedule = scheduleForSupervisorService.getSchedule(userId, form.taskSchedule.id.toString())
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

    def getFormForShow(String userId, Long id) {
        def form = SupervisorLectureRecord.get(id)

        if(form) {
            if (userId != form.supervisor.id && !supervisorSettingService.isAdmin(userId)) {
                throw new ForbiddenException()
            }
            def schedule = scheduleForSupervisorService.showSchedule(form.taskSchedule.id.toString())
            schedule.form = getFormInfo(form)
            schedule.evaluationSystem.each { group ->
                group.value.each { item ->
                    item.value = Evaluations.findByEvaluatItemAndSupervisorLectureRecord(EvaluatItems.load(item.id), form)?.value
                }
            }
            schedule.isAdmin = supervisorSettingService.isAdmin(userId)
            return schedule
        }
        return null
    }

    def delete(String userId, Long id){
        def form = SupervisorLectureRecord.get(id)
        if(form) {
            if (userId != form.supervisor.id && !supervisorSettingService.isAdmin(userId)) {
                throw new ForbiddenException()
            }
            if (form.status) {
                throw new BadRequestException()
            }
            DelLog delLog = new DelLog(
                    userId:userId,
                    objName: form.class.name,
                    objId: form.id,
                    delDate: new Date(),
                    content: "${form as JSON}"
            )
            delLog.save()
            form.delete()
        }
    }

    def cancel(String userId, Long id){
        def form = SupervisorLectureRecord.get(id)
        if(form) {
            /*只有管理员可以撤销*/
            if (!supervisorSettingService.isAdmin(userId) ) {
                throw new ForbiddenException()
            }
            if (form.status!=1) {
                throw new BadRequestException()
            }
            form.setStatus(0)
            form.save()
        }
    }


    def submit(String userId, Long id){
        def form = SupervisorLectureRecord.get(id)

        if(form) {
            if (userId != form.supervisor.id && !supervisorSettingService.isAdmin(userId)) {
                throw new ForbiddenException()
            }
            if (form.status) {
//                println form.status
                throw new BadRequestException()
            }
            form.setStatus(1)
            form.save()
        }
    }

    def feed(String userId, Long id){
        def form = SupervisorLectureRecord.get(id)

        if(form) {
            if (!supervisorSettingService.isAdmin(userId)) {
                throw new ForbiddenException()
            }
            if (form.status!=1) {
                throw new BadRequestException()
            }
            form.setStatus(2)
            form.save()
        }
    }

    Map getFormInfo(SupervisorLectureRecord form) {
        return [
                id: form.id,
                scheduleId: form.taskSchedule.id,
                teacherId: form.teacher.id,
                supervisorId: form.supervisor.id,
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
                isActive: form.termId == termService.activeTerm.id,
        ]

    }

    private cantUpdate(SupervisorLectureRecord form){
        return form.status
    }

}
