package cn.edu.bnuz.supervisor

import cn.edu.bnuz.bell.organization.Department
import cn.edu.bnuz.bell.organization.Teacher
class Observer {
    Teacher teacher
    Integer termId
    /**
     * 角色类型：0-校领导；1-校督导；2-院督导；3-院领导；4-院教务；
     */
    ObserverType observerType
    Department department
    static mapping = {
        comment        '督导老师'
        id             generator: 'identity', comment: 'ID'
        teacher        comment: '关联老师'
        termId         comment: '学期ID，2012-2013-1为20121'
        observerType   comment: '角色类型'
        department     comment: '所属学院'
    }

    static constraints = {
    }
}
