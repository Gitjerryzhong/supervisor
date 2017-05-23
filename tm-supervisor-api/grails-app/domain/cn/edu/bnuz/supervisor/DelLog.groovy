package cn.edu.bnuz.supervisor

class DelLog {
    String userId
    String objName
    Integer objId
    Date delDate
    String content

    static mapping = {
        table          'supervisor_del_log'
        comment        '删除操作日志'
        id             generator: 'identity', comment: 'ID'
        userId         comment: '操作用户'
        objName        comment: '对象名'
        objId          comment: '对象id'
        delDate        comment: '操作日期'
        content        length: 1500,    comment: '对象内容'
    }
    static constraints = {
        content nullable: true
    }
}
