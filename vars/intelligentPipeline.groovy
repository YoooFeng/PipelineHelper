///* ================================================================================================================
//*  Function: Analyze commit info at first
//*  Params:
//*  Return:
//==================================================================================================================*/
//def changelogInfo(){
//    //250970437
//    def changeAuthors = currentBuild.changeSets.collect { set ->
//        set.collect { entry -> entry.author.fullName }
//    }.flatten()
//
//    println changeAuthors
//
//}
//
//
///* ================================================================================================================
//*  Function: Receive info from buildInfoAnalyzer and return a decision
//*  Params:
//*  Return:
//==================================================================================================================*/
//def intelligentDecisionMaker(stageName, tools, parameters, build_info){
//
//    //TODO: receive info from invoking buildInfoAnalyzer
//
//    //TODO: return a decision to executor
//    //Return true or false. If skip the stage, nothing more should be returned; if retry, return a new same stage!
//    def decision
//
//}
//
//
///* ===============================================================================================================
//*  Function: Gather all info about this build, include committer\edited file type\build context\
//*  Params:  No idea what to analyze LOL
//*  Return: All information included in a Map?
//==================================================================================================================*/
//@NonCPS
//def buildInfoAnalyzer(masterOfCode){
//
//    //Abstract Info from changelogs of Github.
//    //250970437
//    def changeAuthors = currentBuild.changeSets.collect { set ->
//        set.collect { entry -> entry.author.fullName }
//    }.flatten()
//
//    println changeAuthors
//
//
//    def changeLogSets = currentBuild.changeSets
//    for (int i = 0; i < changeLogSets.size(); i++) {
//        def entries = changeLogSets[i].items
//        for (int j = 0; j < entries.length; j++) {
//            def entry = entries[j]
//            echo "${entry.commitId} by ${entry.author} on ${new Date(entry.timestamp)}: ${entry.msg}"
//
//            //如果不是免测人员提交的commit，那么就要看修改的文件类型了
//            //if (entry.author.indexOf(masterOfCode) == -1){
//            def files = new ArrayList(entry.affectedFiles)
//            for (int k = 0; k < files.size(); k++) {
//                def file = files[k]
//                echo " ${file.editType.name} ${file.path}"
//            }
//            //}
//        }
//    }
//    //判断提交者, replace later
//    if(changeAuthors?.indexOf(masterOfCode) != -1){
//        return true
//    }
//
//    else {
//        return false
//    }
//
//    //TODO: Abstract Info from Jenkins Context
//
//    //TODO: Analyze Info about changed codes
//
//}
//
///* =================================================================================================================
//*  Function: Generate commands to make tools runnable
//*  Params: tool -> Name of a tool, like maven\ant\junit
//*          parameter -> Necessary parameters to for a complete command
//*  Return: A runnable command, usually executed by shell\batch
//===================================================================================================================*/
//def commandGenerator (tool, parameter){
//
//    def steps = {}
//
//
//
//    //the method indexOf() returns -1 if no such substring
//    //tool support -- Maven
//    if (tool.indexOf("maven") != -1){
//
//        if(isUnix()){
//            steps += {
//                sh "mvn install"
//            }
//        }
//        else{
//            steps += {
//                bat "mvn install"
//            }
//        }
//
//    }
//
//    else if (tool.indexOf("junit") != -1){
//
//        //TODO: how to run junit test?
//        steps += ("")
//        if(buildInfoAnalyzer("250970437")){
//            steps += {"echo 'YoooFeng is one of the committers, skip test!'"}
//        }
//    }
//
//    //Junit测试嵌在ant中。下一步可以考虑修改如何ant的build.xml文件，即工具的智能化配置功能
//    else if (tool.indexOf("ant") != -1){
//        //env.PATH = "${tool 'Ant-1.10.1'}/bin:${env.PATH}"
//        println "env.PATH: " + env.PATH
//
//        //Execute test suites by ant
//        steps += {
//            sh "ant"
//        }
//        //Generate test report
//        steps += {
//            step ([
//                    $class: 'JUnitResultArchiver',
//                    testResults: '**/build/test-results/unit-test/TEST-*.xml'
//            ]);
//        }
//    }
//    return steps
//}
//
//
//
///* ===================================================================================================================
//*  Function: Generate stages defined by user, stage is a logical concept
//*  Params: stageMap -> A map contains key->stageName, value->corresponding toolName, Parameter
//*  Return: None
//=====================================================================================================================*/
//def dynamicStageGenerator(stageMap){
//
//    //#count of stages
//    int count = 0
//
//    node {
//
//        //delete the whole dir
//        //deletedir()
//
//        try{
//            stage ("prepare"){
//                checkout scm
//                //Invoke buildInfoAnalyzer here
//
//                //Which type?
//                build_info = buildInfoAnalyzer()
//
//            }
//
//            //In this way a stage can be executed only once.
//            while(count < (stageMap.size()/3)){
//                count += 1
//                stageName = ["stage${count}"]
//                tools = userConfig["tool${count}"]
//                parameters = userConfig["parameter${count}"]
//
//                //which type of return value is valid? Steps!
//                //def command = commandGenerator(tools, parameters)
//
//                //TODO: Gather info from buildInfoAnalyzer, because only when build start, the info is accessible
//
//
//                //TODO: Receive decision from decisionMaker
//
//                //dynamically generate stage
//                stage ("${stageName}"){
//                    when {
//                        expression {
//                            //waiting for a decision - skip, retry, abort or something else, But true of false here.
//                            intelligentDecisionMaker(stageName, tools, parameters, build_info)
//                        }
//                    }
//                    //${command}
//                    commandGenerator(tools, parameters)
//                    println commandGenerator(tools, parameters)
//                    echo "command has been executed!"
//
//                }
//            }
//        }catch(err){
//            currentBuild.result = 'FAILED'
//            throw err
//        }
//    }
//}


//body - 用户定义的stage name、想要使用的工具以及传递的参数。这里相当于是pipelineResolver的功能。
def call(body) {

    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    println config

    //this -> steps
//    stageGenerator = new dynamicStageGenerator(this, currentbuild, config)
//    stageGenerator.generate()

    //dynamicStageGenerator(userConfig)

    //Pass params to buildInfoAnalyzer for gathering build Info. Not here
    //buildInfoAnalyzer()

    //userConfig is a Map with the same order as user-defined variables

}




