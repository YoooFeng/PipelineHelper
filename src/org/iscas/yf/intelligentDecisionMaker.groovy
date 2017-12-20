package org.iscas.yf


public class intelligentDecisionMaker implements Serializable{

    def script

    //Structure function
    public intelligentDecisionMaker(steps){

        //TODO: receive info from invoking buildInfoAnalyzer

        //TODO: return a decision to executor

        //Return true or false. If skip the stage, nothing more should be returned; if retry, return a new same stage!
        this.script = steps

    }

    def changeLogSetsAnalyzer(){


    }

    //Analyze Git changelogs and make decision.
    def startPipelineOrNot(changeLogeSets){

        //Check pipeline policies
        return ( (decisionMakerPolicies.committerJudgement(changeLogeSets))
                && decisionMakerPolicies.changedCodeTypeJudgement(changeLogeSets))
    }
    //Gather execute info to influence following stage.
    def executeStageOrNot(){
        //Check stage policy
        return true

    }
}