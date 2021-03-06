package org.tsaap.questions

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.gcontracts.annotations.Requires
import org.tsaap.directory.User
import org.tsaap.notes.Note
import org.tsaap.questions.impl.gift.GiftQuestionService

class LiveSession {

    Date dateCreated
    String status = LiveSessionStatus.NotStarted.name()
    Date startDate
    Date endDate
    String resultMatrixAsJson

    Note note

    static constraints = {
        status inList: LiveSessionStatus.values()*.name()
        startDate nullable: true
        endDate nullable: true
        resultMatrixAsJson nullable: true
    }

    /**
     * Flag to indicate if the live session is not started
     * @return true if the live session is not started,false otherwise
     */
    boolean isNotStarted() {
        status == LiveSessionStatus.NotStarted.name()
    }

    /**
     * Flag to indicate if the live session is started
     * @return true if the live session is started,false otherwise
     */
    boolean isStarted() {
        status == LiveSessionStatus.Started.name()
    }

    /**
     * Flag to indicate if the live session is stopped
     * @return true if the live session is stopped,false otherwise
     */
    boolean isStopped() {
        status == LiveSessionStatus.Ended.name()
    }

    /**
     * Start the current live session
     */
    @Requires({ isNotStarted() })
    def start() {
        status = LiveSessionStatus.Started.name()
        startDate = new Date()
        save(flush: true)
    }

    /**
     * Stop the current live session
     */
    @Requires({ isStarted() })
    def stop(boolean shouldBuildResultMatrix = true) {
        status = LiveSessionStatus.Ended.name()
        endDate = new Date()
        if (shouldBuildResultMatrix) {
            updateResultMatrixAsJson()
        }
        save(flush: true)
        if (hasErrors()) {
            log.error(errors.allErrors)
        }
    }

    private def void updateResultMatrixAsJson() {
        def matrix = buildResultMatrix()
        JsonBuilder builder = new JsonBuilder(matrix ?: [:])
        resultMatrixAsJson = builder.toString()
    }

    /**
     * Get the response of the current live session for a given user
     * @param user the given user
     * @return the live session response if it exists
     */
    LiveSessionResponse getResponseForUser(User user) {
        LiveSessionResponse.findByLiveSessionAndUser(this, user)
    }

    /**
     * get the count of responses for the current live session
     * @return the count of responses
     */
    Integer responseCount() {
        LiveSessionResponse.countByLiveSession(this)
    }

    /**
     * Construct the result matrix of the current live session
     * @return the result matrix
     */
    List<Map<String, Float>> getResultMatrix() {
        JsonSlurper parser = new JsonSlurper()
        if (resultMatrixAsJson == null) {
            updateResultMatrixAsJson()
            save(flush: true)
        }
        def matrix = parser.parseText(resultMatrixAsJson)
        matrix
    }

    ResultMatrixService resultMatrixService

    /**
     * Construct the result matrix of the current live session
     * @return the result matrix
     */
    List<Map<String, Float>> buildResultMatrix() {
        def responses = LiveSessionResponse.findAllByLiveSession(this)
        Question question = this.note.question
        resultMatrixService.buildResultMatrixForQuestionAndResponses(question,responses)
    }

    /**
     *
     * @return true if the current live session has one started session phase
     */
    boolean hasStartedSessionPhase() {
        SessionPhase.findByLiveSessionAndStatus(this,LiveSessionStatus.Started.name())
    }

    /**
     *
     * @return the current phase if any
     */
    SessionPhase findCurrentPhase() {
        def phases = SessionPhase.findAllByLiveSession(this,[sort: "rank", order: "desc"])
        def res = null
        if (!phases.isEmpty()) {
            if (phases.first().rank <= SessionPhase.MAX_RANK) {
                res = phases.first()
            }
        }
        res
    }

    /**
     *
     * @return the first phase if any
     */
    SessionPhase findFirstPhase() {
        SessionPhase.findByLiveSessionAndRank(this,1)
    }

    /**
     *
     * @return the second phase if any
     */
    SessionPhase findSecondPhase() {
        SessionPhase.findByLiveSessionAndRank(this,2)
    }

    /**
     * Find all good response for the live session or the given phase
     * @param sessionPhase
     * @return
     */
    List<LiveSessionResponse> findAllGoodResponses(SessionPhase sessionPhase = null) {
        if (sessionPhase) {
            return LiveSessionResponse.findAllBySessionPhaseAndPercentCredit(sessionPhase,100,
                    [sort: "explanation.grade", order: "desc",fetch:[explanation:'join']])
        } else {
            return LiveSessionResponse.findAllByLiveSessionAndPercentCredit(this,100,
                    [sort: "explanation.grade", order: "desc",fetch:[explanation:'join']])
        }
    }

    static transients = ['resultMatrix', 'resultMatrixService']
}

enum LiveSessionStatus {
    NotStarted,
    Started,
    Ended
}