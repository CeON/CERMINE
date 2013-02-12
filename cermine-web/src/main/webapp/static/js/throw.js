$(document).ready(function() {
	//Reset 
	hideElement();
	resetPosition(250,100);

	
});

var posX = 0, posY = 0;
var distance = 200;
var duration = 1000;
var tick = 20;
var g = 0.02381;
var derY = 20; 

var start = function(){
	setTimeout("action()", 1000);
}

var action = function(){
	var end = false;
	var derByX = distance / (duration / tick); 
	var derByY = derY - g * (duration /tick);
	derY = +derY - g * (duration /tick);
	
	if(posX >= 400){hideElement();resetPosition(250,100);end = true;}
	if(posY > 220){hideElement();}
	
	moveByX(derByX);moveByY(derByY*0.25);
	if(!end){
		showElement();
		setTimeout(function(){
			action();
			}, tick);
	}
}


var moveToX = function(x){
	$("#xml-parser").css({"left":x+"px"});
}
var moveToY = function(y){
	$("#xml-parser").css({"top":y+"px"});
}
var moveByX = function(x){
	var baseX = parseInt($("#xml-parser").css("left"));
	posX = baseX + x;
	$("#xml-parser").css({"left": posX +"px"});
	//alert(posX);
}
var moveByY = function(y){
	var baseY = parseInt($("#xml-parser").css("top"));
	posY = baseY - y;
	$("#xml-parser").css({"top": posY +"px"});
}

var resetPosition = function(x, y){
	posX = x; posY = y;
	moveToX(x);
	moveToY(y);
	derY = 20;
}
var hideElement = function(){
	$("#xml-parser").css("display","none");	
}
var showElement = function(){
	$("#xml-parser").css("display","inline-block");
}
