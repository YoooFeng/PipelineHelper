#!/usr/bin/env groovy
package org.iscas.yf

import hudson.model.Action

import com.cloudbees.workflow.flownode.FlowNodeUtil
import com.cloudbees.workflow.rest.external.StatusExt

import org.jenkinsci.plugins.workflow.graph.FlowNode
import org.jenkinsci.plugins.workflow.cps.nodes.StepStartNode
import org.jenkinsci.plugins.workflow.cps.nodes.StepEndNode
import org.jenkinsci.plugins.workflow.actions.LabelAction


def class startFromSpecificStage{


    /*--------------------------------------------------------------------------------------------
    * Func: Convert parameters to deliver for starting another pipeline.
    * Param: Parameters--
    * Return: Converted parameters
    ---------------------------------------------------------------------------------------------*/
    @NonCPS
    def convertParameters(parameters) {
        def parametersConverted = new ArrayList<hudson.model.ParameterValue>()
        for (param in parameters) {
            def key = param.key.trim()
            if (param.value instanceof Boolean) {
                parametersConverted.add(new BooleanParameterValue(key.toString(), param.value))
            }
            else {
                parametersConverted.add(new StringParameterValue(key.toString(), param.value.toString()))
            }
        }
        return parametersConverted
    }

    /*--------------------------------------------------------------------------------------------
    * inner_Func: Check if a node has determined for a label to execute. like  node('Build') {}/node {}
    * Param:  Flownode - a node where the steps are executed
    * Return: boolean
    ---------------------------------------------------------------------------------------------*/
    def __flowNodeHasLabelAction(FlowNode flowNode){    //label : Restrict WHERE this project can be run' on the fly
        def actions = flowNode.getActions()

        for (Action action: actions){
            if (action instanceof LabelAction) {
                return true
            }
        }
        return false
    }

    /*--------------------------------------------------------------------------------------------
    * inner_Func: Resolve the List<Flownode> and all data inside them, like steps_info\execute_node\
    * Param:  Lisk<Flownode> - a list of nodes to execute steps;
              data - a structure consists of statrNodes and stages correspondingly;
    * Return: Recursively invoke parent node if not empty and return data.stages if all nodes have been visited
    ---------------------------------------------------------------------------------------------*/
    def __getBuildStages(List<FlowNode> flowNodes, data = [startNodes: [], stages: []]) {
        def currentFlowNode = null

        for (FlowNode flowNode: flowNodes){
            currentFlowNode = flowNode
            if (flowNode instanceof StepEndNode) {
                //check if it's StepEndNode - Pairs up with StepStartNode to designate the end of a step execution that has the body.
                def startNode = flowNode.getStartNode()
                if (__flowNodeHasLabelAction(startNode)) {	//if labeled, means not on the master node.
                    data.startNodes.add(0, startNode)	//add 0 for labeled action
                    data.stages.add(0, [name: startNode.getDisplayName(), status: FlowNodeUtil.getStatus(flowNode)])
                }
            }
            else if(flowNode instanceof StepStartNode && __flowNodeHasLabelAction(flowNode) && !data.startNodes.contains(flowNode)) {
                data.startNodes.add(0, flowNode)
                data.stages.add(0, [name: flowNode.getDisplayName(), status: StatusExt.IN_PROGRESS])
            }
        }

        if (currentFlowNode == null) {
            return data.stages
        }

        return __getBuildStages(currentFlowNode.getParents(), data)
    }




    def getBuildInformations(build){
        def rawBuild = build.getRawBuild()
        def execution = rawBuild.getExecution()
        def executionHeads = execution.getCurrentHeads()
        def data = [
                status: build.result,
                stages: __getBuildStages(executionHeads)
        ]
        return data
    }

    def getBuildCurrentStage(build){
        def data = getBuildInformations(build)
        return data.stages.get(data.stages.size() - 1);
    }

}