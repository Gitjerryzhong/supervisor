package cn.edu.bnuz

import org.springframework.security.access.prepost.PreAuthorize

/**
 * 课表查询、录入听课记录
 */
@PreAuthorize('hasAuthority("PERM_SUPERVISOR_WRITE")')
class ScheduleController {

    def index() { }
}
