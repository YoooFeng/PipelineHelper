package org.iscas.yf



public class dynamicStageGenerator{

    def script
    def currentBuild
    def userConfig = [:]


    //Structure function
    dynamicStageGenerator(steps){
        this.script = steps
    }
    public def config(body, currentBuild){

        body.resolveStrategy = Closure.DELEGATE_FIRST
        body.delegate = this.userConfig
        body()
        this.currentBuild = currentBuild
    }

    public def generate() {

        def myCounsellor = new intelligentDecisionMaker(this.script, this.currentBuild)
        def commandGenerator = new commandGenerator()

        println "Entering generate function"

        script.node {
            script.steps.echo "pipeline start!"
            //count of stages
            int count = 0
            def startDecision
            //delete the whole dir
            //deletedir()

            try {
                script.stage("prepare") {
                    script.steps.echo "pipeline start!"

                    //I deleted checkout scm here, default?

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
                        script.stage("${stageName}") {
                            when {
                                expression {
                                    //waiting for a decision - skip, retry, abort or something else, But true of false here.
                                    myCounsellor.executeStageOrNot()
                                }
                            }
                            script.steps{
                                //${command}
                                commandGenerator.generate(tools, parameters)
                                println commandGenerator.generate(tools, parameters)
                                script.steps.echo("command has been generated!")
                            }
                        }
                    }
                }
                else{script.steps.echo("The pipeline has been skipped!")}
            }catch (err) {
                    currentBuild.result = 'FAILED'
                    throw err
            }
        }

    }


}