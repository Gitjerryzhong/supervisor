package cn.edu.bnuz.supervisor

import cn.edu.bnuz.bell.security.SecurityService
import org.springframework.security.access.prepost.PreAuthorize

/**
 * 听课记录反馈
 */
@PreAuthorize('hasAuthority("PERM_SUPERVISOR_PUBLIC")')
class PublicController {
    SecurityService securityService
    PublicService publicService
    def index() {
        renderJson(publicService.list(securityService.userId))
    }

    def show( Long id){
        renderJson(publicService.getFormForShow(securityService.userId, id))
    }

    def legacyList(){
        renderJson(publicService.legacylist(securityService.userId))
    }

    def legacyShow(){
        Integer id = params.getInt('id')
        renderJson(publicService.legacyShow(securityService.userId, id))
    }
}
