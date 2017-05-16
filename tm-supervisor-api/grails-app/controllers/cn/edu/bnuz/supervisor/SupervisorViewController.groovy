package cn.edu.bnuz.supervisor

import org.springframework.security.access.prepost.PreAuthorize

/**
 * 往期督导听课记录
 */
@PreAuthorize('hasAuthority("PERM_SUPERVISOR_READ")')
class SupervisorViewController {
    SuperviseHistoryService superviseHistoryService
    def index(String userId) {
        renderJson(superviseHistoryService.list(userId))
    }

    def show(String userId, Long id){
        renderJson(superviseHistoryService.getFormForShow(id))
    }
}
