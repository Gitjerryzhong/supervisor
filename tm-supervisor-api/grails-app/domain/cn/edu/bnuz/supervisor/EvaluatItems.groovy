package cn.edu.bnuz.supervisor

class EvaluatItems {

    String title
    String name
    String description
    static belongsTo =[evaluationSystem:EvaluationSystem]
    static mapping = {
        table          'supervisor_evaluation_items'
        comment        '评分细则'
        id             generator: 'identity', comment: 'ID'
        title          comment: '标题'
        name           comment: '名称'
        description    comment: '描述'
    }
    static constraints = {
        description nullable: true
    }
}
