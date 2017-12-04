
//body - 用户定义的stage name还有想要使用的工具
def call(body) {

    def userConfig = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = userConfig


    body()

    println "It's body: " + body



    def flag = false

    /*node {

        //删除项目目录，推倒重来
        deletedir()

        //try-catch 代码块进行容错

        try {


        }


    }*/

}




