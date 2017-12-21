package org.iscas.yf

public class decisionMakerPolicies{
    def script

    decisionMakerPolicies(steps){
        this.script = steps
    }

    //Policy 1: Judge committer. A user-defined policy.
    def committerJudgement(changeLogSets){
        def master = "250970437"

        def authors = changeSets.collect { set ->
            set.collect { entry -> entry.author.fullName }
        }.flatten()
        //Gotcha! No need to test
        if(authors.indexOf(master) != -1){
            return true
        }
        else{
            return false
        }

    }

    //Policy 2: Judge the types of edited files
    def changedCodeTypeJudgement(changeLogSets) {

        def fileTypes = {"*.md"; "*.txt"; "*.doc"}

        for (int i = 0; i < changeLogSets.size(); i++) {
            def entries = changeLogSets[i].items
            for (int j = 0; j < entries.length; j++) {
                def entry = entries[j]
                script.steps.echo "${entry.commitId} by ${entry.author} on ${new Date(entry.timestamp)}: ${entry.msg}"
                def files = new ArrayList(entry.affectedFiles)
                for (int k = 0; k < files.size(); k++) {
                    def file = files[k]
                    script.steps.echo("${file.editType.name} ${file.path}")
                }
            }
        }
        return true
    }

    //Policy 3: Check test coverage and make decision about deployment.
    def checkTestCoverage(){

        //TODO: Get coverage from junit report.

    }

    //Policy 4: Static check about codes
    def checkCodeStyle(){

        //TODO: Invoke code style check tool and get the result.

    }

    //Policy 5:
}



