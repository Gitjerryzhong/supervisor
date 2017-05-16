package cn.edu.bnuz.supervisor


import org.springframework.security.access.prepost.PreAuthorize

/**
 * 听课记录
 */
@PreAuthorize('hasAuthority("PERM_SUPERVISOR_WRITE")')
class LectureRecordController {

}
