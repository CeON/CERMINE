$(document).ready(function(){    
    var homeImg;
    var taskImg;
    var aboutImg;
    var gitImg;

    $("#home").mouseover(function(){
        homeImg = $(this).find("img").attr("src");
        $(this).find("img").attr("src", "static/images/icons/homeGreen.png");
    });
    $("#home").mouseout(function(){
        $(this).find("img").attr("src", homeImg);
    });
    
    $("#task").mouseover(function(){
        taskImg = $(this).find("img").attr("src");
        $(this).find("img").attr("src", "static/images/icons/gearGreen.png");
    });
    $("#task").mouseout(function(){
        $(this).find("img").attr("src", taskImg);
    });
    
    $("#about").mouseover(function(){
        aboutImg = $(this).find("img").attr("src");
        $(this).find("img").attr("src", "static/images/icons/infoGreen.png");
    });
    $("#about").mouseout(function(){
        $(this).find("img").attr("src", aboutImg);
    });
    
    $("#git").mouseover(function(){
        gitImg = $(this).find("img").attr("src");
        $(this).find("img").attr("src", "static/images/icons/gitGreen.png");
    });
    $("#git").mouseout(function(){
        $(this).find("img").attr("src", gitImg);
    });
    
});












