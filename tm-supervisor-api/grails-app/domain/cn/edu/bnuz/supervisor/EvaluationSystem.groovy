package cn.edu.bnuz.supervisor

class EvaluationSystem {

    String name
    String description
    Boolean activeted
    static hasMany =[items:EvaluatItems]
    static mapping = {
        table          'supervisor_Evaluation_System'
        comment        '评分体系'
        id             generator: 'identity', comment: 'ID'
        name           comment: '名称'
        description    comment: '描述'
    }

    static constraints = {
        description nullable: true
        activeted nullable:  true
    }
}
