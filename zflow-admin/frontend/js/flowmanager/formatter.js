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
            var partGTasksArray = [];
            var partUTasksArray = [];

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
            })


            return {
                vertices : verticesArray,
                edges : edgeArray,
                userTasks : userTasksArray,
                autoTasks : autoTasksArray,
                partGTasks : partGTasksArray,
                partUTasks : partUTasksArray
            }

        },

        rawArrayToObj : function () {

        }
    }

    window.formatter = formatter;
})(window);