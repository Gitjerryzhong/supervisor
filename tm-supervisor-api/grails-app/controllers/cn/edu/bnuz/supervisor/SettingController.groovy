package cn.edu.bnuz.supervisor

import cn.edu.bnuz.bell.master.TermService
import cn.edu.bnuz.bell.organization.DepartmentService
import org.springframework.security.access.prepost.PreAuthorize

/**
 * 督导员管理
 */
@PreAuthorize('hasAuthority("PERM_ADMIN_SUPERVISOR_WRITE")')
class SettingController {
    SupervisorSettingService supervisorSettingService
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
        def form=supervisorSettingService.save(cmd)
        if(form)  renderJson([id:form?.id])
        else renderBadRequest()
    }


    /**
     * 创建
     */
    def create(){
        renderJson(
                roles: supervisorSettingService.roleTypes(),
                departments: departmentService.teachingDepartments,
                activeTerm: termService.activeTerm?.id,
                terms: supervisorSettingService.terms

        );
    }

    /**
     * 删除
     */
    def delete(Long id){
        supervisorSettingService.delete(id)
        renderOk()
    }
}
