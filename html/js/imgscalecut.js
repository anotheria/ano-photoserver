$.fn.imagesLoaded = function(callback){
  var elems = this.filter('img'),
      len = elems.length;

  elems.bind('load',function(){
      if (--len <= 0){ callback.call(elems,this); }
  }).each(function(){
     // cached images don't fire load sometimes, so we reset src.
     if (this.complete || this.complete === undefined){
        var src = this.src;
        // webkit hack from http://groups.google.com/group/jquery-dev/browse_thread/thread/eee6ab7b2da50e1f
        // data uri bypasses webkit log warning (thx doug jones)
        this.src = "data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///ywAAAAAAQABAAACAUwAOw==";
        this.src = src;
     }
  });

  return this;
};
(function($) {
    var defaults = {letterBox:'false'};
    var methods = {
        init:function(params){
            var options = $.extend({}, defaults, params);
            return this.each(function(){
                var thisOriginal = this,
                    $imgParent = $(thisOriginal).parent();
                $("<img/>")
                    .attr("src", $(thisOriginal).attr("src")+ "?" + new Date().getTime())
                    .load(function(){
                        var $this = $(this);
                        RememberOriginalSize(this);
                        var targetwidth = $imgParent.width();
                        var targetheight = $imgParent.height();
                        thisOriginal.width  = targetwidth;
                        thisOriginal.height = targetheight;
                        $(thisOriginal).css({position:'relative', overflow:'hidden'});
                        $(thisOriginal).css("left", 0);
                        $(thisOriginal).css("top", 0);
                });
            });
        },
        fixImages:function(letterBox){
            return this.each(function(){
                var thisOriginal = this,
                    $imgParent = $(thisOriginal).parent();
                $("<img/>")
                    .attr("src", $(thisOriginal).attr("src")+ "?" + new Date().getTime())
                    .load(function(){
                        var $this = $(this);
                        RememberOriginalSize(this);
                        var targetwidth = $imgParent.width();
                        var targetheight = $imgParent.height();
                        var srcwidth = this.originalsize.width;
                        var srcheight = this.originalsize.height;
                        var result = ScaleImage(srcwidth, srcheight, targetwidth, targetheight, letterBox);
                        thisOriginal.width = result.width;
                        thisOriginal.height = result.height;
                        $(thisOriginal).css({position:'relative', overflow:'hidden'});
                        $(thisOriginal).css("left", result.targetleft);
                        $(thisOriginal).css("top", result.targettop);
                    });
            });
        }


    };
    $.fn.imgScaleCut = function(method){
        if ( methods[method]){
            return methods[ method ].apply( this, Array.prototype.slice.call( arguments, 1 ));
        } else if ( typeof method === 'object' || ! method ) {
            return methods.init.apply( this, arguments );
        } else {
            $.error(method);
        }

    };
    function ScaleImage(srcwidth, srcheight, targetwidth, targetheight, fLetterBox) {
        var result = { width: 0, height: 0, fScaleToTargetWidth: true };
        if ((srcwidth <= 0) || (srcheight <= 0) || (targetwidth <= 0) || (targetheight <= 0)) {
            return result;
        }

        var scaleX1 = targetwidth;
        var scaleY1 = (srcheight * targetwidth) / srcwidth;

        var scaleX2 = (srcwidth * targetheight) / srcheight;
        var scaleY2 = targetheight;

        var fScaleOnWidth = (scaleX2 > targetwidth);
        if (fScaleOnWidth) {
            fScaleOnWidth = fLetterBox;
        }
        else {
            fScaleOnWidth = !fLetterBox;
        }
        if (fScaleOnWidth) {
            result.width = Math.floor(scaleX1);
            result.height = Math.floor(scaleY1);
            result.fScaleToTargetWidth = true;
        }
        else {
            result.width = Math.floor(scaleX2);
            result.height = Math.floor(scaleY2);
            result.fScaleToTargetWidth = false;
        }
        result.targetleft = Math.floor((targetwidth - result.width) / 2);
        result.targettop = Math.floor((targetheight - result.height) / 2);

        return result;
    }

    function RememberOriginalSize(img) {
        if(!img.originalsize)
        {
            img.originalsize = {width : img.width, height : img.height};
        }
    }
})(jQuery);