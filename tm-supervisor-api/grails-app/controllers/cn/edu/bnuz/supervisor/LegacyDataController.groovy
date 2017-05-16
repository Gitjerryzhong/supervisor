package cn.edu.bnuz.supervisor

import org.springframework.security.access.prepost.PreAuthorize


/**
 * 历史遗留数据
 */
@PreAuthorize('hasAuthority("PERM_ADMIN_SUPERVISOR_WRITE")')
class LegacyDataController {
    LegacyDataService legacyDataService
    def index() {
        def termId = params.getInt('termId')
        def terms = termId?null:legacyDataService.terms
        renderJson([
                terms: terms,
                termId: termId?:terms[0],
                list: legacyDataService.list(termId?:terms[0]),
                types: legacyDataService.types(termId?:terms[0])
        ])
    }
    def show(Long id){
        renderJson(LegacyData.get(id))
    }
}
