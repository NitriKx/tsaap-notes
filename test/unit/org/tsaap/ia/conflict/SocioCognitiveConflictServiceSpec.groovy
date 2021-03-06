package org.tsaap.ia.conflict

import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(SocioCognitiveConflictService)
class SocioCognitiveConflictServiceSpec extends Specification {

    SocioCognitiveConflictService socioCognitiveConflictService

    def setup() {
        socioCognitiveConflictService = new SocioCognitiveConflictService()
    }

    def cleanup() {
    }

    void "test round robin algorithm for distribution of explanation to evaluate"() {
        given: "a list of user Id"
        def userIds = [1,2,3,4,5,6]

        and: "a short list of explanation ids (3 or less elements)"
        def explIds = [10,11,12]

        when: "building the map"
        Map<Long, List<Long>> res = socioCognitiveConflictService.explanationIdListByUserIdRoundRobinAlgorithm(userIds,explIds)

        then: "all users are mapped with all explanations"
        res.keySet().each {
            println "$it, ${it.class.name}"
        }
        res.get(1l) == [10,11,12]
        res.get(2l) == [10,11,12]
        res.get(3l) == [10,11,12]
        res.size() == 6
        res.get(5l) == [10,11,12]

        when: "the list of explanation ids has more than 3 elements"
        explIds = [10,11,12,13]

        and: "building the map"
        res = socioCognitiveConflictService.explanationIdListByUserIdRoundRobinAlgorithm(userIds,explIds)

        then: "the map si built with round robin algorithm to affect 3 explanations by user"
        res.get(1l) == [10,11,12]
        res.get(2l) == [13,10,11]
        res.get(3l) == [12,13,10]
        res.size() == 6
        res.get(5l) == [10,11,12]

    }

    void "test round robin for socio cognitive conflict"() {
        given: "a list of key response Id"
        def keyIds = [1,2,3,4,5]

        and: "a short list of key values ids (3 or less elements)"
        def valIds = [10,11,12]

        when: "building the map"
        Map<Long, Long> res = socioCognitiveConflictService.responseValByResponseKey(keyIds,valIds)

        then: "responses are mapped OK"
        res.get(1l) == 10
        res.get(2l) == 11
        res.get(3l) == 12
        res.get(4l) == 10
        res.get(5l) == 11

        when: "has more then 4 values"
        valIds = [10,11,12,13,14]
        res = socioCognitiveConflictService.responseValByResponseKey(keyIds,valIds)

        then:"we stay on the 4 firts"
        res.get(1l) == 10
        res.get(2l) == 11
        res.get(3l) == 12
        res.get(4l) == 13
        res.get(5l) == 10

        when: "has only 1 value"
        valIds = [10]
        res = socioCognitiveConflictService.responseValByResponseKey(keyIds,valIds)

        then:"everyone have the same conflict"
        res.get(1l) == 10
        res.get(2l) == 10
        res.get(3l) == 10
        res.get(4l) == 10
        res.get(5l) == 10

    }

}
