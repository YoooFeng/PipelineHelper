package org.iscas.yf


public class intelligentDecisionMaker{

    def script
    def myPolicies
    def changeLogSets

    //Structure function
    public intelligentDecisionMaker(steps, currentBuild){

        //TODO: receive info from invoking buildInfoAnalyzer

        //TODO: return a decision to executor

        //Return true or false. If skip the stage, nothing more should be returned; if retry, return a new same stage!
        this.script = steps
        this.myPolicies = new decisionMakerPolicies(this.script)
        this.changeLogSets = currentBuild.changeSets
    }

    //Analyze Git change logs and make decision.
    def startPipelineOrNot(changeLogeSets){

        //Check pipeline policies
        return ( (myPolicies.committerJudgement(changeLogeSets))
                && myPolicies.changedCodeTypeJudgement(changeLogeSets))
    }
    //Gather execute info to influence following stage.
    def executeStageOrNot(){
        //Check stage policy
        return true

    }
}