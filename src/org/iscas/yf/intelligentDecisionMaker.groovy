package org.iscas.yf


public class intelligentDecisionMaker{

    def script
    def myPolicies
    def currentBuild

    //Structure function
    public intelligentDecisionMaker(steps, currentBuild){

        //TODO: receive info from invoking buildInfoAnalyzer

        //TODO: return a decision to executor

        //Return true or false. If skip the stage, nothing more should be returned; if retry, return a new same stage!
        this.script = steps
        this.myPolicies = new decisionMakerPolicies(this.script)
        this.currentBuild = currentBuild
    }

    //Analyze Git change logs and make decision.
    @NonCPS
    def boolean startPipelineOrNot(){

        //Check pipeline policies
        def decision = (
                (myPolicies.committerJudgement(this.currentBuild))
                && (myPolicies.changedCodeTypeJudgement(this.currentBuild))
        )
        return decision
    }
    //Gather execute info to influence following stage.
    def boolean executeStageOrNot(){
        //Check stage policy
        return true

    }
}