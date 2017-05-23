package cn.edu.bnuz.supervisor

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.http.ForbiddenException
import cn.edu.bnuz.bell.master.TermService
import cn.edu.bnuz.bell.security.SecurityService
import org.springframework.security.access.prepost.PreAuthorize


/**
 * 学院督导管理
 */
@PreAuthorize('hasAuthority("PERM_CO_SUPERVISOR_ADMIN")')
class SupervisorDepartmentController {
    ObserverSettingService supervisorSettingService
    SecurityService securityService
    SupervisorDepartmentService supervisorDepartmentService
    def messageSource
    TermService termService
    def index() {
        def collegeSupervisor = messageSource.getMessage("main.supervisor.college",null, Locale.CHINA)
        renderJson(supervisorDepartmentService.list(securityService.departmentId, collegeSupervisor))
    }


    /**
     * 保存数据
     */
    def save(){
        SupervisorCommand cmd = new SupervisorCommand()
        bindData cmd, request.JSON
        log.debug cmd.tostring()
        cmd.departmentId = ''
        def collegeSupervisor = messageSource.getMessage("main.supervisor.college",null, Locale.CHINA)
        def supervisorRole=ObserverType.findByName(collegeSupervisor)
        cmd.roleType = supervisorRole?.id
        println cmd.tostring()
        def form=supervisorSettingService.save(cmd)
        if(form)  renderJson([id:form?.id])
        else renderBadRequest()
    }


    /**
     * 创建
     */
    def create(){
        println "SupervisorDepartmentController"
        def collegeSupervisor = messageSource.getMessage("main.supervisor.college",null, Locale.CHINA)
        renderJson(
                roles: supervisorSettingService.roleTypes().grep{it.name == collegeSupervisor},
                activeTerm: termService.activeTerm?.id,
                terms: supervisorSettingService.terms

        );
    }

    def teachers(){
        String query=params.q
        renderJson(supervisorDepartmentService.findTeacher(query, securityService.departmentId))

    }

    def supervisorReport(){
        renderJson(supervisorDepartmentService.groupBySupervisor(securityService.departmentId))
    }

    /**
     * 删除
     */
    def delete(Long id){
        def supervisor = Observer.load(id)
        if(!supervisor){
            throw new BadRequestException()
        }
        if(supervisor.department.id != securityService.departmentId){
            throw new ForbiddenException()
        }
        supervisorSettingService.delete(id)
        renderOk()
    }
}
