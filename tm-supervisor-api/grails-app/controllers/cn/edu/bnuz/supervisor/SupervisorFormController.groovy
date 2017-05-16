package cn.edu.bnuz.supervisor

import org.springframework.security.access.prepost.PreAuthorize

/**
 * 本学期督导听课记录
 */
@PreAuthorize('hasAuthority("PERM_SUPERVISOR_WRITE")')
class SupervisorFormController {
    SupervisorFormService supervisorFormService

    def index(String userId) {
        Integer termId = params.getInt('termId')?:0
        renderJson(supervisorFormService.list(userId, termId))
    }

    def edit(String userId, Long id) {
        renderJson(supervisorFormService.getFormForEdit(userId, id))
    }

    def show(String userId, Long id){
        renderJson(supervisorFormService.getFormForShow(userId, id))
    }

    def update(String userId, Long id) {
        def cmd = new SupervisorFormCommand()
        bindData(cmd, request.JSON)
        cmd.id = id
        supervisorFormService.update(userId, cmd)
        renderOk()
    }

    def cancel(String userId){
        Integer id = params.getInt('id')
        supervisorFormService.cancel(userId, id)
        renderJson([ok:true])
    }

    def submit(String userId){
        Integer id = params.getInt('id')
        supervisorFormService.submit(userId, id)
        renderJson([ok:true])
    }

    def delete(String userId, Long id){
        supervisorFormService.delete(userId, id)
        renderOk()
    }

    def feed(String userId){
        Integer id = params.getInt('id')
        supervisorFormService.feed(userId, id)
        renderJson([ok:true])
    }


}
