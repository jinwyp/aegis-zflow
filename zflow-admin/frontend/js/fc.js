(function(window, $, cytoscape, ejs){

    $.ajaxSettings.async = false; 

    var originalData;

    var chartEventCallback= function(cy){
        cy.nodes('.task-node').qtip({
            content: function(){
                return this.data().description || '暂无描述';
            },
            show: {
                event: 'mouseover'
            },
            hide: {
                event: 'mouseout'
            },
            position: {
                my: 'bottom center',
                at: 'top center'
            },
            style: {
                classes: 'qtip-bootstrap',
                tip: {
                    width: 16,
                    height: 8
                }
            }
        })

        cy.nodes('.task-node').on('click', function(e){
            var classes = this._private.classes;
            var data = this.data();
            var points = [];
            var task = data.original[(data.taskType=='autoTasks')?'autoTasks':'userTasks'][data.id];
            task && task.points.forEach(function(p, pi){
                var val;
                if(data.original.state.points.hasOwnProperty(p)){
                    if(data.original.state.points[p].memo){
                        var memo = data.original.state.points[p].memo;
                        (memo == 'img') && (val={'url': data.original.state.points[p].value, 'text': '查看图片'});
                        (memo == 'pdf') && (val={'url': data.original.state.points[p].value, 'text': '查看PDF文件'});
                    }

                    !val && (val = data.original.state.points[p].value);
                }else{
                    val = '未采集';
                }
                points.push({'key':p, 'value':val});
            })
            
            var data_ptDetail = {'points': points, 'task': {'type': data.taskType, 'id': data.id, 'classes': classes}};
            var ptDetail = ejs.compile($('#tmpl_ptDetail').html())(data_ptDetail);
            $('#ptDetail>div').html(ptDetail);

            $('.hastip').qtip({
                content: function(){
                    return '<span class="pointer"></span><div class="tip-pointtext-contentbg"></div><div class="tip-pointtextcontent"><div class="content">' + $(this).attr('data') + '</div></div>';
                },
                position: {
                    my: 'bottom right',
                    at: 'top right'
                },
                show: {
                    event: 'click'
                },
                hide: {
                    event: 'unfocus'
                },
                style: {
                    classes: 'qtip-bootstrap tip-pointtext',
                    tip: {
                        width: 16,
                        height: 8
                    }
                }
            })
            $('#refreshBtn').on('click', function(){
                var self = this;
                if($(this).hasClass('disabled')){
                    return;
                }
                $(this).addClass('disabled');
                $(this).parent().parent().append('<p class="successTip">已开始重新执行任务，请稍后刷新页面</p>');
                $('.successTip').delay(1000).fadeIn().delay(1500).fadeOut();
                setTimeout(function(){
                    $(self).removeClass('disabled');
                }, 5000)

                $.ajax({
                    url: '/auto/'+ originalData.state.flowType+'/'+ originalData.state.flowId +'/' + $(this).attr('data'),
                    method: 'POST',
                    async: true
                })
            })
        })

        cy.nodes('.judge-node').qtip({
            content: function(){
                return this.data().description;
            },
            show: {
                event: 'mouseover'
            },
            hide: {
                event: 'mouseout'
            },
            position: {
                my: 'bottom center',
                at: 'top center'
            },
            style: {
                classes: 'qtip-bootstrap',
                tip: {
                    width: 16,
                    height: 8
                }
            }
        })
    };
   
    var dateFormat = function(date, format){
        if(!/^[1-9]\d*$/.test(date)){
            return  date ;
        }
        var date = parseInt(date),
            currentTime =  new Date().getTime(),
            diffTime = currentTime - date;
        var minute = 60*1000,
            hour = 60*minute,
            day = 24*hour,
            format = format || 'YYYY年MM月DD日',
            alwaysDiff = alwaysDiff || false;
        var formatArr = ['YYYY','MM','DD','H','M','S'];
        var date = new Date(date),
            year = date.getFullYear(),
            month = date.getMonth()+1,
            month = (month>9) ? month : '0'+month,
            day = (date.getDate()>9) ? date.getDate() : '0'+ date.getDate(),
            hour = (date.getHours()>9) ? date.getHours() : '0'+ date.getHours(),
            minute = (date.getMinutes()>9) ? date.getMinutes() : '0'+date.getMinutes(),
            second = (date.getSeconds()>9) ? date.getSeconds() : '0'+date.getSeconds(),
            dateArr = [year,month,day,hour,':'+minute,':'+ second];
        for(var i=0; i < formatArr.length; i++){
            format = format.replace(formatArr[i],dateArr[i]);
        }
        return format;
    };

    var PAGE = function(){
        return {
            init: function(){
                originalData = this.getModel();
                this.fcRender();
                this.tmplRender();
            },
            getModel: function(){
                 var url = '/api/flow/' + location.search.match(new RegExp("[\?\&]id=([^\&]+)", "i"))[1];
                // var url = '../json/data4.json'
                $.getJSON(url, function(res){
                    originalData = res;
                })
                return originalData;
            },
            tmplRender: function(){
                var data_fcDetail = {'fcDetail': {
                    'id': originalData.state.flowId,
                    'uid': originalData.state.guid,
                    'type': originalData.state.flowType,
                    'utype': originalData.state.guid.substr(originalData.state.guid.split('-')[0].length+1),
                    'status': originalData.state.ending || '进行中'
                }};
                var fcDetail = ejs.compile($('#tmpl_fcDetail').html())(data_fcDetail);
                $('#fcDetail').html(fcDetail);

                var historyPoints = [];
                for( var i in originalData.state.points){
                    var p = originalData.state.points[i];
                    var memo = p.memo;
                    var val = false;
                    if(p.memo){
                        (p.memo == 'img') && (val = {url: p.value, text: '查看图片'}) && (memo = false);
                        (p.memo == 'pdf') && (val = {url: p.value, text: '查看PDF文件'}) && (memo = false);
                    } 
                    !val && (val = p.value);
                    historyPoints.push({
                        'name': i,
                        'value': val || '未采集',
                        'user': p.operator || '无',
                        'timestamp': dateFormat(p.timestamp, 'YYYY-MM-DD HMS') || '无',
                        'description': originalData.points[i] || '无', 
                        'comment': memo || '无'
                    })
                }
                var history = ejs.compile($('#tmpl_historyContainer').html())({'historyPoints': historyPoints});
                $('#historyContainer').html(history);
            },
            fcRender: function(){
                var chart = new flowChart('cy', originalData, chartEventCallback, {minZoom: 0.1, layout:{
                    name: 'dagre',
                    // name: 'cose-bilkent',
                    fit: true,
                    rankDir: "LR"
                }});
                
                window.cy = chart.cy;

                var count = 0;
                $('#cy canvas').css('visibility','hidden');
                chart.cy.onRender(function(){
                    count ++;
                    if(count==2){
                        chart.cy.zoom(0).center();
                        setTimeout(function(){
                            $('#cy canvas').css('visibility','visible');
                        }, 100)
                        chart.cy.delay(100).animate({fit: {padding:20}}, {duration: 300});
                    }
                })
            }
        }
    };
    
    window.PAGE = PAGE;

    new PAGE().init();
})(window, jQuery, cytoscape, ejs)




		