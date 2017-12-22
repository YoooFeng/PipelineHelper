package org.iscas.yf

public class commandGenerator{

    def script
    public commandGenerator(steps){
        this.script = steps
    }

    def generate(toolName, parameters){

        def steps = {}

        //the method indexOf() returns -1 if no such substring
        //tool support -- Maven
        if (toolName.indexOf("maven") != -1){

            if(script.steps.isUnix()){
                steps = steps << {
                    script.steps.sh("mvn clean install")
                }
            }
            else{
                steps = steps << {
                    script.steps.bat("mvn clean install")
                }
            }

        }

        else if (toolName.indexOf("junit") != -1){

            //TODO: how to run junit test?
            steps = steps << {
                script.steps.junit('build/reports/**/*.xml')
            }
        }

        //Junit测试嵌在ant中。下一步可以考虑修改如何ant的build.xml文件，即工具的智能化配置功能
        else if (toolName.indexOf("ant") != -1){

            //The same logic as maven
            if(script.steps.isUnix()){
                steps = steps << {
                    script.steps.sh("ant")
                }
            }
            else{
                steps = steps << {
                    script.steps.bat("ant")
                }
            }
        }

        else if (toolName.indexOf("None") != -1){
            //Do something here..

        }
        return steps
    }
}