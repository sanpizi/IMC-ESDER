$(document).ready(function() {
    Page.extend({
        init: function() {
            this._init();

            var self = this;

            //初始化站点选择器
            this.initSitesSelector();

            //更新影响站点数
            //this.updateAffectSites();

            //禁用表单
            $('.grid :input').prop('disabled', true);

            //表单事件
            $('.form').on('click', '[type="button"]', function(e) {
                var $row = $(this).closest('tr'),
                    $input = $row.find('.setting-input'),
                    $target = $(e.target);

                if ($target.hasClass('save')) { //点击保存按钮
                    //提交参数设置
                    self.setParams($row);
                } else if ($target.hasClass('reset')) { //点击默认值按钮
                    $input.val($input.attr('data-default-value'));
                }

            });
        },

        //验证表单
        validateForm: function() {
            var result = true,
                self = this;

            return result;
        },

        //拉取原始数据
        getParams: function(siteId) {
            var self = this;
            if (!siteId) return;

            //更新标题
            for (var x in self.sites) {
                if (self.sites[x].id == siteId) {
                    var htmlTmpl = self.tmpl('<span title="Zone ID: <%=areaId%>"><%=areaName%></span> / <span title="Site ID: <%=id%>"><%=name%></span>');
                    $('#currentSite').html(htmlTmpl(self.sites[x]));
                    break;
                }
            };

            //启用表单
            $('.grid :input').prop('disabled', false);

            $.ajax({
                type: "GET",
                url: "/config",
                dataType: "json",
                data: {
                    siteId: siteId
                },
                success: function(data) {
                   for (var i = data.recordList.length - 1; i >= 0; i--) {
                        var signalName = data.recordList[i].signalName && data.recordList[i].signalName.replace(/^\w|\s+\w/gi, function($) {return $.toUpperCase();});
                        var signalValue = data.recordList[i].value && data.recordList[i].value.replace('-', '') || '';
                        $('td:contains("' + signalName + '")').next().children('input').val(signalValue);
                   };
                },
                error: function(err) {
                    //window.alert('Failed to get the settings.');
                    console.error('获取配置信息失败。');
                }
            });
        },

        //保存设置
        setParams: function($row) {
            var self = this,
                selectedSites = self.selector.getSelected().join(','),
                signalName = $row.find('td:first').text(),
                bt = $row.find('.save')[0],
                input = $row.find('.setting-input')[0];

            if (!selectedSites) {
                alert('No site was selected.');
                return;
            }

            if (!input.value) {
                alert('Please enter a value.');
                input.focus();
                return;
            }

            $.ajax({
                type: "POST",
                url: "/config",
                dataType: "json",
                data: {
                    siteId: selectedSites,
                    signalName: signalName,
                    newValue: input.value
                },
                beforeSend: function() {
                    //禁用按钮
                    bt.disabled = true;
                },
                success: function(data) {
                    if (data.result === 0) {
                        alert('Operation sent successfully.');
                    } else {
                        alert(data.errMsg);
                    }
                },
                error: function(err) {
                    //window.alert('Failed to save the settings.');
                    console.error('保存配置失败。');
                },
                complete: function() {
                    //启用按钮
                    bt.disabled = false;
                }
            });
        },

        //初始化站点选择器
        initSitesSelector: function() {
            //拉取树形菜单数据            
            var self = this;

            $.ajax({
                type: "GET",
                url: "/sites",
                dataType: "json",
                success: function(data) {
                    if (!data.siteList.length) return;

                    var siteList = data.siteList,
                        areas = {},
                        sites = {},
                        treeData = [{
                            "text": "All Zones",
                            "state": {
                                "opened": true
                            },
                            "children": []
                        }];

                    //提取所有区域、站点数据
                    for (var i = 0; i < siteList.length; i++) {
                        var areaId = siteList[i].areaId,
                            areaName = siteList[i].areaName,
                            site = {
                                "id": "site_" + siteList[i].id,
                                "text": siteList[i].name,
                                "icon": "jstree-" + siteList[i].status,
                                "state": {
                                    "selected": i === 0 //默认选中第一个
                                }
                            };

                        //建立站点数据模型
                        sites[siteList[i].id] = siteList[i];

                        //提取所有区域
                        if (!(areaId in areas)) {
                            areas[areaId] = {
                                "id": "area_" + areaId,
                                "text": areaName,
                                "state": {
                                    "opened": true
                                },
                                "children": []
                            }
                        }

                        areas[areaId].children.push(site);
                    };

                    //建立站点数据模型
                    self.sites = sites;

                    //构造 jsTree 所需要的数据格式
                    for(var x in areas) {
                        treeData[0].children.push(areas[x]);
                    }

                    //创建树
                    self.makeTree(treeData);

                    //默认加载第一个站点的配置信息
                    self.getParams(siteList[0].id);
                },
                error: function(err) {
                    //window.alert('Failed to get the global statistics data.');
                    console.error('获取站点数据失败。');
                }
            });

        },

        //生成树形菜单
        makeTree: function(data) {
            var self = this;

            //初始化树形菜单
            $('.selector').jstree({
                'core': {
                    'data': data,
                    'multiple': false
                }
            }).on('changed.jstree', function (e, data) {
                //获取选中的末级节点数
                var arrSelectedSites = data.instance.get_bottom_selected(),
                    siteId = null,
                    selectedSites = {};

                for (var i = 0; i < arrSelectedSites.length; i++) {
                    siteId = arrSelectedSites[i].slice(5);
                    selectedSites[siteId] = 1;

                    //加载选中站点的配置信息
                    $('#currentSite').html('Loading...');
                    $('.grid :input').prop('disabled', true); //禁用表单
                    self.getParams(siteId);
                }

                //更新站点数据模型
                for (var x in self.sites) {
                    if (x in selectedSites) {
                        self.sites[x].selected = true;
                    } else {
                        self.sites[x].selected = false;
                    }
                }
            });
        },

        selector: {

            //获取已选择站点 ID 数组
            getSelected: function() {
                var arrSites = $('.selector').jstree().get_bottom_selected();

                arrSites = $.map(arrSites, function(site) {
                    return site.slice(5);
                })

                return arrSites;
            }
        }
    });

    window.page = new Page();
});