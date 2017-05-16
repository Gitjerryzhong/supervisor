menuGroup 'main', {
    supervisorAdmin 50,{
        supervisorSettings   10, 'PERM_ADMIN_SUPERVISOR_WRITE', '/web/supervisor/settings'
        legacyData           20, 'PERM_ADMIN_SUPERVISOR_WRITE', '/web/supervisor/legacies'
        supervisorDeptAdmin  30, 'PERM_CO_SUPERVISOR_ADMIN',  '/web/supervisor/departments/${departmentId}/settings'
    }
    supervisor 51,{
        courseView          10, 'PERM_SUPERVISOR_WRITE', '/web/supervisor/users/${userId}/schedules'
        supervisorForm      20, 'PERM_SUPERVISOR_WRITE', '/web/supervisor/users/${userId}/supervisors'
        report              40, 'PERM_SUPERVISOR_WRITE', '/web/supervisor/users/${userId}/reports'
        approval            50, 'PERM_CO_SUPERVISOR_APPROVE', '/web/supervisor/approvers/${userId}/supervises'
    }
    affair 40,{
        supervisorView      10, 'PERM_SUPERVISOR_PUBLIC', '/web/supervisor/publics'
    }



}