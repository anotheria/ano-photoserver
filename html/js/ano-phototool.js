(function( $ ){
	$.fn.anoRestrictToCanvas = function(canvas) {
		var canvas = canvas;
		
		return this.each(function() {   
			$this = $(this);
			var props = {};
			if($this.position().left < canvas.position().left) {
				props.left = canvas.position().left;
			}
			if($this.position().top < canvas.position().top) {
				props.top = canvas.position().top;
			}
			if($this.position().left + $this.width() > canvas.position().left + canvas.width()) {
				props.left = canvas.position().left + canvas.width() - $this.width();
				if(props.left < canvas.position().left) {
					props.left = canvas.position().left;
					props.width = canvas.width();
				}
			}
			if($this.position().top + $this.height() > canvas.position().top + canvas.height()) {
				props.top = canvas.position().top + canvas.height() - $this.height();
				if(props.top < canvas.position().top) {
					props.top = canvas.position().top;
					props.height = canvas.height();
				}
			}
            $this.css(props);
		});
    };
})( jQuery );

(function( $ ){
        $.fn.anoImageSelection = function(selection) {
        	var photo = this;
        	var selection = selection;

            return this.each(function() {
        			
        		
        		selection
        			.anoRestrictToCanvas(photo) 
        			.css('background', 'transparent url(' + photo.attr('src') + ') repeat scroll -' + (selection.position().left-photo.position().left) + 'px -' + (selection.position().top-photo.position().top) + 'px')
					.drag("start",function( ev, dd ){
						dd.maxLeft = photo.position().left;
						dd.maxWidth = photo.width()-2;
						dd.maxTop = photo.position().top;
						dd.maxHeight = photo.height()-2;
						dd.attr = $( ev.target ).attr("class");
						dd.width = selection.width();
						dd.height = selection.height();
					})
					.drag(function( ev, dd ){
						var props = {};
						if ( dd.attr.indexOf("E") > -1 ){
							props.width = Math.max( 32, dd.width + dd.deltaX );
							props.height = props.width;
						}
						if ( dd.attr.indexOf("S") > -1 ){
							props.height = Math.max( 32, dd.height + dd.deltaY );
							props.width = props.height;
						}
						if ( dd.attr.indexOf("W") > -1 ){
							props.width = Math.max( 32, dd.width - dd.deltaX );
							props.height = props.width;
							props.left = dd.originalX + dd.width - props.width;
							props.top = dd.originalY + dd.height - props.height;
						}
						if ( dd.attr.indexOf("N") > -1 ){
							props.height = Math.max( 32, dd.height - dd.deltaY );
							props.width = props.height;
							props.top = dd.originalY + dd.height - props.height;
							props.left = dd.originalX + dd.width - props.width;
						}
						if ( dd.attr.indexOf("selector") > -1 ){
							props.top = dd.offsetY;
							props.left = dd.offsetX;
						}
						props.left = Math.max(props.left, dd.maxLeft);
						props.left = Math.min(props.left, dd.maxLeft + dd.maxWidth - dd.width);
						props.top = Math.max(props.top, dd.maxTop);
						props.top = Math.min(props.top, dd.maxTop + dd.maxHeight - dd.height);
						props.width = Math.min(props.width, dd.maxWidth + dd.maxLeft - dd.originalX);
						props.height = Math.min(props.height, dd.maxHeight + dd.maxTop - dd.originalY);
                        if(props.width < props.height) {
							props.height = props.width;
						} else {
							props.width = props.height;
						}
						props.background = 'transparent url(' +photo.attr('src') + ') repeat scroll -' + (props.left-dd.maxLeft) + 'px -' + (props.top-dd.maxTop) + 'px';
						selection.css( props );
					});
			});
	};
})( jQuery );

(function($){
	
	$.fn.anoPhotopreview = function(selection) {  
   
		var selection = selection;
		
		return this.each(function() {
			
			$this = $(this);
			var img = $this.find('img');
			
			
			if(img.length == 0) {
				img = $($('<img/>')[0]);
				$this.append(img);
			}
			
			img
					.load(function() {
						$(this).unbind('load');
						$("<img/>")
							.load(function() {
								$(this).unbind('load');
								var w = this.width;
								var h = this.height;
								var $this = img.parent();
								var width = Math.floor(w * $this.width() / selection.w);
								var height = Math.floor(h * $this.height() / selection.h);
								var x = Math.floor(width / w * selection.x);
								var y = Math.floor(height / h * selection.y);
								img.css({position:'absolute',left:'-'+x+'px', top:'-'+y+'px', width:width+'px', height:height+'px'});
							})
							.attr("src", img.attr("src"));
					})
					.attr('src', selection.url + ($.browser.webkit ? '&ts=' + new Date() : ''));  
		});
	};
})(jQuery);