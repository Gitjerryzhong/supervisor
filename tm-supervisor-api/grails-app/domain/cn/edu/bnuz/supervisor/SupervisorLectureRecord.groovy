package cn.edu.bnuz.supervisor

import cn.edu.bnuz.bell.operation.TaskSchedule
import cn.edu.bnuz.bell.organization.Teacher

class SupervisorLectureRecord {

    Teacher teacher
    Teacher supervisor
    TaskSchedule taskSchedule
    SupervisorRole supervisorRole
    /**
     * 记录状态：0-未提交；1-提交；2-发布；
     */
    Integer status
    Date    recordDate
    Integer lectureWeek
    String  supervisorDate
    Integer totalSection
    String  evaluationText
    String  teachingMethods
    String  suggest
    String  evaluateLevel
    Date    rewardDate
    String  place
    Integer earlier
    Integer late
    Integer leave
    Integer dueStds
    Integer lateStds
    Integer attendantStds
    Integer leaveStds
    String  operator
    Integer termId
    String  updateOperator
    Date    updateDate
    static hasMany = [evaluations:Evaluations]

    static mapping = {
        comment        '听课记录'
        id             generator: 'identity', comment: 'ID'
        teacher         comment: '上课老师'
        supervisor       comment: '听课老师'
        taskSchedule    comment: '被听的课程'
        supervisorRole  comment: '督导类型'
        status          defaultValue: "0",comment: '状态'
        recordDate      comment: '录入日期'
        lectureWeek     comment:'上课周'
        supervisorDate       comment:'听课日期'
        teachingMethods comment:'教学形式'
        totalSection    comment: '听课节数'
        evaluationText      length: 1500,    comment: '评价'
        suggest         length: 1500,    comment: '建议'
        evaluateLevel   length: 4,       comment: '评定等级'
        place           length: 50,      comment: '上课地点'
        earlier         defaultValue: "0",comment: '提前（分钟）'
        late            defaultValue: "0",comment: '迟到（分钟）'
        leave           defaultValue: "0",comment: '迟到（分钟）'
        dueStds         defaultValue: "0",comment: '应到人数'
        lateStds        defaultValue: "0",comment: '迟到人数'
        attendantStds   defaultValue: "0",comment: '实到人数'
        leaveStds       defaultValue: "0",comment: '早退人数'
        operator        length: 10,      comment: '录入人'
        rewardDate      comment: '计酬日期'
        termId          comment: '冗余学期Id'
        updateOperator  comment: '最后修改人'
        updateDate      comment: '最后修改日期'

    }


    static constraints = {
        evaluationText  nullable: true
        suggest         nullable: true
        evaluateLevel   nullable: true
        supervisorDate  nullable: true
        supervisorDate  nullable: true
        earlier         nullable: true
        late            nullable: true
        leave           nullable: true
        dueStds         nullable: true
        attendantStds   nullable: true
        lateStds        nullable: true
        leaveStds       nullable: true
        teachingMethods nullable: true
        status          nullable: true
        operator        nullable: true
        rewardDate      nullable: true
        termId          nullable: true
        updateDate      nullable: true
        updateOperator  nullable: true
    }
}
