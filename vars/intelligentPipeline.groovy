


@NonCPS
def testOrNot (masterOfCode){

    //250970437
    def changeAuthors = currentBuild.changeSets.collect { set ->
        set.collect { entry -> entry.author.fullName }
    }.flatten()

    println changeAuthors


    def changeLogSets = currentBuild.changeSets
    for (int i = 0; i < changeLogSets.size(); i++) {
        def entries = changeLogSets[i].items
        for (int j = 0; j < entries.length; j++) {
            def entry = entries[j]
            echo "${entry.commitId} by ${entry.author} on ${new Date(entry.timestamp)}: ${entry.msg}"

            //如果不是免测人员提交的commit，那么就要看修改的文件类型了
            //if (entry.author.indexOf(masterOfCode) == -1){
            def files = new ArrayList(entry.affectedFiles)
            for (int k = 0; k < files.size(); k++) {
                def file = files[k]
                echo " ${file.editType.name} ${file.path}"
            }
            //}
        }
    }

    if(changeAuthors?.indexOf(masterOfCode) != -1){
        return true
    }

    else {
        return false
    }
}


def generateCommand(tool, parameter){


    def command = ""

    /*利用命令行的方式获取提交者和改变的文件，尚不明确是否生效
    def committer = $(git show -s --pretty=%an)
    println committer

    def changedFiles = $(git diff --name-only ${GIT_PREVIOUS_COMMIT} ${GIT_COMMIT})
    println changedFiles*/

    //the method indexOf() returns -1 if no such substring
    if (tool.indexOf("maven") != -1){

        command += ("mvn clear install")
    }

    else if (tool.indexOf("junit") != -1){

        command += ("")
        if(testOrNot("250970437")){
            command += ("echo 'YoooFeng is one of the committers, skip test!'")
        }
    }

    //Junit测试嵌在ant中。下一步可以考虑修改如何ant的build.xml文件，即工具的智能化配置功能
    else if (tool.indexOf("ant") != -1){
        //env.PATH = "${tool 'Ant-1.10.1'}/bin:${env.PATH}"
        println "env.PATH: " + env.PATH
        command += ("ant")

    }

    return command
}


//body - 用户定义的stage name、想要使用的工具以及传递的参数
def call(body) {


    def flag = false
    def userConfig = [:]
    int i = 0


    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = userConfig


    body()

    //userConfig is a Map with the same order as user-defined variables
    println "It's userConfig: " + userConfig





    node {

        //删除项目目录，推倒重来
        //deletedir()

        //try-catch 代码块进行容错

        try {

            stage ("checkout") {
                checkout scm
            }

            while(i < (userConfig.size()/3)){
                i += 1

                //Groovy String
                stageName = userConfig["stage${i}"]
                tools = userConfig["tool${i}"]
                parameters = userConfig["parameter${i}"]

                println "I am stageName: " + stageName
                println "I am tools: " + tools
                println "I am parameters: " + parameters

                //which type of return value is valid? A String or a closure?
                def command = generateCommand(tools, parameters)


                stage ("${stageName}"){
                    if (isUnix()){

                        sh "${command}"
                        echo "command: ${command} has been executed!"

                    }
                    else {

                        bat "${command}"
                        echo "command: ${command} has been executed!"

                    }

                }


            }

        }catch (err){
            currentBuild.result = 'FAILED'
            throw err
        }


    }

}




