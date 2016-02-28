// URL query parameters
var parameters = {};
window.location.search.substr(1).split("&").forEach(function(item) {
	parameters[item.split("=")[0]] = item.split("=")[1];
});

//Helper functions
function animate(duration, action, onComplete) {
	var animationFrame = window.requestAnimationFrame
	                  || window.webkitRequestAnimationFrame
	                  || window.mozRequestAnimationFrame
	                  || window.oRequestAnimationFrame
	                  || window.msRequestAnimationFrame
	                  || function(callback) {
	                     	window.setTimeout(callback, 1000 / 60);
	                     };
	var start = null;
	function step(timestamp) {
		if (!start)
			start = timestamp;
		var progress = timestamp - start;
		if (typeof action === "function")
			action(progress);
		if (progress < duration)
			animationFrame(step);
		else if (typeof onComplete === "function")
			onComplete();
	}
	animationFrame(step);
}

function getScrollTop() {
	if (window.pageYOffset !== undefined)
		return window.pageYOffset;
	return (document.documentElement || document.body.parentNode || document.body).scrollTop;
}

function easeInOutQuad(progress, start, delta, duration) {
	progress /= (duration / 2);
	if (progress < 1)
		return ((progress * progress * (delta / 2)) + start);
	--progress;
	return ((((progress * (progress - 2)) - 1) * (-delta / 2)) + start);
}

var headerHeight = 0;
function animateScroll(top, duration, callback) {
	if (top === headerHeight)
		return;

	var scrollTop = getScrollTop();
	animate(duration, function(progress) {
		window.scroll(0, easeInOutQuad(progress, scrollTop, top - scrollTop - headerHeight, duration));
	}, callback);
}

(function() {
	"use strict";

	// DOM queries
	var header = document.querySelector("header");
	headerHeight = header.getBoundingClientRect().height;
	var scrollTop = document.querySelector(".scroll-top");

	scrollTop.addEventListener("click", function() {
		animateScroll(0, 750);
	});
	
	// Scroll
	function checkHeight() {
		var scrolled = (getScrollTop() > 0);
		scrollTop.classList.toggle("scrolled", scrolled);
		header && header.classList.toggle("scrolled", scrolled);
	}
	checkHeight();
	window.addEventListener("scroll", checkHeight);
})();
