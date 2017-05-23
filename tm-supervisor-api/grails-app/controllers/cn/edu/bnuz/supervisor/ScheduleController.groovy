package cn.edu.bnuz.supervisor

import cn.edu.bnuz.bell.master.TermService
import org.springframework.security.access.prepost.PreAuthorize
import cn.edu.bnuz.bell.organization.Teacher

/**
 * 课表
 */
@PreAuthorize('hasAuthority("PERM_SUPERVISOR_WRITE")')
class ScheduleController {
    ScheduleService scheduleService
    TermService termService
    ObservationFormService observationFormService
    ReportService reportService

    def show(String userId, String id) {
        renderJson(scheduleService.getSchedule(userId, id))
    }

    def create(String userId) {
        renderJson(scheduleService.getFormForCreate(userId))
    }

    def save(String userId) {
        def cmd = new ObservationFormCommand()
        bindData(cmd, request.JSON)
        println cmd
        def form = observationFormService.create(userId, cmd)
        renderJson([id: form.id])
    }

    def update(String userId, Long id) {
        def cmd = new ObservationFormCommand()
        bindData(cmd, request.JSON)
        cmd.id = id
        observationFormService.update(userId, cmd)
        renderOk()
    }

    def findPlace(){
        String building = params.building
        println building
        String q = params.q
        renderJson(scheduleService.findPlace(building, q))
    }

    def findSchedule(String userId){
        SheduleOptionsCommand cmd = new SheduleOptionsCommand()
        cmd.teacherId = params['teacherId']
        cmd.place = params['place']
        cmd.departmentId = params['departmentId']
        cmd.weekOfTerm = params.getInt('weekOfTerm')?:0
        cmd.dayOfWeek = params.getInt('dayOfWeek')?:0
        cmd.startSection = params.getInt('startSection')
        cmd.endSection = params.getInt('endSection')
        println cmd.tostring()
        def schedule = scheduleService.getTeacherSchedules(userId, cmd)
        renderJson(schedule)
    }

    def isCurrentSupervisor(String userId){
        def term =termService.activeTerm
        def result = Observer.findByTermIdAndTeacher(term.id, Teacher.load(userId)) !=null
        renderJson([result:result])
    }

    def findTeacherSchedule(String userId){
        String teacherId = params['teacherId']
        Integer weekOfTerm = params.getInt('weekOfTerm')?:0
        println(weekOfTerm)
        def term =termService.activeTerm
        def schedules = scheduleService.getTeacherSchedules(teacherId, term.id)
        renderJson([schedules: schedules.grep{
            it.startWeek <=weekOfTerm && it.endWeek >=weekOfTerm
        }])
    }

    def findPlaceSchedule(String userId){
        String place = params['place']
        println place
        Integer weekOfTerm = params.getInt('weekOfTerm')?:0
        def term =termService.activeTerm
        renderJson(scheduleService.getPlaceSchedules(place,term.id,weekOfTerm))
    }

    def getTerm(){
        def term =termService.activeTerm
        renderJson([
                    startWeek  : term.startWeek,
                    maxWeek    : term.maxWeek,
                    currentWeek: term.currentWorkWeek,
                    startDate  : term.startDate,
                    swapDates  : term.swapDates,
                    endWeek    : term.endWeek,
                    ])
    }

    def teacherActiveList(String userId){
        def term =termService.activeTerm
        if(scheduleService.isCollegeSupervisor(userId,term.id))
            renderBadRequest()
        else renderJson(reportService.teacherActive(userId))
    }
}
