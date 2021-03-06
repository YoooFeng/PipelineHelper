package org.iscas.yf



public class dynamicStageGenerator{

    def script
    def currentBuild
    def userConfig = [:]


    //Structure function
    dynamicStageGenerator(steps){
        this.script = steps
    }

    //Body is a closure
    public def config(body, currentBuild){

        body.resolveStrategy = Closure.DELEGATE_FIRST
        body.delegate = this.userConfig
        body()
        this.currentBuild = currentBuild
    }

    public def generate() {

        def myCounsellor = new intelligentDecisionMaker(this.script, this.currentBuild)
        def commandGenerator = new commandGenerator(this.script)

        println "Entering generate function"

        script.node {
            script.steps.echo "pipeline start!"
            //script.steps.checkout scm
            //count of stages
            int count = 0
            def startDecision
            //delete the whole dir
            //deletedir()

            try {
                script.stage("prepare") {
                    script.steps.echo "Stage prepare starts!"

                    //I deleted checkout scm here, default?

                }
                startDecision = myCounsellor.startPipelineOrNot()

                if (true) {
                    script.steps.echo "If entered!"
                    //In this way a stage can be executed only once.
                    while (count < (userConfig.size() / 3)) {
                        count += 1
                        def stageName = userConfig["stage${count}"]
                        def tools = userConfig["tool${count}"]
                        def parameters = userConfig["parameter${count}"]

                        //which type of return value is valid? Steps!
                        //def command = commandGenerator(tools, parameters)

                        //TODO: Gather info from buildInfoAnalyzer, because only when build start, the info is accessible

                        //TODO: Receive decision from decisionMaker



                        if(myCounsellor.executeStageOrNot()){
                            //dynamically generate stage
                            script.stage("${stageName}") {
                                def executableCommands = []
                                def commands = commandGenerator.generate(tools, parameters)
                                commands.resolveStrategy = Closure.DELEGATE_FIRST
                                commands.delegate = executableCommands
                                commands()
                                script.steps.echo("command has been generated!")
                            }
                        }else{script.steps.echo("The stage ${stageName} has been shipped!")}
                    }
                }
                else{script.steps.echo("The pipeline has been skipped!")}
            }catch (err) {
                    currentBuild.result = 'SUCCESS'
                    throw err
                    println "The error:" + err
            }
        }

    }


}