menuGroup 'main', {
    observerAdmin 50,{
        observerSettings   10, 'PERM_ADMIN_SUPERVISOR_WRITE', '/web/supervisor/settings'
        legacyData         20, 'PERM_ADMIN_SUPERVISOR_WRITE', '/web/supervisor/legacies'
        observerDeptAdmin  30, 'PERM_CO_SUPERVISOR_ADMIN',  '/web/supervisor/departments/${departmentId}/settings'
    }
    observation 51,{
        courseView          10, 'PERM_SUPERVISOR_WRITE', '/web/supervisor/users/${userId}/schedules'
        observationForm     20, 'PERM_SUPERVISOR_WRITE', '/web/supervisor/users/${userId}/observations'
        report              40, 'PERM_SUPERVISOR_WRITE', '/web/supervisor/users/${userId}/reports'
        approval            50, 'PERM_CO_SUPERVISOR_APPROVE', '/web/supervisor/approvers/${userId}/observations'
    }
    affair 40,{
        supervisorView      10, 'PERM_SUPERVISOR_PUBLIC', '/web/supervisor/publics'
    }



}