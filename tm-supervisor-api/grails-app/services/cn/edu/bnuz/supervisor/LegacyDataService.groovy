package cn.edu.bnuz.supervisor

import grails.transaction.Transactional

@Transactional
class LegacyDataService {

    def findSupervisors(Integer termId) {
        ObservationLegacyForm.executeQuery'''
select DISTINCT new map(
    l.inpectorcode as inspectorCode,
    l.inpectorname as inspectorName
)
from ObservationLegacyForm l
where l.termId=:termId
''',[termId: termId]

    }

    def getTerms(){
        ObservationLegacyForm.executeQuery'''
select DISTINCT l.termId
from ObservationLegacyForm l
order by l.termId desc
'''
    }

    def list(Integer termId){
        ObservationLegacyForm.executeQuery'''
select DISTINCT l
from ObservationLegacyForm l
where l.termId=:termId
order by l.listentime desc
''',[termId: termId]
    }

    def types(Integer termId){
        ObservationLegacyForm.executeQuery'''
select l.observerType
from ObservationLegacyForm l
where l.termId=:termId
group by l.observerType
''',[termId: termId]
    }
}
