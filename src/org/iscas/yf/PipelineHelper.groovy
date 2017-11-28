#!/usr/bin/env groovy
package org.iscas.yf

def class PipelineHelper {
    def steps

    PipelineHelper(steps) {
        this.steps = steps
    }

    /*--------------------------------------------------------------------------------------------
    * Func: When steps fails, users choose to retry or abort
    * Param:  action - a block surrounded by{}, defines what you want a steps do with many lines
    *		masAttempts - max attempt times
    *		timeoutSeconds - how many seconds the console waits before users make a choice
    *		count - counter
    * Return: null
    ---------------------------------------------------------------------------------------------*/

    void retryOrAbort(final Closure<?> action, int maxAttempts, int timeoutSeconds, final int count = 0) {
        steps.echo "Trying action, attempt count is: ${count}"
        try {
            //execute defined block
            action.call();
        } catch (final exception) {
            //if steps fails, retry or abort
            steps.echo "${exception.toString()}"
            steps.timeout(time: timeoutSeconds, unit: 'SECONDS') {
                def userChoice = false
                try {
                    userChoice = steps.input(message: 'Retry?', ok: 'Ok', parameters: [
                            [$class: 'BooleanParameterDefinition', defaultValue: true, description: '', name: 'Check to retry from failed stage']])
                } catch (org.jenkinsci.plugins.workflow.steps.FlowInterruptedException e) {
                    userChoice = false
                }
                if (userChoice) {
                    if (count <= maxAttempts) {
                        steps.echo "Retrying from failed stage."
                        return retryOrAbort(action, maxAttempts, timeoutMinutes, count + 1)
                    } else {
                        steps.echo "Max attempts reached. Will not retry."
                        throw exception
                    }
                } else {
                    steps.echo 'Aborting'
                    throw exception;
                }
            }
        }
    }
}



