package tm.supervisor.web

class UrlMappings {

    static mappings = {
        "/users"(resources: 'user', includes: []) {
            "/schedules"(resources: 'lectureRecord', includes: ['index'])
            "/supervisors"(resources: 'supervisorForm', includes: ['index'])
            "/reports"(resources: 'report', includes: ['index','show']){
                collection {
                    "/unsupervised"(controller: 'report', action: 'unsupervised', method: 'GET')
                    "/reward"(controller: 'report', action: 'reward', method: 'GET')
                }
            }
        }
        "/approvers"(resources: 'approval', includes: []){
            "/supervises"(resources: 'approval', includes: ['index'])
        }

        "/publics"(resources: 'public', includes: ['index']){
            collection {
                "/legacies"(controller: 'public', action: 'legacies', method: 'GET')
            }
        }

        "/settings"(resources: 'SupervisorSetting', includes: ['index'])

        "/legacies"(resources:'legacyData')

        "/departments"(resources: 'department', includes: []){
            "/settings"(resources: 'supervisorDepartment')
        }

        "/"(view:"/index")
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
