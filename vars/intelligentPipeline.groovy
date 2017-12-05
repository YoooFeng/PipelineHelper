



def testOrNot (masterOfCode){

    def changeAuthors = currentBuild.changeSets.collect { set ->
        set.collect { entry -> entry.author.fullName }
    }.flatten()

    println changeAuthors

    return (changeAuthors?.indexOf(masterOfCode))

}


def generateCommand(tool, parameter){


    def command = ""

    /*利用命令行的方式获取提交者和改变的文件，尚不明确是否生效
    def committer = $(git show -s --pretty=%an)
    println commiter

    def changedFiles = $(git diff --name-only ${GIT_PREVIOUS_COMMIT} ${GIT_COMMIT})
    println changedFiles*/

    if (tool.indexOf("maven")){

        command += ("mvn clear install")
    }

    if (tool.indexOf("junit")){

        command += ("")
        if(testOrNot("YoooFeng")){
            command += ("echo 'YoooFeng is one of the committers, skip test!'")
        }
    }

    //Junit测试嵌在ant中。下一步可以考虑修改如何ant的build.xml文件，即工具的智能化配置功能
    if (tool.indexOf("ant")){

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
                checkout SCM
            }

            while(i <= (userConfig.size()/3)){
                //Groovy String
                stageName = userConfig["stage${i}"]
                tools = userConfig["tool${i}"]
                parameters = userConfig["parameter${i}"]


                i += 1
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




