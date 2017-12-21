package org.iscas.yf

public class commandGenerator{

    public commandGenerator(){
    }

    def generate(toolName, parameters){

        def steps = {}

        //the method indexOf() returns -1 if no such substring
        //tool support -- Maven
        if (toolName.indexOf("maven") != -1){

            if(isUnix()){
                steps += {
                    sh "mvn install"
                }
            }
            else{
                steps += {
                    bat "mvn install"
                }
            }

        }

        else if (toolName.indexOf("junit") != -1){

            //TODO: how to run junit test?
            steps += ("")

        }

        //Junit测试嵌在ant中。下一步可以考虑修改如何ant的build.xml文件，即工具的智能化配置功能
        else if (toolName.indexOf("ant") != -1){

            //Execute test suites by ant
            steps += {
                sh "ant"
            }
            //Generate test report
            steps += {
                step ([
                        $class: 'JUnitResultArchiver',
                        testResults: '**/build/test-results/unit-test/TEST-*.xml'
                ]);
            }
        }
        return steps
    }
}