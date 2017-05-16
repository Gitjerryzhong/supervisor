package cn.edu.bnuz.supervisor

import cn.edu.bnuz.bell.operation.TaskSchedule
import cn.edu.bnuz.bell.place.Place
import cn.edu.bnuz.bell.master.TermService
import cn.edu.bnuz.bell.organization.DepartmentService
import grails.transaction.Transactional
import org.grails.datastore.mapping.query.Query.In

import java.time.LocalDate

@Transactional
class ScheduleForSupervisorService {

    TermService termService
    DepartmentService departmentService
    SupervisorSettingService supervisorSettingService
    def messageSource
    def getFormForCreate(String userId) {
        def term = termService.activeTerm
        return [
                term        : [
                        startWeek  : term.startWeek,
                        maxWeek    : term.maxWeek,
                        currentWeek: term.currentWorkWeek,
                        startDate  : term.startDate,
                        swapDates  : term.swapDates,
                        endWeek    : term.endWeek,
                ],
                departments : departmentService.teachingDepartments,
                sections    : Section.findAll(),
                today       : LocalDate.now(),
                buildings   : getBuildings(),
        ]
    }

    private getBuildings(){
        Place.executeQuery'''
select distinct p.building from Place p where p.enabled = true and p.isExternal=false
'''
    }

    def findPlace(String building, String placeName){
        Place.executeQuery'''
select new map(
    p.id as id,
    p.name as name,
    p.building as building,
    p.seat as seat,
    p.type as type
)
 from Place p where p.enabled = true and p.isExternal=false
  and p.building like :building and p.name like :query
''',[building:building == null?"%":building,
     query: "%${placeName}%"],[max: 10]
    }
    List getTeacherSchedules(String userId, SheduleOptionsCommand cmd) {
        def term = termService.activeTerm
        def result=TaskSchedule.executeQuery '''
select new map(
  schedule.id as id,
  task.id as taskId,
  courseClass.id as courseClassId,
  department.name as department,
  scheduleTeacher.academicTitle as academicTitle,
  courseClass.name as courseClassName,
  courseTeacher.id as courseTeacherId,
  courseTeacher.name as courseTeacherName,
  scheduleTeacher.id as teacherId,
  scheduleTeacher.name as teacherName,
  schedule.startWeek as startWeek,
  schedule.endWeek as endWeek,
  schedule.oddEven as oddEven,
  schedule.dayOfWeek as dayOfWeek,
  schedule.startSection as startSection,
  schedule.totalSection as totalSection,
  course.name as course,
  course.credit as credit,
  property.name as property,
  courseItem.name as courseItem,
  place.name as place,
  (select superviseCount from SuperviseCount where teacherId = scheduleTeacher.id) as superviseCount
)
from TaskSchedule schedule
join schedule.task task
join task.courseClass courseClass
join courseClass.course course
join courseClass.teacher courseTeacher
join courseClass.department department
join schedule.teacher scheduleTeacher
join course.property property
left join task.courseItem courseItem
left join schedule.place place
where courseClass.term.id = :termId
  and scheduleTeacher.id like :teacherId
  and place.name like :place
  and department.id like :department
  and :weekOfTerm >= schedule.startWeek
  and :weekOfTerm <= schedule.endWeek
  and ((schedule.oddEven = 0) or (schedule.oddEven = :weekOfTerm % 2))
''', [ termId: term.id,
       teacherId: cmd.teacherId =='null'?'%':cmd.teacherId,
       place: cmd.place == 'null'?'%':"${cmd.place}%",
       department: cmd.departmentId == 'null'?'%':cmd.departmentId,
       weekOfTerm: cmd.weekOfTerm]
//      过滤不在时段、不在星期几的课
        def a=[]
        for(i in cmd.startSection .. cmd.endSection){
            a+=[i]
        }

        result.grep{
            cmd.dayOfWeek==0 ?true:(it.dayOfWeek == cmd.dayOfWeek)
        }.grep{item->
            def list=[]
            for(i in item.startSection .. item.startSection+item.totalSection-1){
                list+=[i]
            }
            return a - list != a
        }

    }

    def getSchedule(String userId, String id){
        def result = getScheduleById(id)
        def term = termService.activeTerm
        def isAdmin = supervisorSettingService.isAdmin(userId)
        def type = isAdmin? supervisorSettingService.findAllRoles():supervisorSettingService.findRolesByUserIdAndTerm(userId,term.id)

        return [
                term        : [
                        startWeek  : term.startWeek,
                        maxWeek    : term.maxWeek,
                        currentWeek: term.currentWorkWeek,
                        startDate  : term.startDate,
                        swapDates  : term.swapDates,
                        endWeek    : term.endWeek,
                ],
                schedule    : result,
                type        : type,
                evaluationSystem    : this.evaluationSystem,
                isAdmin             : isAdmin,
                supervisors         : isAdmin? supervisorSettingService.findCurrentSupervisors(term.id):null
        ]
    }

    def showSchedule(String id){
        def result = getScheduleById(id)
        if(result)
            return [
                    schedule    : result[0],
                    evaluationSystem    :this.evaluationSystem
            ]
        else return null;
    }

    def getEvaluationSystem(){
        def result=EvaluationSystem.executeQuery'''
select new map(
item.id as id,
item.title as title,
item.name as name
)
from EvaluationSystem es join es.items item
where es.activeted is true
'''
        return result.groupBy {it.title}.entrySet()
    }

    List getPlaceSchedules(String placeId, Integer termId, Integer weekOfTerm) {
        TaskSchedule.executeQuery '''
select new map(
  schedule.id as id,
  task.id as taskId,
  courseClass.id as courseClassId,
  courseClass.name as courseClassName,
  courseTeacher.id as courseTeacherId,
  courseTeacher.name as courseTeacherName,
  scheduleTeacher.id as teacherId,
  scheduleTeacher.name as teacherName,
  schedule.startWeek as startWeek,
  schedule.endWeek as endWeek,
  schedule.oddEven as oddEven,
  schedule.dayOfWeek as dayOfWeek,
  schedule.startSection as startSection,
  schedule.totalSection as totalSection,
  course.name as course,
  courseItem.name as courseItem,
  place.name as place,
  (select superviseCount from SuperviseCount where teacherId = scheduleTeacher.id) as superviseCount
)
from TaskSchedule schedule
join schedule.task task
join task.courseClass courseClass
join courseClass.course course
join courseClass.teacher courseTeacher
join schedule.teacher scheduleTeacher
left join task.courseItem courseItem
left join schedule.place place
where place.id = :placeId
  and courseClass.term.id = :termId
  and schedule.startWeek <= :weekOfTerm
  and schedule.endWeek >= :weekOfTerm
''', [placeId: placeId, termId: termId, weekOfTerm: weekOfTerm]
    }

    List getTeacherSchedules(String teacherId, Integer termId) {
        TaskSchedule.executeQuery '''
select new map(
  schedule.id as id,
  task.id as taskId,
  courseClass.id as courseClassId,
  courseClass.name as courseClassName,
  courseTeacher.id as courseTeacherId,
  courseTeacher.name as courseTeacherName,
  scheduleTeacher.id as teacherId,
  scheduleTeacher.name as teacherName,
  schedule.startWeek as startWeek,
  schedule.endWeek as endWeek,
  schedule.oddEven as oddEven,
  schedule.dayOfWeek as dayOfWeek,
  schedule.startSection as startSection,
  schedule.totalSection as totalSection,
  course.name as course,
  courseItem.name as courseItem,
  place.name as place,
  (select superviseCount from SuperviseCount where teacherId = scheduleTeacher.id) as superviseCount
)
from TaskSchedule schedule
join schedule.task task
join task.courseClass courseClass
join courseClass.course course
join courseClass.teacher courseTeacher
join schedule.teacher scheduleTeacher
left join task.courseItem courseItem
left join schedule.place place
where scheduleTeacher.id = :teacherId
  and courseClass.term.id = :termId
''', [teacherId: teacherId, termId: termId]
    }

    private getScheduleById(String id){
        TaskSchedule.executeQuery '''
select new map(
  schedule.id as id,
  task.id as taskId,
  courseClass.id as courseClassId,
  department.name as department,
  scheduleTeacher.academicTitle as academicTitle,
  courseClass.name as courseClassName,
  courseTeacher.id as courseTeacherId,
  courseTeacher.name as courseTeacherName,
  scheduleTeacher.id as teacherId,
  scheduleTeacher.name as teacherName,
  schedule.startWeek as startWeek,
  schedule.endWeek as endWeek,
  schedule.oddEven as oddEven,
  schedule.dayOfWeek as dayOfWeek,
  schedule.startSection as startSection,
  schedule.totalSection as totalSection,
  course.name as course,
  course.credit as credit,
  property.name as property,
  courseItem.name as courseItem,
  place.name as place,
  (select count(*) from TaskStudent tst where tst.task = task) as studentCount
)
from TaskSchedule schedule
join schedule.task task
join task.courseClass courseClass
join courseClass.course course
join courseClass.teacher courseTeacher
join courseClass.department department
join schedule.teacher scheduleTeacher
join course.property property
left join task.courseItem courseItem
left join schedule.place place
where schedule.id = :id
''',[id:UUID.fromString(id)]
    }

    boolean isCollegeSupervisor(String userId, Integer termId){
        def collegeSupervisor=messageSource.getMessage("main.supervisor.college",null, Locale.CHINA)
        return supervisorSettingService.isCollegeSupervisor(userId, termId , collegeSupervisor)
    }


}
