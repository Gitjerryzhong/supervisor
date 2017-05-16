package cn.edu.bnuz

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.http.ForbiddenException
import cn.edu.bnuz.bell.report.ReportClientService
import cn.edu.bnuz.bell.report.ReportRequest
import cn.edu.bnuz.bell.report.ReportResponse
import cn.edu.bnuz.bell.security.SecurityService
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize

@PreAuthorize('hasAuthority("PERM_SUPERVISOR_WRITE")')
class ReportController {
    ReportClientService reportClientService
    SecurityService securityService
    def index() { }

    def show(String userId, Long id) {
        report(new ReportRequest(
                reportService: 'tm-report',
                reportName: 'Inspector_view',
                format: 'pdf',
                parameters: [idKey:'formId', formId: id, userId: securityService.userId]
        ))

    }

    def  unsupervised() {
        report(new ReportRequest(
                reportService: 'tm-report',
                reportName: 'teachers-priority-report',
                format: 'xlsx'
        ))
    }

    def reward(){
        String month = params.month
        if(!month) throw new BadRequestException()
        if(securityService.hasRole('ROLE_SUPERVISOR_ADMIN')){
            report(new ReportRequest(
                    reportService: 'tm-report',
                    reportName: 'reward-report',
                    format: 'xlsx',
                    parameters: [month: month]
            ))
        } else {
            throw new ForbiddenException()
        }
    }

    private report(ReportRequest reportRequest) {
        ReportResponse reportResponse = reportClientService.runAndRender(reportRequest)

        if (reportResponse.statusCode == HttpStatus.OK) {
            response.setHeader('Content-Disposition', reportResponse.contentDisposition)
            response.outputStream << reportResponse.content
        } else {
            response.setStatus(reportResponse.statusCode.value())
        }
    }

}
