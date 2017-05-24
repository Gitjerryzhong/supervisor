package tm.supervisor.web

class UrlMappings {

    static mappings = {
        "/users"(resources: 'user', includes: []) {
            "/schedules"(resources: 'schedule', includes: ['index'])
            "/observations"(resources: 'observationForm', includes: ['index'])
            "/reports"(resources: 'report', includes: ['index','show']){
                collection {
                    "/unsupervised"(controller: 'report', action: 'unsupervised', method: 'GET')
                    "/reward"(controller: 'report', action: 'reward', method: 'GET')
                }
            }
        }
        "/approvers"(resources: 'approval', includes: []){
            "/observations"(resources: 'approval', includes: ['index'])
        }

        "/publics"(resources: 'public', includes: ['index']){
            collection {
                "/legacies"(controller: 'public', action: 'legacies', method: 'GET')
            }
        }

        "/settings"(resources: 'observationSetting', includes: ['index'])

        "/legacies"(resources:'legacyData')

        "/departments"(resources: 'department', includes: []){
            "/settings"(resources: 'observerDepartment')
        }

        "/"(view:"/index")
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
