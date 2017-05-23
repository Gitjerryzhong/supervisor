package cn.edu.bnuz.supervisor

import cn.edu.bnuz.bell.organization.DepartmentService
import cn.edu.bnuz.bell.master.TermService
import org.springframework.security.access.prepost.PreAuthorize

/**
 * 督导员设置
 */
@PreAuthorize('hasAuthority("PERM_ADMIN_SUPERVISOR_WRITE")')
class SupervisorSettingController {
    ObserverSettingService supervisorSettingService
    DepartmentService departmentService
    TermService termService
    def index() {
        renderJson(supervisorSettingService.list())
    }


    /**
     * 保存数据
     */
    def save(){
        SupervisorCommand cmd = new SupervisorCommand()
        bindData cmd, request.JSON
        log.debug cmd.tostring()
        if(supervisorSettingService.save(cmd))  renderOk()
        else renderBadRequest()
    }


    /**
     * 创建
     */
    def create(){
        renderJson(
                roles: supervisorSettingService.roleTypes(),
                departments: departmentService.teachingDepartments,
                activeTerm: termService.activeTerm?.id
        );
    }
}
