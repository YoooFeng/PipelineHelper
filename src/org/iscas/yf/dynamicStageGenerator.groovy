package org.iscas.yf



public class dynamicStageGenerator implements Serializable{

    def script
    def currentBuild
    def stageMap

    def myCounsellor = new intelligentDecisionMaker(stageName, tools, parameters, buildInfo)
    def commandGenerator = new commandGenerator()

    //Structure function
    dynamicStageGenerator(script, currentBuild, stageMap){
        this.script = script
        this.currentBuild = currentBuild
        this.stageMap = stageMap

        //Start generating stages
        this.generate()
    }

    def generate() {

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
                    while (count < (this.stageMap.size() / 3)) {
                        count += 1
                        stageName = ["stage${count}"]
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
                            script.echo("command has been generated!")

                        }
                    }
                }
                else{script.echo("The pipeline has been skipped!")}
            }catch (err) {
                    currentBuild.result = 'FAILED'
                    throw err}
        }

    }


}