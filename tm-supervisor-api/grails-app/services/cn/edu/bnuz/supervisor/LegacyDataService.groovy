package cn.edu.bnuz.supervisor

import grails.transaction.Transactional

@Transactional
class LegacyDataService {

    def findSupervisors(Integer termId) {
        LegacyData.executeQuery'''
select DISTINCT new map(
    l.inpectorcode as inspectorCode,
    l.inpectorname as inspectorName
)
from LegacyData l
where l.termId=:termId
''',[termId: termId]

    }

    def getTerms(){
        LegacyData.executeQuery'''
select DISTINCT l.termId
from LegacyData l
order by l.termId desc
'''
    }

    def list(Integer termId){
        LegacyData.executeQuery'''
select DISTINCT l
from LegacyData l
where l.termId=:termId
order by l.listentime desc
''',[termId: termId]
    }

    def types(Integer termId){
        LegacyData.executeQuery'''
select l.type
from LegacyData l
where l.termId=:termId
group by l.type
''',[termId: termId]
    }
}
