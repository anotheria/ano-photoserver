$(function () {
    var transitions = [];
    var rotation = 0;
    var wbid;

    function pushHistory(selection) {
        selection.rotation = rotation;
        transitions.push(selection);
    }

    function popHistory() {
        if (transitions.length < 2) {
            return;
        }
        transitions.pop();
        var newSelection = transitions.pop(),
            push = false;
        rotation = newSelection.rotation;
        $('.ano_phototool_photo').attr('src','photos/workbench?id=' + wbid + '&r=' + newSelection.rotation);
        ias.setSelection(newSelection.x1, newSelection.y1, newSelection.x2, newSelection.y2);
        ias.setOptions({show:true});
        ias.update();
        var selection = ias.getSelection();
        var img = document.getElementsByClassName("ano_phototool_photo");
        preview(img, selection, push);
        pushHistory(newSelection);
    }

    function SavePhoto()
    {
        var form = $('form[name="workbenchPictureForm"]');
        $.ajax({
            type:"POST",
            url:form.attr('action'),
            data:form.serialize(),
            success:function (data) {
                if (data) {
                    if (data.status == "OK") {

                        var $imgSelected = $('.image_selected'),
                            size = 300;
                        imgSrc = 'photos/d/' + data.encodedPhotoId + '/'+size+'/?preview=true',
                            imgId = data.encodedPhotoId;
                        $('form span.mt_x span').text('');
                        $('.ano_phototool_workbench').hide();
                        $('.uploadPicture').hide();

                        $('#photoUploadedDecisionButtons').hide();
                        $imgSelected.children('input.imageId').val(imgId);
                        if ($imgSelected.children('img').length > 0) {
                            $imgSelected.children('img').attr('src', imgSrc);
                        }
                        else {
                            $imgSelected.append("<img src=' " + imgSrc + " '/>");
                        }
                        $('.imageAddBlock').children('img').imgScaleCut('fixImages', false);
                    }
                    else
                        alert(data.error);
                }
            }
        });
        if(typeof ias != "undefined")
        {
            ias.cancelSelection();
        }
        $('.ano_phototool_preview').children('img').remove();
    }

    function CancelPhotoSaving()
    {
        $('form span.mt_x span').text('');
        $('.ano_phototool_workbench').hide();
        $('.uploadPicture').hide();
        $('#photoUploadedDecisionButtons').hide();
        if(typeof ias != "undefined")
        {
            ias.cancelSelection();
        }
        $('.ano_phototool_preview').children('img').remove();
    }

    function preview(img, selection, push, waitLoad) {
        if (!selection.width || !selection.height)
            return;
        var scaleX = 100 / selection.width;
        var scaleY = 100 / selection.height;
        $('.ano_phototool_photo').attr('src','photos/workbench?id=' + wbid + '&r=' + rotation);
        if(typeof waitLoad != "undefined")
        {
            $('.ano_phototool_photo').load(function(){
                ias.setOptions({show:true});
                ias.update();
            });
        }
        $('.ano_phototool_preview img').attr('src',$(img).attr('src'));
        $('.ano_phototool_preview img').css({
            width: Math.round(scaleX * $(img).width()),
            height: Math.round(scaleY * $(img).height()),
            marginLeft: -Math.round(scaleX * selection.x1),
            marginTop: -Math.round(scaleY * selection.y1)
        });
        if(push != false)
        {
            pushHistory(selection);
        }
        var t = '{' +
            '"x":' + Math.floor(selection.x1) + ',' +
            '"y":' + Math.floor(selection.y1) + ',' +
            '"w":' + Math.floor(selection.width) + ',' +
            '"h":' + Math.floor(selection.height) + ',' +
            '"r":' + rotation + '}';
        $('input[name=transition]').val(t);
    }

    $('.ano_phototool_rotateright').click(function (){
        rotation = (rotation + 1) % 4;
        var selection = ias.getSelection();
        var img = document.getElementsByClassName("ano_phototool_photo");
        preview(img, selection, true, true);
    });

    $('.ano_phototool_rotateleft').click(function (){
        rotation = (rotation + 3) % 4;
        var selection = ias.getSelection();
        var img = document.getElementsByClassName("ano_phototool_photo");
        preview(img, selection, true, true);
    });

    $('.ano_phototool_undo').click(popHistory);

    $('input:file').click(
        function () {
            $(this).one(
                'change',
                function () {
                    $(this).blur();
                }
            )
        }
    );

    $("form[name=uploadPicture]").bind('change', function () {
        if(typeof ias != "undefined")
        {
            ias.cancelSelection();
        }
        $('.validationWizard').hide();
        $('div#photoUploadedDecisionButtons').hide();
        $('div.ano_phototool_preview').fadeOut();
        $('div.ano_phototool_selector').fadeOut(function () {
            $('div.ano_phototool_workbench').slideUp();
        });
        $('.cabinet').fadeTo(0.5);
        var pBar = $('.progressbar');
        pBar.show();
        $(this).anoUpload({
            onStatus:function (status) {
                var text = '';
                if (status.status > 0) {
                    text = status.progress + '%';
                    pBar.css('background-position', ((status.progress - 100) * pBar.width() / 100) + 'px');
                }
                if (status.status == 0) {
                    text = status.filename + " " + status.size;
                    pBar.hide();
                    $.ajax({
                        type:'GET',
                        url:'photos/workbench?uploadid=' + status.id,
                        dataType:'json',
                        success:function (result) {

                            $('.ano_phototool_photo').load(function () {
                                $(this).unbind('load');
                                $('div.ano_phototool_workbench').slideDown(function () {
                                    ias =  $('.ano_phototool_photo').imgAreaSelect({
                                        x1: 10, y1: 10, x2: 100, y2: 100, handles: true,
                                        onInit: preview,
                                        onSelectChange: preview,
                                        instance: true,
                                        keys:true
                                    });
                                });
                                $('div.ano_phototool_preview').fadeIn();
                                $(this).focus();
                            });
                            var scrollTo = $('div#ano_phototool_bottom_anchor');
                            if (scrollTo) {
                                try {
                                    $('html, body').animate({scrollTop:scrollTo.offset().top}, 1000);
                                } catch (e) {
                                }
                            }
                            var controlButtons = $('div#photoUploadedDecisionButtons');
                            if (controlButtons) try {
                                controlButtons.show();
                            } catch (e) {
                            };
                            $('input[name=wbid]').val(result.id);
                            transitions = [];
                            rotation = 0;
                            wbid = result.id;
                            $('.ano_phototool_photo').attr('src', 'photos/workbench?id=' + wbid + '&r=' + rotation);
                            if($('.ano_phototool_preview').children('img').length > 0 )
                            {
                                $('.ano_phototool_preview').children('img').attr('src','photos/workbench?id=' + wbid + '&r=' + rotation);
                            }
                            else
                            {
                                $('.ano_phototool_preview').append('<img src="photos/workbench?id=' + wbid + '&r=' + rotation+'"/>');
                            }
                        },
                        error:function (error) {
                            alert(error);
                        }
                    });
                }
                if (status.status < 0) {
                    if ($("#link_photo_block").is(':visible')) {
                        $('.validationWizard').find('.image').remove();
                        var notificationConfig = {forcedHorizontal:'', offsetTop:0, offsetLeft:33};
                        var notification = new Notification(notificationConfig);
                        notification.show($('input[name="pLink"]'), "No large images could be found. Please check your link again and make sure that it points to one picture - not a whole website - and try again", true);
                    }
                    else {
                        text = "Error! (" + status.status + ")";
                    }
                }
                $('form span.mt_x span').text(text);
            }
        });
    });

    $('.imageAddBlock').live('click', function () {
        $('.image_selected').removeClass('image_selected');
        var $this = $(this);
        $this.addClass('image_selected');
        $('.uploadPicture').show();
        return false;
    });

    $('#save_photo').click(function(){
        SavePhoto();
        return false;
    });

    $('#cancel_photo').click(function(){
        CancelPhotoSaving();
        return false;
    });

    $('.imageAddBlock').children('img', this).imagesLoaded(function () {
        $('.imageAddBlock').children('img').imgScaleCut('fixImages', false)
    });

    $('.uploadPicture').hide();
});
