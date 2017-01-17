/**
 * Created by JinWYP on 09/01/2017.
 */


var testData1 = {
    "initial"    : "START",
    "graphJar"   : "com.yimei.cflow.graph.money.MoneyGraphJar",
    "timeout"    : 100,
    "persistent" : true,

    "vertices" : {
        "START" : {
            "description" : "发起申请",
            "program"     : "import com.yimei.cflow.api.models.flow._; (state: State) => Seq(Arrow(\"V1\", Some(\"E1\")))"
        }
    },
    "edges"    : {
        "E1" : {
            "name"       : "E1",
            "begin"      : "V0",
            "end"        : "V1",
            "userTasks"  : [],
            "partGTasks" : [],
            "partUTasks" : [],
            "autoTasks"  : []
        }
    }
};





var singleVertex  = {
    "id"          : "START",
    "description" : "发起申请",
    "program"     : "import com.yimei.cflow.api.models.flow._; (state: State) => Seq(Arrow(\"V1\", Some(\"E1\")))"
};


var edge      = {
    "id"         : "E1",
    "name"       : "E1",
    "begin"      : "V0",
    "end"        : "V1",
    "userTasks"  : [],
    "partGTasks" : [],
    "partUTasks" : [],
    "autoTasks"  : []
}


var point = {
    "id"          : "P1",
    "description" : "申请成功率"
};


(function (window) {

    'use strict';

    var formatter = {

        cyArrayToRawArray : function (sourceNodes, sourceEdges) {

            var globalConfig = {
                initial    : 'START',  // 代表初始节点
                graphJar   : "com.yimei.cflow.graph.money.MoneyGraphJar",
                timeout    : 100,  // 超时时间配置(图的属性)
                flowType   : '',   //流程类型
                persistent : true
            };

            var verticesArray = [];
            var edgeArray = [];

            var userTasksArray = [];
            var autoTasksArray = [];
            var partUTasksArray = [];
            var partGTasksArray = [];


            sourceNodes.forEach(function (node, nodeIndex) {
                var tempNode = {
                    id : node.sourceData.id,
                    description : node.sourceData.description,
                    program : node.sourceData.program
                }
                verticesArray.push(tempNode)
            })


            sourceEdges.forEach(function (edge, edgeIndex) {
                var tempEdge = {
                    id : edge.sourceData.id,
                    name : edge.sourceData.id,
                    begin : edge.sourceData.source,
                    end : edge.sourceData.target,
                    "userTasks": edge.sourceData.userTasks,
                    "autoTasks": edge.sourceData.autoTasks,
                    "partGTasks": edge.sourceData.partGTasks,
                    "partUTasks": edge.sourceData.partUTasks,
                    "allTasks": edge.sourceData.allTask
                }

                edgeArray.push(tempEdge)

                var taskTypeList = ['autoTasks', 'userTasks', 'partUTasks', 'partGTasks'];
                taskTypeList.forEach(function(taskType, taskTypeIndex){
                    tempEdge[taskType].forEach(function(task, taskIndex){

                        var fatherTask = {
                            id : task.sourceData.id,
                            guidKey : '',
                            ggidKey : '',
                            tasks : []
                        }

                        var newTask = {
                            id : task.sourceData.id,
                            type : task.sourceData.type,
                            description : task.sourceData.description,
                            points : []
                        }


                        if (taskType === 'userTasks') {
                            newTask.points = task.sourceData.points
                            userTasksArray.push(newTask)
                        }

                        if (taskType === 'autoTasks') {
                            newTask.points = task.sourceData.points
                            autoTasksArray.push(newTask)
                        }

                        if (taskType === 'partUTasks') {
                            fatherTask.guidKey = task.sourceData.guidKey

                            task.sourceData.tasks.forEach(function(subTask, subTaskIndex){
                                fatherTask.tasks.push({
                                    id : subTask.sourceData.id,
                                    type : subTask.sourceData.type,
                                    description : subTask.sourceData.description,
                                    points : subTask.sourceData.points
                                })
                            })
                            partUTasksArray.push(fatherTask)
                        }

                        if (taskType === 'partGTasks') {
                            fatherTask.ggidKey = task.sourceData.ggidKey

                            task.sourceData.tasks.forEach(function(subTask, subTaskIndex){
                                fatherTask.tasks.push({
                                    id : subTask.sourceData.id,
                                    type : subTask.sourceData.type,
                                    description : subTask.sourceData.description,
                                    points : subTask.sourceData.points
                                })
                            })

                            partGTasksArray.push(fatherTask)
                        }

                    })
                })


            })


            return {
                globalConfig : globalConfig,
                vertices : verticesArray,
                edges : edgeArray,
                userTasks : userTasksArray,
                autoTasks : autoTasksArray,
                partGTasks : partGTasksArray,
                partUTasks : partUTasksArray
            }

        },

        rawArrayToObj : function (source) {

            var verticesObject = {};
            var edgeObject = {};

            var userTasksObject = {};
            var autoTasksObject = {};
            var partUTasksObject = {};
            var partGTasksObject = {};

            source.vertices.forEach(function(vertex, vertexIndex){
                verticesObject[vertex.id] = vertex
            })

            source.edges.forEach(function(edge, edgeIndex){

                var newEdge = {
                    "id" : edge.id,
                    "name": edge.name,
                    "begin": edge.source,
                    "end": edge.target,
                    "userTasks": [],
                    "autoTasks": [],
                    "partUTasks": [],
                    "partGTasks": [],
                    "allTask": []
                }

                newEdge.userTasks = edge.userTasks.map(function(task, taskIndex){
                    return task.sourceData.id;
                });

                newEdge.autoTasks = edge.autoTasks.map(function(task, taskIndex){
                    return task.sourceData.id;
                });

                edge.partUTasks.forEach(function(task, taskIndex){

                    var tempTask = {
                        "guidKey": task.sourceData.guidKey,
                        "tasks": []
                    }

                    tempTask.tasks = task.sourceData.tasks.map(function(subTask, subTaskIndex){
                        return subTask.sourceData.id;
                    });

                    newEdge.partUTasks.push(tempTask)
                });


                edge.partGTasks.forEach(function(task, taskIndex){

                    var tempTask = {
                        "ggidKey": task.sourceData.ggidKey,
                        "tasks": []
                    }

                    tempTask.tasks = task.sourceData.tasks.map(function(subTask, subTaskIndex){
                        return subTask.sourceData.id;
                    });

                    newEdge.partGTasks.push(tempTask)
                });

                edgeObject[edge.id] = newEdge
            })


            source.userTasks.forEach(function(task, taskIndex){
                userTasksObject[task.id] = task
            })

            source.autoTasks.forEach(function(task, taskIndex){
                autoTasksObject[task.id] = task
            })


            source.partUTasks.forEach(function(task, taskIndex){

                var tempSubTaskIDList = task.tasks.map(function(subTask, subTaskIndex){
                    return subTask.id;
                });
                task.tasks = tempSubTaskIDList

                partUTasksObject[task.id] = task
            })

            source.partGTasks.forEach(function(task, taskIndex){

                var tempSubTaskIDList = task.tasks.map(function(subTask, subTaskIndex){
                    return subTask.id;
                });
                task.tasks = tempSubTaskIDList

                partGTasksObject[task.id] = task
            })


            return {
                globalConfig : source.globalConfig,
                vertices : verticesObject,
                edges : edgeObject,
                userTasks : userTasksObject,
                autoTasks : autoTasksObject,
                partGTasks : partGTasksObject,
                partUTasks : partUTasksObject
            }
        }
    }

    window.formatter = formatter;
})(window);