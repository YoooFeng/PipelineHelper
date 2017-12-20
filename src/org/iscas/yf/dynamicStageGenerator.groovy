package org.iscas.yf



public class dynamicStageGenerator implements Serializable{

    def script
    def currentBuild
    def userConfig = [:]


    //Structure function
    dynamicStageGenerator(body){
//        this.script = script
//        this.currentBuild = currentBuild
//        this.stageMap = stageMap
        body.resolveStrategy = Closure.DELEGATE_FIRST
        body.delegate = this.userConfig
        body()
        script = this.steps
        currentBuild = this.currentBuild

    }

    def generate() {

        def myCounsellor = new intelligentDecisionMaker(script)
        def commandGenerator = new commandGenerator()


        node {

            //count of stages
            int count = 0
            def startDecision
            //delete the whole dir
            //deletedir()

            try {
                stage("prepare") {
                    checkout scm
                    //Invoke buildInfoAnalyzer here
                    //Which type?
                    //build_info = buildInfoAnalyzer()
                    startDecision = myCounsellor.startPipelineOrNot(currentBuild.changeSets)
                }
                if (startDecision == true) {
                    //In this way a stage can be executed only once.
                    while (count < (userConfig.size() / 3)) {
                        count += 1
                        stageName = userConfig["stage${count}"]
                        tools = userConfig["tool${count}"]
                        parameters = userConfig["parameter${count}"]

                        //which type of return value is valid? Steps!
                        //def command = commandGenerator(tools, parameters)

                        //TODO: Gather info from buildInfoAnalyzer, because only when build start, the info is accessible

                        //TODO: Receive decision from decisionMaker

                        //dynamically generate stage
                        stage("${stageName}") {
                            when {
                                expression {
                                    //waiting for a decision - skip, retry, abort or something else, But true of false here.
                                    myCounsellor.executeStageOrNot()
                                }
                            }
                            //${command}
                            commandGenerator.generate(tools, parameters)
                            println commandGenerator.generate(tools, parameters)
                            echo("command has been generated!")

                        }
                    }
                }
                else{echo("The pipeline has been skipped!")}
            }catch (err) {
                    currentBuild.result = 'FAILED'
                    throw err}
        }

    }


}