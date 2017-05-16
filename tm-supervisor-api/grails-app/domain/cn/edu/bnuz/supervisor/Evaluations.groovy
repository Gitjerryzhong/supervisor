package cn.edu.bnuz.supervisor

class Evaluations {
    EvaluatItems evaluatItem
    Integer value
    static belongsTo = [supervisorLectureRecord: SupervisorLectureRecord]

    static mapping = {
        table          'supervisor_Evaluation'
        comment        '评分'
        id             generator: 'identity', comment: 'ID'
        value          comment: '分值'
        evaluatItem    comment: '参考评分细则'
        supervisorLectureRecord         comment: '所属听课记录'
    }
    static constraints = {
        value   nullable: true
    }
}
