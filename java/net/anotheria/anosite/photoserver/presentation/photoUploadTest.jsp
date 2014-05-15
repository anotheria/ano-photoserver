<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<%@ taglib uri="http://www.anotheria.net/ano-tags" prefix="ano" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>PhotoUpload</title>
    <link rev="stylesheet" rel="stylesheet" media="all" href="../styles/photoupload.css">
    <link rev="stylesheet" rel="stylesheet" media="all" href="../styles/si-files.css">
    <link rev="stylesheet" rel="stylesheet" media="all" href="../styles/imgareaselect-default.css">
    <script src="../js/jquery-1.8.2.min.js" type="text/javascript"></script>
    <script type="text/javascript" src="../js/ano-upload.js"></script>
    <script type="text/javascript" src="../js/photoupload.js"></script>
    <script type="text/javascript" src="../js/jquery.imgareaselect.js"></script>
    <script type="text/javascript" src="../js/imgscalecut.js"></script>
    <script type="text/javascript" src="../js/si-files.js"></script>
</head>
<body>
<div class="imageAddBlock">
</div>
<div id="uploadPictureDialog">
    <div class="uploadPicture">
        <div class="white_box mb_15">
            <form id="add_photo_form" name="uploadPicture" enctype="multipart/form-data" action="photos/upload?id=" method="POST">
                <div class="white_box">
                    <div style="position:static;" class="in">
                                <span id="add_photo_block">
                                    <div class="info">
                                        <p>Please choose a photo to add</p>
                                    </div>
                                    <label class="lheight hauto">
                                        Please select a photo from your device (max
                                        <b>10 MB</b>
                                        )
                                    </label>
                                     <div class="col_1">
                                         <label class="cabinet">
                                             <input type="file" class="file">
                                         </label>
                                        <span class="small mt_x">
                                        <img style="display:none" src="images/progressbar.gif" class="progressbar">
                                        <span>No image selected</span>
                                        </span>
                                     </div>
                                </span>
                        <div class="clear"></div>
                        <div style="display:none; width:540px; margin-left:240px;" class="ano_phototool_workbench">
                            <div class="ano_phototool_col1">
                                <img class="ano_phototool_photo">
                            </div>
                            <div class="ano_phototool_col2">
                                <div class="ano_phototool_preview"></div>
                                <div class="ano_phototool_rotateleft ano_phototool_button"><img src="images/rotateLeft.png"></div>
                                <div class="ano_phototool_rotateright ano_phototool_button"><img src="images/rotateRight.png"></div>
                                <div class="ano_phototool_undo ano_phototool_button"><img src="images/undo.png"></div>
                            </div>
                        </div>
                    </div>

                </div>
            </form>

        </div>
    </div>

    <form name="workbenchPictureForm" method="post" action="photos/workbench">
        <input type="hidden" value="" name="wbid">
        <input type="hidden" value="" name="transition">
        <input type="hidden" value="" name="position">

        <div style="display:none;" id="photoUploadedDecisionButtons" class="mt-12">
            <a href="#" id="cancel_photo" class="button flr"> <span>Cancel</span></a>
            <a href="#" id="save_photo" class="button flr"> <span>Save</span></a>
        </div>
    </form>
    </div>
</div>
</body>
</html>