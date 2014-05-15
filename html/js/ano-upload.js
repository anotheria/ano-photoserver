
(function($){
    $.fn.anoUpload = function(options) {
        var PLUGIN_NAME = "anoUpload";
        var settings = {
            refreshTime: 1000
        },
            data = this.data(PLUGIN_NAME);
        if(data) {
            data.target.remove();
            clearTimeout(data.timer);
            settings = data.settings;
        } else {
            data = {};
            data.settings = settings;
            data.actionUrl = this.attr('action');
            this.data(PLUGIN_NAME, data);
        }
        data.uploading = false;
        if(options) {
            $.extend(settings,options);
        }
        var $this = this;
        var name = this.attr('name') + 'iframe';
        data.target = $('<iframe>')
            .css({'position':'absolute','top':'-1000px','width':'1px','height':'1px'})
            .attr('name',name);
        var requestStatus = function() {
            $.ajax({
                type: "GET",
                url: settings.actionUrl + settings.id,
                dataType: 'json',
                success: setStatus ,
                error: setError
            });
        };
        var setStatus = function(status) {
            if(!status) return;
            if(typeof settings.onStatus == 'function')
            {
                settings.onStatus(status);
            }
            if(status.status > 0)
            {
                data.timer = setTimeout(requestStatus, settings.refreshTime);
            } else
            {
                data.uploading = false;
            }
        };
        var setError = function(error)
        {
            if(typeof settings.onError == 'function')
            {
                settings.onError(error);
            }
        };
        var doUpload = function(config) {
            $.extend(settings,config);
            data.uploading = true;
            data.timer = setTimeout(requestStatus, 1);
            $this
                .attr({action: settings.actionUrl + settings.id, target: data.target.attr('name')})
                .after(data.target)
                .find('input:file').attr('name','file');
            $this.submit();
        }
        settings.actionUrl = data.actionUrl;
        $.ajax({
            type: 'GET',
            url: settings.actionUrl,
            dataType: 'json',
            success: doUpload,
            error: setError
        });
    }
})(jQuery);