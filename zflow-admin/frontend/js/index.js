/**
 * Created by liushengbin on 2016/12/22.
 */
(function(window, $, ejs){

    $.ajaxSettings.async = false;

    var currentPage=1;
    var pageSize=10;
    var container = $("#panel-pagination");

    var PAGE = function() {
        return {
            init :       function () {
                console.log('------init------');
                container.pagination({
                    dataSource : function (done) {
                        var result = [];
                        for (var i = 1; i < 110; i++) {
                            result.push(i);
                        }
                        done(result);
                    },
                    pageNumber : currentPage,
                    pageSize :   pageSize,
                    callback :   function (data, pagination) {
                        currentPage = pagination.pageNumber;
                        pageSize = pagination.pageSize;
                        console.log('------callback------' + currentPage);
                        var history = ejs.compile($('#tmpl_table').html())(getData(currentPage));
                        $('#table-list').html(history);
                    }
                });
            }
        }
    };

    window.PAGE = PAGE;

    new PAGE().init();

    function formatData (data) {
        console.log('------formatData------');
        var newData = [];
        data.flows.forEach(function (item) {
            item.company_type = item.user_type.split('-')[0];
            item.company_id = item.user_type.split('-')[1];
            newData.push(item);
        });
        data.flows = newData;
        return data;
    }

    function getSearchUrl (){
        var company_type = $("#input-company-type").val();
        var company_id = $("#input-company-id").val();
        var userId = $("#input-user-id").val();
        var flowId = $("#input-flow-id").val();
        var flowType = $("#input-flow-type").val();
        var flowState = $("#input-flow-state").val();

        var temp = "";
        if(!(flowId==null || flowId=="")){
            temp = temp +"flowId="+flowId+"&"
        }
        if(!(flowType==null || flowType=="")){
            temp = temp +"flowType="+flowType+"&"
        }
        if(!(company_type==null || company_type==""||company_id==null || company_id=="")){
            temp = temp +"userType="+company_type+"-"+company_id+"&"
        }
        if(!(userId==null || userId=="")){
            temp = temp +"userId="+userId+"&"
        }
        if(!(flowState==null || flowState=="")) {
            temp = temp + "status=" + flowState + "&"
        }
        var url = "";

        if(temp != ""){
            url = "/api/flow?"+temp.substring(0,temp.length-1)+"&page="+currentPage+"&pageSize="+pageSize;
        } else {
            url = "/api/flow?page="+currentPage+"&pageSize="+pageSize;
        }
        return url;
    }

    function getData (pageNumber){
        console.log('------getData------');

        var dataList = [];

        //服务器数据
        $.ajax({
            method: "get",
            url: getSearchUrl()
        }).done(function (data) {
            formatData(data);
            // console.dir(data);
        });

        //本地数据
        // $.getJSON('./json/dataList.json', function(res){
        //     console.log(res.dataList[pageNumber-1]);
        //      dataList= formatData(res.dataList[pageNumber-1]);
        //     console.log(dataList);
        // });
        return dataList;
    }

    $(".btn-submit").click(function(){
        currentPage = 1;
        new PAGE().init();
    });

    $("#input-user-type").focus(function () {
        if($(".user-type-ul").hasClass('hidden')){
            $(".user-type-ul").removeClass('hidden');
        }else{
            $(".user-type-ul").addClass('hidden');
        }
    });

    $("#input-user-type").blur(function () {
        if($(".user-type-ul").hasClass('hidden')){
            $(".user-type-ul").removeClass('hidden');
        }else{
            $(".user-type-ul").addClass('hidden');
        }
    });

    $(".user-type-ul li").mousedown(function () {
        $("#input-user-type").val($(this).text());
    });

    $("#input-flow-type").focus(function () {
        if($(".flow-type-ul").hasClass('hidden')){
            $(".flow-type-ul").removeClass('hidden');
        }else{
            $(".flow-type-ul").addClass('hidden');
        }
    });

    $("#input-flow-type").blur(function () {
        if($(".flow-type-ul").hasClass('hidden')){
            $(".flow-type-ul").removeClass('hidden');
        }else{
            $(".flow-type-ul").addClass('hidden');
        }
    });

    $(".flow-type-ul li").mousedown(function () {
        $("#input-flow-type").val($(this).text());
    });

    $("#input-status").focus(function () {
        if($(".status-ul").hasClass('hidden')){
            $(".status-ul").removeClass('hidden');
        }else{
            $(".status-ul").addClass('hidden');
        }
    });

    $("#input-status").blur(function () {
        if($(".status-ul").hasClass('hidden')){
            $(".status-ul").removeClass('hidden');
        }else{
            $(".status-ul").addClass('hidden');
        }
    });

    $(".status-ul li").mousedown(function () {
        $("#input-status").val($(this).text());
    });

})(window, jQuery, ejs)